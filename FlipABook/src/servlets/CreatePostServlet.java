package servlets;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import objects.*;

@SuppressWarnings("serial")
public class CreatePostServlet extends HttpServlet {

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		HomePage.getInstance();
		FlipABookUser flipABookUser = HomePage.getUser(user);
		Entity flipABookUserEntity = flipABookUser.flipABookUser;
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
		boolean postExists = false;
		List<Post> posts = HomePage.posts;
		for (Post curPost : posts) {
			if (curPost.getIsbn().equals(isbn)) {
				flipABookUser.setRepeatPostAttempt();
				postExists = true;
				break;
			}
		}

		if (postExists || wrongPrice || nullFields || wrongIsbn) {
			resp.sendRedirect("createpost.jsp");
		} else {
			Post newPost = new Post(flipABookUserEntity, title, author, isbn, price, description);
			flipABookUser.addPost(newPost.post);
			resp.sendRedirect("/home");
		}
	}
}