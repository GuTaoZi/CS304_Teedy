package com.sismics.docs.rest.util;

import com.sismics.docs.core.dao.UserRequestDao;
import com.sismics.docs.core.model.jpa.UserRequest;
import java.util.List;

public class UserRequestUtil {
    public enum UserRequestStatus {
        ACCEPTED, REJECTED, PENDING
    }

    private static final UserRequestDao userRequestDao = new UserRequestDao();

    public static void submitUserRequest(UserRequest userRequest) {
        userRequest.setStatus(UserRequestStatus.PENDING.name());
        userRequestDao.create(userRequest);
    }

    public static List<UserRequest> getPendingUserRequestList() {
        return userRequestDao.getPendingRequests();
    }

    public static void acceptUserRequest(UserRequest userRequest) {
        userRequestDao.updateStatus(userRequest, UserRequestStatus.ACCEPTED.name());
    }

    public static void rejectUserRequest(UserRequest userRequest) {
        userRequestDao.updateStatus(userRequest, UserRequestStatus.REJECTED.name());
    }
}
