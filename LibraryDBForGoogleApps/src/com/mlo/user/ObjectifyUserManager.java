package com.mlo.user;

import java.util.ArrayList;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

public class ObjectifyUserManager implements UserManager {

	private static Objectify ofy;
	private static final String NAME = "name";
	private static final String EMAIL = "email";
	private static final String ISADMIN = "isAdmin";
	private static final String PASSWORD = "password";
	public static final String NONAME = "No name available.";
	private static final String NOEMAIL = "No email available.";
	private static final boolean NOISADMIN = false;
	private static final String NOPASSWORD = com.mlo.LibraryDBForGoogleAppsServlet.SYSTEM_PASSWORD;

	public void initialise() {
		ObjectifyService.register(User.class);
		ofy = ObjectifyService.begin();
	}

	public Long addUser(String name, String email) {
		if (name.equals("") || null == name) {
			name = NONAME;
		}

		if (email.equals("") || null == email) {
			email = NOEMAIL;
		}

		User user = new User(name, email);

		ofy.put(user);
		return user.getId();
	}

	public Long addUser(User userWithoutId) {
		ofy.put(userWithoutId);
		return userWithoutId.getId();
	}

	public void updateUser(Long userId, String field, String newData) {
		User user = ofy.get(User.class, userId);

		if (field.equals(NAME)) {
			if (newData.equals("") || null == newData) {
				newData = NONAME;
			}
			user.setName(newData);

		} else if (field.equals(EMAIL)) {
			if (newData.equals("") || null == newData) {
				newData = NOEMAIL;
			}
			user.setEmail(newData);

		} else if (field.equals(ISADMIN)) {
			boolean setAdmin;
			if (newData.equals("") || null == newData) {
				setAdmin = NOISADMIN;
			} else if (newData.equals("true")) {
				setAdmin = true;
			} else {
				setAdmin = NOISADMIN;
			}

			user.setIsAdmin(setAdmin);

		} else if (field.equals(PASSWORD)) {
			if (newData.equals("") || null == newData) {
				newData = NOPASSWORD;
			}
			user.setPassword(newData);
		}

		ofy.put(user);
	}

	public void deleteUser(Long userId) {
		ofy.delete(User.class, userId);
	}

	public ArrayList<User> getAllUsers() {
		ArrayList<User> allUsers = new ArrayList<User>();
		for (User u : ofy.query(User.class)) {
			allUsers.add(u);
		}
		return allUsers;
	}

	public ArrayList<User> getAllAdmins() {
		ArrayList<User> allAdmins = new ArrayList<User>();
		for (User u : ofy.query(User.class)) {
			if (u.getIsAdmin()) {
				allAdmins.add(u);
			}
		}
		return allAdmins;
	}

	public void deleteAllUsers() {
		ofy.delete(ofy.query(User.class).fetchKeys());
	}

	public User getUser(Long userId) {
		return ofy.get(User.class, userId);
	}

}
