package com.mlo.book;

import java.util.ArrayList;

/**
 * 
 * @author Michael Lo
 * Hibernate database interface class. Has methods to Create, Read, Update and Delete Book entries and objects to and from the database.
 * Also provides methods to get and delete all Books in the database, for convenience.
 *
 */
public class BookManagerList implements LibraryManager {
	private static ArrayList<Book> bookList = new ArrayList<Book>();
	private static int nextID = 0;
	public BookManagerList() {

	}

	/**
	 * Sets up the database interface objects. Only needs to be called once, although no harm can come of calling it again.
	 */
	public void initialise () {
	} 

	/**
	 * @param isbn
	 * @param title
	 * @param inPossessionOf
	 * @return Book ID
	 * 
	 * Method to create a new row in the database, and return its unique primary key identifier.
	 * Takes as arguments the three fields of a Book object, each corresponding to a column in the database.
	 */
	public Integer addBook(String isbn, String title, String inPossessionOf) { 
		if (null == isbn || "".equals(isbn)) {
			isbn = "No isbn available.";
		}
		if (null == title || "".equals(title)) {
			title = "No title available.";
		}
		if (null == inPossessionOf || "".equals(inPossessionOf)) {
			inPossessionOf = "_library";
		}
		
		Integer bookID = null; 
		Book book = new Book(isbn, title, inPossessionOf); 
		bookID = (Integer) nextID;
		book.setId(bookID);
		bookList.add(book);
		nextID++;
		return bookID; 
	} 

	/**
	 * @param bookWithoutId
	 * @return Book ID
	 * 
	 * Method to create a new row in the database, and return its unique primary key identifier.
	 * Takes as arguments a Book object. Should only be called for Books that are not already in the database (have no id), or a copy will be created.
	 */
	public Integer addBook(Book bookWithoutId) { 
		if (null == bookWithoutId.getIsbn() || "".equals(bookWithoutId.getIsbn())) {
			bookWithoutId.setIsbn("No isbn available.");
		}
		if (null == bookWithoutId.getTitle() || "".equals(bookWithoutId.getTitle())) {
			bookWithoutId.setTitle("No title available.");
		}
		if (null == bookWithoutId.getInPossessionOf() || "".equals(bookWithoutId.getInPossessionOf())) {
			bookWithoutId.setInPossessionOf("_library");
		}
		
		Integer bookID = (Integer) nextID;
		bookWithoutId.setId(bookID);
		bookList.add(bookWithoutId);
		return bookID; 
	} 

	/**
	 * @param BookID
	 * @param field
	 * @param newData
	 * 
	 * Method to update a single value in the database. Book ID corresponds to the row, field determines the column to be altered.
	 */
	public void updateBook(Integer bookID, String field, String newData) { 
		Book book = this.getBook(bookID);
		switch (field) {

		case "isbn":
			if (null == newData || "".equals(newData)) {
				newData = "No isbn available.";
			}
			book.setIsbn(newData); 
			break;
			
		case "title":
			if (null == newData || "".equals(newData)) {
				newData = "No title available.";
			}
			book.setTitle(newData); 
			break;

		case "inPossessionOf":
			if (null == newData || "".equals(newData)) {
				newData = "_library";
			}
			book.setInPossessionOf(newData); 
			break;
		}
	} 

	/**
	 * @param BookID
	 * 
	 * Method to delete the row from the database with the given primary key.
	 */
	public void deleteBook(Integer bookID) { 
		try {
			for (Book b: bookList) {
				if (b.getId() == bookID) {
					bookList.remove(b);
				}
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 

	/**
	 * @return AllBooks
	 * 
	 * Method to return a list of Book objects, each representing one row in the database.
	 */
	public ArrayList<Book> getAllBooks(){ 
		return bookList;
	}

	/**
	 * Method to delete all entries in all rows of the database.
	 */
	public void deleteAllBooks() { 
		for (Book b: bookList) {
			bookList.remove(b);
		}
	} 

	/**
	 * @param bookID
	 * @return book
	 * 
	 * Method to return the Book object representing the row of the database identified by the given primary key.
	 */
	public Book getBook(Integer bookID) {
		for (Book b: bookList) {
			if (b.getId() == bookID) {
				return b;
			}
		}
		return null;
	}
}
