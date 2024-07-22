package org.bold.sim;

import java.io.IOException;
import java.io.OutputStream;
import java.util.GregorianCalendar;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.RedirectionException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.bold.Configurator;
import org.bold.sim.SimulationEngine;
import org.bold.sim.Vocabulary;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;

@Path("/")
public class SimulationResource {

	@Context
	ServletContext _ctx;
	
//	@GET
//	@Produces("text/turtle")
//	public StreamingOutput getSimulationConfigTurtle(@Context UriInfo uriinfo) {
//		return getSimulationConfigRDF(uriinfo, RDFFormat.TURTLE);
//	}
//
//	@GET
//	@Produces("application/rdf+xml")
//	public StreamingOutput getSimulationConfigRdfXml(@Context UriInfo uriinfo) {
//		return getSimulationConfigRDF(uriinfo, RDFFormat.RDFXML);
//	}
//
//	@GET
//	@Produces("application/n-triples")
//	public StreamingOutput getSimulationConfigNtriples(@Context UriInfo uriinfo) {
//		return getSimulationConfigRDF(uriinfo, RDFFormat.NTRIPLES);
//	}
//
//	public StreamingOutput getSimulationConfigRDF(UriInfo uriinfo, RDFFormat rdfformat) {

//		if (uriinfo.getAbsolutePath().toString().endsWith("/"))
//			throw new RedirectionException(Response.Status.FOUND, uriinfo.getAbsolutePathBuilder().path("../sim").build());
//
//		SimulationEngine simulation = (SimulationEngine) _ctx.getAttribute(Configurator.SIMULATION_ENGINE_SERVLET_ATTRIBUTE);
//
//		ValueFactory factory = SimpleValueFactory.getInstance();
//		IRI simIRI = factory.createIRI(uriinfo.getAbsolutePathBuilder().fragment("sim").build().toString());
//		final IRI initialTime = Vocabulary.INITIAL_TIME;
//		final IRI timeslotDuration = Vocabulary.TIMESLOT_DURATION;
//		final IRI iterations = Vocabulary.ITERATIONS;
//
//		Model model = new TreeModel();
//		model.add(simIRI, timeslotDuration, factory.createLiteral(simulation.getTimeSlotDuration()));
//		try {
//			model.add(simIRI, initialTime, factory.createLiteral(DatatypeFactory.newInstance()
//					.newXMLGregorianCalendar(GregorianCalendar.from(simulation.getInitialTime()))));
//		} catch (DatatypeConfigurationException e) {
//			_ctx.log("could not create datatype factory", e);
//			throw new InternalServerErrorException("could not create datatype factory");
//		}
//		model.add(simIRI, iterations, factory.createLiteral(simulation.getIterations()));
//
//		// RIO wants to write to a stream, thus we have to wrap RIO's writer
//		StreamingOutput output = new StreamingOutput() {
//
//			@Override
//			public void write(OutputStream os) throws IOException, WebApplicationException {
//				Rio.write(model, os, rdfformat);
//			}
//		};
//
//		return output;

//	}
	
	@POST
	public Response startSimulation() {
		SimulationEngine simulation = (SimulationEngine) _ctx.getAttribute(Configurator.SIMULATION_ENGINE_SERVLET_ATTRIBUTE);
		if (simulation.getCurrentState().equals(org.bold.sim.SimulationEngine.EngineState.EMPTY_STORE)) {
			simulation.callTransition();
			return Response.status(Status.ACCEPTED).build();
		}
		return Response.status(Status.SERVICE_UNAVAILABLE).build();	
	}
}
