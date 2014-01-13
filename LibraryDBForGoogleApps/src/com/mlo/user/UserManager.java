package com.mlo.user;

import java.util.ArrayList;

/**
 * @author Michael Lo
 * Database interface class. Has methods to Create, Read, Update and Delete User entries and objects to and from the database.
 * Also provides methods to get and delete all Users in the database, for convenience.
 */
public interface UserManager {

	/**
	 * Sets up the database interface objects. Only needs to be called once, although no harm can come of calling it again.
	 */
	public void initialise ();

	/**
	 * @param name
	 * @param email
	 * @return User ID
	 * Method to create a new row in the database, and return its unique primary key identifier.
	 * Takes as arguments the three fields of a User object, each corresponding to a column in the database.
	 */
	public Long addUser(String name, String email);

	/**
	 * @param userWithoutId
	 * @return User ID
	 * Method to create a new row in the database, and return its unique primary key identifier.
	 * Takes as arguments a User object. Should only be called for Users that are not already in the database (have no id), or a copy will be created.
	 */
	public Long addUser(User userWithoutId);

	/**
	 * @param userId
	 * @param field
	 * @param newData
	 * Method to update a single value in the database. User ID corresponds to the row, field is the name of the column to be altered.
	 */
	public void updateUser(Long userId, String field, String newData);

	/**
	 * @param id
	 * Method to delete the row from the database with the given primary key.
	 */
	public void deleteUser(Long id);

	/**
	 * @return AllUsers
	 * Method to return a list of User objects, each representing one row in the database.
	 */
	public ArrayList<User> getAllUsers( );

	/**
	 * Method to delete all entries in all rows of the database.
	 */
	public void deleteAllUsers( );

	/**
	 * @param id
	 * @return user
	 * Method to return the User object representing the row of the database identified by the given primary key.
	 */
	public User getUser(Long id);
}
