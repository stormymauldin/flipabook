package flipabook;

import java.util.ArrayList;

import com.google.appengine.api.users.User;

public class HomePage{
	private static HomePage uniqueInstance;
	private ArrayList<Post> posts;
	private ArrayList<FlipABookUser> users;
 
	private HomePage() {
		posts = new ArrayList<Post>();
		users = new ArrayList<FlipABookUser>();
	}
 
	public static synchronized HomePage getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new HomePage();
		}
		return uniqueInstance;
	}
 
	
	public void createUser(User user){
		FlipABookUser flipABookUser = null;
		//TODO: other stuff here to add to objectify
		//TODO: check to see if user has email address
		users.add(flipABookUser);
	}
	public void deleteUser(FlipABookUser user){
		if(users.contains(user)){
			posts.remove(users.indexOf(user));
			//TODO: other stuff here to remove from objectify
		}
	}
	
	public void addPost(Post post){
		//TODO: other stuff here to add to objectify
		posts.add(post);
	}
	
	public void deletePost(Post post){
		if(posts.contains(post)){
			posts.remove(posts.indexOf(post));
			//TODO: other stuff here to remove from objectify
		}
	}
	
	public void displayPosts(){
		//TODO: print out posts to view
	}
}
