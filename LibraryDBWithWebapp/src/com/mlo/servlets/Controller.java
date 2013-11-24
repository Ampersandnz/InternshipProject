package com.mlo.servlets;

import java.io.IOException;
import java.util.ArrayList;
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
 * Servlet implementation class Controller
 */

@WebServlet("/Controller")
public class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String ADD_JSP = "/Add.jsp";
	private static String DELETE_JSP = "/Delete.jsp";
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
					for(String parameter : parameters.keySet()) {
						if(parameter.startsWith("book")) {
							int ID = Integer.parseInt(parameter.substring(4));
							System.out.println("" + ID);
							Book book = BM.getBook(ID);
							for (Book b: BM.getAllBooks()) {
								System.out.println("BOOK ID: " + b.getId());
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
						// Display delete page.
						forward = DELETE_JSP;
					}

				} else {
					// Return to main list.
					forward = SHOWALL_JSP;
				}

				break;

			case "add":
				for (String s: parameters.keySet()) {
				}
				if (parameters.containsKey("save")) {

					String isbn = "";
					String title = "";
					boolean inLibrary = false;
					String inPossessionOf = "";

					try {
						isbn = request.getParameter("isbn");
						title = request.getParameter("title");
						if (parameters.containsKey("inLibrary")) {
							inLibrary = true;
						}
						inPossessionOf = request.getParameter("inPossessionOf");
					} catch (Exception e) {
						e.printStackTrace();
					}

					BM.addBook(isbn, title, inLibrary, inPossessionOf);
				}

				// Return to main list.
				forward = SHOWALL_JSP;
				break;

			case "delete":
				// Just return to main list without doing anything.
				forward = SHOWALL_JSP;
				break;
			}
		}

		// Populate the list of books with the contents of the database.
		ArrayList<Book> allBooks = new ArrayList<Book>(); 
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
		BM.addBook("9780316007573", "The Ashes Of Worlds", false, "Michael Lo"); 
		BM.addBook("9780425037454", "The Stars My Destination", true, "Library"); 
		BM.addBook("9780756404079", "The Name Of The Wind", false, "Library"); 
		BM.addBook("9781429943840", "Earth Afire", true, "Michael Lo");
		BM.addBook("9780345490711", "Judas Unchained", false, "Library");
		BM.addBook("9780606005739", "A Wizard Of Earthsea", true, "Library");
	}
} 