package servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import objects.Conversation;
import objects.FlipABookUser;
import objects.HomePage;
import objects.Post;

public class DeletePostServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		String deleted = req.getParameter("deletedpost");
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key postkey = KeyFactory.createKey("Post", deleted+user.getEmail());
		//Entity post_datastore = new Entity("Post", postkey);
		datastore.delete(postkey);
		HomePage.getInstance();
		for (Post userPost: HomePage.posts) {
			if (userPost.getSeller().getUserInfo().equals(user)) {
				if (deleted.equals(userPost.getIsbn())){
					

					
					
					
					HomePage.posts.remove(userPost);
					FlipABookUser temp_user = HomePage.getUser(user);
					temp_user.getPosts().remove(userPost);
					
					
				}
			}
		}
		
		
		System.out.println("Post deleted: " + deleted);
		resp.sendRedirect("posts.jsp");

	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		String deleted = req.getParameter("deletedpost");
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key postkey = KeyFactory.createKey("Post", deleted+user.getEmail());
		//Entity post_datastore = new Entity("Post", postkey);
		//datastore.delete(postkey);
		//It turns out we need an even more super special key than this. 
		//Welp, now we just have to look through the entire datastore to find it! 
		HomePage.getInstance();
		Post deleted_post = null;
		ArrayList<Conversation> deleted_convos = new ArrayList<Conversation>();
		
		for (Post userPost: HomePage.posts) {
			if (userPost.getSeller().getUserInfo().equals(user)) {
				if (deleted.equals(userPost.getIsbn())){
					deleted_post = userPost;
					
					for (Conversation conversation: HomePage.conversations) {
						if (conversation.getPost().equals(userPost)){
							//Something should happen here? 
							deleted_convos.add(conversation);
						}	
					}
				}
			}
		}
		HomePage.conversations.removeAll(deleted_convos);
		HomePage.posts.remove(deleted_post);
	    Query query = new Query("Post").addSort("date", Query.SortDirection.DESCENDING);
		List<Entity> posts = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(1000));
		for (int i = 0; i < posts.size(); i++) {
			Entity post = posts.get(i);
			User temp_user = (User) post.getProperty("user");
			String temp_isbn = (String) post.getProperty("isbn");
			if (temp_user.equals(user) && temp_isbn.equals(deleted)) {
				postkey = post.getKey();
			}
		}
	    Query query_convos = new Query("Conversation").addSort("convoID", Query.SortDirection.DESCENDING);
	    List<Entity> datastore_convos = datastore.prepare(query_convos).asList(FetchOptions.Builder.withLimit(1000));
	    ArrayList<Key> deleted_keys = new ArrayList<Key>();
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
	    datastore.delete(deleted_keys);
//		for (Key deletekey: deleted_keys){
//			datastore.delete(deletekey);
//		}
		
		System.out.println("Post deleted: " + postkey);
		datastore.delete(postkey);
		resp.sendRedirect("posts.jsp");
		return;
	}
}

