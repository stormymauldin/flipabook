package objects;

import java.util.ArrayList;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Book implements Comparable<Book> {
	@Id
	Long id;
	String title;
	String isbn;
	ArrayList<String> tags;
	
	public Book(){}

	public Book(String title, String isbn) {
		this.title = title;
		this.isbn = isbn;
		tags = new ArrayList<String>();
	}

	public Book(String title, String isbn, String tag) {
		this(title, isbn);
		tags.add(tag);
	}

	public Book(String title, String isbn, ArrayList<String> tags) {
		this(title, isbn);
		this.tags = tags;
	}

	public String getTitle() {
		return title;
	}

	public String getIsbn() {
		return isbn;
	}

	public ArrayList<String> getTags() {
		return tags;
	}

	public void addTag(String tag) {
		tags.add(tag);
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
