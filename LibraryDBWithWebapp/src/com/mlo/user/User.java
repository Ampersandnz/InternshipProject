package com.mlo.user;

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
@Table(name = "USERS") 
public class User { 

	@Id @GeneratedValue 
	@Column(name = "id") 
	private Long id; 

	@Column(name = "name") 
	private String name; 
	
	@Column(name = "email") 
	private String email; 
	
	public User() {
	}

	public User(String name, String email) {
		this();
		this.name = name;
		this.email = email;
	}
	
	public User(Long id, String name, String email) {
		this(name, email);
		this.id = id;
	}

	public Long getId() { 
		return id; 
	} 

	public User setId( Long id ) { 
		this.id = id;
		return this;
	} 

	public String getName() { 
		return name; 
	} 

	public User setName( String name ) { 
		this.name = name;
		return this;
	} 

	public String getEmail() { 
		return email; 
	} 

	public User setEmail( String email ) { 
		this.email = email;
		return this;
	} 
	
	public String toString() {
		return this.name + " (" + this.email + ")";
	}
} 
