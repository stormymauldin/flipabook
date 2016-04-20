package objects;

import java.io.Serializable;

import com.googlecode.objectify.annotation.*;

@Entity
@Serialize
public class Message implements Subject, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7210318335481909978L;
	@Id
	Long id;
	@Container
	FlipABookUser sender;
	@Container
	FlipABookUser recipient;
	String content;
	@Container
	Conversation conversation;
	boolean senderDeleted = false;
	boolean recipientDeleted = false;
	boolean read = false;

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

	public Conversation getConversation() {
		return conversation;
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
