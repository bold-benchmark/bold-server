package org.bold.sim;

import org.bold.Configurator;
import org.bold.io.FileUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.IntegerLiteral;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.*;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Stream;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Main entity of the BOLD server, managing the state of the simulation (configuration, init, runtime, replay) and the
 * RDF dataset underlying the simulation.
 *
 * The class {@link SimulationHandler} provides an interface between the simulation engine and hypermedia agents.
 */
public class SimulationEngine {

    public enum EngineState {
        CREATED,
        CONFIGURED,
        EMPTY_STORE,
        READY,
        RUNNING,
        REPLAYING,
        DIRTY_STORE
    }

    private final Logger log = LoggerFactory.getLogger(SimulationEngine.class);

    private Long timeSlotDuration = null;

    private EngineState currentState = EngineState.CREATED;

    private final Model dataset = new LinkedHashModel();

    private final Map<String, Update> singleUpdates = new LinkedHashMap<>();

    private final Map<String, Update> continuousUpdates = new LinkedHashMap<>();

    private final Map<String, TupleQuery> queries = new HashMap<>();

    private String dumpPattern = null;

    private String faultFilename = "faults.tsv"; // FIXME as config parameter

    private String interactionFilename = "interactions.tsv"; // FIXME as config parameter

    private final RepositoryConnection connection;

    private RepositoryConnection replayConnection = null;

    private final UpdateHistory updateHistory;

    private final InteractionHistory interactionHistory;

    private final List<Statement> simSav = new LinkedList<Statement>();
    Set<IRI> predicatesInterestingForSaving = new HashSet<IRI>(
			Arrays.asList(Vocabulary.TIMESLOT_DURATION, Vocabulary.WALLCLOCK_TIMESLOT_DURATION,
					Vocabulary.INITIAL_TIME, Vocabulary.RANDOM_SEED, Vocabulary.ITERATIONS));

    private final String baseURI;

    private final IRI simResource;

    private Timer timer;

    private BooleanQuery simRunningQuery = null; // TODO clean assignment

    public SimulationEngine(String base, RepositoryConnection con, UpdateHistory updates, InteractionHistory interactions) {
        baseURI = base;

        // RDF store initialization
        Vocabulary.registerFunctions();
        connection = con;

        simResource = connection.getValueFactory().createIRI(baseURI + Configurator.SIMULATION_RESOURCE_TARGET);

        updateHistory = updates;
        interactionHistory = interactions;

        try {
            // sim resource must be updated first, before any other resource
            registerSingleUpdate("sim-init.rq");
            registerContinuousUpdate("sim.rq");

            // simulation ends when no iteration is left in sim resource
            String buf = FileUtils.asString(FileUtils.getFileOrResource("sim-running.rq"));
            simRunningQuery = connection.prepareBooleanQuery(QueryLanguage.SPARQL, buf, baseURI);

            callTransition();
        } catch (Exception e) {
            e.printStackTrace(); // TODO clean error handling
        }
    }

    public EngineState getCurrentState() {
        return currentState;
    }

    public SimulationEngine registerContinuousUpdate(String filename) throws IOException {
        String buf = FileUtils.asString((FileUtils.getFileOrResource(filename)));
        registerContinuousUpdate(filename, buf);

        return this;
    }

    public SimulationEngine registerContinuousUpdate(String name, String sparulString) throws IOException {
        Update u = connection.prepareUpdate(QueryLanguage.SPARQL, sparulString, baseURI);
        continuousUpdates.put(name, u);

        return this;
    }

    public SimulationEngine registerQuery(String filename) throws IOException {
        String buf = FileUtils.asString((FileUtils.getFileOrResource(filename)));
        registerQuery(filename, buf);

        return this;
    }

    public SimulationEngine registerQuery(String name, String sparqlString) throws IOException {
        TupleQuery q = connection.prepareTupleQuery(QueryLanguage.SPARQL, sparqlString, baseURI);
        queries.put(name, q);

        return this;
    }

    public SimulationEngine registerDataset(String filename) throws IOException {
        RDFFormat format = Rio.getParserFormatForFileName(filename).orElseThrow(() -> new IOException());
        Model ds = Rio.parse(FileUtils.getFileOrResource(filename), baseURI, format);
        dataset.addAll(ds);
        Iterable<Statement> triplesFromDefaultGraph = ds.getStatements(null, null, null, (Resource) null);
		for (Statement s : triplesFromDefaultGraph) {
			dataset.add(s.getSubject(), s.getPredicate(), s.getObject(),
					new Resource[] { connection.getValueFactory().createIRI(baseURI.toString()) });
		}

        return this;
    }

    public SimulationEngine registerSingleUpdate(String filename) throws IOException {
        String buf = FileUtils.asString((FileUtils.getFileOrResource(filename)));
        registerSingleUpdate(filename, buf);

        return this;
    }

