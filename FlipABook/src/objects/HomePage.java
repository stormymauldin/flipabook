package objects;

import java.util.ArrayList;

import com.google.appengine.api.users.User;

public class HomePage {
	private static HomePage uniqueInstance;
	public static ArrayList<Post> posts;
	public static ArrayList<User> users;
	public static ArrayList<FlipABookUser> flipABookUsers;
	public static ArrayList<Book> books;

	private HomePage() {
		posts = new ArrayList<Post>();
		users = new ArrayList<User>();
		flipABookUsers = new ArrayList<FlipABookUser>();
		books = new ArrayList<Book>();
	}

	public static synchronized HomePage getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new HomePage();
		}
		return uniqueInstance;
	}

	public void createUser(User user) {
		FlipABookUser flipABookUser = null;
		// TODO: other stuff here to add to objectify
		// TODO: check to see if user has email address
		flipABookUsers.add(flipABookUser);
	}

	public void deleteUser(FlipABookUser user) {
		if (flipABookUsers.contains(user)) {
			//remove the user from existing conversations
			for (Conversation conversation : user.getConversations()) {
				//remove the user's interactions and update associated parties
				if(conversation.getBuyer().compareTo(user) == 0)
				{
					conversation.deleteConversation(Conversation.BUYER_DELETED);
				}
				else{
					conversation.deleteConversation(Conversation.POST_DELETED);
				}
			}
			for(Post post : user.getPosts()){
				deletePost(post);
			}
			
			// TODO: delete the user's objectify data
		}

	}

	public void addPost(Post post) {
		posts.add(post);
	}

	public void deletePost(Post post) {
		for(Post curPost : posts){
			if(curPost.compareTo(post) == 0)
			{
				posts.remove(posts.indexOf(curPost));
			}
		}
	}

	public void displayPosts() {
		// TODO: print out posts to view
	}
}
