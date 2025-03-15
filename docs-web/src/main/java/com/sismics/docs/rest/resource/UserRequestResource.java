package com.sismics.docs.rest.resource;

import com.sismics.docs.core.dao.UserDao;
import com.sismics.docs.core.dao.UserRequestDao;
import com.sismics.docs.core.model.jpa.User;
import com.sismics.docs.core.model.jpa.UserRequest;
import com.sismics.docs.rest.constant.BaseFunction;
import com.sismics.rest.exception.ClientException;
import com.sismics.rest.exception.ForbiddenClientException;
import com.sismics.docs.rest.util.UserRequestUtil;
import com.sismics.docs.core.util.SecurityUtil;
import com.sismics.rest.exception.ServerException;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Path("/user_request")
public class UserRequestResource extends BaseResource {
    private static final Logger log = LoggerFactory.getLogger(UserRequestResource.class);

    /**
     * Submit a new user request.
     */
    @POST
    @Path("/submit")
    public Response submitUserRequest(@FormParam("name") String name,
                                    @FormParam("email") String email,
                                    @FormParam("message") String message,
                                    @FormParam("password") String password) {
        // Validate input
        if (name == null || name.isEmpty()) {
            throw new ClientException("ValidationError", "Name is required");
        }
        if (email == null || email.isEmpty()) {
            throw new ClientException("ValidationError", "Email is required");
        }
        if (password == null || password.isEmpty()) {
            throw new ClientException("ValidationError", "Password is required");
        }

        // Check if user is already logged in and is not a guest
        if (authenticate() && !principal.isGuest()) {
            throw new ClientException("ValidationError", "Only guest users can submit registration requests");
        }

        // Create and submit request
        UserRequest userRequest = new UserRequest();
        userRequest.setName(name);
        userRequest.setEmail(email);
        userRequest.setMessage(message);
        userRequest.setPassword(SecurityUtil.hashPassword(password));
        UserRequestUtil.submitUserRequest(userRequest);

        return Response.ok().build();
    }

    // Update other methods to use the new UserRequest entity
    // The logic remains the same, just use the new entity class
    /**
     * Get all pending user requests.
     */
    @GET
    @Path("/pending")
    public Response getPendingRequests() {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        checkBaseFunction(BaseFunction.ADMIN);

        JsonArrayBuilder requests = Json.createArrayBuilder();
        for (UserRequest request : UserRequestUtil.getPendingUserRequestList()) {
            requests.add(Json.createObjectBuilder()
                    .add("name", request.getName())
                    .add("email", request.getEmail())
                    .add("message", request.getMessage() != null ? request.getMessage() : "")
                    .add("status", request.getStatus().toString()));
        }

        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("requests", requests);
        return Response.ok().entity(response.build()).build();
    }

    /**
     * Accept a user request.
     */
    @POST
    @Path("/accept/{name}")
    public Response acceptRequest(@PathParam("name") String name) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        checkBaseFunction(BaseFunction.ADMIN);

        // Find the request
        UserRequest targetRequest = null;
        for (UserRequest request : UserRequestUtil.getPendingUserRequestList()) {
            if (request.getName().equals(name)) {
                targetRequest = request;
                break;
            }
        }

        if (targetRequest == null) {
            throw new ClientException("ValidationError", "Request not found");
        }

        // Create the user
        UserDao userDao = new UserDao();
        User user = new User();
        user.setUsername(targetRequest.getName());
        user.setEmail(targetRequest.getEmail());
        user.setPassword(SecurityUtil.hashPassword(targetRequest.getPassword()));
        user.setRoleId("user");
        user.setStorageQuota(100000000L);
        try {
            userDao.create(user, targetRequest.getName());
        } catch (Exception e) {
            log.error("Error creating user", e);
            throw new ServerException("Dao Failure", "Error creating user: " + e.getMessage());
        }

        // Update request status
        UserRequestUtil.acceptUserRequest(targetRequest);

        return Response.ok().build();
    }

    /**
     * Reject a user request.
     */
    @POST
    @Path("/reject/{name}")
    public Response rejectRequest(@PathParam("name") String name) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        checkBaseFunction(BaseFunction.ADMIN);

        // Find the request
        UserRequest targetRequest = null;
        for (UserRequest request : UserRequestUtil.getPendingUserRequestList()) {
            if (request.getName().equals(name)) {
                targetRequest = request;
                break;
            }
        }

        if (targetRequest == null) {
            throw new ClientException("ValidationError", "Request not found");
        }

        // Update request status
        UserRequestUtil.rejectUserRequest(targetRequest);

        return Response.ok().build();
    }

    /**
     * Returns all user registration requests history.
     * 
     * @return Response
     */
    @GET
    @Path("/history")
    public Response history() {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        
        // Only administrators can view history
        if (!hasBaseFunction(BaseFunction.ADMIN)) {
            throw new ForbiddenClientException();
        }

        UserRequestDao userRequestDao = new UserRequestDao();
        List<UserRequest> requests = userRequestDao.getRequestHistory();

        JsonArrayBuilder items = Json.createArrayBuilder();
        for (UserRequest userRequest : requests) {
            items.add(Json.createObjectBuilder()
                    .add("id", userRequest.getId())
                    .add("name", userRequest.getName())
                    .add("email", userRequest.getEmail())
                    .add("status", userRequest.getStatus())
                    .add("message", userRequest.getMessage() != null ? userRequest.getMessage() : "")
                    .add("create_date", userRequest.getCreateDate().getTime()));
        }

        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("requests", items);
        
        return Response.ok().entity(response.build()).build();
    }
}
