package objects;

import java.util.ArrayList;
import java.util.Date;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;

public class FlipABookUser implements Comparable<FlipABookUser>, Observer {
	Key key;
	User user;
	public Entity flipABookUser;
	public boolean validEmail;
	ArrayList<Entity> posts;
	ArrayList<Entity> conversations;
	ArrayList<Entity> unreadMessages;
	ArrayList<Entity> sentMessagesNotRead;
	boolean repeatPostAttempt = false;
	boolean wrongPrice = false;
	boolean nullFields = false;
	boolean wrongIsbn = false;
	boolean conductingSearch = false;

	public FlipABookUser() {
	}

	public FlipABookUser(Entity entity) {
		this.flipABookUser = entity;
		this.key = entity.getKey();
		setPropertiesFromEntity();
		addToDatastore();
		verifyEmail();
	}

	public FlipABookUser(Key key) {
		this.key = key;
		try {
			flipABookUser = DatastoreServiceFactory.getDatastoreService().get(key);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		setPropertiesFromEntity();
		addToDatastore();
		verifyEmail();
	}

	public FlipABookUser(User user) {
		this.user = user;
		posts = new ArrayList<Entity>();
		conversations = new ArrayList<Entity>();
		unreadMessages = new ArrayList<Entity>();
		sentMessagesNotRead = new ArrayList<Entity>();

		keyGen();
		addToDatastore();
		verifyEmail();
		HomePage.users.add(user);
		HomePage.flipABookUsers.add(this);
	}

	public void setPropertiesFromEntity() {
		user = (User) flipABookUser.getProperty("user");
		posts = (ArrayList<Entity>) flipABookUser.getProperty("posts");
		conversations = (ArrayList<Entity>) flipABookUser.getProperty("conversations");
		unreadMessages = (ArrayList<Entity>) flipABookUser.getProperty("unreadMessages");
		sentMessagesNotRead = (ArrayList<Entity>) flipABookUser.getProperty("sentMessagesNotRead");
		repeatPostAttempt = (boolean) flipABookUser.getProperty("repeatPostAttempt");
		wrongPrice = (boolean) flipABookUser.getProperty("wrongPrice");
		nullFields = (boolean) flipABookUser.getProperty("nullFields");
		wrongIsbn = (boolean) flipABookUser.getProperty("wrongIsbn");
		conductingSearch = (boolean) flipABookUser.getProperty("conductingSearch");
	}

	public User getUserInfo() {
		return user;
	}

	public void addPost(Entity post) {
		posts.add(post);
	}

	public String getEmail() {
		return user.getEmail();
	}

	public ArrayList<Entity> getPosts() {
		return posts;
	}

	public ArrayList<Entity> getConversations() {
		return conversations;
	}

	public void readMessage(Message message) {
		int index = unreadMessages.indexOf(message);
		if (index != -1) {
			unreadMessages.remove(index);
		}
		message.setRead();
		addToDatastore();
	}

	public boolean repeatPostAttempt() {
		return repeatPostAttempt;
	}

	public void setRepeatPostAttempt() {
		repeatPostAttempt = true;
		addToDatastore();
	}

	public void removeRepeatPostAttempt() {
		repeatPostAttempt = false;
		addToDatastore();
	}

	public boolean wrongPrice() {
		return wrongPrice;
	}

	public void setWrongPrice() {
		wrongPrice = true;
		addToDatastore();
	}

	public void removeWrongPrice() {
		wrongPrice = false;
		addToDatastore();
	}

	public boolean nullFields() {
		return nullFields;
	}

	public void setNullFields() {
		nullFields = true;
		addToDatastore();
	}

	public void removeNullFields() {
		nullFields = false;
		addToDatastore();
	}

	public boolean wrongIsbn() {
		return wrongIsbn;
	}

	public void setWrongIsbn() {
		wrongIsbn = true;
		addToDatastore();
	}

	public void removeWrongIsbn() {
		wrongIsbn = false;
		addToDatastore();
	}

	public boolean conductingSearch() {
		return conductingSearch;
	}

	public void setConductingSearch() {
		conductingSearch = true;
		addToDatastore();
	}

	public void removeConductingSearch() {
		conductingSearch = false;
		addToDatastore();
	}

	private int getConversationIndex(Entity post, Entity buyer) {
		String keyString = post.getKey().toString() + ((User) (buyer.getProperty("user"))).getEmail();
		Key aKey = KeyFactory.createKey("Conversation", keyString);
		for (Entity conversation : conversations) {
			if (conversation.getKey().equals(aKey)) {
				return conversations.indexOf(conversation);
			}
		}
		return -1;

	}

	public void sendMessage(int buyingOrSelling, Entity post, Entity buyer, String content) {
		// TODO Update so it works with entities

		Conversation curConversation = null;
		int indexOfConversation;
		int direction;
		if (buyingOrSelling == Observer.BUYING) {
			direction = Subject.BUYER_TO_SELLER;
			int index = getConversationIndex(post, buyer);
			indexOfConversation = getConversationIndex(post, flipABookUser);
			if (indexOfConversation == -1) {
				indexOfConversation = conversations.size();
				curConversation = new Conversation(post, buyer);
				conversations.add(curConversation.conversation);
			}
		} else {
			direction = Subject.SELLER_TO_BUYER;
			indexOfConversation = getConversationIndex(post, buyer);
			for (Conversation convo : HomePage.conversations) {
				if (convo.conversation.equals(conversations.get(indexOfConversation))) {
					curConversation = convo;
				}
			}
		}
		curConversation.newMessage(direction, content);
		int messageIndex = curConversation.getMessages().size() - 1;
		sentMessagesNotRead.add(curConversation.getMessages().get(messageIndex));
		addToDatastore(); // Note: creating the message automatically adds both
							// parties as observers

	}

	// Since the conversation object is shared by both parties, the message
	// isn't actually deleted, it just stops being observed by one party
	public void deleteMessage(Message message) {
		message.removeObserver(this);
		addToDatastore();
	}

	// This implementation of updates either i) adds a new message to the list
	// of the user's unread messages and adds a new conversation if needed, or
	// ii) notifies the user that a previously sent message has been read.
	@Override
	public void update(Message message, int updateType) {
		// TODO update so that it works with entities
		/*
		 * if (updateType == Observer.NEW_MESSAGE) { if
		 * (!conversations.contains(message.getConversation())) {
		 * conversations.add(message.getConversation()); }
		 * unreadMessages.add(message); } else if (updateType == Observer.READ)
		 * { int index = sentMessagesNotRead.indexOf(message); if (index != -1)
		 * { sentMessagesNotRead.remove(index); } } addToDatastore();
		 */
	}

	@Override
	public int compareTo(FlipABookUser o) {
		if (key.equals(o.key)) {
			return 0;
		}
		return 1;
	}

	public void addToDatastore() {
		flipABookUser.setProperty("user", user);
		flipABookUser.setProperty("validEmail", validEmail);
		flipABookUser.setProperty("posts", posts);
		flipABookUser.setProperty("conversations", conversations);
		flipABookUser.setProperty("unreadMessages", unreadMessages);
		flipABookUser.setProperty("sentMessagesNotRead", sentMessagesNotRead);
		flipABookUser.setProperty("repeatPostAttempt", repeatPostAttempt);
		flipABookUser.setProperty("wrongPrice", wrongPrice);
		flipABookUser.setProperty("nullFields", nullFields);
		flipABookUser.setProperty("wrongIsbn", wrongIsbn);
		flipABookUser.setProperty("conductingSearch", conductingSearch);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(flipABookUser);
	}

	public void keyGen() {
		String keyString = user.getEmail();
		key = KeyFactory.createKey("FlipABookUser", keyString);
		flipABookUser = new Entity("FlipABookUser", key);
	}

	public void verifyEmail() {
		String[] parsedEmail = user.getEmail().split("@");
		if (parsedEmail.length != 2 || !parsedEmail[1].equals("utexas.edu")) {
			validEmail = false;
		} else {
			validEmail = true;
		}
	}
}
