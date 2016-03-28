package flipabook;

import java.util.ArrayList;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class FlipABookUser implements Comparable<FlipABookUser>, Observer {
	@Id
	Long id;
	User user;
	ArrayList<Post> posts;
	ArrayList<Conversation> conversations;
	ArrayList<Message> unreadMessages;
	ArrayList<Message> sentMessagesNotRead;

	public FlipABookUser(User user) {
		this.user = user;
		posts = new ArrayList<Post>();
		conversations = new ArrayList<Conversation>();
		unreadMessages = new ArrayList<Message>();
		sentMessagesNotRead = new ArrayList<Message>();
	}

	public User getUserInfo() {
		return user;
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
		if (updateType == Observer.NEW_MESSAGE){
			if (!conversations.contains(message.getConversation())) {
				conversations.add(message.getConversation());
			}
			unreadMessages.add(message);
		}
		else if(updateType == Observer.READ){
			int index = sentMessagesNotRead.indexOf(message);
			if(index != -1){
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