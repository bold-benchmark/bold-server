package org.bold.gsp;

import org.bold.conneg.AcceptedContentTypes;
import org.bold.io.RDFValueFormats;
import org.bold.sim.Vocabulary;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Basic implementation of the SPARQL Graph Store protocol, giving
 * RESTful access to named graphs in an RDF dataset.
 *
 * TODO ASK queries (~ RDF shapes) to control resource handler I/O
 */
public class GraphStoreHandler extends AbstractHandler {

    public static final RDFFormat DEFAULT_RDF_FORMAT = RDFFormat.TURTLE;
    public static final Set<RDFFormat> ACTUAL_RDF_FORMATS;
    static {
        ACTUAL_RDF_FORMATS = new HashSet<RDFFormat>();
        ACTUAL_RDF_FORMATS.add(RDFFormat.TURTLE);
        ACTUAL_RDF_FORMATS.add(RDFFormat.JSONLD);
        ACTUAL_RDF_FORMATS.add(RDFFormat.NTRIPLES);
        ACTUAL_RDF_FORMATS.add(RDFFormat.RDFXML);
    }

    private final URI baseURI;

    private final RepositoryConnection connection;

    private final Set<GraphStoreListener> listeners = new HashSet<>();

    public GraphStoreHandler(URI base, RepositoryConnection con) {
        baseURI = base;
        connection = con;
    }

    public void addGraphStoreListener(GraphStoreListener listener) {
        listeners.add(listener);
    }

    public void removeGraphStoreListener(GraphStoreListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        IRI graphName = Vocabulary.VALUE_FACTORY.createIRI(baseURI.resolve(target).toString()); // direct addressing

        boolean created = !exists(graphName);

        // TODO use a ServletFilter instead, for processing Accept/Content-Type

        String acceptString = request.getHeader("Accept");
        List<String> accepted = new AcceptedContentTypes(acceptString).getContentTypes();
        RDFFormat accept = getFormatForMediaTypes(accepted);

        if (accept == null) {
            response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
            baseRequest.setHandled(true);
            return;
        }

        String contentTypeString = request.getHeader("Content-Type");
        RDFFormat contentType = getFormatForMediaType(contentTypeString);

        if (contentType == null) {
            response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            baseRequest.setHandled(true);
            return;
        }

        long before, after;

        try {
            switch (baseRequest.getMethod()) {
                case "GET":
                    if (!created) {
                        response.setHeader("Content-Type", accept.getDefaultMIMEType());

                        RDFWriter writer = Rio.createWriter(accept, response.getOutputStream());

                        before = System.currentTimeMillis();
                        connection.export(writer, graphName);
                        after = System.currentTimeMillis();

                        response.setStatus(HttpServletResponse.SC_OK);

                        for (GraphStoreListener l : listeners) {
                            l.graphRetrieved(graphName, after - before);
                        }
                    } else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    }
                    break;

                case "PUT":
                    // TODO test transaction isolation (and rollback if necessary)
                    before = System.currentTimeMillis();
                    connection.begin();
                    connection.clear(graphName);
                    connection.add(request.getInputStream(), baseRequest.getRequestURI(), contentType, graphName);
                    connection.commit();
                    after = System.currentTimeMillis();

                    response.setStatus(created ? HttpServletResponse.SC_CREATED : HttpServletResponse.SC_NO_CONTENT);

                    for (GraphStoreListener l : listeners) {
                        l.graphReplaced(graphName, after - before);
                    }
                    break;

                case "POST":
                    before = System.currentTimeMillis();
                    connection.add(request.getInputStream(), baseRequest.getRequestURI(), contentType, graphName);
                    after = System.currentTimeMillis();

                    response.setStatus(created ? HttpServletResponse.SC_CREATED : HttpServletResponse.SC_NO_CONTENT);

                    for (GraphStoreListener l : listeners) {
                        l.graphExtended(graphName, after - before);
                    }
                    break;

                case "DELETE":
                    if (!created) {
                        before = System.currentTimeMillis();
                        connection.clear(graphName);
                        after = System.currentTimeMillis();

                        response.setStatus(HttpServletResponse.SC_NO_CONTENT);

                        for (GraphStoreListener l : listeners) {
                            l.graphDeleted(graphName, after - before);
                        }
                    } else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    }
                    break;

                default:
                    response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                    break;
            }
        } catch (RDFParseException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } // other exceptions caught by jetty and 500 Internal Server Error returned

        baseRequest.setHandled(true);
    }

    private boolean exists(IRI graphName) {
        return connection.hasStatement(null, null, null, false, graphName);
    }

    private RDFFormat getFormatForMediaTypes(List<String> mediaTypes) {
        if (mediaTypes.isEmpty()) return DEFAULT_RDF_FORMAT;

        for (String mediaType : mediaTypes) {
            RDFFormat f = getFormatForMediaType(mediaType);
            if (f != null && ACTUAL_RDF_FORMATS.contains(f))
                return f;
        }

        return DEFAULT_RDF_FORMAT;
    }

    private RDFFormat getFormatForMediaType(String mediaType) {
        if (mediaType == null || mediaType.equals("*/*")) return DEFAULT_RDF_FORMAT;

        Optional<RDFFormat> opt = Rio.getParserFormatForMIMEType(mediaType);
        if (opt.isPresent()) return opt.get();

        return RDFValueFormats.getFormatForMediaType(mediaType);
    }

}
