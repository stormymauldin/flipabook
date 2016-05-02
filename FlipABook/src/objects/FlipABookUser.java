package objects;

import java.util.ArrayList;
import com.google.appengine.api.users.User;

public class FlipABookUser implements Comparable<FlipABookUser> {

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

	@Override
	public int compareTo(FlipABookUser o) {
		if (user.equals(o.getUserInfo())) {
			return 0;
		}
		return 1;
	}
}
