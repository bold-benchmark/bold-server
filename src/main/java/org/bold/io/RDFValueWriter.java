package org.bold.io;

import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
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

    public RDFValueWriter(Writer baseWriter, RDFFormat format) {
        this.baseWriter = baseWriter;
        this.format = format;
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

            if (o instanceof Literal) {
                try {
                    // TODO if several representations are available, choose one?
                    handleValue((Literal) o);
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

    private void handleValue(Literal l) throws IOException {
        if (l.getDatatype().equals(format.getStandardURI()) || format.equals(RDFValueFormats.TXT)) {
            baseWriter.write(l.stringValue());
        } else if (format.equals(RDFValueFormats.JSON)) {
            if (l.getDatatype().equals(XMLSchema.BOOLEAN)) baseWriter.write(l.stringValue());
            else if (isIntegerDatatype(l.getDatatype())) baseWriter.write(String.valueOf(l.longValue()));
            else if (isDecimalDatatype(l.getDatatype())) baseWriter.write(String.valueOf(l.doubleValue()));
            else baseWriter.write("\"" + l.stringValue().replace("\"", "\\") + "\"");
        }
    }

    private boolean isIntegerDatatype(IRI dt) {
        return dt.equals(XMLSchema.INTEGER) ||
                dt.equals(XMLSchema.LONG) ||
                dt.equals(XMLSchema.INT) ||
                dt.equals(XMLSchema.SHORT) ||
                dt.equals(XMLSchema.BYTE) ||
                dt.equals(XMLSchema.NON_NEGATIVE_INTEGER) ||
                dt.equals(XMLSchema.POSITIVE_INTEGER) ||
                dt.equals(XMLSchema.UNSIGNED_LONG) ||
                dt.equals(XMLSchema.UNSIGNED_INT) ||
                dt.equals(XMLSchema.UNSIGNED_SHORT) ||
                dt.equals(XMLSchema.UNSIGNED_BYTE) ||
                dt.equals(XMLSchema.NON_POSITIVE_INTEGER) ||
                dt.equals(XMLSchema.NEGATIVE_INTEGER);
    }

    private boolean isDecimalDatatype(IRI dt) {
        return dt.equals(XMLSchema.DECIMAL) ||
                dt.equals(XMLSchema.FLOAT) ||
                dt.equals(XMLSchema.DOUBLE);
    }

}
