package objects;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class Facade {
	public static User getCurrentUser(){
		UserService userService = UserServiceFactory.getUserService();
		return userService.getCurrentUser();
	}
	
	
	public static DatastoreService datastore(){
		return DatastoreServiceFactory.getDatastoreService();
	}
	
	public static void delete(Entity entity){
		datastore().delete(entity.getKey());
	}
	
	public static List<Entity> getPosts(){
		Query query = new Query("Post").addSort("date", Query.SortDirection.DESCENDING);
		return datastore().prepare(query).asList(FetchOptions.Builder.withLimit(Integer.MAX_VALUE));
	}
	
	public static List<Entity> getBooks(){
		Query query = new Query("Book");
		return datastore().prepare(query).asList(FetchOptions.Builder.withLimit(Integer.MAX_VALUE));
	}
	
	public static List<Entity> getMessages(){
		Query query = new Query("Message");
		return datastore().prepare(query).asList(FetchOptions.Builder.withLimit(Integer.MAX_VALUE));
	}
	
	public static List<Entity> getConversations(){
		Query query = new Query("Conversation");
		return datastore().prepare(query).asList(FetchOptions.Builder.withLimit(Integer.MAX_VALUE));
	}
	
	public static List<Entity> getUsers(){
		Query query = new Query("User");
		return datastore().prepare(query).asList(FetchOptions.Builder.withLimit(Integer.MAX_VALUE));
	}

}
