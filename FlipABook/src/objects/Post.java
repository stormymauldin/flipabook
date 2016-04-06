package objects;

import java.util.Date;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Post implements Comparable<Post> {
	@Id
	Long id;
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

	public Post(FlipABookUser seller, String title, String author, String isbn, String price, String description) {
		this.seller = seller;
		getBook(isbn, title, author);
		this.price = price;
		this.description = description;
		date = new Date();
		deadline = new Date(date.getTime() + TWO_WEEKS);
		status = ACTIVE;
	}
	
	private void getBook(String title, String author, String isbn){
		for(int i = 0; i < HomePage.books.size(); i++){
			if(isbn.equals(HomePage.books.get(i).getIsbn())){
				book = HomePage.books.get(i);
				return;
			}
		}
		book = new Book(title, author, isbn);
		HomePage.books.add(book);
	}

	public String getTitle() {
		return book.getTitle();
	}
	
	public String getIsbn(){
		return book.getIsbn();
	}
	
	public String getAuthor(){
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
	}

	public void editPrice(String newPrice) {
		price = newPrice;
	}

	public void editStatus(int newStatus) {
		if (newStatus == ACTIVE || newStatus == SUSPENDED) {
			status = newStatus;
		}
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
