package com.mlo.book;

/**
 * @author Michael Lo
 * Set() methods return this, so multiple set() calls can be concatenated together.
 */

public class Book { 

	private Long id; 
	
	private String isbn; 
	private String title; 
	private String inPossessionOf; 
	
	public Book() {
	}

	public Book(String isbn, String title, String inPossessionOf) {
		this();
		this.isbn = isbn;
		this.title = title;
		this.inPossessionOf = inPossessionOf;
	}

	public Book(Long id, String isbn, String title, String inPossessionOf) {
		this(isbn, title, inPossessionOf);
		this.id = id;
	}

	public Long getId() { 
		return id; 
	} 

	public Book setId( Long bookID ) { 
		this.id = bookID;
		return this;
	} 

	public String getIsbn() { 
		return isbn; 
	} 

	public Book setIsbn( String isbn ) { 
		this.isbn = isbn;
		return this;
	} 

	public String getTitle() { 
		return title; 
	} 

	public Book setTitle( String title ) { 
		this.title = title;
		return this;
	} 

	public String getInPossessionOf() { 
		return inPossessionOf; 
	} 

	public Book setInPossessionOf( String inPossessionOf ) { 
		this.inPossessionOf = inPossessionOf;
		return this;
	} 
	
	public String toString() {
		return this.title + " (" + this.isbn + ")";
	}
} 
