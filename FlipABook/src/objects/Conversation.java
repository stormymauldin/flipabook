package objects;

import java.util.ArrayList;
import java.util.Date;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.User;

public class Conversation implements Comparable<Conversation> {

	Post post;
	// note: participant 0 is seller, participant 1 is buyer.
	FlipABookUser buyer;
	ArrayList<Message> messages;
	String title;
	String convoID;
	boolean meetingIsScheduled = false;
	Date scheduleDate;
	boolean transactionWasSuccessful = false;
	static final int POST_DELETED = 0;
	static final int BUYER_DELETED = 1;

	public Conversation() {
	}

	public Conversation(Post post, FlipABookUser buyer) {
		title = post.getTitle();
		this.post = post;
		this.buyer = buyer;
		messages = new ArrayList<Message>();
		// Please note, convoID is the postID + buyer + seller IN THAT ORDER
		convoID = post.getIsbn() + buyer.getUserInfo().getEmail() + post.getSeller().getEmail();
		Entity convo = new Entity("Conversation");
		convo.setProperty("convoID", convoID);
		convo.setProperty("buyer", buyer.getUserInfo());
		convo.setProperty("title", post.getTitle());
		convo.setProperty("seller", post.getSeller().getUserInfo());
		convo.setProperty("date", post.getDate());
		convo.setProperty("isbn", post.getIsbn());
		convo.setProperty("author", post.getAuthor());
		convo.setProperty("description", post.getDescription());
		convo.setProperty("price", post.getPrice());
		convo.setProperty("date", new Date());
		convo.setProperty("meetup", meetingIsScheduled);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(convo);
	}

	public Conversation(Post post, FlipABookUser buyer, boolean flag) {
		title = post.getTitle();
		this.post = post;
		this.buyer = buyer;
		messages = new ArrayList<Message>();
		// Please note, convoID is the postID + buyer + seller IN THAT ORDER
		convoID = post.getIsbn() + buyer.getUserInfo().getEmail() + post.getSeller().getEmail();

	}

	public String getConvoID() {
		return convoID;
	}

	public Post getPost() {
		return post;
	}

	public void newMessage(String content, User sender) {
		Message message = new Message(content, sender, this);
		messages.add(message);
		HomePage.messages.add(message);
	}

	public void scheduleMeeting() {
		meetingIsScheduled = true;
		scheduleDate = new Date();
		post.editStatus(Post.SUSPENDED);
	}

	public boolean meetingIsScheduled() {
		return meetingIsScheduled;
	}

	public Date getScheduledDate() {
		return scheduleDate;
	}

	public void transactionWasSuccessful() {
		transactionWasSuccessful = true;
	}

	public void transactionWasNotSuccesful() {
		meetingIsScheduled = false;
		post.editStatus(Post.ACTIVE);
	}

	public ArrayList<Message> getMessages() {
		return messages;
	}

	public FlipABookUser getBuyer() {
		return buyer;
	}

	@Override
	public int compareTo(Conversation o) {
		if (post.compareTo(o.getPost()) == 0 && buyer.compareTo(o.getBuyer()) == 0) {
			return 0;
		}
		return -1;
	}
}