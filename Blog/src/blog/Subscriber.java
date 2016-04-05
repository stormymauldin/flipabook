//http://tyestormyblog.appspot.com

package blog;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Subscriber {

	@Id
	Long id;
	User user;

	@SuppressWarnings("unused")
	private Subscriber() {
	}

	public Subscriber(User user) {
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
}