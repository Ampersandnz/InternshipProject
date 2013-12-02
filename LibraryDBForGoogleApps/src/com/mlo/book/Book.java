package com.mlo.book;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;

/**
 * 
 * @author Michael Lo
 * 
 */

@Entity
public class Book { 

	@Id private Long id; 
	
	private String isbn; 
	private String title; 
	private String inPossessionOf; 
	
	public Book() {
	}

	Book(String isbn, String title, String inPossessionOf) {
		this();
		this.isbn = isbn;
		this.title = title;
		this.inPossessionOf = inPossessionOf;
	}

	Book(Long id, String isbn, String title, String inPossessionOf) {
		this(isbn, title, inPossessionOf);
		this.id = id;
	}

	public Long getId() { 
		return id; 
	} 

	public void setId( Long bookID ) { 
		this.id = bookID; 
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
