package com.mlo.user;

import com.googlecode.objectify.annotation.Entity;

import javax.persistence.Id;

/**
 * @author Michael Lo
 * Objectify mapping bean. Each User represents a row in the database. Constructors, getters and setters. No computation or other methods.
 * Set() methods return this, so multiple set() calls can be concatenated together.
 */
@Entity
public class User implements Comparable<User>{ 

	@Id private Long id; 

	private String name; 
	private String email; 
	private boolean isAdmin = false;
	private String password;
	
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

	public User setId(Long id) { 
		this.id = id;
		return this;
	} 

	public String getName() { 
		return name; 
	} 

	public User setName(String name) { 
		this.name = name;
		return this;
	} 

	public String getEmail() { 
		return email; 
	}

	public User setEmail(String email) { 
		this.email = email;
		return this;
	}
	
	public boolean getIsAdmin() { 
		return isAdmin; 
	}

	public User setIsAdmin(boolean isAdmin) { 
		this.isAdmin = isAdmin;
		return this;
	}
	
	public String getPassword() { 
		return password; 
	}

	public User setPassword(String password) { 
		this.password = password;
		return this;
	}
	
	public String toString() {
		String string = this.name;
		if (isAdmin) {
			string = string + " (Admin)";
		}
		return string;
	}

	@Override
	public int compareTo(User u) {
	    if (this == u) {
	    	return 0;
	    }

	    int comparison = this.name.compareTo(u.name);
	    
	    if (comparison != 0) {
	    	return comparison;
	    }

	    return 0;
	}
} 
