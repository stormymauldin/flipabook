package flipabook;

import java.util.ArrayList;
import java.util.Date;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class MessageGroup {
	@Id
	Long id;
	Post post;
	User buyer;
	ArrayList<Message> messages;
	boolean meetingIsScheduled = false;
	Date scheduleDate;
	boolean transactionWasSuccessful = false;
	
	
	public MessageGroup(Post post, User buyer){
		this.post = post;
		this.buyer = buyer;
		messages = new ArrayList<Message>();
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
}