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
	public static ArrayList<Book> books;
	public static ArrayList<Conversation> conversations;
	public static ArrayList<Message> messages;
	public static boolean searchFilter = false;
	public static boolean advancedSearch = false;
	public static ArrayList<Post> searchResults;
	public static ArrayList<Integer> searchResultsWeighted;
	public static boolean init = false;

	private HomePage() {
		posts = new ArrayList<Post>();
		users = new ArrayList<User>();
		conversations = new ArrayList<Conversation>();
		messages = new ArrayList<Message>();
		flipABookUsers = new ArrayList<FlipABookUser>();
		books = new ArrayList<Book>();
		update();
	}

	public static synchronized HomePage getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new HomePage();
		}
		return uniqueInstance;
	}
	
	public static void update(){
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		posts.clear();
		users.clear();
		conversations.clear();
		messages.clear();
		flipABookUsers.clear();
		books.clear();
		List<Entity> postEntities = datastore.prepare(new Query("Post").addSort("date", Query.SortDirection.DESCENDING)).asList(FetchOptions.Builder.withLimit(1000));
		List<Entity> conversationEntities = datastore.prepare(new Query("Conversation")).asList(FetchOptions.Builder.withLimit(Integer.MAX_VALUE));
		List<Entity> messageEntities = datastore.prepare(new Query("Message")).asList(FetchOptions.Builder.withLimit(Integer.MAX_VALUE));
		List<Entity> bookEntities = datastore.prepare(new Query("Book")).asList(FetchOptions.Builder.withLimit(Integer.MAX_VALUE));
		List<Entity> flipABookUserEntities = datastore.prepare(new Query("Conversation")).asList(FetchOptions.Builder.withLimit(Integer.MAX_VALUE));

		for(Entity entity : postEntities){
			new Post(entity.getKey());
		}
		
		for(Entity entity : conversationEntities){
			new 
		}
	}

	public static void initialize() {
		if (!init) {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			Query query = new Query("Post").addSort("date", Query.SortDirection.DESCENDING);
			List<Entity> temp = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(1000));

			for (Entity temp_post : temp) {
				String temp_title = (String) temp_post.getProperty("title");
				User temp_user = (User) temp_post.getProperty("user");
				FlipABookUser temp_flipabook_user = HomePage.getUser(temp_user);
				Date temp_date = (Date) temp_post.getProperty("date");
				if (temp_date == null) {
					temp_date = new Date();
				}
				String temp_isbn = (String) temp_post.getProperty("isbn");
				String temp_author = (String) temp_post.getProperty("author");
				String temp_description = (String) temp_post.getProperty("description");
				String temp_price = (String) temp_post.getProperty("price");
				HomePage.posts.add(new Post(temp_flipabook_user, temp_title, temp_author, temp_isbn, temp_price,
						temp_description, temp_date));
			}
			// This is for debugging purposes.
			for (Post post : HomePage.posts) {
				System.out.println("Found Post from Datastore: " + post.getTitle());
			}
			System.out.println("Number of Posts: " + HomePage.posts.size());
			init = true;
		}
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
		int index = -1;
		for (int i = 0; i < HomePage.users.size(); i++) {
			if (users.get(i).compareTo(user) == 0) {
				index = i;
				return flipABookUsers.get(index);
				// break;
			}
		}
		createUser(user);
		return flipABookUsers.get(flipABookUsers.size() - 1);

	}

	public static void createUser(User user) {
		// FlipABookUser flipABookUser = null;
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
