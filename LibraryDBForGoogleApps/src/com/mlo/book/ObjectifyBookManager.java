package com.mlo.book;

import java.util.ArrayList;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.mlo.LibraryDBForGoogleAppsServlet;

public class ObjectifyBookManager implements BookManager {

	private static Objectify ofy;
	private static final String ISBN = "isbn";
	private static final String TITLE = "title";
	private static final String INPOSSESSIONOF = "inPossessionOf";
	private static final String NOISBN = "No isbn available.";
	private static final String NOTITLE = "No title available.";

	public void initialise() {
		ObjectifyService.register(Book.class);
		ofy = ObjectifyService.begin();
	}

	public Long addBook(String isbn, String title, String inPossessionOf) {
		if (isbn.equals("") || null == isbn) {
			isbn = NOISBN;
		}

		if (title.equals("") || null == title) {
			title = NOTITLE;
		}

		if (inPossessionOf.equals("") || null == inPossessionOf) {
			inPossessionOf = LibraryDBForGoogleAppsServlet.LIBRARY_USERNAME;
		}

		Book book = new Book(isbn, title, inPossessionOf);

		ofy.put(book);
		return book.getId();
	}

	public Long addBook(Book bookWithoutId) {
		ofy.put(bookWithoutId);
		return bookWithoutId.getId();
	}

	public void updateBook(Long bookId, String field, String newData) {
		Book book = ofy.get(Book.class, bookId);

		if (field.equals(ISBN)) {
			if (newData.equals("") || null == newData) {
				newData = NOISBN;
			}
			book.setIsbn(newData);

		} else if (field.equals(TITLE)) {
			if (newData.equals("") || null == newData) {
				newData = NOTITLE;
			}
			book.setTitle(newData);

		} else if (field.equals(INPOSSESSIONOF)) {
			if (newData.equals("") || null == newData) {
				newData = LibraryDBForGoogleAppsServlet.LIBRARY_USERNAME;
			}

			book.setInPossessionOf(newData);
		}
		ofy.put(book);
	}

	public void deleteBook(Long bookId) {
		ofy.delete(Book.class, bookId);
	}

	public ArrayList<Book> getAllBooks() {
		ArrayList<Book> allBooks = new ArrayList<Book>();
		for (Book b : ofy.query(Book.class)) {
			allBooks.add(b);
		}
		return allBooks;
	}

	public void deleteAllBooks() {
		ofy.delete(ofy.query(Book.class).fetchKeys());
	}

	public Book getBook(Long bookId) {
		return ofy.get(Book.class, bookId);
	}

}
