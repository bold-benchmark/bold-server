package org.bold.sparql;

import org.bold.sim.Vocabulary;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.query.algebra.evaluation.ValueExprEvaluationException;
import org.eclipse.rdf4j.query.algebra.evaluation.function.Function;

/**
 * SPARQL function to provide the Cumulative Distribution Function (CDF) value
 * of a normal distribution with parameters mu, sigma.
 *
 * (Currently approximated by a linear function...)
 *
 * CDF(x) = (x - mu) / (4 * sigma) + 1/2
 */
public class ComputeNormalCDFFunction implements Function {

    @Override
    public String getURI() {
        return Vocabulary.NS + "cdf-normal";
    }

    @Override
    public Value evaluate(ValueFactory vf, Value... args) throws ValueExprEvaluationException {
        double mu = ((Literal) args[0]).doubleValue();
        double sigma = ((Literal) args[1]).doubleValue();;
        double value = ((Literal) args[2]).doubleValue();

        if (value < (mu - sigma)) return vf.createLiteral(0);
        if (value > (mu + sigma)) return vf.createLiteral(1);

        double cdf = (value - mu) / (4 * sigma) + 0.5;
        return vf.createLiteral(cdf);
    }
}
