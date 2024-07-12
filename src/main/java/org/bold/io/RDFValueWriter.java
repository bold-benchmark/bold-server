package org.bold.io;

import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;

/**
 * RDF writer to include in RDF4J's RIO module that implements the original semantics of the rdf:value property.
 *
 * If a resource {@code res} includes a triple of the form {@code res rdf:value val} in its graph representation, where
 * {@code val} is a typed literal, the RDFValueWriter considers {@code val} to be an alternative representation of the
 * resource and writes it to output.
 *
 * See https://lists.w3.org/Archives/Public/semantic-web/2010Jul/0252.html
 */
public class RDFValueWriter implements RDFWriter {

    private final Logger log = LoggerFactory.getLogger(RDFValueWriter.class);

    private final Writer baseWriter;

    private final RDFFormat format;

    private final IRI datatypeIRI;

    public RDFValueWriter(Writer baseWriter, RDFFormat format) {
        this.baseWriter = baseWriter;
        this.format = format;
        this.datatypeIRI = format.getStandardURI();
    }

    @Override
    public RDFFormat getRDFFormat() {
        return format;
    }

    @Override
    public RDFWriter setWriterConfig(WriterConfig config) {
        log.warn("Configuration object passed to RDFValueWriter will be ignored...");
        return this;
    }

    @Override
    public WriterConfig getWriterConfig() {
        return null;
    }

    @Override
    public Collection<RioSetting<?>> getSupportedSettings() {
        return new ArrayList<>();
    }

    @Override
    public <T> RDFWriter set(RioSetting<T> setting, T value) {
        log.warn("Setting passed to RDFValueWriter will be ignored...");
        return this;
    }

    @Override
    public void startRDF() throws RDFHandlerException {
        // ignore
    }

    @Override
    public void endRDF() throws RDFHandlerException {
        try {
            baseWriter.close();
        } catch (IOException e) {
            log.error("Couldn't close stream while writing literal representation", e);
        }
    }

    @Override
    public void handleNamespace(String prefix, String uri) throws RDFHandlerException {
        // ignore
    }

    @Override
    public void handleStatement(Statement st) throws RDFHandlerException {
        Resource s = st.getSubject();
        IRI p = st.getPredicate();
        Resource g = st.getContext();

        if (p.equals(RDF.VALUE) && s.equals(g)) {
            Value o = st.getObject();

            if (o instanceof Literal && ((Literal) o).getDatatype().equals(datatypeIRI)) {
                String representation = o.stringValue();

                try {
                    // TODO if several representations are available, choose one?
                    baseWriter.write(representation);
                } catch (IOException e) {
                    log.error("Couldn't write literal representation to stream", e);
                }
            }
        }
    }

    @Override
    public void handleComment(String comment) throws RDFHandlerException {
        // ignore
    }

}
