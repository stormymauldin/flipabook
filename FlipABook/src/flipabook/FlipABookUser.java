//http://tyestormyblog.appspot.com

package flipabook;

import java.util.List;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class FlipABookUser {

	@Id
	Long id;
	User user;
	boolean viewAllPosts = false;
	List<Post> history;

	@SuppressWarnings("unused")
	private FlipABookUser() {
	}

	public FlipABookUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public String getEmail() {
		return user.getEmail();
	}
	
	public String getNickname(){
		return user.getNickname();
	}
	
	public List<Post> getHistory(){
		return history;
	}
}