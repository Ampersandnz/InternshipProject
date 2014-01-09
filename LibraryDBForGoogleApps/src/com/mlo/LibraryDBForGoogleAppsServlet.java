package com.mlo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mlo.book.*;
import com.mlo.user.*;

public class LibraryDBForGoogleAppsServlet extends HttpServlet {

	private static final long serialVersionUID = 8890271763290445675L;
	
	public static final String TEST_USERNAME = "Michael Lo";
	public static final String LIBRARY_USERNAME = "_library";

	static final String ADD_BOOK = "/AddBook.jsp";
	static final String DELETE_BOOK = "/DeleteBook.jsp";
	static final String EDIT_BOOK = "/EditBook.jsp";
	static final String SHOWALL = "/ShowAll.jsp";
	static final String BORROW_BOOK = "/BorrowBook.jsp";
	static final String RETURN_BOOK = "/ReturnBook.jsp";

	static final String ADD_USER = "/AddUser.jsp";
	static final String DELETE_USER = "/DeleteUser.jsp";
	static final String EDIT_USER = "/EditUser.jsp";
	
	static BookManager BM = new ObjectifyBookManager();
	static UserManager UM = new ObjectifyUserManager();
	
	private static ServletHelper SH = new ServletHelper(BM, UM);
	
	private static boolean firstRun = true;

	/**
	 * Method to clear the database and then add a few default entries. Purely for ease of use, is called upon app startup. 
	 * Will be removed when system is complete.
	 */
	private void populateDB() {
		// Empty the databases
		BM.deleteAllBooks();
		UM.deleteAllUsers();

		// Add few Book records to database
		BM.addBook("9780316007573", "The Ashes Of Worlds", TEST_USERNAME); 
		BM.addBook("9780425037454", "The Stars My Destination", "_library"); 
		BM.addBook("9780756404079", "The Name Of The Wind", "_library"); 
		BM.addBook("9781429943840", "Earth Afire",  TEST_USERNAME);
		BM.addBook("9780345490711", "Judas Unchained", "_library");
		BM.addBook("9780606005739", "A Wizard Of Earthsea", "_library");

		// Add few User records to database
		UM.addUser(TEST_USERNAME, "michael.lo@optimation.co.nz"); 
		UM.addUser("Michael_Personal", "nz.ampersand@gmail.com"); 

	}

