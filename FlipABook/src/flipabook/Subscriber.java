//http://tyestormyblog.appspot.com

package flipabook;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Subscriber {

	@Id
	Long id;
	FlipABookUser user;

	@SuppressWarnings("unused")
	private Subscriber() {
	}

	public Subscriber(FlipABookUser user) {
		this.user = user;
	}

	public FlipABookUser getUser() {
		return user;
	}
}