    public SimulationEngine registerSingleUpdate(String name, String sparulString) throws IOException {
        Update u = connection.prepareUpdate(QueryLanguage.SPARQL, sparulString, baseURI);
        singleUpdates.put(name, u);

        return this;
    }

    public SimulationEngine setDumpPattern(String filenamePattern) {
        dumpPattern = filenamePattern;

        return this;
    }

    public void registrationDone() {
        callTransition();
    }

    /**
     * For test purposes.
     *
     * @return the engine's repository connection
     */
    RepositoryConnection getConnection() {
        return connection;
    }

    void callTransition() {
		boolean oneMoreTransition = false;
		do {
		oneMoreTransition = false;
        switch (currentState) {
            case CREATED:
                log.info("Simulation engine created.");
                // TODO move constructor statements to separate function?
                currentState = EngineState.CONFIGURED;
                break;

            case CONFIGURED:
                // configuration done by successive calls to class methods
                // TODO use a Configuration object
                log.info("Simulation engine configured. Current configuration: (single updates) {}; (continuous updates) {}; (queries) {}; (dump pattern) {}.", singleUpdates.keySet(), continuousUpdates.keySet(), queries.keySet(), dumpPattern);
                log.info("Waiting for agent's start command...");
                loadSimAndDataset();
                currentState = EngineState.EMPTY_STORE;
                break;

            case EMPTY_STORE:
                // FIXME source state should be called 'configured' (unncessary indirection)
                log.info("Initializing simulation run...");
                init();
                log.info("Simulation ready: {} resources, {} quads in dataset.", dataset.contexts().size(), dataset.size());
                // TODO log nb of iterations (estimated duration)
                currentState = EngineState.READY;
				oneMoreTransition = true;
                break;

            case READY:
                log.info("Simulation parameters: ");
                IRI simIRI = connection.getValueFactory().createIRI(baseURI + Configurator.SIMULATION_RESOURCE_TARGET);
                RepositoryResult<Statement> res = connection.getStatements(null, null, null, simIRI);
                for (Statement stmt:res)
					log.info(stmt.toString());
                log.info("Simulation stops when the following query returns true: {}", simRunningQuery);
                log.info("Simulation running...");
                run();
                currentState = EngineState.RUNNING;
                break;

            case RUNNING:
                Boolean simRunning = simRunningQuery.evaluate();
                if (simRunning) {
                    update();
                } else {
                    currentState = EngineState.REPLAYING;
                    oneMoreTransition = true;
                }
                break;

            case REPLAYING:
                log.info("Simulation run done. Replaying simulation...");
                saveSim();
                replay();
                log.info("Replay done.");
                currentState = EngineState.DIRTY_STORE;
				oneMoreTransition = true;
                break;

            case DIRTY_STORE:
                log.info("Results written to file(s). Cleaning resources...");
                clean();
                currentState = EngineState.CONFIGURED;
				oneMoreTransition = true;
                break;

            default:
                throw new IllegalSimulationStateException();
        }
        } while (oneMoreTransition);
    }


	private void loadSimAndDataset() {
		// If there is a potentially modified configuration in the store, use it and not
		// the one from the loaded dataset.
		if (connection.hasStatement(null, null, null, false, simResource)) {
			log.info("I do not use the configuration from the Trig files but the the configuration at {}",
					Configurator.SIMULATION_RESOURCE_TARGET);
			// saving the important content of sim
			simSav.clear();
			for (Statement stmt : connection.getStatements(null, null, null, simResource)) {
				if (predicatesInterestingForSaving.contains(stmt.getPredicate())) {
					log.info("Saving configuration statement {}", stmt);
					simSav.add(stmt);
				}
			}
		}

		connection.add(dataset);

		// overwriting with the saved configuration
		if (!simSav.isEmpty()) {
			log.info("loading simulation configuration saved before the cleanup");
			connection.clear(simResource);
			connection.add(simSav, simResource);
		}
	}

    private void init() {
        long before = System.currentTimeMillis();

		for (Entry<String, Update> u : singleUpdates.entrySet()) {
            log.debug("Applying single update from {}", u.getKey());
            u.getValue().execute();
        }
        long after = System.currentTimeMillis();
        long t = after - before;
        updateHistory.timeIncremented(t);
        interactionHistory.timeIncremented(t);
    }

