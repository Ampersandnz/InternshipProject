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

public class BookManager {
	private static SessionFactory factory;
	private static ServiceRegistry serviceRegistry;

	public BookManager() {

	}

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

	/* Method to CREATE an Book in the database */ 
	public Integer addBook(String isbn, String title, String inPossessionOf) { 
		Session session = factory.openSession(); 
		Transaction tx = null; 
		Integer BookID = null; 
		try { 
			tx = session.beginTransaction(); 
			Book book = new Book(isbn, title, inPossessionOf); 
			BookID = (Integer) session.save(book);
			tx.commit();
		} catch (HibernateException e) { 
			if (tx!=null) {
				tx.rollback(); 
			}
			e.printStackTrace(); 
		} finally { 
			session.close(); 
		} 
		return BookID; 
	} 

	/* Method to UPDATE details for a Book */ 
	public void updateBook(Integer BookID, String field, String newData) { 
		Session session = factory.openSession(); 
		Transaction tx = null; 
		try { 
			tx = session.beginTransaction(); 
			Book book = (Book)session.get(Book.class, BookID); 
			switch (field) {

			case "isbn":
				book.setIsbn(newData); 
				break;
				
			case "title":
				book.setTitle(newData); 
				break;

			case "inPossessionOf":
				book.setInPossessionOf(newData); 
				break;
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

	/* Method to DELETE an Book from the records */ 
	public void deleteBook(Integer BookID) { 
		Session session = factory.openSession(); 
		Transaction tx = null; 

		try { 
			tx = session.beginTransaction(); 
			Book book = (Book)session.get(Book.class, BookID); 
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

	/* Method to RETURN all the Books */ 
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

	public void deleteAllBooks( ) { 
		for (Book b: this.getAllBooks()) {
			this.deleteBook(b.getId());
		}
	} 

	public Book getBook(Integer bookID) {
		Session session = factory.openSession(); 
		return ((Book) session.get(Book.class, bookID));
	}
}
