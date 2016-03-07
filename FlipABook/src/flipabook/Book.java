package flipabook;

import java.util.ArrayList;

public class Book implements Comparable<Book>{
	private String title;
	private String isbn;
	private ArrayList<String> tags;
	
	public Book(String title, String isbn){
		this.title = title;
		this.isbn = isbn;
		tags = new ArrayList<String>();
	}
	
	public Book(String title, String isbn, ArrayList<String> tags){
		this(title, isbn);
		this.tags = tags;
	}
	
	public void addTag(String tag){
		tags.add(tag);
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getIsbn(){
		return isbn;
	}
	
	public ArrayList<String> getTag(){
		return tags;
	}

	@Override
	public int compareTo(Book other) {
		if (isbn.equals(other.getIsbn())) {
			return 0;
		} else {
			return 1;
		}
	}
}
