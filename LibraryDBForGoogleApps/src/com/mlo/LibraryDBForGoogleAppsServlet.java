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
import com.googlecode.objectify.ObjectifyService;
import com.mlo.book.Book;
import com.mlo.book.LibraryManager;
import com.mlo.book.ObjectifyBookManager;

@SuppressWarnings("serial")
public class LibraryDBForGoogleAppsServlet extends HttpServlet {





	private static final String TEST_USERNAME = "Michael Lo";
	private static final String LIBRARY_USERNAME = "_library";




	private static final String ADD_JSP = "/Add.jsp";
	private static final String DELETE_JSP = "/Delete.jsp";
	private static final String EDIT_JSP = "/Edit.jsp";
	private static final String SHOWALL_JSP = "/ShowAll.jsp";
	private static final String BORROW_JSP = "/Borrow.jsp";
	private static final String RETURN_JSP = "/Return.jsp";
	//private static LibraryManager BM = new BookManagerList(); // = new BookManager();
	private static LibraryManager BM = new ObjectifyBookManager();
	private static boolean firstRun = true;

	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Hidden parameter identifying the page from which the browser is returning.
		String hiddenParam = request.getParameter("page");
		String forward="";

		// Populate the list of books with the contents of the database.
		List<Book> allBooks = new ArrayList<Book>(); 
		try {
			if (firstRun) {
				ObjectifyService.register(Book.class);
				BM.initialise();
				populateDB();
				firstRun = false;
			}
			allBooks = BM.getAllBooks();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (null == hiddenParam) {
			// Return to main list.
			forward = SHOWALL_JSP;
		} else {

			// Get a map of the request parameters
			Map<String, String[]> parameters = request.getParameterMap();

			switch(hiddenParam) {

			case "mainList":

				if (parameters.containsKey("add")) {
					// Display add page.
					forward = ADD_JSP;

				} else if (parameters.containsKey("delete")) {
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
						}

						if (bookBeingDeleted) {
							// Display delete page.
							forward = DELETE_JSP;
						} else {
							// No books were chosen for deletion. Do nothing.
							forward = SHOWALL_JSP;
						}
					}

				} else if (parameters.containsKey("edit")) {
					List<Book> booksToEdit = new ArrayList<Book>();
					boolean bookBeingEdited = false;
					for(String parameter : parameters.keySet()) {
						if(parameter.startsWith("book")) {
							bookBeingEdited = true;
							Long Id = Long.parseLong(parameter.substring(4));
							booksToEdit.add(BM.getBook(Id));
						}
						if (bookBeingEdited) {
							// Display edit page.
							request.setAttribute("booksToEdit", booksToEdit);
							forward = EDIT_JSP;
						} else {
							// No books were selected for editing. Do nothing.
							forward = SHOWALL_JSP;
						}
					}
				} else if (parameters.containsKey("borrow")) {
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

						if (bookBeingBorrowed) {
							// Display borrow page.
							forward = BORROW_JSP;
						} else {
							// No books were chosen for borrowing. Do nothing.
							forward = SHOWALL_JSP;
						}
					}
				} else if (parameters.containsKey("return")) {
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

						if (bookBeingReturned) {
							// Display return page.
							forward = RETURN_JSP;
						} else {
							// No books were chosen for returning. Do nothing.
							forward = SHOWALL_JSP;
						}
					}
				} else {
					// Return to main list.
					forward = SHOWALL_JSP;
				}

				break;

			case "add":
				if (parameters.containsKey("save")) {

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

				// Return to main list.
				forward = SHOWALL_JSP;
				break;

			case "delete":
				// User has clicked back button from delete page. Show updated main list again.
				forward = SHOWALL_JSP;
				break;

			case "borrow":
				// User has clicked back button from borrow page. Show updated main list again.
				forward = SHOWALL_JSP;
				break;

			case "return":
				// User has clicked back button from return page. Show updated main list again.
				forward = SHOWALL_JSP;
				break;

			case "edit":
				if (parameters.containsKey("save")) {
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
				forward = SHOWALL_JSP;
				break;
			}
		}

		// Send book list to next page.
		request.setAttribute("allBooks",allBooks);

		// Change to required page.
		RequestDispatcher view = request.getRequestDispatcher(forward);
		view.forward(request, response);
	}

	/***************************************************
	 * doPost(): receives JSON data, parse it, map it and send back as JSON
	 * Code based on that sourced from http://hmkcode.com/java-servlet-send-receive-json-using-jquery-ajax/
	 * 							   and http://hmkcode.com/android-send-json-data-to-server/
	 ****************************************************/
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// 1. get received JSON data from request
		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
		String json = "";
		if (br != null) {
			json = br.readLine();
		}

		// 2. initiate jackson mapper
		ObjectMapper mapper = new ObjectMapper();

		// 3. Convert received JSON to Book
		Book book = mapper.readValue(json, Book.class);

		// 4. Set response type to JSON
		response.setContentType("application/json");            

		//	TODO: Add functionality for borrow and return buttons on app to both just add a book.
		//	TODO: Then complete functionality for borrowing and returning with current app saved username.

		// 5. Add book to database
		BM.addBook(book);

		// 6. Send database as JSON to client
		//	TODO: SEND BACK LIST OF BOOKS CURRENTLY BORROWED BY THIS USER
		//mapper.writeValue(response.getOutputStream(), BM.getAllBooks());
	}

	/**
	 * Method to clear the database and then add a few default entries. Purely for ease of use, is called upon app startup. Will be removed when app is complete.
	 */
	private void populateDB() {
		// Empty the database
		BM.deleteAllBooks();

		// Add few Book records to database
		BM.addBook("9780316007573", "The Ashes Of Worlds", "Michael Lo"); 
		BM.addBook("9780425037454", "The Stars My Destination", "_library"); 
		BM.addBook("9780756404079", "The Name Of The Wind", "_library"); 
		BM.addBook("9781429943840", "Earth Afire",  "Michael Lo");
		BM.addBook("9780345490711", "Judas Unchained", "_library");
		BM.addBook("9780606005739", "A Wizard Of Earthsea", "_library");
	}
}
