package objects;

import java.util.Date;
import java.util.List;

import com.google.api.server.spi.auth.common.User;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

import java.util.ArrayList;
import java.util.Calendar;

public class Post implements Comparable<Post> {
	Key key;
	public Entity post;
	String description;
	Entity seller;
	Entity book;
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

	public Post(Entity entity) {
		this.post = entity;
		this.key = entity.getKey();
		setPropertiesFromEntity();
		addToDatastore();
	}

	public Post(Key key) {
		this.key = key;
		try {
			post = DatastoreServiceFactory.getDatastoreService().get(key);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		setPropertiesFromEntity();
		addToDatastore();
	}

	public Post(Entity seller, String title, String author, String isbn, String price, String description) {
		this.seller = seller;
		getBook(title, author, isbn);
		this.price = price;
		this.description = description;
		date = new Date();
		deadline = new Date(date.getTime() + TWO_WEEKS);
		status = ACTIVE;
		keyGen();
		addToDatastore();
		HomePage.posts.add(this);
	}

	public Post(Entity seller, String title, String author, String isbn, String price, String description,
			Date postdate) {
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
		deadline = cal.getTime(); // the most janky way of adding two weeks to a
									// given date ever
		status = ACTIVE;
		keyGen();
		addToDatastore();
		HomePage.posts.add(this);
	}

	public void setPropertiesFromEntity() {
		description = (String) post.getProperty("description");
		seller = (Entity) post.getProperty("seller");
		book = (Entity) post.getProperty("book");
		price = (String) post.getProperty("price");
		date = (Date) post.getProperty("date");
		deadline = (Date) post.getProperty("deadline");
		status = (int) post.getProperty("status");
	}

	private void getBook(String title, String author, String isbn) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		List<Entity> bookEntities = datastore.prepare(new Query("Book"))
				.asList(FetchOptions.Builder.withLimit(Integer.MAX_VALUE));

		for (Entity entity : bookEntities) {
			if (entity.getKey().equals(isbn)) {
				book = entity;
				addToDatastore();
				return;
			}
		}
		Book newBook = new Book(title, author, isbn);
		book = newBook.book;
		addToDatastore();
	}

	public String getTitle() {
		return (String) book.getProperty("title");
	}

	public String getIsbn() {
		return (String) book.getProperty("isbn");
	}

	public String getAuthor() {
		return (String) book.getProperty("author");

	}

	public String getDescription() {
		return description;
	}

	public Entity getSeller() {
		return seller;
	}

	public Entity getBook() {
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

	public void addToDatastore() {
		post.setProperty("seller", seller);
		post.setProperty("book", book);
		post.setProperty("price", price);
		post.setProperty("description", description);
		post.setProperty("date", date);
		post.setProperty("deadline", deadline);
		post.setProperty("status", status);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(post);
	}

	public void keyGen() {
		String keyString = book.getProperty("isbn") + ((User) (seller.getProperty("user"))).getEmail();
		key = KeyFactory.createKey("Post", keyString);
		post = new Entity("Post", key);
	}

	@Override
	public int compareTo(Post other) {
		if (key.equals(other.key)) {
			// posts are the same
			return 0;
		}
		// posts are not the same
		return -1;
	}
}
