package objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
	public static final int MAX_NUM_IN_LIST = 100000;
	public static final int MAX_POSTS = 100;
	public static ArrayList<Entity> searchResults;
	private static HashMap<Entity, Integer> weights;

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
		return datastore().prepare(query).asList(FetchOptions.Builder.withLimit(MAX_POSTS));
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

	public static void basicSearch(String term) {
		HashSet<String> filteredTerm = breakup(term.toLowerCase());
		resetSearchWeights();
		List<Entity> posts = getPosts();
		for (Entity post : posts) {
			searchEachPostField(filteredTerm, filteredTerm, filteredTerm, filteredTerm, post);
		}
		collectMatchingPosts();
		Collections.reverse(searchResults);
	}

	public static void advancedSearch(String title, String author, String isbn, String keywords) {
		HashSet<String> filteredTitle = breakup(title.toLowerCase());
		HashSet<String> filteredAuthor = breakup(author.toLowerCase());
		HashSet<String> filteredIsbn = breakup(isbn);
		HashSet<String> filteredKeywords = breakupNoArticleRemoval(keywords.toLowerCase());
		resetSearchWeights();
		List<Entity> posts = getPosts();
		for (Entity post : posts) {
			searchEachPostField(filteredTitle, filteredAuthor, filteredIsbn, filteredKeywords, post);
		}
		collectMatchingPosts();
		Collections.reverse(searchResults);
	}

	private static void collectMatchingPosts() {
		Entity minWeightPost = getMinWeightIndex();
		if (minWeightPost == null) {
			return;
		} else {
			while (minWeightPost != null) {
				weights.remove(minWeightPost);
				searchResults.add(minWeightPost);
				minWeightPost = getMinWeightIndex();
			}
		}
	}

	private static Entity getMinWeightIndex() {
		int min = 0;
		Entity newMin = null;
		List<Entity> posts = getPosts();
		for (Entity post : posts) {
			if (weights.get(post) != null && weights.get(post) > min) {
				newMin = post;
				break;
			}
		}
		return newMin;
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
		String originalKeyWords[] = original.split("\\P{Alnum}+");
		HashSet<String> filtered = new HashSet<String>();
		filtered.addAll(Arrays.asList(originalKeyWords));
		return filtered;
	}

	private static void resetSearchWeights() {
		weights = new HashMap<Entity, Integer>();
		searchResults = new ArrayList<Entity>();
	}

	private static void searchEachPostField(HashSet<String> title, HashSet<String> author, HashSet<String> isbn,
			HashSet<String> keywords, Entity post) {
		if (!title.isEmpty()) {
			String thisTitle = (String) (post.getProperty("title"));
			thisTitle = thisTitle.toLowerCase();
			HashSet<String> postTitle = breakup(thisTitle);
			compareSets(title, postTitle, post);
		}

		if (!author.isEmpty()) {
			String thisAuthor = (String) (post.getProperty("author"));
			thisAuthor = thisAuthor.toLowerCase();
			HashSet<String> postAuthor = breakup(thisAuthor);
			compareSets(author, postAuthor, post);
		}

		if (!isbn.isEmpty()) {
			String thisIsbn = (String) (post.getProperty("isbn"));
			thisIsbn = thisIsbn.toLowerCase();
			HashSet<String> postIsbn = breakup(thisIsbn);
			compareSets(isbn, postIsbn, post);
		}

		if (!keywords.isEmpty()) {
			String thisDescription = (String) (post.getProperty("description"));
			thisDescription = thisDescription.toLowerCase();
			HashSet<String> postDescription = breakup(thisDescription);
			compareSets(keywords, postDescription, post);
		}
	}

	private static void compareSets(HashSet<String> search, HashSet<String> original, Entity post) {
		for (String word : search) {
			// if the word is contained within the specified area of search, add
			// weight to this post
			if (original.contains(word)) {
				if (weights.get(post) == null) {
					weights.put(post, 1);
				} else {
					weights.put(post, weights.get(post) + 1);
				}
			}
		}
	}

	public static boolean verifyEmail(User user) {
		if (user == null) {
			return false;
		}
		String[] parsedEmail = user.getEmail().split("@");
		if (parsedEmail.length != 2 || !parsedEmail[1].equals("utexas.edu")) {
			return false;
		} else {
			return true;
		}
	}
}
