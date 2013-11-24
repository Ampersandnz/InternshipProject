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
							BM.deleteBook(ID);
							request.setAttribute("deletedIsbn", BM.getBook(ID).getIsbn());
						}
					}
					// Display delete page.
					forward = DELETE_JSP;

				} else {
					// Return to main list.
					forward = SHOWALL_JSP;
				}

				break;

			case "add":
				System.out.println("RETURNING FROM ADD PAGE");
				for (String s: parameters.keySet()) {
					System.out.println(s);
				}
				if (parameters.containsKey("save")) {

					String isbn = "";
					String title = "title";
					boolean inLibrary = false;
					String inPossessionOf = "";
					
					try {
						isbn = request.getParameter("isbn");
						title = request.getParameter("title");
						inLibrary = Boolean.parseBoolean(request.getParameter("inLibrary"));
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
		BM.addBook("9780316007573", "The Ashes Of Worlds", true, "Michael Lo"); 
		BM.addBook("isbn", "title", true, "inPossessionOf"); 
		BM.addBook("isbn", "title", false, "inPossessionOf"); 
		BM.addBook("isbn", "title", true, "inPossessionOf");
		BM.addBook("isbn", "title", false, "inPossessionOf");
		BM.addBook("isbn", "title", true, "inPossessionOf");
		BM.addBook("isbn", "title", true, "inPossessionOf");
	}
} 