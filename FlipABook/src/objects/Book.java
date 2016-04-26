package objects;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class Book implements Comparable<Book> {
	Key key;
	Entity book;
	String title;
	String isbn;
	String author;
	ArrayList<String> tags = new ArrayList<String>();

	public Book() {
	}
	
	public Book(Entity entity){
		this.book = entity;
		key = entity.getKey();
		setPropertiesFromEntity();
		addToDatastore();
	}
	
	public Book(Key key){
		this.key = key;
		try {
			book = DatastoreServiceFactory.getDatastoreService().get(key);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		setPropertiesFromEntity();
		addToDatastore();
		//HomePage.books.add(this);
	}

	public Book(String title, String author, String isbn) {
		this.title = title;
		this.isbn = isbn;
		this.author = author;
		generateTags();
		keyGen();
		addToDatastore();
		HomePage.books.add(this);
	}

	private void generateTags() {
		String[] titleTags = title.split(" ");
		String[] authorTags = title.split(" ");
		tags.addAll(Arrays.asList(titleTags));
		tags.addAll(Arrays.asList(authorTags));
	}

	public void setPropertiesFromEntity(){
		title = (String) book.getProperty("title");
		isbn = (String) book.getProperty("isbn");
		author = (String) book.getProperty("author");
		tags = (ArrayList<String>) book.getProperty("tags");
	}
	
	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}

	public String getIsbn() {
		return isbn;
	}

	public ArrayList<String> getTags() {
		return tags;
	}

	public void addTag(String tag) {
		tags.add(tag);
		addToDatastore();
	}
	
	public void addToDatastore(){
		book.setProperty("title", title);
		book.setProperty("isbn", isbn);
		book.setProperty("author", author);
		book.setProperty("tags", tags);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(book);
	}
	
	public void keyGen(){
		String keyString = isbn;
		key = KeyFactory.createKey("Book", keyString);
		book = new Entity("Book", key);
	}
	

	@Override
	public int compareTo(Book other) {
		if (key.equals(other.key)) {
			// books are the same
			return 0;
		}
		// posts are not the same
		return 1;
	}
}
