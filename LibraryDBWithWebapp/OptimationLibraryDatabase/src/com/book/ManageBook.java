package com.book;

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

public class ManageBook { 
	private static SessionFactory factory;
	private static ServiceRegistry serviceRegistry;
	
	public static void main (String[] args) {
		try {
			Configuration configuration = new Configuration();
			configuration.configure();
			serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();        
			factory = configuration.buildSessionFactory(serviceRegistry);
		} catch (Throwable ex) { 
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex); 
		}

		doThings();
	} 

	public static void doThings() {
		ManageBook MB = new ManageBook();

		/*Empty the database*/
		MB.deleteAllBooks();

		/* Add few Book records to database */ 
		Integer bookID1 = MB.addBook("Joe Abercrombie", "Last Argument Of Kings", "The First Law", "Logen Ninefingers, infamous barbarian, has finally run out of luck. Caught in one feud too many, he’s on the verge of becoming a dead barbarian – leaving nothing behind him but bad songs, dead friends, and a lot of happy enemies. Nobleman Captain Jezal dan Luthar, dashing officer, and paragon of selfishness, has nothing more dangerous in mind than fleecing his friends at cards and dreaming of glory in the fencing circle. But war is brewing, and on the battlefields of the frozen North they fight by altogether bloodier rules. Inquisitor Glokta, cripple turned torturer, would like nothing better than to see Jezal come home in a box. But then Glokta hates everyone: cutting treason out of the Union one confession at a time leaves little room for friendship. His latest trail of corpses may lead him right to the rotten heart of government, if he can stay alive long enough to follow it. Enter the wizard, Bayaz. A bald old man with a terrible temper and a pathetic assistant, he could be the First of the Magi, he could be a spectacular fraud, but whatever he is, he's about to make the lives of Logen, Jezal, and Glotka a whole lot more difficult. Murderous conspiracies rise to the surface, old scores are ready to be settled, and the line between hero and villain is sharp enough to draw blood.", "March 2007", 4, "34,845", null); 
		Integer bookID2 = MB.addBook("David Thorne", "The Internet Is A Playground", "", null, null, 0, null, null); 
		Integer bookID3 = MB.addBook("Patrick Rothfuss", "The Name Of The Wind", "The Kingkiller Chronicles", null, null, 0, null, null); 
		
		/* List down all the Books */
		MB.listBooks();
		System.out.println("#-----------------------------------------#");

		/* Update Book's records */ 
		MB.updateBook(bookID1, "The Blade Itself");
		MB.updateBook(bookID3, "The Wise Man's Fear");

		/* Delete an Book from the database */
		MB.deleteBook(bookID2);

		/* List down new list of the Books */ 
		MB.listBooks();
		System.out.println("#-----------------------------------------#");
	}

	public void printBookDetails(Book book) {
		System.out.println("Author: " + book.getAuthor()); 
		System.out.println("Title: " + book.getTitle()); 
		System.out.println("Series: " + book.getSeries()); 
		System.out.println("Description: " + book.getDescription()); 
		System.out.println("Date of Publication: " + book.getDate()); 
		System.out.println("Average rating: " + book.getNumStars() + " from " + book.getNumRatings() + " ratings."); 
		if ((null == book.getImageData())) {
			System.out.println("No cover image available."); 
		}
	}

	/* Method to CREATE an Book in the database */ 
	public Integer addBook(String author, String title, String series, String description, String date, int numStars, String numRatings, byte[] imageData) { 
		Session session = factory.openSession(); 
		Transaction tx = null; 
		Integer BookID = null; 
		try { 
			tx = session.beginTransaction(); 
			Book book = new Book(); 

			book.setAuthor(author); 
			book.setTitle(title); 
			book.setSeries(series); 
			book.setDescription(description); 
			book.setDate(date); 
			book.setNumStars(numStars); 
			book.setNumRatings(numRatings); 
			book.setImageData(imageData); 

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

	/* Method to LIST all the Books' details */ 
	public void listBooks( ) { 
		Session session = factory.openSession(); 
		Transaction tx = null; 

		try { 
			tx = session.beginTransaction(); 
			List<?> Books = session.createQuery("FROM Book").list(); 
			for (Iterator<?> iterator = Books.iterator(); iterator.hasNext();){ 
				Book book = (Book) iterator.next(); 
				printBookDetails(book);
			} 
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

	/* Method to UPDATE series for an Book */ 
	public void updateBook(Integer BookID, String title ) { 
		Session session = factory.openSession(); 
		Transaction tx = null; 
		try { 
			tx = session.beginTransaction(); 
			Book book = (Book)session.get(Book.class, BookID); 
			book.setTitle( title ); 
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
			Book book = 
					(Book)session.get(Book.class, BookID); 
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

	/* Method to READ all the Books */ 
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
		ManageBook MB = new ManageBook();
		for (Book b: MB.getAllBooks()) {
			MB.deleteBook(b.getId());
		}
	} 

	public Book getBook(Integer BookID) {
		Session session = factory.openSession(); 
		Transaction tx = null; 
		try { 
			tx = session.beginTransaction(); 
			Book book = (Book) session.createQuery("FROM Book WHERE ID EQUALS " + BookID);
			tx.commit();
			return book;
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
} 