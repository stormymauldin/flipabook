package objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

import com.googlecode.objectify.annotation.*;

@Serialize
public class Conversation implements Comparable<Conversation>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -961566006389798376L;
	@Id
	Long id;
//	@Container
	Post post;
	// note: participant 0 is seller, participant 1 is buyer.
//	@Container
	FlipABookUser buyer;
//	@Container
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
		//Please note, convoID is the postID + buyer + seller IN THAT ORDER
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
		//Please note, convoID is the postID + buyer + seller IN THAT ORDER
		convoID = post.getIsbn() + buyer.getUserInfo().getEmail() + post.getSeller().getEmail(); 
		
	}
	
	public String getConvoID(){
		return convoID;
	}
	
	public Post getPost() {
		return post;
	}

	public void newMessage(String content) {
		messages.add(new Message(content, this));
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
		// TODO: call function to delete post
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

	public void deleteConversation(int deletionType) {
		if (deletionType == POST_DELETED) {
			for (Message message : messages) {
				message.removeObserver(message.getSender());
				message.removeObserver(message.getRecipient());
			}
		} else {
			for (Message message : messages) {
				if (buyer.compareTo(message.getSender()) == 0) {
					message.removeObserver(message.getSender());
				} else {
					message.removeObserver(message.getRecipient());
				}
			}
		}
	}
}