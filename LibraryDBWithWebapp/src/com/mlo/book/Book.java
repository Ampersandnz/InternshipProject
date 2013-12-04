package com.mlo.book;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author Michael Lo
 * Hibernate mapping bean. Each Book represents a row in the database. Constructors, getters and setters. No computation or other methods.
 *
 */
@Entity
@Table(name = "BOOK") 
public class Book { 

	@Id @GeneratedValue 
	@Column(name = "id") 
	private Long id; 

	@Column(name = "isbn") 
	private String isbn; 
	
	@Column(name = "title") 
	private String title; 

	@Column(name = "inPossessionOf") 
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

	public void setId( Long id ) { 
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
		return this.title + " (" + this.isbn + ")";
	}
} 
