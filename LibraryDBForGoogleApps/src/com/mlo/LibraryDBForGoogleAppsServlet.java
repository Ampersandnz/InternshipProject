package com.mlo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mlo.book.Book;
import com.mlo.book.BookManager;
import com.mlo.book.BookManagerList;
import com.mlo.book.LibraryManager;

@WebServlet("/librarydbforgoogleapps")
public class LibraryDBForGoogleAppsServlet extends HttpServlet {





	private static final String TEST_USERNAME = "Michael Lo";
	private static final String LIBRARY_USERNAME = "_library";




	private static final long serialVersionUID = 1L;
	private static final String ADD_JSP = "/Add.jsp";
	private static final String DELETE_JSP = "/Delete.jsp";
	private static final String EDIT_JSP = "/Edit.jsp";
	private static final String SHOWALL_JSP = "/ShowAll.jsp";
	private static final String BORROW_JSP = "/Borrow.jsp";
	private static final String RETURN_JSP = "/Return.jsp";
	private static LibraryManager BM = new BookManagerList(); // = new BookManager();
	private static int needToInitialiseDatabase = 0;

	/*public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/plain");
		response.getWriter().println("Current books in memory:");
		ArrayList<Book> bookList = testList();
		for (Book b: bookList) {
			response.getWriter().println(b.toString());
		}
	}

	private ArrayList<Book> testList() {
		ArrayList<Book> bookList = new ArrayList<Book>();
		bookList.add(new Book("9780316007573", "The Ashes Of Worlds", "Michael Lo"));
		bookList.add(new Book("9780425037454", "The Stars My Destination", "_library")); 
		bookList.add(new Book("9780756404079", "The Name Of The Wind", "_library")); 
		bookList.add(new Book("9781429943840", "Earth Afire",  "Michael Lo"));
		bookList.add(new Book("9780345490711", "Judas Unchained", "_library"));
		bookList.add(new Book("9780606005739", "A Wizard Of Earthsea", "_library"));
		return bookList;
	}*/

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Hidden parameter identifying the page from which the browser is returning.
		String hiddenParam = request.getParameter("page");
		String forward="";

		// Populate the list of books with the contents of the database.
		List<Book> allBooks = new ArrayList<Book>(); 
		try {
			if (needToInitialiseDatabase == 0) {
				BM.initialise();
				populateDB();
				needToInitialiseDatabase = 1;
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
							int ID = Integer.parseInt(parameter.substring(4));
							Book book = BM.getBook(ID);
							for (Book b: BM.getAllBooks()) {
								if (b.getId() == ID) {
									book = b;
								}
							}

							String currentISBNs = (String) request.getAttribute("deletedISBNs");
							String updatedISBNs = "";

							if (!(null == currentISBNs)) {
								updatedISBNs = currentISBNs + ", " + book.getIsbn();
							} else {
								updatedISBNs =  book.getIsbn();
							}
							request.setAttribute("deletedISBNs", updatedISBNs);
							BM.deleteBook(ID);
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
							int ID = Integer.parseInt(parameter.substring(4));
							for (Book b: BM.getAllBooks()) {
								if (b.getId() == ID) {
									booksToEdit.add(b);
								}
							}
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
							int ID = Integer.parseInt(parameter.substring(4));
							Book book = new Book();
							for (Book b: BM.getAllBooks()) {
								if (b.getId() == ID) {
									book = b;
								}
							}

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
								BM.updateBook(ID, "inPossessionOf", TEST_USERNAME);
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
							int ID = Integer.parseInt(parameter.substring(4));
							Book book = new Book();
							for (Book b: BM.getAllBooks()) {
								if (b.getId() == ID) {
									book = b;
								}
							}

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
								BM.updateBook(ID, "inPossessionOf", LIBRARY_USERNAME);
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
							Integer ID = Integer.parseInt(parameter.substring(4));
							BM.updateBook(ID, "isbn", request.getParameter(parameter));
						}

						if(parameter.startsWith("title")) {
							Integer ID = Integer.parseInt(parameter.substring(5));
							BM.updateBook(ID, "title", request.getParameter(parameter));
						}

						if(parameter.startsWith("inPossessionOf")) {
							Integer ID = Integer.parseInt(parameter.substring(14));
							BM.updateBook(ID, "inPossessionOf", request.getParameter(parameter));
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
	 ****************************************************/
	/* protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{

        // 1. get received JSON data from request
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String json = "";
        if(br != null){
            json = br.readLine();
        }

        // 2. initiate jackson mapper
        ObjectMapper mapper = new ObjectMapper();

        // 3. Convert received JSON to Book
        Book book = mapper.readValue(json, Book.class);

        // 4. Set response type to JSON
        response.setContentType("application/json");            

        // 5. Add book to database
        BM.addBook(book);

        // 6. Send database as JSON to client
        mapper.writeValue(response.getOutputStream(), BM.getAllBooks());
    }*/

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
