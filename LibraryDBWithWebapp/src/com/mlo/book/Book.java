package com.mlo.book;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Michael Lo
 * Hibernate mapping bean. Each User represents a row in the database. Constructors, getters and setters. No computation or other methods.
 * Set() methods return this, so multiple set() calls can be concatenated together.
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

	public Book setId( Long id ) { 
		this.id = id;
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
