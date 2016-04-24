package objects;

import java.util.Date;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import java.util.ArrayList;
import java.util.Calendar; 

public class Post implements Comparable<Post> {
	Key key;
	String description;
	FlipABookUser seller;
	Book book;
	String price;
	Date date;
	Date deadline;
	int status;
	public static final int ACTIVE = 0;
	public static final int SUSPENDED = 1;
	private static final long DAY_IN_MS = 1000 * 60 * 60 * 24;
	private static final long TWO_WEEKS = 14 * DAY_IN_MS;

	public Post() {
	}
	
	public Post(Key key){
		this.key = key;
		Entity entity = null;
		try {
			entity = DatastoreServiceFactory.getDatastoreService().get(key);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		setPropertiesFromEntity(entity);
		HomePage.posts.add(this);
	}

	public Post(FlipABookUser seller, String title, String author, String isbn, String price, String description) {
		this.seller = seller;
		getBook(title, author, isbn);
		this.price = price;
		this.description = description;
		date = new Date();
		deadline = new Date(date.getTime() + TWO_WEEKS);
		status = ACTIVE;
		keyGen();
		addToDatastore();
	}
	
	public Post(FlipABookUser seller, String title, String author, String isbn, String price, String description, Date postdate) {
		this.seller = seller;
		getBook(title, author, isbn);
		this.price = price;
		this.description = description;
		postdate = date;
		Calendar cal = Calendar.getInstance(); 
		if (postdate == null) {
			postdate = new Date(); 
		}
		cal.setTime(postdate);
		cal.add(Calendar.DAY_OF_WEEK, 14);
		deadline = cal.getTime(); //the most janky way of adding two weeks to a given date ever
		status = ACTIVE;
		keyGen();
		addToDatastore();
	}
	
	public void setPropertiesFromEntity(Entity entity){		
		description = (String) entity.getProperty("description");
		seller = (FlipABookUser) entity.getProperty("seller");
		book = (Book) entity.getProperty("book");
		price = (String) entity.getProperty("price");
		date = (Date) entity.getProperty("date");
		deadline = (Date) entity.getProperty("deadline");
		status = (int) entity.getProperty("status");
	}


	private void getBook(String title, String author, String isbn) {
		for (int i = 0; i < HomePage.books.size(); i++) {
			if (isbn.equals(HomePage.books.get(i).getIsbn())) {
				book = HomePage.books.get(i);
				return;
			}
		}
		book = new Book(title, author, isbn);
	}

	public String getTitle() {
		return book.getTitle();
	}

	public String getIsbn() {
		return book.getIsbn();
	}

	public String getAuthor() {
		return book.getAuthor();
	}

	public String getDescription() {
		return description;
	}

	public FlipABookUser getSeller() {
		return seller;
	}

	public Book getBook() {
		return book;
	}

	public String getPrice() {
		return price;
	}

	public Date getDate() {
		return date;
	}

	public int getStatus() {
		return status;
	}

	public void editDescription(String newDescription) {
		description = newDescription;
		addToDatastore();
	}

	public void editPrice(String newPrice) {
		price = newPrice;
		addToDatastore();
	}

	public void editStatus(int newStatus) {
		if (newStatus == ACTIVE || newStatus == SUSPENDED) {
			status = newStatus;
		}
		addToDatastore();
	}
	
	public void addToDatastore(){
		Entity post_datastore = new Entity("Post", key);
		post_datastore.setProperty("seller", seller);
		post_datastore.setProperty("book", book);
		post_datastore.setProperty("price", price);
		post_datastore.setProperty("description", description);
		post_datastore.setProperty("date", date);
		post_datastore.setProperty("deadline", deadline);
		post_datastore.setProperty("status", status);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(post_datastore);
	}
	
	public void keyGen(){
		String keyString = book.isbn + seller.getEmail();
		key = KeyFactory.createKey("Post", keyString);
	}

	@Override
	public int compareTo(Post other) {
		if (book.compareTo(other.getBook()) == 0 && seller.compareTo(other.getSeller()) == 0) {
			// posts are the same
			return 0;
		}
		// posts are not the same
		return -1;
	}
}
