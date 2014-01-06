package com.mlo.book;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

/**
 * 
 * @author Michael Lo
 * Hibernate database interface class. Has methods to Create, Read, Update and Delete User entries and objects to and from the database.
 * Also provides methods to get and delete all Books in the database, for convenience.
 *
 */
public class BookManager {
	private static SessionFactory factory;
	private static ServiceRegistry serviceRegistry;
	
	private static final String LIBRARY_USERNAME = "_library";
	private static final String ISBN = "isbn";
	private static final String TITLE = "title";
	private static final String INPOSSESSIONOF = "inPossessionOf";
	private static final String NOISBN = "No isbn available.";
	private static final String NOTITLE = "inPossessionOf";

	public BookManager() {
	}

	/**
	 * Sets up the database interface objects. Only needs to be called once, although no harm can come of calling it again.
	 */
	public void initialise () {
		try {
			Configuration configuration = new Configuration();
			configuration.configure();
			serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();        
			factory = configuration.buildSessionFactory(serviceRegistry);
		} catch (Throwable ex) { 
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex); 
		}
	} 

	/**
	 * @param isbn
	 * @param title
	 * @param inPossessionOf
	 * @return User ID
	 * 
	 * Method to create a new row in the database, and return its unique primary key identifier.
	 * Takes as arguments the three fields of a User object, each corresponding to a column in the database.
	 */
	public Long addBook(String isbn, String title, String inPossessionOf) { 
		if (isbn.equals("")||null==isbn) {
			isbn = NOISBN;
		}

		if (title.equals("")||null==title) {
			title = NOTITLE;
		}

		if (inPossessionOf.equals("")||null==inPossessionOf) {
			inPossessionOf = LIBRARY_USERNAME;
		}

		Session session = factory.openSession(); 
		Transaction tx = null; 
		Long bookId = null; 
		try { 
			tx = session.beginTransaction(); 
			Book book = new Book(isbn, title, inPossessionOf); 
			bookId = (Long) session.save(book);
			tx.commit();
		} catch (HibernateException e) { 
			if (tx!=null) {
				tx.rollback(); 
			}
			e.printStackTrace(); 
		} finally { 
			session.close(); 
		} 
		return bookId; 
	} 

	/**
	 * @param bookWithoutId
	 * @return User ID
	 * 
	 * Method to create a new row in the database, and return its unique primary key identifier.
	 * Takes as arguments a User object. Should only be called for Books that are not already in the database (have no id), or a copy will be created.
	 */
	public Long addBook(Book bookWithoutId) { 
		Session session = factory.openSession(); 
		Transaction tx = null; 
		Long bookId = null; 
		try { 
			tx = session.beginTransaction(); 
			bookId = (Long) session.save(bookWithoutId);
			tx.commit();
		} catch (HibernateException e) { 
			if (tx!=null) {
				tx.rollback(); 
			}
			e.printStackTrace(); 
		} finally { 
			session.close(); 
		} 
		return bookId; 
	} 

	/**
	 * @param bookId
	 * @param field
	 * @param newData
	 * 
	 * Method to update a single value in the database. User ID corresponds to the row, field determines the column to be altered.
	 */
	public void updateBook(Long bookId, String field, String newData) { 
		Session session = factory.openSession(); 
		Transaction tx = null; 
		try { 
			tx = session.beginTransaction(); 
			Book book = (Book)session.get(Book.class, bookId); 
			
			if (field.equals(ISBN)) {
				if (newData.equals("")||null==newData) {
					newData = NOISBN;
				}
				book.setIsbn(newData);
				
			} else if (field.equals(TITLE)) {
				if (newData.equals("")||null==newData) {
					newData = NOTITLE;
				}
				book.setTitle(newData);
				
			} else if (field.equals(INPOSSESSIONOF)) {
				if (newData.equals("")||null==newData) {
					newData = LIBRARY_USERNAME;
				}
				book.setInPossessionOf(newData);
			}
			
			session.update(book); 
			tx.commit(); 
		} catch (HibernateException e) { 
			if (tx!=null) {
				tx.rollback(); 
			}
			e.printStackTrace(); 
		} finally { 
			session.close(); 
		} 
	} 

	/**
	 * @param id
	 * 
	 * Method to delete the row from the database with the given primary key.
	 */
	public void deleteBook(Long id) { 
		Session session = factory.openSession(); 
		Transaction tx = null; 

		try { 
			tx = session.beginTransaction(); 
			Book book = (Book)session.get(Book.class, id); 
			session.delete(book); 
			tx.commit(); 
		} catch (HibernateException e) { 
			if (tx!=null) {
				tx.rollback(); 
			}
			e.printStackTrace(); 
		} finally { 
			session.close(); 
		} 
	} 

	/**
	 * @return AllBooks
	 * 
	 * Method to return a list of User objects, each representing one row in the database.
	 */
	public ArrayList<Book> getAllBooks( ){ 
		Session session = factory.openSession(); 
		Transaction tx = null; 
		ArrayList<Book> allBooks = new ArrayList<Book>();

		try { 
			tx = session.beginTransaction(); 
			List<?> Books = session.createQuery("FROM Book").list(); 
			for (Iterator<?> iterator = Books.iterator(); iterator.hasNext();){ 
				Book book = (Book) iterator.next(); 
				allBooks.add(book);
			} 
			tx.commit(); 
			return allBooks;
		} catch (HibernateException e) { 
			if (tx!=null) {
				tx.rollback(); 
			}
			e.printStackTrace(); 
		} finally { 
			session.close(); 
		} 
		return null;
	}

	/**
	 * Method to delete all entries in all rows of the database.
	 */
	public void deleteAllBooks( ) { 
		try {
			for (Book b: this.getAllBooks()) {
				this.deleteBook(b.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 

	/**
	 * @param id
	 * @return book
	 * 
	 * Method to return the User object representing the row of the database identified by the given primary key.
	 */
	public Book getBook(Long id) {
		Session session = factory.openSession(); 
		return ((Book) session.get(Book.class, id));
	}
}
