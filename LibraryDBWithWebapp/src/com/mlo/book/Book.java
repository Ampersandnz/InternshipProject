package com.mlo.book;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BOOK") 
public class Book { 

	@Id @GeneratedValue 
	@Column(name = "id") 
	private int id; 

	@Column(name = "isbn") 
	private String isbn; 
	
	@Column(name = "title") 
	private String title; 

	@Column(name = "inLibrary") 
	private boolean inLibrary; 

	@Column(name = "inPossessionOf") 
	private String inPossessionOf; 
	
	public Book() {
	}

	public Book(String isbn, String title, boolean inLibrary, String inPossessionOf) {
		super();
		this.isbn = isbn;
		this.title = title;
		this.inLibrary = inLibrary;
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
		this.isbn = title; 
	} 

	public boolean getInLibrary() { 
		return inLibrary; 
	} 

	public void setInLibrary( boolean inLibrary ) { 
		this.inLibrary = inLibrary; 
	} 

	public String getInPossessionOf() { 
		return inPossessionOf; 
	} 

	public void setInPossessionOf( String inPossessionOf ) { 
		this.inPossessionOf = inPossessionOf; 
	} 
} 