package com.mlo.servlets;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mlo.book.Book;
import com.mlo.book.BookManager;

/**
 * 
 * @author Michael Lo
 * Class to control the behaviour of the webapp. Performs actions and displays different pages in response to user activities.
 */
@WebServlet("/Controller")
public class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String ADD_JSP = "/Add.jsp";
	private static String DELETE_JSP = "/Delete.jsp";
	private static String EDIT_JSP = "/Edit.jsp";
	private static String SHOWALL_JSP = "/ShowAll.jsp";
	private static BookManager BM = new BookManager();
	private static int needToInitialiseDatabase = 0;

	/**
	 * Whenever the user clicks a button, this method is called. It performs different actions and redirects the user's browser to different pages depending on the status of the page and the button that was clicked.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Hidden parameter identifying the page from which the browser is returning.
		String hiddenParam = request.getParameter("page");
		String forward="";

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

		// Send book list to next page.
		request.setAttribute("allBooks",allBooks);

		// Change to required page.
		RequestDispatcher view = request.getRequestDispatcher(forward);
		view.forward(request, response);
	}
	
	/***************************************************
     * doPost(): receives JSON data, parse it, map it and send back as JSON
     ****************************************************/
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
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