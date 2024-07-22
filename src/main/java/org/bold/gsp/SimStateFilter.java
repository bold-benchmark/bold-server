package org.bold.gsp;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.bold.Configurator;
import org.bold.sim.SimulationEngine;
import org.bold.sim.SimulationEngine.EngineState;

/**
 * Filter that aborts request with {@link Status.SERVICE_UNAVAILABLE} if and only of
 * the {@link SimulationEngine} is in {@link DIRTY_STATE} or {@link REPLAYING}
 * state.
 * 
 * @author Tobias Käfer
 *
 */
@PreMatching
public class SimStateFilter implements ContainerRequestFilter {

	@Context
	ServletContext _ctx;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		SimulationEngine simulation = (SimulationEngine) _ctx.getAttribute(Configurator.SIMULATION_ENGINE_SERVLET_ATTRIBUTE);

		if (simulation == null)
			return;

		EngineState state = simulation.getCurrentState();

		switch (state) {
		case DIRTY_STORE:
		case REPLAYING:
			requestContext.abortWith(Response.status(Status.SERVICE_UNAVAILABLE).build());
			break;
		case CONFIGURED:
			break;
		case CREATED:
			break;
		case EMPTY_STORE:
			break;
		case READY:
			break;
		case RUNNING:
			break;
		default:
			break;
		}
	}

}
