package org.bold.sim;

import org.bold.sparql.*;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.algebra.evaluation.function.FunctionRegistry;

public class Vocabulary {

    public static final ValueFactory VALUE_FACTORY = SimpleValueFactory.getInstance();

    public static final String NS = "http://ti.rw.fau.de/sim#";

    public static final IRI SIM = VALUE_FACTORY.createIRI(NS, "sim");

    public static final IRI TIMESLOT_DURATION = VALUE_FACTORY.createIRI(NS, "timeslotDuration");

    public static final IRI WALLCLOCK_TIMESLOT_DURATION = VALUE_FACTORY.createIRI(NS, "wallclockTimeslotDuration");

    public static final IRI ITERATIONS = VALUE_FACTORY.createIRI(NS, "iterations");

    public static final IRI INITIAL_TIME = VALUE_FACTORY.createIRI(NS, "initialTime");

    public static final IRI CURRENT_TIME = VALUE_FACTORY.createIRI(NS, "currentTime");

    public static final IRI RANDOM_SEED = VALUE_FACTORY.createIRI(NS, "randomSeed");

    public static void registerFunctions() {
        FunctionRegistry.getInstance().add(new ComputeExponentialCDFFunction());
        FunctionRegistry.getInstance().add(new ComputeNormalCDFFunction());
        FunctionRegistry.getInstance().add(new RandomFunction());
        FunctionRegistry.getInstance().add(new AfterFunction());
        FunctionRegistry.getInstance().add(new InUnixTimeStampFunction());
    }

}
