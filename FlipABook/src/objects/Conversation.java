package objects;

import java.util.ArrayList;
import java.util.Date;

import com.google.appengine.api.datastore.*;

public class Conversation implements Comparable<Conversation> {
	Key key;
	Post post;
	// note: participant 0 is seller, participant 1 is buyer.
	FlipABookUser buyer;
	ArrayList<Message> messages;
	boolean meetingIsScheduled = false;
	Date scheduleDate;
	boolean transactionWasSuccessful = false;
	static final int POST_DELETED = 0;
	static final int BUYER_DELETED = 1;

	public Conversation(){
	}
	
	public Conversation (Key key){
		this.key = key;
		Entity entity = null;
		try {
			entity = DatastoreServiceFactory.getDatastoreService().get(key);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		setPropertiesFromEntity(entity);
		HomePage.conversations.add(this);
	}

	public Conversation(Post post, FlipABookUser buyer) {
		this.post = post;
		this.buyer = buyer;
		messages = new ArrayList<Message>();
		keyGen();
		addToDatastore();
	}

	public Post getPost() {
		return post;
	}
	
	public void setPropertiesFromEntity(Entity entity){
		post = (Post) entity.getProperty("post");
		buyer = (FlipABookUser) entity.getProperty("buyer");
		messages = (ArrayList<Message>) entity.getProperty("messages");
		meetingIsScheduled = (boolean) entity.getProperty("meetingIsScheduled");
		scheduleDate = (Date) entity.getProperty("scheduleDate");
		transactionWasSuccessful = (boolean) entity.getProperty("transactionWasSuccessful");
	}

	public void newMessage(int direction, String content) {
		messages.add(new Message(direction, content, this));
		addToDatastore();
	}

	public void scheduleMeeting() {
		meetingIsScheduled = true;
		scheduleDate = new Date();
		post.editStatus(Post.SUSPENDED);
		addToDatastore();
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
		addToDatastore();
	}

	public void transactionWasNotSuccesful() {
		meetingIsScheduled = false;
		post.editStatus(Post.ACTIVE);
		addToDatastore();
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
		addToDatastore();
	}
	
	public void addToDatastore(){		
		Entity post_datastore = new Entity("Conversation", key);		
		post_datastore.setProperty("post", post);
		post_datastore.setProperty("buyer", buyer);
		post_datastore.setProperty("messages", messages);
		post_datastore.setProperty("meetingIsScheduled", meetingIsScheduled);
		post_datastore.setProperty("scheduleDate", scheduleDate);
		post_datastore.setProperty("transactionWasSuccessful", transactionWasSuccessful);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(post_datastore);
	}
	
	public void keyGen(){
		String keyString = post.key.toString() + buyer.getEmail();
		key = KeyFactory.createKey("Conversation", keyString);
	}
}