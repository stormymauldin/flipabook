package objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;

public class HomePage {
	private static HomePage uniqueInstance;
	public static ArrayList<Post> posts;
	public static ArrayList<User> users;
	public static ArrayList<FlipABookUser> flipABookUsers;
	public static ArrayList<Conversation> conversations;
	public static ArrayList<Message> messages;
	public static ArrayList<Book> books;
	public static boolean searchFilter = false;
	public static boolean advancedSearch = false;
	public static ArrayList<Post> searchResults;
	public static ArrayList<Integer> searchResultsWeighted;
	public static boolean init = false; 
	
	
	private HomePage() {
		posts = new ArrayList<Post>();
		users = new ArrayList<User>();
		flipABookUsers = new ArrayList<FlipABookUser>();
		books = new ArrayList<Book>();
		conversations = new ArrayList<Conversation>();
		messages = new ArrayList<Message>();
	}

	public static synchronized HomePage getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new HomePage();
		}
		else {
			ArrayList<Key> deleted_keys = new ArrayList<Key>();
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		    Query query = new Query("Post").addSort("date", Query.SortDirection.DESCENDING);
			List<Entity> temp = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(1000));
			for (Entity temp_post: temp) {
				String temp_title = (String)temp_post.getProperty("title");
				User temp_user = (User)temp_post.getProperty("user");
				FlipABookUser temp_flipabook_user = HomePage.getUser(temp_user); 
				Date temp_date = (Date)temp_post.getProperty("date");
				if (temp_date == null) {
					temp_date = new Date(); 
				}
				String temp_isbn = (String)temp_post.getProperty("isbn");
				String temp_author = (String)temp_post.getProperty("author");
				String temp_description = (String)temp_post.getProperty("description");
				String temp_price = (String)temp_post.getProperty("price");
				Calendar cal = Calendar.getInstance(); 
				cal.setTime(temp_date);
				cal.add(Calendar.DAY_OF_WEEK, 14);
				//If a post is expired, delete it and all associated conversations
				System.out.println("Post time is " + temp_date);
				if ((new Date()).after(cal.getTime())) {
					Post deleted_post = null;
					ArrayList<Conversation> deleted_convos = new ArrayList<Conversation>();					
					deleted_keys.add(temp_post.getKey());
					System.out.println("Expired Post found from Datastore: " + temp_post.getKey());
					for (Post userPost: HomePage.posts) {
							if (temp_isbn.equals(userPost.getIsbn())){
								deleted_post = userPost;
								
								for (Conversation conversation: HomePage.conversations) {
									if (conversation.getPost().equals(userPost)){
										deleted_convos.add(conversation);
									}	
								}
							}
					}
				    Query query_convos = new Query("Conversation").addSort("convoID", Query.SortDirection.DESCENDING);
				    List<Entity> datastore_convos = datastore.prepare(query_convos).asList(FetchOptions.Builder.withLimit(1000));
				    for (int i = 0; i < datastore_convos.size(); i++) {
				    	Entity datastore_convo = datastore_convos.get(i);
				    	String temp_convo_id = (String) datastore_convo.getProperty("convoID");
				    	for (Conversation delete_convo: deleted_convos){
				    		if (delete_convo.getConvoID().equals(temp_convo_id)){
				    			deleted_keys.add(datastore_convo.getKey());
				    			System.out.println("Conversation deleted: " + delete_convo.getConvoID());
				    		}
				    	}
				    	
				    }

					HomePage.conversations.removeAll(deleted_convos);
					HomePage.posts.remove(deleted_post);
				    datastore.delete(deleted_keys);
				  }		
			}

		}
		if (!init){
			initialize();
		}
		return uniqueInstance;
	}

	public static synchronized void initialize() {
		//This will initialize all the datas from the datastore! It's probably not that useful for appengine, but it's essential for debugging. 
		
		if (!init) {
			ArrayList<Key> deleted_keys = new ArrayList<Key>();
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		    Query user_query = new Query("User").addSort("name", Query.SortDirection.DESCENDING);
		    List<Entity> users = datastore.prepare(user_query).asList(FetchOptions.Builder.withLimit(100000));
		    for (Entity datastore_user: users) {
		    	User next_user = (User)datastore_user.getProperty("user");
		    	String name = (String)datastore_user.getProperty("name");
		    	int totalPosts = ((Long)datastore_user.getProperty("totalposts")).intValue();
		    	FlipABookUser temp_flipabook_user = HomePage.getUser(next_user); 
		    	temp_flipabook_user.setTotalPosts(totalPosts);
		    }
		    
		    Query query = new Query("Post").addSort("date", Query.SortDirection.DESCENDING);
			List<Entity> temp = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(1000));
			for (Entity temp_post: temp) {
				String temp_title = (String)temp_post.getProperty("title");
				User temp_user = (User)temp_post.getProperty("user");
				FlipABookUser temp_flipabook_user = HomePage.getUser(temp_user); 
				Date temp_date = (Date)temp_post.getProperty("date");
				if (temp_date == null) {
					temp_date = new Date(); 
				}
				String temp_isbn = (String)temp_post.getProperty("isbn");
				String temp_author = (String)temp_post.getProperty("author");
				String temp_description = (String)temp_post.getProperty("description");
				String temp_price = (String)temp_post.getProperty("price");
				Calendar cal = Calendar.getInstance(); 
				cal.setTime(temp_date);
				cal.add(Calendar.DAY_OF_WEEK, 14);
				if (temp_date.after(cal.getTime())){
					deleted_keys.add(temp_post.getKey());
					System.out.println("Expired Post found from Datastore: " + temp_post.getKey());
				}
				else {
					Post post_obj = new Post(temp_flipabook_user, temp_title, temp_author, temp_isbn, temp_price, temp_description, temp_date);
					HomePage.posts.add(post_obj);
					temp_flipabook_user.posts.add(post_obj);
					System.out.println("Post found from Datastore: " + temp_post.getKey());
					System.out.println("User: " + temp_user.getEmail() + " has number of posts: " + temp_flipabook_user.getNumCurrentPosts());

				}
			}
			//This is for debugging purposes. 
		    Query convo_query = new Query("Conversation").addSort("convoID", Query.SortDirection.DESCENDING);
			List<Entity> convos = datastore.prepare(convo_query).asList(FetchOptions.Builder.withLimit(100000));
			
			//Initializes all stored conversations on the server
			for (Entity conversation: convos){
				User buyer = (User) conversation.getProperty("buyer");
				FlipABookUser temp_buyer = HomePage.getUser(buyer); 
				User seller = (User) conversation.getProperty("seller");
				FlipABookUser temp_seller = HomePage.getUser(seller); 
				String temp_title = (String)conversation.getProperty("title");
				String temp_isbn = (String)conversation.getProperty("isbn");
				boolean foundPost = false;
				for (Post temp_post: HomePage.posts){
					if (temp_post.getIsbn().equals(temp_isbn) && seller.equals(temp_post.getSeller().getUserInfo())){
						foundPost = true; 
						System.out.println("Found Conversation: " + temp_title);
						HomePage.conversations.add(new Conversation(temp_post, temp_buyer, false));
						break;
					}
				}
				if (!foundPost) {
					Date temp_date = (Date)conversation.getProperty("date");
					String temp_author = (String)conversation.getProperty("author");
					String temp_description = (String)conversation.getProperty("description");
					String temp_price = (String)conversation.getProperty("price");
					Post post_obj = new Post(temp_seller, temp_title, temp_author, temp_isbn, temp_price, temp_description, temp_date);
					HomePage.conversations.add(new Conversation(post_obj, temp_buyer, false));
					System.out.println("Found Conversation: " + temp_title);
				}
			}
			
			//Initializes all messages on the server
		    Query message_query = new Query("Message").addSort("convoID", Query.SortDirection.DESCENDING);
			List<Entity> datastore_messages = datastore.prepare(message_query).asList(FetchOptions.Builder.withLimit(100000));
			for (Entity message: datastore_messages){
				Date messDate = (Date) message.getProperty("date");
				User sender = (User) message.getProperty("sender");
				String content = (String) message.getProperty("content");
				String convoID = (String) message.getProperty("convoID");
				Conversation temp_convo = getConversation(convoID);
				if (temp_convo != null) {
					Message temp_message = new Message(content, sender, temp_convo, messDate);
					messages.add(temp_message);
					temp_convo.messages.add(temp_message);
				}
				else {
					deleted_keys.add(message.getKey());
					System.out.println("Zombie message found: " + message.getProperty("content"));
				}
			}

			datastore.delete(deleted_keys);
			System.out.println("Number of Posts: " + HomePage.posts.size());
			init = true; 
		}
	}
	
	public static Conversation getConversation(String ID){
		//May return null if Conversation doesn't exist given specific convoID
		for (Conversation convo: conversations) {
			if (convo.convoID.equals(ID)){
				return convo;
			}
		}
		
		return null; 
	}
	
	public static FlipABookUser getUser(User user) {
		boolean found = false;
		for (FlipABookUser find_user: flipABookUsers){
			if(find_user.getUserInfo().equals(user) ){
				found = true;
				return find_user;
			}
			
		}
		if (!found) {
			createUser(user);
		}
		return flipABookUsers.get(flipABookUsers.size() - 1);
		
	}

	public static void createUser(User user) {
		flipABookUsers.add(new FlipABookUser(user));
	}

	public void deleteUser(FlipABookUser user) {
		if (flipABookUsers.contains(user)) {
			// remove the user from existing conversations
			for (Conversation conversation : user.getConversations()) {
				// remove the user's interactions and update associated parties
				if (conversation.getBuyer().compareTo(user) == 0) {
					conversation.deleteConversation(Conversation.BUYER_DELETED);
				} else {
					conversation.deleteConversation(Conversation.POST_DELETED);
				}
			}
			for (Post post : user.getPosts()) {
				deletePost(post);
			}

		}

	}

	public void addPost(Post post) {
		posts.add(post);
	}

	public void deletePost(Post post) {
		for (Post curPost : posts) {
			if (curPost.compareTo(post) == 0) {
				posts.remove(posts.indexOf(curPost));
			}
		}
	}
}
