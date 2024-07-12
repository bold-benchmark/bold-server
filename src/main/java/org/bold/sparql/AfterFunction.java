package org.bold.sparql;

import org.bold.sim.Vocabulary;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.query.algebra.evaluation.ValueExprEvaluationException;
import org.eclipse.rdf4j.query.algebra.evaluation.function.Function;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * This custom function is mostly a convenience function as SPARQL has no support for time durations
 * (expressed here in milliseconds).
 */
public class AfterFunction implements Function {

    private DatatypeFactory factory;

    public AfterFunction() {
        try {
            factory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace(); // TODO proper handling
        }
    }

    @Override
    public String getURI() {
        return Vocabulary.NS + "after";
    }

    @Override
    public Value evaluate(ValueFactory valueFactory, Value... args) throws ValueExprEvaluationException {
        if (factory == null) throw new ValueExprEvaluationException(); // TODO not exactly the best exception

        XMLGregorianCalendar date = ((Literal) args[0]).calendarValue();
        Long millis = ((Literal) args[1]).longValue();

        date.add(factory.newDuration(millis));

        return Vocabulary.VALUE_FACTORY.createLiteral(date);
    }
}
