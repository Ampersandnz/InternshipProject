package com.mlo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.mlo.book.Book;
import com.mlo.book.BookManager;
import com.mlo.user.User;
import com.mlo.user.UserManager;

public class ServletHelper {
	private static BookManager BM;
	private static UserManager UM;

	private static final String TEST_USERNAME = "Michael Lo";

	static List<User> beingMadeAdmin = new ArrayList<User>();

	/**
	 * Class of helper methods for LibraryDBForGoogleAppsServlet. 
	 * Moves most functionality out of the servlet class to simplify maintenance and ease of understanding the system.
	 */
	ServletHelper () {
		BM = LibraryDBForGoogleAppsServlet.BM;
		UM = LibraryDBForGoogleAppsServlet.UM;
	}

	/**
	 * Method to initialise and clear the databases, then add default fake entries.
	 * Will be altered when development is complete.
	 */
	void firstRun() {
		// Initialise the database managers
		BM.initialise();
		UM.initialise();

		// Empty the databases
		BM.deleteAllBooks();
		UM.deleteAllUsers();

		// Add few Book records to database
		BM.addBook("9780316007573", "The Ashes Of Worlds", TEST_USERNAME); 
		BM.addBook("9780425037454", "The Stars My Destination", LibraryDBForGoogleAppsServlet.LIBRARY_USERNAME); 
		BM.addBook("9780756404079", "The Name Of The Wind", TEST_USERNAME); 
		BM.addBook("9781429943840", "Earth Afire",  TEST_USERNAME);
		BM.addBook("9780345490711", "Judas Unchained", LibraryDBForGoogleAppsServlet.LIBRARY_USERNAME);
		BM.addBook("9780606005739", "A Wizard Of Earthsea", LibraryDBForGoogleAppsServlet.LIBRARY_USERNAME);

		// Add few User records to database
		Long id1 = UM.addUser(TEST_USERNAME, "michael.lo@optimation.co.nz"); 
		UM.addUser("Michael_Personal", "nz.ampersand@gmail.com"); 
		UM.addUser("test", "test@fake.com"); 
		UM.addUser("test", "test@test.com"); 
		Long id2 = UM.addUser(LibraryDBForGoogleAppsServlet.LIBRARY_USERNAME, " "); 

		UM.updateUser(id1, "isAdmin", "true");
		UM.updateUser(id1, "password", "lolcano");
		UM.updateUser(id2, "isAdmin", "true");
		UM.updateUser(id2, "password", LibraryDBForGoogleAppsServlet.SYSTEM_PASSWORD);
	}

	/**
	 * @param request
	 * @param parameters
	 * @return borrowedBooks
	 */
	boolean sendBorrowedBooks(HttpServletRequest request, Map<String, String[]> parameters) {
		boolean bookBeingBorrowed = false;
		for(String parameter : parameters.keySet()) {
			if(parameter.startsWith("book")) {
				bookBeingBorrowed = true;
				Long Id = Long.parseLong(parameter.substring(4));
				Book book = BM.getBook(Id);

				String currentBorrowedISBNs = (String) request.getAttribute("borrowedISBNs");
				String currentNotBorrowedISBNs = (String) request.getAttribute("notBorrowedISBNs");
				String borrowedISBNs = null;
				String notBorrowedISBNs = null;

				if (book.getInPossessionOf().equals(LibraryDBForGoogleAppsServlet.selectedUser)) {
					if (!(null == currentNotBorrowedISBNs)) {
						notBorrowedISBNs = currentNotBorrowedISBNs + ", " + book.getIsbn();
					} else {
						notBorrowedISBNs =  book.getIsbn();
					}
					borrowedISBNs = currentBorrowedISBNs;
				} else {
					if (!(null == currentBorrowedISBNs)) {
						borrowedISBNs = currentBorrowedISBNs + ", " + book.getIsbn();
					} else {
						borrowedISBNs =  book.getIsbn();
					}
					notBorrowedISBNs = currentNotBorrowedISBNs;
					BM.updateBook(Id, "inPossessionOf", LibraryDBForGoogleAppsServlet.selectedUser.getName());
				}

				request.setAttribute("notBorrowedISBNs", notBorrowedISBNs);
				request.setAttribute("borrowedISBNs", borrowedISBNs);
				request.setAttribute("borrower", LibraryDBForGoogleAppsServlet.selectedUser);
			}
		}
		return bookBeingBorrowed;
	}

