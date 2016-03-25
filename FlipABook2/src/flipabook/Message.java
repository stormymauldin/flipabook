package flipabook;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Message implements Subject{
	@Id
	Long id;
	FlipABookUser sender;
	FlipABookUser recipient;
	String content;
	MessageGroup messageGroup;
	boolean read = false;

	public Message(FlipABookUser sender, FlipABookUser recipient, String content, MessageGroup messageGroup) {
		this.sender = sender;
		this.recipient = recipient;
		this.content = content;
		this.messageGroup = messageGroup;
		sender.update(this);
		recipient.update(this);
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

	public MessageGroup getMessageGroup() {
		return messageGroup;
	}

	@Override
	public void registerObserver(Observer o) {
		sender.update(this);
		recipient.update(this);
	}

	@Override
	public void removeObserver(Observer o) {
		//FlipABookUser remo
		
	}

	@Override
	public void notifyObservers() {
		sender.update(this);
		recipient.update(this);
	}
}
