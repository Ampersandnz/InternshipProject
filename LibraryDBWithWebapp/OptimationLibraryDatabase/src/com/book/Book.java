package com.book;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "BOOK") 
public class Book { 

	@Id @GeneratedValue 
	@Column(name = "id") 
	private int id; 

	@Column(name = "author") 
	private String author; 

	@Column(name = "title") 
	private String title; 

	@Column(name = "series") 
	private String series; 

	@Column(name = "description") 
	private String description; 

	@Column(name = "date") 
	private String date; 

	@Column(name = "numStars") 
	private int numStars; 

	@Column(name = "numRatings") 
	private String numRatings; 

	@Column(name = "imageData")	@Lob
	private byte[] imageData;

	public Book() {
	}

	public Book(String author, String title, String series, String description, String date, int numStars, String numRatings, byte[] imageData) {
		super();
		this.author = author;
		this.title = title;
		this.series = series;
		this.description = description;
		this.date = date;
		this.numStars = numStars;
		this.numRatings = numRatings;
		this.imageData = imageData;
	}

	public int getId() { 
		return id; 
	} 

	public void setId( int id ) { 
		this.id = id; 
	} 

	public String getAuthor() { 
		return author; 
	} 

	public void setAuthor( String author ) { 
		this.author = author; 
	} 

	public String getTitle() { 
		return title; 
	} 

	public void setTitle( String title ) { 
		this.title = title; 
	} 

	public String getSeries() { 
		return series; 
	} 

	public void setSeries( String series ) { 
		this.series = series; 
	} 

	public String getDescription() { 
		return description; 
	} 

	public void setDescription( String description ) { 
		this.description = description; 
	} 

	public String getDate() { 
		return date; 
	} 

	public void setDate( String date ) { 
		this.date = date; 
	} 

	public int getNumStars() { 
		return numStars; 
	} 

	public void setNumStars( int numStars ) { 
		this.numStars = numStars; 
	} 


	public String getNumRatings() { 
		return numRatings; 
	} 

	public void setNumRatings( String numRatings ) { 
		this.numRatings = numRatings; 
	} 

	public byte[] getImageData() { 
		return imageData;
	} 

	public void setImageData( byte[] imageData ) { 
		this.imageData = imageData; 
	} 
} 
