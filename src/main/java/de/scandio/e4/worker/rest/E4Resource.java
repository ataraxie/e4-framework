package de.scandio.e4.worker.rest;

import de.scandio.e4.client.config.ClientConfig;
import de.scandio.e4.worker.services.ApplicationStatusService;
import de.scandio.e4.worker.services.PreparationService;
import de.scandio.e4.worker.services.TestRunnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Component
@Path("/e4")
public class E4Resource {
	private final TestRunnerService testRunnerService;
	private final ApplicationStatusService applicationStatusService;
	private final PreparationService preparationService;

	private static final Logger log = LoggerFactory.getLogger(E4Resource.class);

	public E4Resource(TestRunnerService testRunnerService,
					  ApplicationStatusService applicationStatusService,
					  PreparationService preparationService) {
		this.testRunnerService = testRunnerService;
		this.applicationStatusService = applicationStatusService;
		this.preparationService = preparationService;
	}

	@POST
	@Path("/start")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response start(Map<String, Object> parameters) {
		log.info("[ENDPOINT] /start: " + parameters);
		Response response;
		try {
			String targetUrl = (String) parameters.get("targetUrl");
			String testPackage = (String) parameters.get("testPackage");
			String screenshotDir = (String) parameters.get("screenshotDir");
			testRunnerService.runTestPackage(targetUrl, testPackage, screenshotDir);
			response = Response.ok().build();
		} catch (Exception e) {
			log.error("Error in E4Resource: ", e);
			response = Response.status(400).build();
		}

		return response;
	}


	@POST
	@Path("/prepare")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response prepare(Map<String, Object> parameters) {
		log.info("[ENDPOINT] /prepare: " + parameters);
		Response response;
		try {
			String targetUrl = (String) parameters.get("target");
			String testPackage = (String) parameters.get("testPackage");
			String username = (String) parameters.get("username");
			String password = (String) parameters.get("password");
			String screenshotDir = (String) parameters.get("screenshotDir");
			preparationService.prepare(targetUrl, testPackage, username, password, screenshotDir);
			response = Response.ok().build();
		} catch (Exception e) {
			log.error("Error in E4Resource: ", e);
			response = Response.status(400).build();
		}
		return response;
	}

	@POST
	@Path("/stop")
	public Response stop() {
		log.info("[ENDPOINT] /stop");
		return Response.status(500, "not yet implemented").build();
	}

	@GET
	@Path("/status")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStatus() {
		log.info("[ENDPOINT] /status");
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
