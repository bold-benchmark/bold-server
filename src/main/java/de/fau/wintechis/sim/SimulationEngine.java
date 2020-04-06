package de.fau.wintechis.sim;

import de.fau.wintechis.gsp.GraphStoreHandler;
import de.fau.wintechis.gsp.GraphStoreListener;
import de.fau.wintechis.io.FileUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sail.SailRepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.NotifyingSailConnection;
import org.eclipse.rdf4j.sail.SailConnection;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class SimulationEngine {

    private final static String UPDATE_TIME =
        "PREFIX : <" + Vocabulary.NS + ">\n" +
        "DELETE { ?sim :currentTime ?time } INSERT { ?sim :currentTime ?time_p }\n" +
        "WHERE { ?sim :currentTime ?time BIND (?time + 1 AS ?time_p) }";

    private final Timer timer;

    private final Map<String, org.eclipse.rdf4j.query.Update> updates;

    private final Map<String, TupleQuery> queries;

    private final Map<TupleQuery, Writer> writers;

    private final RepositoryConnection connection;

    private final UpdateHistory history;

    private final Server server;

    public SimulationEngine(int port) {
        this.timer = new Timer();

        this.updates = new HashMap<>();
        this.queries = new HashMap<>();
        this.writers = new HashMap<>();

        MemoryStore store = new MemoryStore();
        Repository repo = new SailRepository(store);
        connection = repo.getConnection();

        history = new UpdateHistory();

        ValueFactory vf = Vocabulary.VALUE_FACTORY;
        Resource sim = vf.createIRI(Vocabulary.NS, "sim");
        // simulation time is an arbitrary integer starting from 0
        // (makes it easier to formulate time-dependent updates in SPARQL)
        connection.add(sim, Vocabulary.CURRENT_TIME, vf.createLiteral(0));

        // time must be updated first, before any other resource
        this.updates.put("sim.rq", connection.prepareUpdate(UPDATE_TIME));

        GraphStoreHandler handler = new GraphStoreHandler(repo);
        server = new Server(port);
        server.setHandler(handler);

        try {
            server.start();

            SimulationRunner runner = new SimulationRunner();
            handler.addGraphStoreListener(runner);
        } catch (Exception e) {
            e.printStackTrace(); // TODO clean error handling
        }

        Vocabulary.registerFunctions();
    }

    public void registerUpdate(String filename) throws IOException {
        String buf = FileUtils.asString((FileUtils.getFileOrResource(filename)));
        this.registerUpdate(filename, buf);
    }

    public void registerUpdate(String name, String sparulString) throws IOException {
        org.eclipse.rdf4j.query.Update u = connection.prepareUpdate(sparulString);
        this.updates.put(name, u);
    }

    public void registerQuery(String filename) throws IOException {
        String buf = FileUtils.asString((FileUtils.getFileOrResource(filename)));
        this.registerQuery(filename, buf);
    }

    public void registerQuery(String name, String sparqlString) throws IOException {
        TupleQuery q = connection.prepareTupleQuery(sparqlString);
        this.queries.put(name, q);
    }

    public void loadData(String filename) throws IOException {
        String base = server.getURI().toString();
        RDFFormat format = Rio.getParserFormatForFileName(filename).orElseThrow(() -> new IOException());

        connection.add(FileUtils.getFileOrResource(filename), base, format);
    }

    public void executeUpdate(String filename) throws IOException {
        String sparulString = FileUtils.asString(FileUtils.getFileOrResource(filename));
        connection.prepareUpdate(sparulString).execute();
    }

    private class SimulationRunner implements GraphStoreListener {

        private static final String QUERY_RUNNER_CONFIG =
            "PREFIX : <" + Vocabulary.NS + ">\n" +
            "SELECT ?slot ?it WHERE {\n" +
            "?sim :timeslotDuration ?slot ; :iterations ?it }";

        private final IRI sim;

        private final TupleQuery query;

        public SimulationRunner() {
            sim = Vocabulary.VALUE_FACTORY.createIRI(server.getURI().resolve("sim").toString());
            query = connection.prepareTupleQuery(QUERY_RUNNER_CONFIG);
        }

        @Override
        public void graphRetrieved(IRI graphName) {
            // does nothing
        }

        @Override
        public void graphUpdated(IRI graphName) {
            if (graphName.equals(sim)) {
                TupleQueryResult res = query.evaluate();

                if (res.hasNext()) {
                    BindingSet mu = res.next();

                    Integer timeSlotDuration = Integer.parseInt(mu.getValue("slot").stringValue());
                    Integer numberOfIterations = Integer.parseInt(mu.getValue("it").stringValue());

                    run(timeSlotDuration, numberOfIterations);
                }
            }
        }

        @Override
        public void graphExtended(IRI graphName) {
            graphUpdated(graphName);
        }

        @Override
        public void graphDeleted(IRI graphName) {
            if (graphName.equals(sim)) {
                cancel();
            }
        }

        private void run(Integer timeSlot, Integer iterations) {
            cancel(); // TODO avoid generating empty files when cancelling

            for (Map.Entry<String, TupleQuery> kv : queries.entrySet()) {
                try {
                    String name = kv.getKey().replaceFirst("(\\.rq|\\.sparql)?$", ".tsv");
                    writers.put(kv.getValue(),  new FileWriter(name));
                } catch (IOException e) {
                    e.printStackTrace(); // TODO clean error handling
                }
            }

            getNotifyingSailConnection(connection).addConnectionListener(history);

            TimerTask task = new IterationTask(iterations);
            timer.scheduleAtFixedRate(task, 0, timeSlot);
        }

        private void cancel() {
            timer.purge();
        }

    }

    private class IterationTask extends TimerTask {

        private Integer count = 0;

        private Integer maxIterations;

        public IterationTask(Integer max) {
            this.maxIterations = max;
        }

        @Override
        public boolean cancel() {
            getNotifyingSailConnection(connection).removeConnectionListener(history);
            clear();
            return true;
        }

        @Override
        public void run() {
            if (count++ > maxIterations) {
                getNotifyingSailConnection(connection).removeConnectionListener(history);

                connection.clear();

                // replays updates and submit queries at each timestamp
                for (UpdateHistory.Update cs : history) {
                    connection.remove(cs.getDeletions());
                    connection.add(cs.getInsertions());

                    for (TupleQuery q : queries.values()) {
                        // TODO put this code in a TupleQueryResultHandler
                        // vars are ordered for deterministic rendering
                        Set<String> vars = new TreeSet<>();
                        for (BindingSet mu : q.evaluate()) {
                            String row = "";

                            if (vars.isEmpty()) {
                                vars.addAll(mu.getBindingNames());
                            }

                            for (String v : vars) {
                                if (!row.isEmpty()) row += "\t";
                                row += mu.getValue(v).stringValue();
                            }
                            row += "\n";

                            try {
                                writers.get(q).append(row);
                            } catch (IOException e) {
                                e.printStackTrace(); // TODO clean error handling
                            }
                        }
                    }
                }

                for (Writer w : writers.values()) {
                    try {
                        w.close();
                    } catch (IOException e) {
                        e.printStackTrace(); // TODO clean error handling
                    }
                }

                clear();
            } else {
                history.timeIncremented();

                for (org.eclipse.rdf4j.query.Update u : updates.values()) {
                    u.execute();
                }
            }
        }

        private void clear() {
            writers.clear();
            history.clear();
        }

    }

    private NotifyingSailConnection getNotifyingSailConnection(RepositoryConnection con) {
        SailConnection sailCon = ((SailRepositoryConnection) con).getSailConnection();
        return (NotifyingSailConnection) sailCon;
    }

}
