package flipabook;

import java.util.ArrayList;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class FlipABookUser{
	@Id
	Long id;
	User user;
	ArrayList<Post> sellHistory;
	ArrayList<MessageGroup> messageGroups;

	public FlipABookUser(User user){
		this.user = user;
		sellHistory = new ArrayList<Post>();
		messageGroups = new ArrayList<MessageGroup>();
	}
	
	public User getUserInfo(){
		return user;
	}
	
	public ArrayList<Post> getSellHistory() {
		return sellHistory;
	}
	
	public ArrayList<MessageGroup> getMessageGroups() {
		return messageGroups;
	}
}
