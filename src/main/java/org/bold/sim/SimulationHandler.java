package org.bold.sim;

import org.bold.gsp.GraphStoreHandler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sail.SailRepositoryConnection;
import org.eclipse.rdf4j.sail.NotifyingSailConnection;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

/**
 * High-level handler providing an interface to agents for managing simulations.
 * After configuration is done, the simulation handler waits for a command to start the simulation (PUT /sim). When a
 * simulation runs, processing is delegated to {@link GraphStoreHandler}.
 */
public class SimulationHandler extends AbstractHandler {

    public static final String SIMULATION_RESOURCE_TARGET = "/sim";

    public static final String PUBLIC_RESOURCE_FOLDER = "doc"; // TODO make it relative to distribution, not working directory

    private final Logger log = LoggerFactory.getLogger(SimulationHandler.class);

    private final Server server;

    private final ResourceHandler staticHandler;

    private final GraphStoreHandler gsHandler;

    private final SimulationEngine engine;

    public SimulationHandler(int port) throws Exception {
        server = new Server(port);
        server.setHandler(this);
        server.start();

        // TODO isn't there a handler collection to do the job instead?
        staticHandler = new ResourceHandler();
        staticHandler.setBaseResource(Resource.newResource(PUBLIC_RESOURCE_FOLDER));
        staticHandler.setDirectoriesListed(false);
        staticHandler.setWelcomeFiles(new String[] { "index.html" });
        staticHandler.doStart();

        MemoryStore store = new MemoryStore();
        SailRepository repo = new SailRepository(store);

        UpdateHistory history = new UpdateHistory(); // TODO finer-grained reporting: distinct histories
        SailRepositoryConnection engineConnection = repo.getConnection();
        ((NotifyingSailConnection) engineConnection.getSailConnection()).addConnectionListener(history);
        SailRepositoryConnection handlerConnection = repo.getConnection();
        ((NotifyingSailConnection) handlerConnection.getSailConnection()).addConnectionListener(history);

        InteractionHistory interactions = new InteractionHistory();

        log.info("Jetty Server reported base URI: {}", server.getURI().toString());

        URI baseURI = System.getenv("BOLD_SERVER_BASE_URI") == null ? server.getURI() : new URI(System.getenv("BOLD_SERVER_BASE_URI"));

        log.info("Base URI after considering environment variable: {}", baseURI);

        engine = new SimulationEngine(baseURI.toString(), engineConnection, history, interactions);

        // TODO have a handler thread pool (see org.eclipse.jetty.util.thread.QueuedThreadPool)
        // TODO manage RepositoryConnections for all individual threads
        // note: server's base URI is set only after server starts
        gsHandler = new GraphStoreHandler(baseURI, handlerConnection);
        gsHandler.addGraphStoreListener(interactions);

        log.info("Server started on port {}. Waiting for command on resource {}...", port, SIMULATION_RESOURCE_TARGET);
    }

    public SimulationEngine getSimulationEngine() {
        return engine;
    }

    public void terminate() throws Exception {
        server.stop();
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        staticHandler.handle(target, baseRequest, request, response);
        if (baseRequest.isHandled()) return; // static resource was found

        switch (engine.getCurrentState()) {
            case EMPTY_STORE:
                // only recognizes PUT /sim
                // TODO check validity of RDF payload with shape
                if (request.getMethod().equals("PUT") && target.equals(SIMULATION_RESOURCE_TARGET)) {
                    gsHandler.handle(target, baseRequest, request, response);
                    engine.callTransition();
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    baseRequest.setHandled(true);
                }
                break;

            case RUNNING:
                gsHandler.handle(target, baseRequest, request, response);
                break;

            default:
                response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                baseRequest.setHandled(true);
                break;
        }
    }

}
