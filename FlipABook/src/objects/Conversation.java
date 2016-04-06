package objects;

import java.util.ArrayList;
import java.util.Date;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Conversation implements Comparable <Conversation>{
	@Id
	Long id;
	Post post;
	//note: participant 0 is seller, participant 1 is buyer.
	FlipABookUser buyer;
	ArrayList<Message> messages;
	boolean meetingIsScheduled = false;
	Date scheduleDate;
	boolean transactionWasSuccessful = false;
	static final int POST_DELETED = 0;
	static final int BUYER_DELETED = 1;
	
	public Conversation(){}
	public Conversation(Post post, FlipABookUser buyer){
		this.post = post;
		this.buyer = buyer;
		messages = new ArrayList<Message>();
	}
	
	public Post getPost(){
		return post;
	}
	
	public void newMessage(int direction, String content){
		messages.add(new Message(direction, content, this));
	}
	
	public void scheduleMeeting(){
		meetingIsScheduled = true;
		scheduleDate = new Date();
		post.editStatus(Post.SUSPENDED);
	}
	
	public boolean meetingIsScheduled(){
		return meetingIsScheduled;
	}
	
	public Date getScheduledDate(){
		return scheduleDate;
	}
	
	public void transactionWasSuccessful(){
		transactionWasSuccessful = true;
		//TODO: call function to delete post
	}
	
	public void transactionWasNotSuccesful(){
		meetingIsScheduled = false;
		post.editStatus(Post.ACTIVE);
	}
	
	public ArrayList<Message> getMessages(){
		return messages;
	}
	
	public FlipABookUser getBuyer(){
		return buyer;
	}
	@Override
	public int compareTo(Conversation o) {
		if(post.compareTo(o.getPost()) == 0 && buyer.compareTo(o.getBuyer()) == 0){
			return 0;
		}
		return -1;
	}
	
	public void deleteConversation(int deletionType){
		if(deletionType == POST_DELETED){
			for(Message message : messages){
				message.removeObserver(message.getSender());
				message.removeObserver(message.getRecipient());
			}
		}
		else{
			for(Message message : messages){
				if(buyer.compareTo(message.getSender()) == 0){
					message.removeObserver(message.getSender());
				}
				else{
					message.removeObserver(message.getRecipient());
				}
			}
		}
	}
}