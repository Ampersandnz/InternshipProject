package com.mlo;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.objectify.ObjectifyService;
import com.mlo.book.Book;
import com.mlo.book.ObjectifyBookManager;

@SuppressWarnings("serial")
public class LibraryDBForGoogleAppsServlet extends HttpServlet {





	private static final String TEST_USERNAME = "Michael Lo";
	private static final String LIBRARY_USERNAME = "_library";

	private static final Logger log = Logger.getLogger(LibraryDBForGoogleAppsServlet.class.getName());


	private static final String ADD_JSP = "/Add.jsp";
	private static final String DELETE_JSP = "/Delete.jsp";
	private static final String EDIT_JSP = "/Edit.jsp";
	private static final String SHOWALL_JSP = "/ShowAll.jsp";
	private static final String BORROW_JSP = "/Borrow.jsp";
	private static final String RETURN_JSP = "/Return.jsp";
	private static ObjectifyBookManager BM = new ObjectifyBookManager();
	private static boolean firstRun = true;

	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Hidden parameter identifying the page from which the browser is returning.
		String hiddenParam = request.getParameter("page");
		String forward = SHOWALL_JSP;

		if (!(null == hiddenParam)) {
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
						}
					}
				} else {
					// Return to main list.
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
				break;

			case "delete":
				// User has clicked back button from delete page. Show updated main list again.
				break;

			case "borrow":
				// User has clicked back button from borrow page. Show updated main list again.
				break;

			case "return":
				// User has clicked back button from return page. Show updated main list again.
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
				break;
			}
		}
		
		//Initialise default values
		if (firstRun) {
			ObjectifyService.register(Book.class);
			BM.initialise();
			populateDB();
			firstRun = false;
		}

		// Populate the list of books with the contents of the database.
		ArrayList<Book> allBooks = BM.getAllBooks();

		log.warning("Current size of allBooks: " + allBooks.size());
		
		// Send book list to next page.
		if (forward.equals(SHOWALL_JSP)) {
			request.setAttribute("allBooks", allBooks);
		}
		
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
		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
		String json = "";
		ObjectMapper mapper = new ObjectMapper();
		
		if (br != null) {
			json = br.readLine();
		}
		
		if (json.startsWith("ADD")) {
			Book book = mapper.readValue(json.substring(4), Book.class);
			BM.addBook(book);
			mapper.writeValue(response.getOutputStream(), BM.getAllBooks());
			
		} else if (json.startsWith("DELETE")) {
			BM.deleteBook(Long.parseLong(json.substring(7)));
			
		} else if (json.startsWith("BORROW")) {			
			Book book = mapper.readValue(json.substring(7), Book.class);
			BM.updateBook(book.getId(), "inPossessionOf", book.getInPossessionOf());
			
		} else if (json.startsWith("RETURN")) {		
			Book book = mapper.readValue(json.substring(4), Book.class);
			BM.updateBook(book.getId(), "inPossessionOf", LIBRARY_USERNAME);
			
		} else if (json.startsWith("GETBORROWED")) {
			List<Book> borrowedByUsername = new ArrayList<Book>();
			for (Book b: BM.getAllBooks()) {
				if (b.getInPossessionOf().equals(json.substring(12))) {
					borrowedByUsername.add(b);
				}
			}
			mapper.writeValue(response.getOutputStream(), borrowedByUsername);
			
		} else if (json.startsWith("GETBOOKFROMISBN")) {
			List<Book> booksMatchingIsbn = new ArrayList<Book>();
			for (Book b: BM.getAllBooks()) {
				if (b.getIsbn().equals(json.substring(16))) {
					booksMatchingIsbn.add(b);
				}
			}
			mapper.writeValue(response.getOutputStream(), booksMatchingIsbn);
		}
		
		response.setContentType("application/json");
	}

	/**
	 * Method to clear the database and then add a few default entries. Purely for ease of use, is called upon webapp startup. 
	 * Will be removed when system is complete.
	 */
	private void populateDB() {
		// Empty the database
		BM.deleteAllBooks();

		// Add few Book records to database
		BM.addBook("9780316007573", "The Ashes of Worlds", "Michael Lo");
		BM.addBook("9780425037454", "The Stars My Destination", "_library");
		BM.addBook("9780756404079", "The Name of the Wind", "_library");
		BM.addBook("9781429943840", "Earth Afire",  "Michael Lo");
		BM.addBook("9780345490711", "Judas Unchained", "_library");
		log.warning("Added five books");
		BM.addBook("9780606005739", "A Wizard of Earthsea", "_library");
		log.warning("Added sixth book");
	}
}
