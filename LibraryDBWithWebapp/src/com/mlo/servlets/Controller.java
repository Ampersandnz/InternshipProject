package com.mlo.servlets;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mlo.book.Book;
import com.mlo.book.BookManager;

/**
 * 
 * @author Michael Lo
 *
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

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
							// Do nothing.
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
							// Do nothing.
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
				// Just return to main list without doing anything.
				forward = SHOWALL_JSP;
				break;

			case "edit":
				if (parameters.containsKey("save")) {
					// No way to tell if a field has been edited or not, so just update all of them.
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

		//Send book list to next page.
		request.setAttribute("allBooks",allBooks);

		// Change to required page.
		RequestDispatcher view = request.getRequestDispatcher(forward);
		view.forward(request, response);
	}

	private void populateDB() {
		/*Empty the database*/
		BM.deleteAllBooks();

		/* Add few Book records to database */ 
		BM.addBook("9780316007573", "The Ashes Of Worlds", "Michael Lo"); 
		BM.addBook("9780425037454", "The Stars My Destination", "Library"); 
		BM.addBook("9780756404079", "The Name Of The Wind", "Library"); 
		BM.addBook("9781429943840", "Earth Afire",  "Michael Lo");
		BM.addBook("9780345490711", "Judas Unchained", "Library");
		BM.addBook("9780606005739", "A Wizard Of Earthsea", "Library");
	}
} 