    private void run() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                callTransition();
            }
        };

        timer = new Timer();
        long tsDuration = getTimeslotDuration();
        log.info("Wallclock timeslot duration: {}", tsDuration);
        timer.scheduleAtFixedRate(task, 0, tsDuration);
    }

    private void update() {
        long before = System.currentTimeMillis();
        for (Update u : continuousUpdates.values()) u.execute();
        long after = System.currentTimeMillis();

        long t = after - before;
        updateHistory.timeIncremented(t);
        interactionHistory.timeIncremented(t);

        if (t > getTimeslotDuration()) { // TODO cache results
            log.warn("updates took more than timeslot duration ({} ms).", after - before); // TODO record as TSV instead
        }
    }

	private void saveSim() {
		log.info("Saving the contents of {}", simResource);
		simSav.clear();
		for (Statement stmt : connection.getStatements(null, null, null, simResource)) {
			if (predicatesInterestingForSaving.contains(stmt.getPredicate())) {
				log.debug("Saving configuration statement {}", stmt);
				simSav.add(stmt);
			}
		}
	}

    private void replay() {
        timer.cancel();
        timer.purge();

        // TODO put all formatting to separate classes

        String timestamp = String.format("# end of run: %1$tFT%1$tT%1$tz\n", Calendar.getInstance());

        try {
            Writer w = new FileWriter(faultFilename, true);

            StringBuilder str = new StringBuilder();
            for (String f : queries.keySet()) {
                str.append(String.format("\t\"%s\"", f));
            }
            w.append(String.format("# \"iteration\"%s\n", str.toString()));

            RDFFormat dumpFormat = null;
            if (dumpPattern != null) {
                FileUtils.makePath(dumpPattern);
                dumpFormat = Rio.getParserFormatForFileName(dumpPattern).orElse(RDFFormat.TRIG);
            }

            if (replayConnection == null)
                replayConnection = connection.getRepository().getConnection();
            else
                replayConnection.clear();

            // replays updates and submits queries at each timestamp
            for (int iteration = 0; iteration < updateHistory.size(); iteration++) {
                try {
                    UpdateHistory.UpdateSequence cs = updateHistory.get(iteration);

                    log.info("Replaying iteration {}...", iteration);

                    int insertions = 0, deletions = 0;
                    for (UpdateHistory.Update u : cs) {
                        if (u.getOperation().equals(UpdateHistory.UpdateOperation.INSERT)) {
                            replayConnection.add(u.getStatement());
                            insertions++;
                        } else if (u.getOperation().equals(UpdateHistory.UpdateOperation.DELETE)) {
                            replayConnection.remove(u.getStatement());
                            deletions++;
                        }
                    }

                    log.info("Done {} insertions, {} deletions.", insertions, deletions);

                    if (dumpFormat != null) {
                        String dumpFilename = String.format(dumpPattern, iteration);
                        try {
                            Writer dumpWriter = new FileWriter(dumpFilename);
                            replayConnection.export(Rio.createWriter(dumpFormat, dumpWriter));

                            log.info("Dumped dataset to {}.", dumpFilename);
                        } catch (IOException e) {
                            e.printStackTrace(); // TODO clean error handling
                        }
                    }

                    str = new StringBuilder();
                    for (TupleQuery q : queries.values()) {
                        long before = System.currentTimeMillis();

                        Stream<BindingSet> stream = q.evaluate().stream().distinct();
                        str.append(String.format("\t%d", stream.count()));

                        long after = System.currentTimeMillis();

                        log.info("Executed query in {} ms.", after - before); // TODO sum
                    }

                    w.append(String.format("%d%s\n", iteration, str.toString()));
                } catch (Exception e) {
                    // TODO why is there randomly a NullPointerException here?
                    // TODO maybe because of remaining updates still running on the same repository?
                    e.printStackTrace(); // TODO clean error handling
                }
            }

            w.append(timestamp);
            w.append("\n\n");
            w.close();
        } catch (IOException e) {
            e.printStackTrace(); // TODO clean error handling
        }

        try {
            Writer w = new FileWriter(interactionFilename, true);
            interactionHistory.write(w);
            w.append(timestamp);
            w.append("\n\n");
            w.close();

            log.info("Stored interaction counts/times to {}.", interactionFilename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clean() {
        updateHistory.clear();
        interactionHistory.clear();
        replayConnection.clear();
        timeSlotDuration = null;
    }

    private long getTimeslotDuration() {
		if (timeSlotDuration == null) {
			IRI simIRI = connection.getValueFactory().createIRI(baseURI + Configurator.SIMULATION_RESOURCE_TARGET);
			RepositoryResult<Statement> res =
					connection.getStatements(simIRI, Vocabulary.WALLCLOCK_TIMESLOT_DURATION, null, simIRI);
			if (!res.hasNext())
				throw new RuntimeException("Timeslot duration not configured.");
			Statement stmt = res.next();
			if (res.hasNext())
				log.warn("Multiple timeslot duration statements.");
			timeSlotDuration = ((Literal) stmt.getObject()).longValue();
		}
		return timeSlotDuration;
    }
}
