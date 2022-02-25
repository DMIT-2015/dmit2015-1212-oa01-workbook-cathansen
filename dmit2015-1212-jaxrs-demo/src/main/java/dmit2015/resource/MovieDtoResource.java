package dmit2015.resource;

import common.validator.BeanValidator;
import dmit2015.entity.Movie;
import dmit2015.dto.MovieDto;
import dmit2015.mapper.MovieMapper;
import dmit2015.repository.MovieRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.stream.Collectors;

@ApplicationScoped
@Path("MovieDtos")	                // All methods of this class are associated this URL path
@Consumes(MediaType.APPLICATION_JSON)	// All methods this class accept only JSON format data
@Produces(MediaType.APPLICATION_JSON)	// All methods returns data that has been converted to JSON format
public class MovieDtoResource {

    @Inject
    private MovieRepository _movieRepository;

    @GET	// This method only accepts HTTP GET requests.
    public Response listMovies() {
        return Response.ok(
            _movieRepository
                .list()
                .stream()
                .map(MovieMapper.INSTANCE::toDto)
                .collect(Collectors.toList())
            ).build();
    }

    @Path("{id}")
    @GET	// This method only accepts HTTP GET requests.
    public Response findMovieById(@PathParam("id") long movieId) {
       Movie existingMovie = _movieRepository.findOptional(movieId).orElseThrow(NotFoundException::new);

       MovieDto dto = MovieMapper.INSTANCE.toDto(existingMovie);

       return Response.ok(dto).build();
    }

    @POST	// This method only accepts HTTP POST requests.
    public Response addMovie(MovieDto dto, @Context UriInfo uriInfo) {
        Movie newMovie = MovieMapper.INSTANCE.toEntity(dto);

        String errorMessage = BeanValidator.validateBean(Movie.class, newMovie);
        if (errorMessage != null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage)
                    .build();
        }

        try {
            // Persist the new Movie into the database
            _movieRepository.create(newMovie);
        } catch (Exception ex) {
            // Return a HTTP status of "500 Internal Server Error" containing the exception message
            return Response.
                    serverError()
                    .entity(ex.getMessage())
                    .build();
        }

        // userInfo is injected via @Context parameter to this method
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(newMovie.getId() + "")
                .build();

        // Set the location path of the new entity with its identifier
        // Returns an HTTP status of "201 Created" if the Movie was successfully persisted
        return Response
                .created(location)
                .build();
    }

    @PUT 			// This method only accepts HTTP PUT requests.
    @Path("{id}")	// This method accepts a path parameter and gives it a name of id
    public Response updateMovie(@PathParam("id") long id, MovieDto dto) {
        if (!id.equals(dto.getId())) {
            throw new BadRequestException();
        }

        Movie existingMovie = _movieRepository
                .findOptional(id)
                .orElseThrow(NotFoundException::new);

        Movie updatedMovie = MovieMapper.INSTANCE.toEntity(dto);

        String errorMessage = BeanValidator.validateBean(Movie.class, updatedMovie);
        if (errorMessage != null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage)
                    .build();
        }

        // TODO: copy properties from the updated entity to the existing entity such as copy the version property shown below
        existingMovie.setVersion(updatedMovie.getVersion());

        try {
            _movieRepository.update(existingMovie);
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
        return Response.ok(existingMovie).build();
    }

    @DELETE 			// This method only accepts HTTP DELETE requests.
    @Path("{id}")	// This method accepts a path parameter and gives it a name of id
    public Response delete(@PathParam("id") long movieId) {

        Movie existingMovie = _movieRepository
                .findOptional(movieId)
                .orElseThrow(NotFoundException::new);

        try {
            _movieRepository.remove(existingMovie);	// Removes the Movie from being persisted
        } catch (Exception ex) {
            // Return a HTTP status of "500 Internal Server Error" containing the exception message
            return Response
                    .serverError()
                    .encoding(ex.getMessage())
                    .build();
        }

        // Returns an HTTP status "204 No Content" if the Movie was successfully deleted
        return Response.noContent().build();

    }

}