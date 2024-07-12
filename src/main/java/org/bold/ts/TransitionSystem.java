package org.bold.ts;

import org.bold.sim.SimulationEngine;
import org.eclipse.rdf4j.repository.RepositoryConnection;

public abstract class TransitionSystem {

    protected final SimulationEngine engine;

    public TransitionSystem(SimulationEngine engine) {
        this.engine = engine;
    }

    public abstract void init();

    public abstract void update();

    public abstract void end();

}
