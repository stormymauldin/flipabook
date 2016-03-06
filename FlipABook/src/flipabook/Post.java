//http://tyestormyblog.appspot.com

package flipabook;

import java.util.Date;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Post implements Comparable<Post> {

	@Id
	Long id;
	User user;
	String title;
	String content;
	Date date;

	@SuppressWarnings("unused")
	private Post() {
	}

	public Post(User user, String title, String content) {
		this.user = user;
		this.title = title;
		this.content = content;
		date = new Date();
	}

	public User getUser() {
		return user;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public Date getDate() {
		return date;
	}
	
	public String getEmailablePost(){
		return title + ":\n\n" + content + "\n\nPosted by " + user.getNickname() + " on " + date + ".";
	}

	@Override
	public int compareTo(Post other) {
		if (date.after(other.date)) {
			return 1;
		} else if (date.before(other.date)) {
			return -1;
		}
		return 0;
	}
}