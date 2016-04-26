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
	Entity message;
	Entity sender;
	Entity recipient;
	String content;
	Entity conversation;
	boolean senderDeleted = false;
	boolean recipientDeleted = false;
	boolean read = false;

	public Message(Entity entity) {
		message = entity;
		key = entity.getKey();
		setPropertiesFromEntity();
		addToDatastore();
	}

	public Message(Key key) {
		this.key = key;
		try {
			message = DatastoreServiceFactory.getDatastoreService().get(key);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		setPropertiesFromEntity();
		addToDatastore();
	}

	public Message(int direction, String content, Conversation conversation) {
		if (direction == SELLER_TO_BUYER) {
			sender = (Entity) conversation.getPost().getProperty("seller");
			recipient = conversation.getBuyer();
		} else {
			sender = conversation.getBuyer();
			recipient = (Entity) conversation.getPost().getProperty("seller");
		}

		this.content = content;
		this.conversation = conversation.conversation;
		registerObservers(getFlipABookUser(sender), getFlipABookUser(recipient));
		notifyObservers(Observer.NEW_MESSAGE);
		HomePage.messages.add(this);
		keyGen();
		addToDatastore();
	}

	public void setPropertiesFromEntity() {
		sender = (Entity) message.getProperty("sender");
		recipient = (Entity) message.getProperty("recipient");
		content = (String) message.getProperty("content");
		conversation = (Entity) message.getProperty("conversation");
		senderDeleted = (boolean) message.getProperty("senderDeleted");
		recipientDeleted = (boolean) message.getProperty("recipientDeleted");
		read = (boolean) message.getProperty("read");
	}

	public void setRead() {
		read = true;
		addToDatastore();
	}

	public boolean wasRead() {
		return read;
	}

	public Entity getSender() {
		return sender;
	}

	public Entity getRecipient() {
		return recipient;
	}

	public Entity getConversation() {
		return conversation;
	}

	public void addToDatastore() {
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

	public void keyGen() {
		String keyString = new BigInteger(130, new SecureRandom()).toString(32);
		key = KeyFactory.createKey("Message", keyString);
	}

	// this implementation is called to establish the sender and recipient
	@Override
	public void registerObservers(Observer o0, Observer o1) {
		this.sender = ((FlipABookUser) o0).flipABookUser;
		this.recipient = ((FlipABookUser) o1).flipABookUser;
	}

	// this implementation is called when a user deletes a message
	@Override
	public void removeObserver(Observer o) {
		FlipABookUser toBeRemoved = (FlipABookUser) o;
		FlipABookUser senderO = getFlipABookUser(sender);
		FlipABookUser recipientO = getFlipABookUser(recipient);
		if (senderO.compareTo(toBeRemoved) == 0) {
			senderO.update(this, Observer.DELETE);
			senderDeleted = true;
		} else {
			recipientO.update(this, Observer.DELETE);
			recipientDeleted = true;
		}
		addToDatastore();
	}

	// this implementation is called to update the participants
	@Override
	public void notifyObservers(int updateType) {
		FlipABookUser senderO = getFlipABookUser(sender);
		FlipABookUser recipientO = getFlipABookUser(recipient);
		if (!senderDeleted) {
			senderO.update(this, updateType);
		}
		if (!recipientDeleted) {
			recipientO.update(this, updateType);
		}
		addToDatastore();
	}

	public FlipABookUser getFlipABookUser(Entity flipABookUser) {
		for (FlipABookUser curFlipABookUser : HomePage.flipABookUsers) {
			if (curFlipABookUser.flipABookUser.equals(flipABookUser)) {
				return curFlipABookUser;
			}
		}
		return null;
	}
}
