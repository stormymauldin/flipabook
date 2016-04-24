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
	ArrayList<Post> posts;
	ArrayList<Conversation> conversations;
	ArrayList<Message> unreadMessages;
	ArrayList<Message> sentMessagesNotRead;
	boolean repeatPostAttempt = false;
	boolean wrongPrice = false;
	boolean nullFields = false;
	boolean wrongIsbn = false;
	boolean conductingSearch = false;

	public FlipABookUser() {
	}
	
	public FlipABookUser(Key key){
		this.key = key;
		Entity entity = null;
		try {
			entity = DatastoreServiceFactory.getDatastoreService().get(key);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		setPropertiesFromEntity(entity);
		HomePage.users.add(user);
		HomePage.flipABookUsers.add(this);
	}

	public FlipABookUser(User user) {
		this.user = user;
		posts = new ArrayList<Post>();
		conversations = new ArrayList<Conversation>();
		unreadMessages = new ArrayList<Message>();
		sentMessagesNotRead = new ArrayList<Message>();
		HomePage.flipABookUsers.add(this);
		keyGen();
		addToDatastore();
	}
	
	public void setPropertiesFromEntity(Entity entity){				
		user = (User) entity.getProperty("user");
		posts = (ArrayList<Post>) entity.getProperty("posts");
		conversations = (ArrayList<Conversation>) entity.getProperty("conversations");
		unreadMessages = (ArrayList<Message>) entity.getProperty("unreadMessages");
		sentMessagesNotRead = (ArrayList<Message>) entity.getProperty("sentMessagesNotRead");
		repeatPostAttempt = (boolean) entity.getProperty("repeatPostAttempt");
		wrongPrice = (boolean) entity.getProperty("wrongPrice");
		nullFields = (boolean) entity.getProperty("nullFields");
		wrongIsbn = (boolean) entity.getProperty("wrongIsbn");
		conductingSearch = (boolean) entity.getProperty("conductingSearch");
	}

	public User getUserInfo() {
		return user;
	}

	public String getEmail() {
		return user.getEmail();
	}

	public ArrayList<Post> getPosts() {
		return posts;
	}

	public ArrayList<Conversation> getConversations() {
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

	private int getConversationIndex(Post post, FlipABookUser buyer) {
		Conversation curConversation = new Conversation(post, buyer);
		for (Conversation conversation : conversations) {
			if (conversation.compareTo(curConversation) == 0) {
				return conversations.indexOf(conversation);
			}
		}
		return -1;
	}

	public void sendMessage(int buyingOrSelling, Post post, FlipABookUser buyer, String content) {
		Conversation curConversation = null;
		int indexOfConversation;
		int direction;
		if (buyingOrSelling == Observer.BUYING) {
			direction = Subject.BUYER_TO_SELLER;
			indexOfConversation = getConversationIndex(post, this);
			if (indexOfConversation == -1) {
				indexOfConversation = conversations.size();
				curConversation = new Conversation(post, this);
				conversations.add(curConversation);
			}
		} else {
			direction = Subject.SELLER_TO_BUYER;
			indexOfConversation = getConversationIndex(post, buyer);
			curConversation = conversations.get(indexOfConversation);
		}
		curConversation.newMessage(direction, content);
		int messageIndex = curConversation.getMessages().size() - 1;
		sentMessagesNotRead.add(curConversation.getMessages().get(messageIndex));
		addToDatastore();
		// Note: creating the message automatically adds both parties as
		// observers
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
		if (updateType == Observer.NEW_MESSAGE) {
			if (!conversations.contains(message.getConversation())) {
				conversations.add(message.getConversation());
			}
			unreadMessages.add(message);
		} else if (updateType == Observer.READ) {
			int index = sentMessagesNotRead.indexOf(message);
			if (index != -1) {
				sentMessagesNotRead.remove(index);
			}
		}
		addToDatastore();
	}

	@Override
	public int compareTo(FlipABookUser o) {
		if (user.equals(o.getUserInfo())) {
			return 0;
		}
		return 1;
	}
	
	public void addToDatastore(){		
		Entity post_datastore = new Entity("FlipABookUser", key);
		post_datastore.setProperty("user", user);
		post_datastore.setProperty("posts", posts);
		post_datastore.setProperty("conversations", conversations);
		post_datastore.setProperty("unreadMessages", unreadMessages);
		post_datastore.setProperty("sentMessagesNotRead", sentMessagesNotRead);
		post_datastore.setProperty("repeatPostAttempt", repeatPostAttempt);
		post_datastore.setProperty("wrongPrice", wrongPrice);
		post_datastore.setProperty("nullFields", nullFields);
		post_datastore.setProperty("wrongIsbn", wrongIsbn);
		post_datastore.setProperty("conductingSearch", conductingSearch);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(post_datastore);
	}
	
	public void keyGen(){
		String keyString = user.getEmail();
		key = KeyFactory.createKey("FlipABookUser", keyString);
	}
}
