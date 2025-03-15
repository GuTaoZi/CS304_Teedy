package com.sismics.docs.rest.resource;

import com.sismics.docs.core.dao.UserDao;
import com.sismics.docs.core.model.jpa.User;
import com.sismics.docs.rest.constant.BaseFunction;
import com.sismics.rest.exception.ClientException;
import com.sismics.rest.exception.ForbiddenClientException;
import com.sismics.docs.rest.util.UserRequestUtil;
import com.sismics.docs.rest.util.UserRequestUtil.UserRequest;
import com.sismics.docs.core.util.SecurityUtil;
import com.sismics.rest.exception.ServerException;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        // Create and submit request
        UserRequest userRequest = new UserRequest();
        userRequest.setName(name);
        userRequest.setEmail(email);
        userRequest.setMessage(message);
        userRequest.setPassword(password);
        UserRequestUtil.submitUserRequest(userRequest);

        return Response.ok().build();
    }

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
        user.setStorageQuota(100L * 1024 * 1024); // Set default storage quota to 100MB
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
}
