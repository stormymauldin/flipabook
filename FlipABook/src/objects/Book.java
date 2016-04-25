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
	String title;
	String isbn;
	String author;
	ArrayList<String> tags = new ArrayList<String>();

	public Book() {
	}
	
	public Book(Key key){
		this.key = key;
		Entity entity = null;
		try {
			entity = DatastoreServiceFactory.getDatastoreService().get(key);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		setPropertiesFromEntity(entity);
		HomePage.books.add(this);
	}

	public Book(String title, String author, String isbn) {
		this.title = title;
		this.isbn = isbn;
		this.author = author;
		generateTags();
		keyGen();
		addToDatastore();
	}

	private void generateTags() {
		String[] titleTags = title.split(" ");
		String[] authorTags = title.split(" ");
		tags.addAll(Arrays.asList(titleTags));
		tags.addAll(Arrays.asList(authorTags));
	}

	public void setPropertiesFromEntity(Entity entity){
		title = (String) entity.getProperty("title");
		isbn = (String) entity.getProperty("isbn");
		author = (String) entity.getProperty("author");
		tags = (ArrayList<String>) entity.getProperty("tags");
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
		Entity post_datastore = new Entity("Book", key);
		post_datastore.setProperty("title", title);
		post_datastore.setProperty("isbn", isbn);
		post_datastore.setProperty("author", author);
		post_datastore.setProperty("tags", tags);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(post_datastore);
	}
	
	public void keyGen(){
		String keyString = isbn;
		key = KeyFactory.createKey("Book", keyString);
	}
	

	@Override
	public int compareTo(Book other) {
		if (isbn.equals(other.getIsbn())) {
			// books are the same
			return 0;
		}
		// posts are not the same
		return 1;
	}
}