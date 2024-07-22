package org.bold.gsp;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.bold.Configurator;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sail.SailRepositoryConnection;

/**
 * Checks for a HTTP request, whether there is a graph for the request URI in
 * the backing triple store, except for HTTP-PUT requests, which always pass.
 * 
 * @author Tobias Käfer
 *
 */
@PreMatching
public class IsPutOrNamedGraphInDatasetFilter implements ContainerRequestFilter {

	@Context
	ServletContext _ctx;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		// For a PUT request we do not need to check whether a graph exists in the dataset,
		// as a PUT can also be used to create a resource.
		if (HttpMethod.PUT.equals(requestContext.getMethod()))
			return;

		SailRepository repo = (SailRepository) _ctx.getAttribute(Configurator.SAIL_REPOSITORY_SERVLET_ATTRIBUTE);
		SailRepositoryConnection connection = repo.getConnection();

		ValueFactory vf = connection.getValueFactory();

		IRI graphName = vf.createIRI(requestContext.getUriInfo().getAbsolutePath().toString());

		// Checking if a graph with the right name exists in triple store:
		if (!connection.hasStatement(null, null, null, false, graphName)) {
			connection.close();
			requestContext.abortWith(Response.status(404, "Graph not found in RDF dataset: " + graphName).build());
		}
		connection.close();
	}
}
