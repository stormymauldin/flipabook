package objects;

import java.util.Date;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Post implements Comparable<Post> {
	@Id
	Long id;
	String title;
	String description;
	FlipABookUser seller;
	Book book;
	double price;
	Date date;
	Date deadline;
	int status;
	public static final int ACTIVE = 0;
	public static final int SUSPENDED = 1;
	private static final long DAY_IN_MS = 1000 * 60 * 60 * 24;
	private static final long TWO_WEEKS = 14 * DAY_IN_MS;
	
	public Post(){}

	public Post(FlipABookUser seller, Book book, double price, String title, String description) {
		this.seller = seller;
		this.book = book;
		this.price = price;
		this.title = title;
		this.description = description;
		date = new Date();
		deadline = new Date(date.getTime() + TWO_WEEKS);
		status = ACTIVE;
	}

	public String getTitle() {
		return title;
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

	public double getPrice() {
		return price;
	}

	public Date getDate() {
		return date;
	}

	public int getStatus() {
		return status;
	}

	public void editTitle(String newTitle) {
		title = newTitle;
	}

	public void editDescription(String newDescription) {
		description = newDescription;
	}

	public void editPrice(double newPrice) {
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
