package objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
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
		return uniqueInstance;
	}

	public static synchronized void initialize() {
		//This will initialize all the datas from the datastore! It's probably not that useful for appengine, but it's essential for debugging. 
		
		if (!init) {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		    Query user_query = new Query("User").addSort("name", Query.SortDirection.DESCENDING);
		    List<Entity> users = datastore.prepare(user_query).asList(FetchOptions.Builder.withLimit(1000));
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
				Post post_obj = new Post(temp_flipabook_user, temp_title, temp_author, temp_isbn, temp_price, temp_description, temp_date);
				HomePage.posts.add(post_obj);
//				HomePage.posts.add(new Post(temp_flipabook_user, temp_title, temp_author, temp_isbn, temp_price, temp_description, temp_date));
				temp_flipabook_user.posts.add(post_obj);
				System.out.println("Post found from Datastore: " + temp_post.getKey());
				System.out.println("User: " + temp_user.getEmail() + " has number of posts: " + temp_flipabook_user.getNumCurrentPosts());
			}
			//This is for debugging purposes. 
		    Query convo_query = new Query("Conversation").addSort("convoID", Query.SortDirection.DESCENDING);
			List<Entity> convos = datastore.prepare(convo_query).asList(FetchOptions.Builder.withLimit(1000));
			
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
			List<Entity> datastore_messages = datastore.prepare(message_query).asList(FetchOptions.Builder.withLimit(1000));
			for (Entity message: datastore_messages){
				Date messDate = (Date) message.getProperty("date");
				String content = (String) message.getProperty("content");
				String convoID = (String) message.getProperty("convoID");
				Conversation temp_convo = getConversation(convoID);
				if (temp_convo != null) {
					Message temp_message = new Message(content, temp_convo, messDate);
					messages.add(temp_message);
					temp_convo.messages.add(temp_message);
				}
			}

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
	
	public static void basicSearch(String term) {

		HashSet<String> filteredTerm = breakup(term);
		resetSearchWeights();
		for (int i = 0; i < posts.size(); i++) {
			searchEachPostField(filteredTerm, filteredTerm, filteredTerm, filteredTerm, i);
		}
		collectMatchingPosts();
	}

	public static void advancedSearch(String title, String author, String isbn, String keywords) {
		HashSet<String> filteredTitle = null;
		HashSet<String> filteredAuthor = null;
		HashSet<String> filteredIsbn = null;
		HashSet<String> filteredKeywords = null;
		formatSearches(title, author, isbn, keywords, filteredTitle, filteredAuthor, filteredIsbn, filteredKeywords);
		resetSearchWeights();
		for (int i = 0; i < posts.size(); i++) {
			searchEachPostField(filteredTitle, filteredAuthor, filteredIsbn, filteredKeywords, i);
		}
		collectMatchingPosts();
	}

	private static void collectMatchingPosts() {
		int max = 0;
		int maxIndex = getMaxWeightIndex(max);
		if (maxIndex == -1) {
			return;
		} else {
			while (maxIndex != -1) {
				max = searchResultsWeighted.get(maxIndex);
				searchResultsWeighted.set(maxIndex, 0);
				searchResults.add(posts.get(maxIndex));
				maxIndex = getMaxWeightIndex(max);
			}
		}
	}

	private static int getMaxWeightIndex(int max) {
		int indexOfNewMax = -1;
		for (int i = 0; i < searchResultsWeighted.size(); i++) {
			if (searchResultsWeighted.get(i) > max) {
				max = searchResultsWeighted.get(i);
				indexOfNewMax = i;
			}
		}
		return indexOfNewMax;
	}

	private static void formatSearches(String title, String author, String isbn, String keywords,
			HashSet<String> filteredTitle, HashSet<String> filteredAuthor, HashSet<String> filteredIsbn,
			HashSet<String> filteredKeywords) {
		if (!(title == null || title == "")) {
			filteredTitle = breakup(title);
		}
		if (!(author == null || author == "")) {
			filteredAuthor = breakup(author);
		}

		if (!(isbn == null || isbn == "")) {
			filteredIsbn = breakup(isbn);
		}

		if (!(keywords == null || keywords == "")) {
			filteredKeywords = breakupNoArticleRemoval(keywords);
		}
	}

	private static void searchEachPostField(HashSet<String> title, HashSet<String> author, HashSet<String> isbn,
			HashSet<String> keywords, int postID) {
		if (title != null) {
			HashSet<String> postTitle = breakup(posts.get(postID).getBook().getTitle());
			compareSets(title, postTitle, postID);
		}

		if (author != null) {
			HashSet<String> postAuthor = breakup(posts.get(postID).getBook().getAuthor());
			compareSets(author, postAuthor, postID);
		}

		if (isbn != null) {
			HashSet<String> postIsbn = breakup(posts.get(postID).getBook().getIsbn());
			compareSets(isbn, postIsbn, postID);
		}

		if (keywords != null) {
			HashSet<String> postDescription = breakup(posts.get(postID).getDescription());
			compareSets(keywords, postDescription, postID);
		}
	}

	private static void resetSearchWeights() {
		searchResultsWeighted = new ArrayList<Integer>();
		for (Post post : posts) {
			searchResultsWeighted.add(0);
		}
		searchResults = new ArrayList<Post>();
	}

	private static void compareSets(HashSet<String> search, HashSet<String> original, int postID) {
		for (String word : search) {
			// if the word is contained within the specified area of search, add
			// weight to this post
			if (original.contains(word)) {
				searchResultsWeighted.set(postID, searchResultsWeighted.get(postID) + 1);
			}
		}

	}

	private static HashSet<String> breakup(String original) {
		HashSet<String> removedArticles = breakupNoArticleRemoval(original);
		while (removedArticles.contains("the") || removedArticles.contains("a") || removedArticles.contains("an")
				|| removedArticles.contains("in") || removedArticles.contains("of")) {
			removedArticles.remove("the");
			removedArticles.remove("a");
			removedArticles.remove("an");
			removedArticles.remove("in");
			removedArticles.remove("of");
		}
		return removedArticles;
	}

	private static HashSet<String> breakupNoArticleRemoval(String original) {
		original.toLowerCase();
		String originalKeyWords[] = original.split("\\P{Alpha}+");
		HashSet<String> filtered = new HashSet<String>();
		filtered.addAll(Arrays.asList(originalKeyWords));
		return filtered;
	}

	public static FlipABookUser getUser(User user) {
		boolean found = false;
		for (FlipABookUser find_user: flipABookUsers){
			if(find_user.getUserInfo().equals(user) ){
				found = true;
				return find_user;
			}
			
		}
		//Whoever made this search function is bad and should feel bad
//		for (int i = 0; i < HomePage.users.size(); i++) {
//			if (users.get(i).compareTo(user) == 0) {
//				index = i;
//				return flipABookUsers.get(index);
////				break;
//			}
//		}
		if (!found) {
			createUser(user);
		}
		return flipABookUsers.get(flipABookUsers.size() - 1);
		
	}

	public static void createUser(User user) {
		//FlipABookUser flipABookUser = null;
		// TODO: other stuff here to add to objectify
		// TODO: check to see if user has email address
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

			// TODO: delete the user's objectify data
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

	public void displayPosts() {
		// TODO: print out posts to view
	}
}
