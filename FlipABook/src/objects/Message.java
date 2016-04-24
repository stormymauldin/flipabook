package objects;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;

public class Message implements Subject {
	Key key;
	FlipABookUser sender;
	FlipABookUser recipient;
	String content;
	Conversation conversation;
	boolean senderDeleted = false;
	boolean recipientDeleted = false;
	boolean read = false;
	
	public Message(Key key){
		this.key = key;
		Entity entity = null;
		try {
			entity = DatastoreServiceFactory.getDatastoreService().get(key);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		setPropertiesFromEntity(entity);
		HomePage.messages.add(this);
	}

	public Message(int direction, String content, Conversation conversation) {
		if (direction == SELLER_TO_BUYER) {
			sender = conversation.getPost().getSeller();
			recipient = conversation.getBuyer();
		} else {
			sender = conversation.getBuyer();
			recipient = conversation.getPost().getSeller();
		}

		this.content = content;
		this.conversation = conversation;
		registerObservers(sender, recipient);
		notifyObservers(Observer.NEW_MESSAGE);
		keyGen();
		addToDatastore();
	}
	
	public void setPropertiesFromEntity(Entity entity){		
		sender = (FlipABookUser) entity.getProperty("sender");
		recipient = (FlipABookUser) entity.getProperty("recipient");
		content = (String) entity.getProperty("content");
		conversation = (Conversation) entity.getProperty("conversation");
		senderDeleted = (boolean) entity.getProperty("senderDeleted");
		recipientDeleted = (boolean) entity.getProperty("recipientDeleted");
		read = (boolean) entity.getProperty("read");
	}

	public void setRead() {
		read = true;
		addToDatastore();
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
	
	public void addToDatastore(){
		Entity post_datastore = new Entity("Message", key);
		post_datastore.setProperty("sender", sender);
		post_datastore.setProperty("content", content);
		post_datastore.setProperty("content", content);
		post_datastore.setProperty("conversation", conversation);
		post_datastore.setProperty("senderDeleted", senderDeleted);
		post_datastore.setProperty("recipientDeleted", recipientDeleted);
		post_datastore.setProperty("read", read);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(post_datastore);
	}
	
	public void keyGen(){
		String keyString = new BigInteger(130, new SecureRandom()).toString(32);
		key = KeyFactory.createKey("Message", keyString);
	}

	// this implementation is called to establish the sender and recipient
	@Override
	public void registerObservers(Observer o0, Observer o1) {
		this.sender = (FlipABookUser) o0;
		this.recipient = (FlipABookUser) o1;
	}

	// this implementation is called when a user deletes a message
	@Override
	public void removeObserver(Observer o) {
		FlipABookUser toBeRemoved = (FlipABookUser) o;
		if (sender.compareTo(toBeRemoved) == 0) {
			sender.update(this, Observer.DELETE);
			senderDeleted = true;
		} else {
			recipient.update(this, Observer.DELETE);
			recipientDeleted = true;
		}
	}

	// this implementation is called to update the participants
	@Override
	public void notifyObservers(int updateType) {
		if (!senderDeleted) {
			sender.update(this, updateType);
		}
		if (!recipientDeleted) {
			recipient.update(this, updateType);
		}
	}
}