	/**
	 * Whenever the user clicks a button, this method is called. 
	 * It performs different actions and redirects the user's browser to different pages depending on the status of the page and the button that was clicked.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Initialise default values
		if (firstRun) {
			BM.initialise();
			UM.initialise();
			populateDB();
			firstRun = false;
		}

		// Hidden parameter identifying the page from which the browser is returning.
		String forward = SHOWALL;

		// Populate the list of books with the contents of the database.
		ArrayList<Book> allBooks = BM.getAllBooks();
		ArrayList<User> allUsers = UM.getAllUsers();

		// Send book list to next page.
		if (forward.equals(SHOWALL)) {
			request.setAttribute("allBooks", allBooks);
			request.setAttribute("allUsers", allUsers);
		}

		// Change to required page.
		RequestDispatcher view = request.getRequestDispatcher(forward);
		view.forward(request, response);
	}

	/**
	 * doPost(): receives JSON data, parse it, map it and send back as JSON
	 * Code based on that sourced from http://hmkcode.com/java-servlet-send-receive-json-using-jquery-ajax/
	 * 							   and http://hmkcode.com/android-send-json-data-to-server/
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Initialise default values
		if (firstRun) {
			BM.initialise();
			UM.initialise();
			populateDB();
			firstRun = false;
		}

		// Hidden parameter identifying the page from which the browser is returning.
		String hiddenParam = request.getParameter("page");

		if (hiddenParam != null) {
			//Initialise default values
			if (firstRun) {
				BM.initialise();
				populateDB();
				firstRun = false;
			}

			// Get a map of the request parameters
			@SuppressWarnings("unchecked")
			Map<String, String[]> parameters = request.getParameterMap();

			try { 
				comingFromPage(request, response, parameters, hiddenParam);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			try {
				comingFromApp(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void comingFromPage(HttpServletRequest request, HttpServletResponse response, Map<String, String[]> parameters, String hiddenParam) {

		// Returning from a .jsp page.
		String forward = SHOWALL;

		switch(hiddenParam) {

		case "mainList":

			if (parameters.containsKey("addBook")) {
				// Display add page.
				forward = ADD_BOOK;

			} else if (parameters.containsKey("deleteBook")) {					
				if (SH.deleteBook(parameters, request)) {
					// Display delete page.
					forward = DELETE_BOOK;
				} else {
					// No books were chosen for deletion. Do nothing.
				}

			} else if (parameters.containsKey("editBook")) {
				List<Book> booksToEdit = SH.getBooksToEdit(parameters, request);
				if (booksToEdit.size() != 0) {
					// Display edit page.
					request.setAttribute("booksToEdit", booksToEdit);
					forward = EDIT_BOOK;
				} else {
					// No books were selected for editing. Do nothing.
				}

			} else if (parameters.containsKey("borrowBook")) {
				boolean bookBeingBorrowed = SH.sendBorrowedBooks(request, parameters);

				if (bookBeingBorrowed) {
					// Display borrow page.
					forward = BORROW_BOOK;
				} else {
					// No books were chosen for borrowing. Do nothing.
				}
			} else if (parameters.containsKey("returnBook")) {
				boolean bookBeingReturned = SH.sendReturnedBooks(request, parameters);
				if (bookBeingReturned) {
					// Display return page.
					forward = RETURN_BOOK;
				} else {
					// No books were chosen for returning. Do nothing.
				}

			} else if (parameters.containsKey("addUser")) {
				// Display add page.
				forward = ADD_USER;

			} else if (parameters.containsKey("deleteUser")) {					
				if (SH.deleteUser(parameters, request)) {
					// Display delete page.
					forward = DELETE_USER;
				} else {
					// No users were chosen for deletion. Do nothing.
				}

			} else if (parameters.containsKey("editUser")) {
				List<User> usersToEdit = SH.getUsersToEdit(parameters, request);
				if (usersToEdit.size() != 0) {
					// Display edit page.
					request.setAttribute("usersToEdit", usersToEdit);
					forward = EDIT_USER;
				} else {
					// No users were selected for editing. Do nothing.
				}

			} else {
				// Return to main list.
			}

			break;

		case "addBook":
			if (parameters.containsKey("save")) {
				SH.addBook(request, parameters);
			}

			// Return to main list.
			break;

		case "editBook":
			if (parameters.containsKey("save")) {
				SH.editBook(request, parameters);
			}

			// Return to main list.
			break;
		
		case "addUser":
			if (parameters.containsKey("save")) {
				SH.addUser(request, parameters);
			}
			
			// Return to main list.
			break;
	
		case "editUser":
			if (parameters.containsKey("save")) {
				SH.editUser(request, parameters);
			}
			
			// Return to main list.
			break;
		}


		// Populate the list of books with the contents of the database.
		ArrayList<Book> allBooks = BM.getAllBooks();
		ArrayList<User> allUsers = UM.getAllUsers();

		// Send book list to next page.
		if (forward.equals(SHOWALL)) {
			request.setAttribute("allBooks", allBooks);
			request.setAttribute("allUsers", allUsers);
		}

		// Change to required page.
		RequestDispatcher view = request.getRequestDispatcher(forward);
		try {
			view.forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void comingFromApp(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// Responding to a POST command from the mobile app.
		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
		String json = "";
		ObjectMapper mapper = new ObjectMapper();

		if (br != null) {
			json = br.readLine();
		}

		if (json.startsWith("ADD")) {
			Book book = mapper.readValue(json.substring(3), Book.class);
			Long id = BM.addBook(book);
			mapper.writeValue(response.getOutputStream(), id);

		} else if (json.startsWith("DELETE")) {
			BM.deleteBook(Long.parseLong(json.substring(6)));

		} else if (json.startsWith("BORROW")) {
			Book book = mapper.readValue(json.substring(6), Book.class);
			BM.updateBook(book.getId(), "inPossessionOf", book.getInPossessionOf());

		} else if (json.startsWith("RETURN")) {
			BM.updateBook(Long.parseLong(json.substring(6)), "inPossessionOf", LIBRARY_USERNAME);

		} else if (json.startsWith("ISALLOWEDNAME")) {
			String chosenName = json.substring(13);

			if (SH.checkUsers(chosenName)) {
				response.getOutputStream().print("TRUE");
			} else {
				response.getOutputStream().print("FALSE");
			}

		} else if (json.startsWith("GETBORROWED")) {
			List<Book> borrowedByUsername = new ArrayList<Book>();
			for (Book b: BM.getAllBooks()) {
				if (b.getInPossessionOf().equals(json.substring(11))) {
					borrowedByUsername.add(b);
				}
			}
			mapper.writeValue(response.getOutputStream(), borrowedByUsername);

		} else if (json.startsWith("GETBOOKFROMISBN")) {
			List<Book> booksMatchingIsbn = new ArrayList<Book>();
			for (Book b: BM.getAllBooks()) {
				if (b.getIsbn().equals(json.substring(15))) {
					booksMatchingIsbn.add(b);
				}
			}
			mapper.writeValue(response.getOutputStream(), booksMatchingIsbn);
		}

		response.setContentType("application/json");
	}
}