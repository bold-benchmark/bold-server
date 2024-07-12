package org.bold.ts;

import org.bold.sim.SimulationEngine;
import org.eclipse.rdf4j.repository.RepositoryConnection;

public class SPARQLTransitionSystem extends TransitionSystem {

    public SPARQLTransitionSystem(SimulationEngine engine) {
        super(engine);
    }

    @Override
    public void init() {
        // TODO call singleUpdate
    }

    public void update() {
        // TODO call continuousUpdate
    }

    @Override
    public void end() {
        // TODO do nothing?
    }
}
