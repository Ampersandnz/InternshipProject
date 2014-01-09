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
	private static final String TEST_USERNAME = "Michael Lo";
	public static final String LIBRARY_USERNAME = "_library";

	private static final String ADD_BOOK = "/AddBook.jsp";
	private static final String DELETE_BOOK = "/DeleteBook.jsp";
	private static final String EDIT_BOOK = "/EditBook.jsp";
	private static final String SHOWALL = "/ShowAll.jsp";
	private static final String BORROW_BOOK = "/BorrowBook.jsp";
	private static final String RETURN_BOOK = "/ReturnBook.jsp";

	private static BookManager BM = new ObjectifyBookManager();
	private static UserManager UM = new ObjectifyUserManager();
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
				if (deleteBook(parameters, request)) {
					// Display delete page.
					forward = DELETE_BOOK;
				} else {
					// No books were chosen for deletion. Do nothing.
				}

			} else if (parameters.containsKey("editBook")) {
				List<Book> booksToEdit = getBooksToEdit(parameters, request);
				if (booksToEdit.size() != 0) {
					// Display edit page.
					request.setAttribute("booksToEdit", booksToEdit);
					forward = EDIT_BOOK;
				} else {
					// No books were selected for editing. Do nothing.
				}

			} else if (parameters.containsKey("borrowBook")) {
				boolean bookBeingBorrowed = sendBorrowedBooks(request, parameters);

				if (bookBeingBorrowed) {
					// Display borrow page.
					forward = BORROW_BOOK;
				} else {
					// No books were chosen for borrowing. Do nothing.
				}
			} else if (parameters.containsKey("returnBook")) {
				boolean bookBeingReturned = sendReturnedBooks(request, parameters);
				if (bookBeingReturned) {
					// Display return page.
					forward = RETURN_BOOK;
				} else {
					// No books were chosen for returning. Do nothing.
				}
			} else {
				// Return to main list.
			}

			break;

		case "addBook":
			if (parameters.containsKey("save")) {
				addBook(request, parameters);
			}

			// Return to main list.
			break;

		case "editBook":
			if (parameters.containsKey("save")) {
				editBook(request, parameters);
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

			if (checkUsers(chosenName)) {
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

	private boolean sendBorrowedBooks(HttpServletRequest request, Map<String, String[]> parameters) {
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

				if (book.getInPossessionOf().equals(TEST_USERNAME)) {
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
					BM.updateBook(Id, "inPossessionOf", TEST_USERNAME);
				}

				request.setAttribute("notBorrowedISBNs", notBorrowedISBNs);
				request.setAttribute("borrowedISBNs", borrowedISBNs);
				request.setAttribute("borrower", TEST_USERNAME);
			}
		}
		return bookBeingBorrowed;
	}

	private boolean sendReturnedBooks(HttpServletRequest request, Map<String, String[]> parameters) {
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

				if (book.getInPossessionOf().equals(LIBRARY_USERNAME)) {
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
					BM.updateBook(Id, "inPossessionOf", LIBRARY_USERNAME);
				}

				request.setAttribute("notReturnedISBNs", notReturnedISBNs);
				request.setAttribute("returnedISBNs", returnedISBNs);
			}
		}
		return bookBeingReturned;
	}

	private void addBook(HttpServletRequest request, Map<String, String[]> parameters) {
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

	private boolean deleteBook(Map<String, String[]> parameters, HttpServletRequest request) {
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

	private void editBook(HttpServletRequest request, Map<String, String[]> parameters) {
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

	private List<Book> getBooksToEdit(Map<String, String[]> parameters, HttpServletRequest request) {
		List<Book> booksToEdit = new ArrayList<Book>();
		for(String parameter : parameters.keySet()) {
			if(parameter.startsWith("book")) {
				Long Id = Long.parseLong(parameter.substring(4));
				booksToEdit.add(BM.getBook(Id));
			}
		}
		return booksToEdit;
	}

	private boolean checkUsers(String chosenName) {
		for (User u: UM.getAllUsers()) {
			if (u.getName().equals(chosenName)) {
				return true;
			}
		}
		return false;
	}
}