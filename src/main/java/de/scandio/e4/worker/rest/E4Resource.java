package de.scandio.e4.worker.rest;

import de.scandio.e4.client.config.ClientConfig;
import de.scandio.e4.client.config.WorkerConfig;
import de.scandio.e4.dto.ApplicationStatusResponse;
import de.scandio.e4.dto.PreparationStatus;
import de.scandio.e4.dto.TestsStatus;
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

	@GET
	@Path("/start")
	public Response start() {
		log.debug("[ENDPOINT] /start");
		Response response;

		try {
			new Thread(() -> {
				try {
					testRunnerService.runTestPackage();
				} catch (Exception e) {
					e.printStackTrace();
					applicationStatusService.setTestsStatus(TestsStatus.ERROR);
				}
			}).start();
			response = Response.ok().build();
		} catch (Exception e) {
			log.error("Error in /start endpoint: ", e);
			response = Response.status(500).entity(e).build();
		}

		return response;
	}


	@POST
	@Path("/prepare")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response prepare(PreparePostParams params) {
		WorkerConfig workerConfig = params.getWorkerConfig();
		int workerIndex = params.getWorkerIndex();

		log.debug("[ENDPOINT] /prepare - config:" + workerConfig);
		Response response;
		try {
			new Thread(() -> {
				try {
					preparationService.prepare(workerIndex, workerConfig);
				} catch (Exception e) {
					e.printStackTrace();
					applicationStatusService.setPreparationStatus(PreparationStatus.ERROR);
				}
			}).start();
			response = Response.ok().build();
		} catch (Exception e) {
			log.error("Error in /prepare endpoint: ", e);
			response = Response.status(500).entity(e).build();
		}
		return response;
	}

	@POST
	@Path("/stop")
	public Response stop() {
		log.debug("[ENDPOINT] /stop");
		return Response.status(500, "not yet implemented").build();
	}

	@GET
	@Path("/status")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStatus() {
		final ApplicationStatusResponse applicationStatus = applicationStatusService.getApplicationStatus();
		log.debug("[ENDPOINT] /status\n{}", applicationStatus);
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
