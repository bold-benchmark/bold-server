package org.bold.ts;

import no.ntnu.ihb.fmi4j.Fmi4jVariableUtils;
import no.ntnu.ihb.fmi4j.SlaveInstance;
import no.ntnu.ihb.fmi4j.importer.fmi2.CoSimulationFmu;
import no.ntnu.ihb.fmi4j.importer.fmi2.Fmu;
import no.ntnu.ihb.fmi4j.modeldescription.variables.TypedScalarVariable;
import no.ntnu.ihb.fmi4j.modeldescription.variables.VariableType;
import org.bold.io.FileUtils;
import org.bold.sim.SimulationEngine;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.repository.RepositoryResult;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FMUTransitionSystem extends TransitionSystem {

    private final CoSimulationFmu fmu;

    private final SlaveInstance fmuSlave;

    private final Map<String, Resource> resources = new HashMap<>();

    private final TupleQuery timeQuery;

    public FMUTransitionSystem(SimulationEngine engine, String fmuFile) throws IOException {
        super(engine);

        fmu = Fmu.from(new File(fmuFile)).asCoSimulationFmu();
        fmuSlave = fmu.newInstance();

        ValueFactory f = engine.getConnection().getValueFactory();
        String base = engine.getBaseURI();

        for (TypedScalarVariable<?> v : fmuSlave.getModelVariables()) {
            Resource r = f.createIRI(base + v.getName());
            resources.put(v.getName(), r);
        }

        String q = FileUtils.asString(FileUtils.getFileOrResource("sim-time.rq"));
        timeQuery = engine.getConnection().prepareTupleQuery(QueryLanguage.SPARQL, q, base);
    }

    @Override
    public void init() {
        fmuSlave.reset();
        fmuSlave.simpleSetup();

        updateDataset();
    }

    @Override
    public void update() {
        Optional<BindingSet> muOpt = timeQuery.evaluate().stream().findAny();

        if (!muOpt.isPresent()) {
            // TODO warn that something's probably wrong
            return;
        }

        BindingSet mu = muOpt.get();
        // times in seconds
        double currentTime = ((Literal) mu.getValue("elapsedTime")).doubleValue();
        double stepSize = ((Literal) mu.getValue("timeslotDuration")).doubleValue();

        updateFMU();
        fmuSlave.doStep(currentTime, stepSize);
        updateDataset();
    }

    @Override
    public void end() {
        fmuSlave.terminate();
        // TODO close FMU at some point
    }

    public void updateFMU() {
        for (TypedScalarVariable<?> v : fmuSlave.getModelVariables()) {
            // TODO restrict to input vars? Some FMU have no causality
            Resource r = resources.get(v.getName());
            RepositoryResult<Statement> st = engine.getConnection().getStatements(r, RDF.VALUE, null, r);

            if (st.hasNext()) {
                Literal l = (Literal) st.next().getObject();
                Object val = getPlainValue(v, l);

                Fmi4jVariableUtils.write(v, fmuSlave, val);
            }
        }
    }

    public void updateDataset() {
        for (TypedScalarVariable<?> v : fmuSlave.getModelVariables()) {
            // TODO restrict to output vars?
            Resource r = resources.get(v.getName());
            Literal val = getLiteralValue(v);

            if (val == null) {
                // TODO warn that val is ignored
                return;
            }

            engine.getConnection().remove(r, RDF.VALUE, null, r);
            engine.getConnection().add(r, RDF.VALUE, val, r);
        }
    }

    private Object getPlainValue(TypedScalarVariable<?> v, Literal l) {
        if (v.getType().equals(VariableType.REAL)) return l.doubleValue();
        else if (v.getType().equals(VariableType.INTEGER)) return l.intValue();
        else if (v.getType().equals(VariableType.BOOLEAN)) return l.booleanValue();
        else if (v.getType().equals(VariableType.ENUMERATION)) return l.intValue();
        else return l.stringValue();
    }

    private Literal getLiteralValue(TypedScalarVariable<?> v) {
        ValueFactory f = engine.getConnection().getValueFactory();

        if (v.getType().equals(VariableType.REAL))
            return f.createLiteral(Fmi4jVariableUtils.read(v.asRealVariable(), fmuSlave).getValue());
        else if (v.getType().equals(VariableType.INTEGER))
            return f.createLiteral(Fmi4jVariableUtils.read(v.asIntegerVariable(), fmuSlave).getValue());
        else if (v.getType().equals(VariableType.BOOLEAN))
            return f.createLiteral(Fmi4jVariableUtils.read(v.asBooleanVariable(), fmuSlave).getValue());
        else if (v.getType().equals(VariableType.STRING))
            return f.createLiteral(Fmi4jVariableUtils.read(v.asStringVariable(), fmuSlave).getValue());
        else if (v.getType().equals(VariableType.ENUMERATION))
            return f.createLiteral(Fmi4jVariableUtils.read(v.asEnumerationVariable(), fmuSlave).getValue());
        else
            return null;
    }

}
