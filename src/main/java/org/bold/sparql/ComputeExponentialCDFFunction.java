package org.bold.sparql;

import org.bold.sim.Vocabulary;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.query.algebra.evaluation.ValueExprEvaluationException;
import org.eclipse.rdf4j.query.algebra.evaluation.function.Function;

/**
 * SPARQL function to provide the Cumulative Distribution Function (CDF) value
 * of an exponential distribution with parameter lambda.
 *
 * CDF(x) = 1 - e^{-lambda * x}
 */
public class ComputeExponentialCDFFunction implements Function {

    @Override
    public String getURI() {
        return Vocabulary.NS + "cdf-exp";
    }

    @Override
    public Value evaluate(ValueFactory vf, Value... args) throws ValueExprEvaluationException {
        try {
            Literal lambda = (Literal) args[0];
            Literal value = (Literal) args[1];
            double cdf = 1 - Math.exp(- lambda.doubleValue() * value.doubleValue());
            return vf.createLiteral(cdf);
        } catch (Exception e) {
            throw new ValueExprEvaluationException(e); // TODO proper reporting of the error
        }
    }
}
