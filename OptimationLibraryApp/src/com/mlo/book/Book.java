package com.mlo.book;

/**
 * 
 * @author Michael Lo
 *
 */
public class Book { 

	private int id; 

	private String isbn; 
	private String title; 
	private String inPossessionOf; 
	
	public Book() {
	}

	public Book(String isbn, String title, String inPossessionOf) {
		super();
		this.isbn = isbn;
		this.title = title;
		this.inPossessionOf = inPossessionOf;
	}

	public int getId() { 
		return id; 
	} 

	public void setId( int id ) { 
		this.id = id; 
	} 

	public String getIsbn() { 
		return isbn; 
	} 

	public void setIsbn( String isbn ) { 
		this.isbn = isbn; 
	} 

	public String getTitle() { 
		return title; 
	} 

	public void setTitle( String title ) { 
		this.title = title; 
	} 

	public String getInPossessionOf() { 
		return inPossessionOf; 
	} 

	public void setInPossessionOf( String inPossessionOf ) { 
		this.inPossessionOf = inPossessionOf; 
	} 
	
	public String toString() {
		return this.title + " (" + this.isbn + "), currently in possession of: " + this.inPossessionOf;
	}
} 
