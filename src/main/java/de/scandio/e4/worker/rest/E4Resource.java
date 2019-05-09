package de.scandio.e4.worker.rest;

import de.scandio.e4.worker.services.ApplicationStatusService;
import de.scandio.e4.worker.services.TestRunnerService;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Component
@Path("/e4")
public class E4Resource {
	private final TestRunnerService testRunnerService;
	private final ApplicationStatusService applicationStatusService;

	public E4Resource(TestRunnerService testRunnerService, ApplicationStatusService applicationStatusService) {
		this.testRunnerService = testRunnerService;
		this.applicationStatusService = applicationStatusService;
	}

	@GET
	@Path("/start")
	public Response start(@QueryParam("key")String testPackageKey) {
		Response response;

		try {
			testRunnerService.runTestPackage(testPackageKey);
			response = Response.ok().build();
		} catch (Exception e) {
			response = Response.status(400).build();
		}

		return response;
	}


	@POST
	@Path("/prepare")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response stop(Map<String, Object> parameters) {
		//testRunnerService.stopTests();
		return Response.status(500, "not yet implemented").build();
	}

	@POST
	@Path("/stop")
	public Response prepare() {
		return Response.status(500, "not yet implemented").build();
	}

	@GET
	@Path("/status")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStatus() {
		final Map<String, Object> applicationStatus = applicationStatusService.getApplicationStatus();

		return Response.status(200).entity(applicationStatus).build();
	}


	// This is only for later when we are actually doing distributed tests
	// We don't need to implement the file download today as we are probably just doing local stuff


	/**
	 * Returns all file names of all files that were collected.
	 * @return Response containing all file names.
	 */
	@GET
	@Path("/files")
	public Response getFiles() {
		return Response.status(500, "not yet implemented").build();
	}

	/**
	 * Returns a given file.
	 * @param fileName The name of the file.
	 * @return Response containing file.
	 */
	@GET
	@Path("/file/{fileName}")
	public Response getFile(@PathParam("fileName")String fileName) {
		return Response.status(500, "not yet implemented").build();
	}

}
