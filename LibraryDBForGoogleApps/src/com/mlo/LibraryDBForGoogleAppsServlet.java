package com.mlo;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.*;

import com.mlo.book.Book;

@SuppressWarnings("serial")
public class LibraryDBForGoogleAppsServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Current books in memory:");
		ArrayList<Book> bookList = testList();
		for (Book b: bookList) {
			resp.getWriter().println(b.toString());
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
	}
}
