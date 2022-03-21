package dmit2015.cathansen.assignment05.resource;

import common.validator.BeanValidator;
import dmit2015.cathansen.assignment05.repository.CurrentCasesByLocalGeographicAreaRepository;
import dmit2015.cathansen.assignment05.entity.CurrentCasesByLocalGeographicArea;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

@ApplicationScoped
@Path("CurrentCasesByLocalGeographicAreas")                    // All methods of this class are associated this URL path
@Consumes(MediaType.APPLICATION_JSON)    // All methods this class accept only JSON format data
@Produces(MediaType.APPLICATION_JSON)    // All methods returns data that has been converted to JSON format
public class CurrentCasesByLocalGeographicAreaResource {

    @Inject
    private CurrentCasesByLocalGeographicAreaRepository _currentCasesByLocalGeographicAreaRepository;

    @GET    // This method only accepts HTTP GET requests.
    public Response listCurrentCasesByLocalGeographicAreas() {
        return Response.ok(_currentCasesByLocalGeographicAreaRepository.list()).build();
    }

    @GET
    @Path("/contains")
    public Response contains(@QueryParam("longitude") double longitude, @QueryParam("latitude") double latitude) {
        CurrentCasesByLocalGeographicArea querySingleResult = _currentCasesByLocalGeographicAreaRepository
                .contains(longitude, latitude)
                .orElseThrow(NotFoundException::new);

        return Response.ok(querySingleResult).build();
    }

    @Path("{id}")
    @GET    // This method only accepts HTTP GET requests.
    public Response findCurrentCasesByLocalGeographicAreaById(@PathParam("id") Long currentCasesByLocalGeographicAreaId) {
        CurrentCasesByLocalGeographicArea existingCurrentCasesByLocalGeographicArea = _currentCasesByLocalGeographicAreaRepository.findOptional(currentCasesByLocalGeographicAreaId).orElseThrow(NotFoundException::new);

        return Response.ok(existingCurrentCasesByLocalGeographicArea).build();
    }

    @POST    // This method only accepts HTTP POST requests.
    public Response addCurrentCasesByLocalGeographicArea(CurrentCasesByLocalGeographicArea newCurrentCasesByLocalGeographicArea, @Context UriInfo uriInfo) {

        String errorMessage = BeanValidator.validateBean(CurrentCasesByLocalGeographicArea.class, newCurrentCasesByLocalGeographicArea);
        if (errorMessage != null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage)
                    .build();
        }

        try {
            // Persist the new CurrentCasesByLocalGeographicArea into the database
            _currentCasesByLocalGeographicAreaRepository.create(newCurrentCasesByLocalGeographicArea);
        } catch (Exception ex) {
            // Return a HTTP status of "500 Internal Server Error" containing the exception message
            return Response.
                    serverError()
                    .entity(ex.getMessage())
                    .build();
        }

        // userInfo is injected via @Context parameter to this method
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(newCurrentCasesByLocalGeographicArea.getId() + "")
                .build();

        // Set the location path of the new entity with its identifier
        // Returns an HTTP status of "201 Created" if the CurrentCasesByLocalGeographicArea was successfully persisted
        return Response
                .created(location)
                .build();
    }

    @PUT            // This method only accepts HTTP PUT requests.
    @Path("{id}")    // This method accepts a path parameter and gives it a name of id
    public Response updateCurrentCasesByLocalGeographicArea(@PathParam("id") Long id, CurrentCasesByLocalGeographicArea updatedCurrentCasesByLocalGeographicArea) {
        if (!id.equals(updatedCurrentCasesByLocalGeographicArea.getId())) {
            throw new BadRequestException();
        }

        String errorMessage = BeanValidator.validateBean(CurrentCasesByLocalGeographicArea.class, updatedCurrentCasesByLocalGeographicArea);
        if (errorMessage != null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage)
                    .build();
        }

        CurrentCasesByLocalGeographicArea existingCurrentCasesByLocalGeographicArea = _currentCasesByLocalGeographicAreaRepository
                .findOptional(id)
                .orElseThrow(NotFoundException::new);
        // TODO: copy properties from the updated entity to the existing entity such as copy the version property shown below
        // existingCurrentCasesByLocalGeographicArea.setVersion(updatedCurrentCasesByLocalGeographicArea.getVersion());

        try {
            _currentCasesByLocalGeographicAreaRepository.update(existingCurrentCasesByLocalGeographicArea);
        } catch (OptimisticLockException ex) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("The data you are trying to update has changed since your last read request.")
                    .build();
        } catch (Exception ex) {
            // Return an HTTP status of "500 Internal Server Error" containing the exception message
            return Response.
                    serverError()
                    .entity(ex.getMessage())
                    .build();
        }

        // Returns an HTTP status "200 OK" and include in the body of the response the object that was updated
        return Response.ok(existingCurrentCasesByLocalGeographicArea).build();
    }

    @DELETE            // This method only accepts HTTP DELETE requests.
    @Path("{id}")    // This method accepts a path parameter and gives it a name of id
    public Response delete(@PathParam("id") Long id) {

        CurrentCasesByLocalGeographicArea existingCurrentCasesByLocalGeographicArea = _currentCasesByLocalGeographicAreaRepository
                .findOptional(id)
                .orElseThrow(NotFoundException::new);

        try {
            _currentCasesByLocalGeographicAreaRepository.remove(existingCurrentCasesByLocalGeographicArea);    // Removes the CurrentCasesByLocalGeographicArea from being persisted
        } catch (Exception ex) {
            // Return a HTTP status of "500 Internal Server Error" containing the exception message
            return Response
                    .serverError()
                    .encoding(ex.getMessage())
                    .build();
        }

        // Returns an HTTP status "204 No Content" if the CurrentCasesByLocalGeographicArea was successfully deleted
        return Response.noContent().build();
    }

}