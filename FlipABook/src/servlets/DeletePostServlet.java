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
		for (Post userPost: HomePage.posts) {
			if (userPost.getSeller().getUserInfo().equals(user)) {
				if (deleted.equals(userPost.getIsbn())){
					deleted_post = userPost;
				}
			}
		}
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
		System.out.println("Post deleted: " + postkey);
		datastore.delete(postkey);
		resp.sendRedirect("posts.jsp");

	}
}
