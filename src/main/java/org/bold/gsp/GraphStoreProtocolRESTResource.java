package org.bold.gsp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.bold.Configurator;
import org.eclipse.rdf4j.IsolationLevels;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sail.SailRepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;

/**
 * Exposing the named graphs in the RDF Dataset as REST resources, in the style
 * of the SPARQL Graph Store Protocol.
 * 
 * TODO: Implement POST, LDP-style
 * 
 * @author Tobias Käfer
 *
 */

// Catching all, which can only be done using a regex. Must assign a name ("id") to use a regex. 
// Use IsPutOrNamedGraphInDatasetFilter to create 404 responses before we get here.
@Path("/{id: .*}")
public class GraphStoreProtocolRESTResource {

	@Context
	ServletContext _ctx;

	// Instead of multiple methods with individual @Produces annotations, we could
	// also use just one method with multiple @Produces annotations. However, the
	// eventually chosen media type is not retrievable programmatically, so we write
	// several methods and thus access the choice.

	@GET
	@Produces("text/turtle")
	public StreamingOutput getTurtle(@Context UriInfo uriinfo) {
		return getRDF(uriinfo, RDFFormat.TURTLE);
	}

	@GET
	@Produces("application/rdf+xml")
	public StreamingOutput getRDFXML(@Context UriInfo uriinfo) {
		return getRDF(uriinfo, RDFFormat.RDFXML);
	}

	@GET
	@Produces("application/n-triples")
	public StreamingOutput getNtriples(@Context UriInfo uriinfo) {
		return getRDF(uriinfo, RDFFormat.NTRIPLES);
	}

	private StreamingOutput getRDF(UriInfo uriinfo, RDFFormat finalOutputFormat) {

		SailRepository repo = (SailRepository) _ctx.getAttribute(Configurator.SAIL_REPOSITORY_SERVLET_ATTRIBUTE);
		SailRepositoryConnection connection = repo.getConnection();

		ValueFactory vf = connection.getValueFactory();

		IRI graphName = vf.createIRI(uriinfo.getAbsolutePath().toString());

		// checking if graph exists in triple store
		if (!connection.hasStatement(null, null, null, false, graphName)) {
			connection.close();
			throw new NotFoundException("Graph not found in RDF dataset: " + graphName);
		}

		// RIO wants to write to a stream, thus we have to wrap RIO's writer
		StreamingOutput output = new StreamingOutput() {

			@Override
			public void write(OutputStream os) throws IOException, WebApplicationException {
				RDFWriter writer = Rio.createWriter(finalOutputFormat, os);
				connection.export(writer, graphName);
				connection.close();
			}
		};

		return output;
	}

	// Instead of writing multiple methods with different @Consumes annotations, we
	// could also just write one method with multiple @Consumes annotations. But then, we
	// would need to ask Rio to parse the Content-type header a second time in order
	// to create the instances of RDFFormat that we need when adding the triples via
	// the {@link SailRepositoryConnection}.

	@PUT
	@Consumes("text/turtle")
	public Response putTurtle(@Context UriInfo uriinfo, @Context HttpServletRequest req, InputStream is) {
		return putRDF(uriinfo, req, is, RDFFormat.TURTLE);
	}

	@PUT
	@Consumes("application/n-triples")
	public Response putNtriples(@Context UriInfo uriinfo, @Context HttpServletRequest req, InputStream is) {
		return putRDF(uriinfo, req, is, RDFFormat.NTRIPLES);
	}

	@PUT
	@Consumes("application/rdf+xml")
	public Response putRDFXML(@Context UriInfo uriinfo, @Context HttpServletRequest req, InputStream is) {
		return putRDF(uriinfo, req, is, RDFFormat.RDFXML);
	}

	private Response putRDF(UriInfo uriinfo, HttpServletRequest req, InputStream is, RDFFormat parsedMimeType) {

		// Connecting to the repository.
		SailRepository repo = (SailRepository) _ctx.getAttribute(Configurator.SAIL_REPOSITORY_SERVLET_ATTRIBUTE);
		SailRepositoryConnection connection = repo.getConnection();

		// Determining target URI from request.
		String requestTargetUriString = uriinfo.getAbsolutePath().toString();

		// Converting target URI to Rio's classes.
		ValueFactory vf = connection.getValueFactory();
		IRI requestTargetUriIRI = vf.createIRI(requestTargetUriString);

		// Begin transaction
		connection.begin(IsolationLevels.READ_COMMITTED);

		boolean resourceExistedBeforeRequest = false;

		// checking if graph exists in triple store
		if (connection.hasStatement(null, null, null, false, requestTargetUriIRI)) {
			resourceExistedBeforeRequest = true;
		}

		// PUT semantics: Content supplied replaces what's at the target URI. Thus removing what's there...
        connection.clear(requestTargetUriIRI);
        try {
        	// ...and then adding the new stuff.
			connection.add(is, requestTargetUriString, parsedMimeType, requestTargetUriIRI);
			connection.commit();
		} catch (RDFParseException e) {
			connection.rollback();
			throw new BadRequestException(e);
		} catch (RepositoryException | IOException e) {
			connection.rollback();
			throw new InternalServerErrorException(e);
		} finally {
			connection.close();
		}

        // Creating response with appropriate response code.
        if (resourceExistedBeforeRequest)
        	return Response.noContent().build();
        else
        	return Response.created(URI.create(requestTargetUriString)).build();
	}	

	@DELETE
	public Response deleteResource(@Context UriInfo uriinfo) {

		SailRepository repo = (SailRepository) _ctx.getAttribute(Configurator.SAIL_REPOSITORY_SERVLET_ATTRIBUTE);
		SailRepositoryConnection connection = repo.getConnection();

		// Determining target URI from request.
		String requestTargetUriString = uriinfo.getAbsolutePath().toString();

		// Converting target URI to Rio's classes.
		ValueFactory vf = connection.getValueFactory();
		IRI requestTargetUriIRI = vf.createIRI(requestTargetUriString);

		// checking if graph exists in triple store
		if (!connection.hasStatement(null, null, null, false, requestTargetUriIRI)) {
			connection.close();
			throw new NotFoundException();
		}

		// Deleting the named graph
		connection.clear(requestTargetUriIRI);

		// Cleanup
		connection.close();

		return Response.noContent().build();
	}
}
