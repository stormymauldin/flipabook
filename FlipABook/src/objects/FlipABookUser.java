package objects;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
//import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;

public class FlipABookUser implements Comparable<FlipABookUser>, Observer {

	User user;
	ArrayList<Post> posts;
	ArrayList<Conversation> conversations;
	ArrayList<Message> unreadMessages;
	ArrayList<Message> sentMessagesNotRead;
	int totalSales;
	int totalPosts;
	boolean repeatPostAttempt = false;
	boolean wrongPrice = false;
	boolean nullFields = false;
	boolean wrongIsbn = false;
	boolean conductingSearch = false;

	public FlipABookUser() {
	}

	public FlipABookUser(User user) {
		totalSales = 0;
		totalPosts = 0;
		this.user = user;
		posts = new ArrayList<Post>();
		conversations = new ArrayList<Conversation>();
		unreadMessages = new ArrayList<Message>();
		sentMessagesNotRead = new ArrayList<Message>();

		boolean exists = false;
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		// Entity user_datastore = new Entity("User");
		// Query user_query = new Query("User").addSort("name",
		// Query.SortDirection.DESCENDING);
		// List<Entity> users =
		// datastore.prepare(user_query).asList(FetchOptions.Builder.withLimit(1000));
		// for (Entity datastore_user: users) {
		// User next_user = (User)datastore_user.getProperty("user");
		// if (next_user.equals(user)){
		// exists = true;
		// break;
		// }
		// }
		// if (!exists){
		// user_datastore.setProperty("user", user);
		// user_datastore.setProperty("name", user.getNickname());
		// user_datastore.setProperty("totalposts", totalPosts);
		// datastore.put(user_datastore);
		// System.out.println("Added user to datastore: " + user.getEmail());
		// }
	}

	public User getUserInfo() {
		return user;
	}

	public String getEmail() {
		return user.getEmail();
	}

	public void addPost(Post addedPost) {
		posts.add(addedPost);
		totalPosts++;
		// DatastoreService datastore =
		// DatastoreServiceFactory.getDatastoreService();
		// Query user_query = new Query("User").addSort("name",
		// Query.SortDirection.DESCENDING);
		// List<Entity> users =
		// datastore.prepare(user_query).asList(FetchOptions.Builder.withLimit(1000));
		// Entity temp = null;
		// for (Entity datastore_user: users) {
		// User next_user = (User)datastore_user.getProperty("user");
		// if (next_user.equals(user)){
		// datastore_user.setProperty("totalposts", totalPosts); //Temporarily
		// not this (allows whole datastore to be re-init
		// datastore_user.setProperty("totalposts", posts.size());
		// datastore.delete(datastore_user.getKey());
		// datastore.put(datastore_user);
		// break;
		// }
		// }

	}

	public int getNumCurrentPosts() {
		return posts.size();

	}

	public int getNumTotalPosts() {
		return totalPosts;
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
	}

	public boolean repeatPostAttempt() {
		return repeatPostAttempt;
	}

	public void setRepeatPostAttempt() {
		repeatPostAttempt = true;
	}

	public void removeRepeatPostAttempt() {
		repeatPostAttempt = false;
	}

	public boolean wrongPrice() {
		return wrongPrice;
	}

	public void setTotalPosts(int numPosts) {
		totalPosts = numPosts;
	}

	public void setWrongPrice() {
		wrongPrice = true;
	}

	public void removeWrongPrice() {
		wrongPrice = false;
	}

	public boolean nullFields() {
		return nullFields;
	}

	public void setNullFields() {
		nullFields = true;
	}

	public void removeNullFields() {
		nullFields = false;
	}

	public boolean wrongIsbn() {
		return wrongIsbn;
	}

	public void setWrongIsbn() {
		wrongIsbn = true;
	}

	public void removeWrongIsbn() {
		wrongIsbn = false;
	}

	public boolean conductingSearch() {
		return conductingSearch;
	}

	public void setConductingSearch() {
		conductingSearch = true;
	}

	public void removeConductingSearch() {
		conductingSearch = false;
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

	// Sorry this looks too hard and it's 4am and I can't understand it, please
	// refactor later
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
		// curConversation.newMessage(content);
		int messageIndex = curConversation.getMessages().size() - 1;
		sentMessagesNotRead.add(curConversation.getMessages().get(messageIndex));
		// Note: creating the message automatically adds both parties as
		// observers
	}

	// Since the conversation object is shared by both parties, the message
	// isn't actually deleted, it just stops being observed by one party
	public void deleteMessage(Message message) {
		message.removeObserver(this);
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
	}

	@Override
	public int compareTo(FlipABookUser o) {
		if (user.equals(o.getUserInfo())) {
			return 0;
		}
		return 1;
	}
}
