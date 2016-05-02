package objects;

import java.util.ArrayList;
import java.util.Arrays;

public class Book implements Comparable<Book> {

	String title;
	String isbn;
	String author;
	ArrayList<String> tags = new ArrayList<String>();

	public Book() {
	}

	public Book(String title, String author, String isbn) {
		this.title = title;
		this.isbn = isbn;
		this.author = author;
		generateTags();
	}

	private void generateTags() {
		String[] titleTags = title.split(" ");
		String[] authorTags = title.split(" ");
		tags.addAll(Arrays.asList(titleTags));
		tags.addAll(Arrays.asList(authorTags));
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
