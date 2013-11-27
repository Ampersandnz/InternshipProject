package com.mlo.book;

import java.util.ArrayList;

public interface LibraryManager {
	public void initialise ();
	public Integer addBook(String isbn, String title, String inPossessionOf);
	public Integer addBook(Book bookWithoutId);
	public void updateBook(Integer bookID, String field, String newData);
	public void deleteBook(Integer bookID);
	public ArrayList<Book> getAllBooks();
	public void deleteAllBooks();
	public Book getBook(Integer bookID);
}
