package com.sismics.docs.rest.util;

import java.util.ArrayList;
import java.util.List;

public class UserRequestUtil {

	public enum UserRequestStatus {
		ACCEPTED, REJECTED, PENDING
	}
	
	public static class UserRequest {
		private String name;
		private String email;
		private String message;
		private UserRequestStatus status;
		private String password; // Add password field

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
		
		public UserRequestStatus getStatus() {
			return status;
		}

		public void setStatus(UserRequestStatus status) {
			this.status = status;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}

	private static List<UserRequest> userRequestList = new ArrayList<UserRequest>();

	public static void submitUserRequest(UserRequest userRequest) {
		userRequest.setStatus(UserRequestStatus.PENDING); // Set initial status
		userRequestList.add(userRequest);
	}

	public static List<UserRequest> getAllUserRequestList() {
		return userRequestList;
	}

	public static List<UserRequest> getPendingUserRequestList() {
		List<UserRequest> pendingList = new ArrayList<UserRequest>();
		for (UserRequest userRequest : userRequestList) {
			if (userRequest.getStatus() == UserRequestStatus.PENDING) {
				pendingList.add(userRequest);
			}
		}
		return pendingList;
	}

	public static void acceptUserRequest(UserRequest userRequest) {
		userRequest.setStatus(UserRequestStatus.ACCEPTED);
	}

	public static void rejectUserRequest(UserRequest userRequest) {
		userRequest.setStatus(UserRequestStatus.REJECTED);
	}
}
