package org.bold.io;

import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.RDFWriterFactory;
import org.eclipse.rdf4j.rio.RDFWriterRegistry;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URISyntaxException;

public class RDFValueWriterFactory implements RDFWriterFactory {

    private final RDFFormat format;

    RDFValueWriterFactory(RDFFormat format) {
        this.format = format;
    }

    @Override
    public RDFFormat getRDFFormat() {
        return format;
    }

    @Override
    public RDFWriter getWriter(OutputStream out) {
        Writer w = new OutputStreamWriter(out);
        return getWriter(w);
    }

    @Override
    public RDFWriter getWriter(OutputStream out, String baseURI) throws URISyntaxException {
        return getWriter(out);
    }

    @Override
    public RDFWriter getWriter(Writer writer) {
        return new RDFValueWriter(writer, format);
    }

    @Override
    public RDFWriter getWriter(Writer writer, String baseURI) throws URISyntaxException {
        return getWriter(writer);
    }

}
