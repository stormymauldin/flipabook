package objects;

import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class Facade {
	public static final int MAX_NUM_IN_LIST=1000;
	public static User getCurrentUser() {
		return getCurrentUser(UserServiceFactory.getUserService());
	}

	public static User getCurrentUser(UserService userService) {
		return userService.getCurrentUser();
	}

	public static DatastoreService datastore() {
		return DatastoreServiceFactory.getDatastoreService();
	}

	public static void delete(Entity entity) {
		datastore().delete(entity.getKey());
	}

	public static List<Entity> getPosts() {
		Query query = new Query("Post").addSort("date", Query.SortDirection.DESCENDING);
		return datastore().prepare(query).asList(FetchOptions.Builder.withLimit(MAX_NUM_IN_LIST));
	}

	public static List<Entity> getBooks() {
		Query query = new Query("Book");
		return datastore().prepare(query).asList(FetchOptions.Builder.withLimit(MAX_NUM_IN_LIST));
	}

	public static List<Entity> getMessages() {
		Query query = new Query("Message");
		return datastore().prepare(query).asList(FetchOptions.Builder.withLimit(MAX_NUM_IN_LIST));
	}

	public static List<Entity> getConversations() {
		Query query = new Query("Conversation");
		return datastore().prepare(query).asList(FetchOptions.Builder.withLimit(MAX_NUM_IN_LIST));
	}

	public static List<Entity> getUsers() {
		Query query = new Query("User").addSort("name", Query.SortDirection.DESCENDING);
		return datastore().prepare(query).asList(FetchOptions.Builder.withLimit(MAX_NUM_IN_LIST));
	}

	public static FlipABookUser getFlipABookUser(User user) {
		int index = -1;
		for (int i = 0; i < HomePage.users.size(); i++) {
			if (HomePage.users.get(i).compareTo(user) == 0) {
				index = i;
				break;
			}
		}
		FlipABookUser flipABookUser = null;
		if (index == -1) {
			HomePage.users.add(user);
			flipABookUser = new FlipABookUser(user);
			Key userkey = KeyFactory.createKey("Post", user.getEmail());
			Entity user_datastore = new Entity("User", userkey);
			user_datastore.setProperty("user", user);
			HomePage.flipABookUsers.add(flipABookUser);
		} else {
			flipABookUser = HomePage.flipABookUsers.get(index);
		}
		return flipABookUser;
	}

}
