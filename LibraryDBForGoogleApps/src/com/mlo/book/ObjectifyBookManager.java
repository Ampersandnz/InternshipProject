package com.mlo.book;

import java.util.ArrayList;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

public class ObjectifyBookManager {

	private static Objectify ofy;
	private static final String LIBRARY_USERNAME = "_library";
	
	public void initialise() {
		ofy = ObjectifyService.begin();
	}

	public Long addBook(String isbn, String title, String inPossessionOf) {
		if (isbn.equals("")||null==isbn) {
			isbn = "No isbn available.";
		}
		if (title.equals("")||null==title) {
			title = "No title available.";
		}
		if (inPossessionOf.equals("")||null==inPossessionOf) {
			inPossessionOf = LIBRARY_USERNAME;
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
		if (field.equals("isbn")) {
			book.setIsbn(newData);
		} else if (field.equals("title")) {
			book.setTitle(newData);
		} else if (field.equals("inPossessionOf")) {
			book.setInPossessionOf(newData);
		}
		ofy.put(book);
	}

	
	public void deleteBook(Long bookId) {
		ofy.delete(Book.class, bookId);
	}

	
	public ArrayList<Book> getAllBooks() {
		ArrayList<Book> allBooks = new ArrayList<Book>();
		for (Book b: ofy.query(Book.class)){
			allBooks.add(b);
		}
		return allBooks;
	}

	
	public void deleteAllBooks() {
		for (Book b: ofy.query(Book.class)){
			ofy.delete(b);
		}
	}

	
	public Book getBook(Long bookId) {
		return ofy.get(Book.class, bookId);
	}

}
