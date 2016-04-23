package servlets;

//import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

//import com.googlecode.objectify.ObjectifyService;

import objects.FlipABookUser;
import objects.HomePage;
import objects.Post;

@SuppressWarnings("serial")
public class CreatePostServlet extends HttpServlet {

//	static {
//		ObjectifyService.register(FlipABookUser.class);
//		ObjectifyService.register(Post.class);
//	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		HomePage.getInstance();
		FlipABookUser flipABookUser = HomePage.getUser(user);

		String title = req.getParameter("title");
		String isbn = req.getParameter("isbn").replaceAll("\\D", "");
		String author = req.getParameter("author");
		String description = req.getParameter("description");
		String price = req.getParameter("price");
		boolean nullFields = false;
		if (title == null || title.equals("") || isbn == null || isbn.equals("") || author == null || author.equals("")
				|| description == null || description.equals("") || price == null || price.equals("")) {
			flipABookUser.setNullFields();
			nullFields = true;
		}
		boolean wrongIsbn = false;
		if (isbn.length() != 10 && isbn.length() != 13) {
			wrongIsbn = true;
			flipABookUser.setWrongIsbn();
		}

		boolean wrongPrice = false;
		try {
			double parsed = Double.parseDouble(price);
			if (parsed < 0) {
				flipABookUser.setWrongPrice();
				wrongPrice = true;
			}
			DecimalFormat moneyFormat = new DecimalFormat("#.00");
			price = moneyFormat.format(parsed);
		} catch (NumberFormatException e) {
			flipABookUser.setWrongPrice();
			wrongPrice = true;
		}
		Post post = new Post(flipABookUser, title, author, isbn, price, description);
		boolean postExists = false;
		
//		List<Post> posts = ObjectifyService.ofy().load().type(Post.class).list();
		List<Post> posts = HomePage.posts;
		for (Post curPost : posts) {
			if (curPost.compareTo(post) == 0) {
				flipABookUser.setRepeatPostAttempt();
				postExists = true;
				break;
			}
		}

		if (postExists || wrongPrice || nullFields || wrongIsbn) {
			resp.sendRedirect("createpost.jsp");
		} else {
			//This will add the post to the datastore (YES WE ARE USING THE DATASTORE NOW BECAUSE OBJECTIFY IS EVIL) 
			HomePage.posts.add(post);
			//Key will be the ISBN of the book followed by the USERNAME (please remember this)
			String specific_post_key = isbn + user.getEmail(); 
			Key postkey = KeyFactory.createKey("Post", specific_post_key);
			Entity post_datastore = new Entity("Post", postkey);
			post_datastore.setProperty("title", title);
			post_datastore.setProperty("user", user);
			post_datastore.setProperty("date", post.getDate());
			post_datastore.setProperty("isbn", isbn);
			post_datastore.setProperty("author", author);
			post_datastore.setProperty("description", description);
			post_datastore.setProperty("price", price);
	        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	        datastore.put(post_datastore);
//			ofy().save().entity(post).now();
			resp.sendRedirect("/home");
		}
	}
}