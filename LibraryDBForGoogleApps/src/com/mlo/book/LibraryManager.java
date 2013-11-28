package com.mlo.book;

import java.util.ArrayList;

public interface LibraryManager {
	public void initialise ();
	public Long addBook(String isbn, String title, String inPossessionOf);
	public Long addBook(Book bookWithoutId);
	public void updateBook(Long bookId, String field, String newData);
	public void deleteBook(Long bookId);
	public ArrayList<Book> getAllBooks();
	public void deleteAllBooks();
	public Book getBook(Long bookId);
}