	/**
	 * @param request
	 * @param parameters
	 * @return returnedBooks
	 */
	boolean sendReturnedBooks(HttpServletRequest request, Map<String, String[]> parameters) {
		boolean bookBeingReturned = false;
		for(String parameter : parameters.keySet()) {
			if(parameter.startsWith("book")) {
				bookBeingReturned = true;
				Long Id = Long.parseLong(parameter.substring(4));
				Book book = BM.getBook(Id);

				String currentReturnedISBNs = (String) request.getAttribute("returnedISBNs");
				String currentNotReturnedISBNs = (String) request.getAttribute("notReturnedISBNs");
				String returnedISBNs = null;
				String notReturnedISBNs = null;

				if (book.getInPossessionOf().equals(LibraryDBForGoogleAppsServlet.LIBRARY_USERNAME)) {
					if (!(null == currentNotReturnedISBNs)) {
						notReturnedISBNs = currentNotReturnedISBNs + ", " + book.getIsbn();
					} else {
						notReturnedISBNs =  book.getIsbn();
					}
					returnedISBNs = currentReturnedISBNs;
				} else {
					if (!(null == currentReturnedISBNs)) {
						returnedISBNs = currentReturnedISBNs + ", " + book.getIsbn();
					} else {
						returnedISBNs =  book.getIsbn();
					}
					notReturnedISBNs = currentNotReturnedISBNs;
					BM.updateBook(Id, "inPossessionOf", LibraryDBForGoogleAppsServlet.LIBRARY_USERNAME);
				}

				request.setAttribute("notReturnedISBNs", notReturnedISBNs);
				request.setAttribute("returnedISBNs", returnedISBNs);
			}
		}
		return bookBeingReturned;
	}

	/**
	 * @param request
	 * @param parameters
	 */
	void addBook(HttpServletRequest request, Map<String, String[]> parameters) {
		String isbn = "";
		String title = "";
		String inPossessionOf = "";

		try {
			isbn = request.getParameter("isbn");
			title = request.getParameter("title");
			inPossessionOf = request.getParameter("inPossessionOf");
		} catch (Exception e) {
			e.printStackTrace();
		}

		BM.addBook(isbn, title, inPossessionOf);
	}

