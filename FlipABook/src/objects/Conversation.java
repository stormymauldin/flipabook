package objects;

import java.util.ArrayList;
import java.util.Date;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.User;

public class Conversation implements Comparable<Conversation> {
	Key key;
	Entity conversation;
	Entity post;
	// note: participant 0 is seller, participant 1 is buyer.
	Entity buyer;
	ArrayList<Entity> messages;
	boolean meetingIsScheduled = false;
	Date scheduleDate;
	boolean transactionWasSuccessful = false;
	static final int POST_DELETED = 0;
	static final int BUYER_DELETED = 1;

	public Conversation(){
	}
	
	public Conversation(Entity entity){
		conversation = entity;
		key = conversation.getKey();
		setPropertiesFromEntity();
		addToDatastore();
		
	}
	
	public Conversation (Key key){
		this.key = key;
		try {
			conversation = DatastoreServiceFactory.getDatastoreService().get(key);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		setPropertiesFromEntity();
		//HomePage.conversations.add(this);
	}

	public Conversation(Entity post, Entity buyer) {
		this.post = post;
		this.buyer = buyer;
		messages = new ArrayList<Entity>();
		keyGen();
		addToDatastore();
		HomePage.conversations.add(this);
	}

	public Entity getPost() {
		return post;
	}
	
	public void setPropertiesFromEntity(){
		post = (Entity) conversation.getProperty("post");
		buyer = (Entity) conversation.getProperty("buyer");
		messages = (ArrayList<Entity>) conversation.getProperty("messages");
		meetingIsScheduled = (boolean) conversation.getProperty("meetingIsScheduled");
		scheduleDate = (Date) conversation.getProperty("scheduleDate");
		transactionWasSuccessful = (boolean) conversation.getProperty("transactionWasSuccessful");
	}

	public void newMessage(int direction, String content) {
		Message message = new Message(direction, content, this);
		messages.add(message.message);
		addToDatastore();
	}

	public void scheduleMeeting() {
		meetingIsScheduled = true;
		scheduleDate = new Date();
		post.setProperty("status", Post.SUSPENDED);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(post);
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
		post.setProperty("status", Post.ACTIVE);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(post);
		addToDatastore();
	}

	public ArrayList<Entity> getMessages() {
		return messages;
	}

	public Entity getBuyer() {
		return buyer;
	}

	@Override
	public int compareTo(Conversation o) {
		if (key.equals(o.key)) {
			return 0;
		}
		return -1;
	}

	public void deleteConversation(int deletionType) {
		//TODO update this function to work with entities
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		if (deletionType == POST_DELETED) {
			for (Entity message : messages) {
				Message curMessage = getMessage(message);
				Entity senderEntity = curMessage.getSender();
				Entity recipientEntity = curMessage.getRecipient();
				FlipABookUser sender = curMessage.getFlipABookUser(senderEntity);
				FlipABookUser recipient = curMessage.getFlipABookUser(recipientEntity);
				curMessage.removeObserver(sender);
				curMessage.removeObserver(recipient);
				datastore.delete(message.getKey());
				int i = HomePage.messages.indexOf(curMessage);
				HomePage.messages.remove(i);
			}
		} else {
			for (Entity message : messages) {
				Message curMessage = getMessage(message);
				Entity senderEntity = curMessage.getSender();
				Entity recipientEntity = curMessage.getRecipient();
				FlipABookUser sender = curMessage.getFlipABookUser(senderEntity);
				FlipABookUser recipient = curMessage.getFlipABookUser(recipientEntity);
				if (buyer.equals(senderEntity)) {
					curMessage.removeObserver(sender);
				} else {
					curMessage.removeObserver(recipient);
				}
			}
		}
		addToDatastore();
	}
	
	public void addToDatastore(){		
		conversation.setProperty("post", post);
		conversation.setProperty("buyer", buyer);
		conversation.setProperty("messages", messages);
		conversation.setProperty("meetingIsScheduled", meetingIsScheduled);
		conversation.setProperty("scheduleDate", scheduleDate);
		conversation.setProperty("transactionWasSuccessful", transactionWasSuccessful);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(conversation);
	}
	
	public void keyGen(){
		String keyString = post.getKey().toString() + ((User)(buyer.getProperty("user"))).getEmail();
		key = KeyFactory.createKey("Conversation", keyString);
		conversation = new Entity("Conversation", key);
	}
	
	public Message getMessage(Entity message){
		for (Message curMessage : HomePage.messages) {
			if (curMessage.message.equals(message)) {
				return curMessage;
			}
		}
		return null;
	}
}