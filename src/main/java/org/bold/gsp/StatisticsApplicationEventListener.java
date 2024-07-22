package org.bold.gsp;

import javax.servlet.ServletContext;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Context;

import org.bold.Configurator;
import org.bold.sim.InteractionHistory;
import org.bold.sim.SimulationEngine;
import org.bold.sim.SimulationEngine.EngineState;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

/**
 * Application event listener to hook up the statistics-generating code in
 * ({@link InteractionHistory}) with JAX-RS APIs.
 * 
 * @author Tobias Käfer
 *
 */
public class StatisticsApplicationEventListener implements ApplicationEventListener {

	@Context
	ServletContext _ctx;

	@Override
	public void onEvent(ApplicationEvent event) {
		// Not interested.
	}

	@Override
	public RequestEventListener onRequest(RequestEvent requestEvent) {
		SimulationEngine engine = (SimulationEngine) _ctx
				.getAttribute(Configurator.SIMULATION_ENGINE_SERVLET_ATTRIBUTE);
		if (engine.getCurrentState().equals(EngineState.RUNNING)) {
			InteractionHistory ih = (InteractionHistory) _ctx
					.getAttribute(Configurator.INTERACTION_HISTORY_SERVLET_ATTRIBUTE);
			return new RequestReporter(ih);
		} else {
			// We do not record events if the simulation is not running.
			return null;
		}
	}

	/**
	 * Reports request processing times to an {@link InteractionHistory}.
	 * 
	 * @author Tobias Käfer
	 *
	 */
	public static class RequestReporter implements RequestEventListener {

	    private final InteractionHistory interactionHistory;
		private final long startTime;

	    public RequestReporter(InteractionHistory ih) {
	    	interactionHistory = ih;
	        startTime = System.currentTimeMillis();
	    }

	    @SuppressWarnings("incomplete-switch")
		@Override
		public void onEvent(RequestEvent event) {

	    	IRI targetIRI = SimpleValueFactory.getInstance().createIRI(event.getUriInfo().getAbsolutePath().toString());
			long timeElapsed = (System.currentTimeMillis() - startTime);

			if (event.getType().equals(org.glassfish.jersey.server.monitoring.RequestEvent.Type.FINISHED)) {
				switch (event.getContainerRequest().getMethod()) {
				case HttpMethod.GET:
					interactionHistory.graphRetrieved(targetIRI, timeElapsed);
					break;
				case HttpMethod.PUT:
					interactionHistory.graphReplaced(targetIRI, timeElapsed);
					break;
				case HttpMethod.POST:
					interactionHistory.graphExtended(targetIRI, timeElapsed);
					break;
				case HttpMethod.DELETE:
					interactionHistory.graphDeleted(targetIRI, timeElapsed);
					break;
				}
			}
		}
	}
}
