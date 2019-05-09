package de.scandio.e4.rest;

import de.scandio.e4.services.TestRunnerService;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/e4")
public class E4Resource {
    private final TestRunnerService testRunnerService;

    public E4Resource(TestRunnerService testRunnerService) {
        this.testRunnerService = testRunnerService;
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
    public Response stop(Map<String, Object> parameters) {
        return Response.status(500, "not yet implemented").build();
    }

    @POST
    @Path("/stop")
    public Response prepare() {
        return Response.status(500, "not yet implemented").build();
    }

    @GET
    @Path("/status")
    public Response getStatus() {
        return Response.status(500, "not yet implemented").build();
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
