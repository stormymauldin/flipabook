package servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.ObjectifyService;

import objects.Book;
import objects.FlipABookUser;
import objects.HomePage;
import objects.Post;

@SuppressWarnings("serial")
public class CreatePostServlet extends HttpServlet {

	static {
		ObjectifyService.register(FlipABookUser.class);
		ObjectifyService.register(Post.class);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		HomePage.getInstance();
		int index = -1;
		for (int i = 0; i < HomePage.users.size(); i++) {
			if (HomePage.users.get(i).compareTo(user) == 0) {
				index = i;
				break;
			}
		}
		FlipABookUser flipABookUser = HomePage.flipABookUsers.get(index);

		String title = req.getParameter("title");
		String isbn = req.getParameter("isbn");
		String author = req.getParameter("author");
		String description = req.getParameter("description");
		String price = req.getParameter("price");
		boolean nullFields = false;
		if (title == null || title.equals("") || isbn == null || isbn.equals("") || author == null || author.equals("")
				|| description == null || description.equals("") || price == null || price.equals("")) {
			flipABookUser.setNullFields();
			nullFields = true;
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
		List<Post> posts = ObjectifyService.ofy().load().type(Post.class).list();
		for (Post curPost : posts) {
			if (curPost.compareTo(post) == 0) {
				flipABookUser.setRepeatPostAttempt();
				postExists = true;
				break;
			}
		}

		if (postExists || wrongPrice || nullFields) {
			resp.sendRedirect("createpost.jsp");
		} else {
			HomePage.posts.add(post);
			ofy().save().entity(post).now();
			resp.sendRedirect("/home");
		}
	}
}