	/**
	 * @param request
	 * @param parameters
	 * Will return, silently failing, if user with identical name already exists in the system.
	 */
	void addUser(HttpServletRequest request, Map<String, String[]> parameters) {
		String name = "";
		String email = "";

		try {
			name = request.getParameter("name");
			email = request.getParameter("email");
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (User u: UM.getAllUsers()) {
			if (u.getName().equals(name)) {
				return;
			}
		}
		UM.addUser(name, email);
	}

	/**
	 * @param parameters
	 * @param request
	 * @return bookDeleted
	 */
	boolean deleteBook(Map<String, String[]> parameters, HttpServletRequest request) {
		boolean bookBeingDeleted = false;
		for(String parameter : parameters.keySet()) {
			if(parameter.startsWith("book")) {
				bookBeingDeleted = true;
				Long Id = Long.parseLong(parameter.substring(4));
				Book book = BM.getBook(Id);

				String currentISBNs = (String) request.getAttribute("deletedISBNs");
				String updatedISBNs = "";

				if (!(null == currentISBNs)) {
					updatedISBNs = currentISBNs + ", " + book.getIsbn();
				} else {
					updatedISBNs =  book.getIsbn();
				}
				request.setAttribute("deletedISBNs", updatedISBNs);
				BM.deleteBook(Id);
				bookBeingDeleted = true;
			}
		}
		return bookBeingDeleted;
	}

	/**
	 * @param parameters
	 * @param request
	 * @return userDeleted
	 */
	boolean deleteUser(Map<String, String[]> parameters, HttpServletRequest request) {
		boolean userBeingDeleted = false;
		for(String parameter : parameters.keySet()) {
			if(parameter.startsWith("user")) {
				userBeingDeleted = true;
				Long Id = Long.parseLong(parameter.substring(4));
				User user = UM.getUser(Id);
				
				// Library account cannot be deleted to ensure that one admin account always remains.
				if (!(user.getName().equals(LibraryDBForGoogleAppsServlet.LIBRARY_USERNAME))) {
					String currentNames = (String) request.getAttribute("deletedNames");
					String updatedNames = "";

					if (!(null == currentNames)) {
						updatedNames = currentNames + ", " + user.getName();
					} else {
						updatedNames =  user.getName();
					}
					request.setAttribute("deletedNames", updatedNames);
					UM.deleteUser(Id);
					userBeingDeleted = true;
				}
			}
		}
		return userBeingDeleted;
	}

	/**
	 * @param request
	 * @param parameters
	 */
	void editBook(HttpServletRequest request, Map<String, String[]> parameters) {
		// No way to tell if a field has been changed or not, so just update all of them.
		for(String parameter : parameters.keySet()) {
			if(parameter.startsWith("isbn")) {
				Long Id = Long.parseLong(parameter.substring(4));
				BM.updateBook(Id, "isbn", request.getParameter(parameter));
			}

			if(parameter.startsWith("title")) {
				Long Id = Long.parseLong(parameter.substring(5));
				BM.updateBook(Id, "title", request.getParameter(parameter));
			}

			if(parameter.startsWith("inPossessionOf")) {
				Long Id = Long.parseLong(parameter.substring(14));
				BM.updateBook(Id, "inPossessionOf", request.getParameter(parameter));
			}
		}
	}

	/**
	 * @param request
	 * @param parameters
	 * Will return, silently failing, if user with identical name already exists in the system.
	 */
	void editUser(HttpServletRequest request, Map<String, String[]> parameters) {
		// No way to tell if a field has been changed or not, so just update all of them.
		for(String parameter : parameters.keySet()) {
			if(parameter.startsWith("name")) {
				Long Id = Long.parseLong(parameter.substring(4));

				String name = request.getParameter(parameter);

				for (User u: UM.getAllUsers()) {
					if (u.getName().equals(name)) {
						return;
					}
				}

				UM.updateUser(Id, "name", name);
			}

			if(parameter.startsWith("email")) {
				Long Id = Long.parseLong(parameter.substring(5));
				UM.updateUser(Id, "email", request.getParameter(parameter));
			}
		}
	}

	/**
	 * @param request
	 * @param parameters
	 * Will return, silently failing, if incorrect password was entered.
	 */
	void makeUserAdmin(HttpServletRequest request, Map<String, String[]> parameters) {
		// Check whether correct password was entered.
		if (request.getParameter("systemPassword").equals(LibraryDBForGoogleAppsServlet.SYSTEM_PASSWORD)) {
			// Make all selected users admins
			for (User u: beingMadeAdmin) {
				UM.updateUser(u.getId(), "isAdmin", "true");
			}

			// Add the submitted passwords to all new admins
			for(String parameter : parameters.keySet()) {
				if(parameter.startsWith("password")) {
					Long Id = Long.parseLong(parameter.substring(8));
					String password = request.getParameter(parameter);
					if (password == null || password.equals("")) {
						password = LibraryDBForGoogleAppsServlet.SYSTEM_PASSWORD;
					}
					UM.updateUser(Id, "password", password);
				}
			}
		}
		beingMadeAdmin.clear();
	}

	/**
	 * @param parameters
	 * @param request
	 * @return booksToEdit
	 */
	List<Book> getBooksToEdit(Map<String, String[]> parameters, HttpServletRequest request) {
		List<Book> booksToEdit = new ArrayList<Book>();
		for(String parameter : parameters.keySet()) {
			if(parameter.startsWith("book")) {
				Long Id = Long.parseLong(parameter.substring(4));
				booksToEdit.add(BM.getBook(Id));
			}
		}
		return booksToEdit;
	}

	/**
	 * @param parameters
	 * @param request
	 * @return usersToEdit
	 */
	List<User> getUsersToEdit(Map<String, String[]> parameters, HttpServletRequest request) {
		List<User> usersToEdit = new ArrayList<User>();
		for(String parameter : parameters.keySet()) {
			if(parameter.startsWith("user")) {
				Long Id = Long.parseLong(parameter.substring(4));
				usersToEdit.add(UM.getUser(Id));
			}
		}
		return usersToEdit;
	}

	/**
	 * @param parameters
	 * @param request
	 * @return usersToMakeAdmin
	 */
	List<User> getUsersToMakeAdmin(Map<String, String[]> parameters, HttpServletRequest request) {
		List<User> usersToMakeAdmin = new ArrayList<User>();
		for(String parameter : parameters.keySet()) {
			if(parameter.startsWith("user")) {
				Long Id = Long.parseLong(parameter.substring(4));
				usersToMakeAdmin.add(UM.getUser(Id));
			}
		}

		beingMadeAdmin = usersToMakeAdmin;
		return usersToMakeAdmin;
	}

	/**
	 * @param chosenName
	 * @return nameAllowed
	 */
	int checkUser(String chosenName) {
		for (User u: UM.getAllUsers()) {
			if (u.getName().toLowerCase().equals(chosenName)) {
				if (u.getIsAdmin()) {
					return LibraryDBForGoogleAppsServlet.USERNAMEADMIN;
				}
				return LibraryDBForGoogleAppsServlet.USERNAMEALLOWED;
			}
		}
		return LibraryDBForGoogleAppsServlet.USERNAMENOTALLOWED;
	}

	/**
	 * @param request
	 * @param parameters
	 * @return newUserSelected
	 */
	int selectUser(HttpServletRequest request, Map<String, String[]> parameters) {
		int newUserSelected = 0;
		User selected = null;

		for(String parameter : parameters.keySet()) {
			if(parameter.startsWith("user")) {
				if (newUserSelected > 0) {
					// Multiple users selected.
					newUserSelected = 0;
					break;
				} else {
					newUserSelected = 1;

					Long id = Long.parseLong(parameter.substring(4));
					selected = UM.getUser(id);
					if (selected.getIsAdmin()) {
						newUserSelected = 2;
					}
				}
			}
		}

		switch (newUserSelected) {
		case 0:
			break;

		case 1:
			LibraryDBForGoogleAppsServlet.selectedUser = selected;
			request.setAttribute("newUser", selected.getName());
			break;

		case 2:
			request.setAttribute("userId", selected.getId());
			break;
		}

		return newUserSelected;
	}

	/**
	 * @param request
	 * @param parameters
	 * Will return, silently failing, if incorrect password was entered.
	 */
	boolean checkAdminPassword(HttpServletRequest request, Map<String, String[]> parameters) {
		Object temp = request.getParameter("userId");
		Long id = null;
		if (temp instanceof String) {
			id = Long.parseLong((String) temp);
		}

		User user = UM.getUser(id);

		// Check whether correct password was entered.
		if (request.getParameter("userPassword").equals(user.getPassword())) {
			LibraryDBForGoogleAppsServlet.selectedUser = user;
			request.setAttribute("newUser", user.getName());
			return true;
		}
		return false;
	}
}
