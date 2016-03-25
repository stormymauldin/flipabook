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
	ArrayList<Post> sellHistory;
	ArrayList<MessageGroup> messageGroups;

	public FlipABookUser(User user) {
		this.user = user;
		sellHistory = new ArrayList<Post>();
		messageGroups = new ArrayList<MessageGroup>();
	}

	public User getUserInfo() {
		return user;
	}

	public ArrayList<Post> getSellHistory() {
		return sellHistory;
	}

	public ArrayList<MessageGroup> getMessageGroups() {
		return messageGroups;
	}

	@Override
	public void update(Message message) {
		if (messageGroups.contains(message.getMessageGroup())) {
			int index = messageGroups.indexOf(message.getMessageGroup());
			ArrayList<Message> theseMessages = messageGroups.get(index).getMessages();
			theseMessages.add(message);
		}

		for (MessageGroup messageGroup : messageGroups) {
			FlipABookUser user0 = messageGroup.getParticipants().get(0);
			FlipABookUser user1 = messageGroup.getParticipants().get(1);
			//if the two users already have a conversation going, add this message to the conversation
			if (user0.compareTo(message.getSender()) == 0 || user1.compareTo(message.getSender()) == 0) {
				messageGroup.getMessages().add(message);
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
