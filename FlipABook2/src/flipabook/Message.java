package flipabook;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Message {
	@Id
	Long id;
	User sender;
	User recipient;
	String content;
	boolean read = false;
	
	public Message(User sender, User recipient, String content){
		this.sender = sender;
		this.recipient = recipient;
		this.content = content;
	}
	
	public void setRead(){
		read = true;
	}
	
	public boolean wasRead(){
		return read;
	}
}
