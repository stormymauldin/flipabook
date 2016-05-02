package objects;

import java.util.Date;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.User;

public class Message implements Comparable<Message> {
	Long id;

	FlipABookUser sender;

	FlipABookUser recipient;
	String content;

	Date date; 
	Conversation conversation;
	boolean senderDeleted = false;
	boolean recipientDeleted = false;
	boolean read = false;

	public Message(String content, User sender, Conversation conversation) {
		date = new Date();
		this.content = content;
		this.conversation = conversation;
		this.sender = HomePage.getUser(sender);
		Entity message = new Entity("Message");
		message.setProperty("date", date);
		message.setProperty("content", content);
		message.setProperty("convoID", conversation.convoID);
		message.setProperty("sender", sender);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(message);
	}
	//Only use this constructor if the message already exists in the datastore
	public Message(String content, User sender, Conversation conversation, Date messageDate) {
		this.content = content;
		this.conversation = conversation;
		this.date = messageDate;
		this.sender = HomePage.getUser(sender);
	}
	
	public String getContent() {
		return content;
	}
	
	public void setRead() {
		read = true;
	}

	public boolean wasRead() {
		return read;
	}

	public FlipABookUser getSender() {
		return sender;
	}

	public FlipABookUser getRecipient() {
		return recipient;
	}

	public Conversation getConversation() {
		return conversation;
	}

	
	@Override
	public int compareTo(Message o) {
		return date.compareTo(o.date);
	}
}
