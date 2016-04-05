package flipabook;

import com.googlecode.objectify.ObjectifyService;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class CreatePostServlet extends HttpServlet {

	static {
		ObjectifyService.register(Post.class);
		ObjectifyService.register(FlipABookUser.class);
		ObjectifyService.register(Book.class);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		FlipABookUser flipABookUser = null;
		for (FlipABookUser flip : HomePage.users) {
			if (flip.getEmail().equals(user.getEmail())) {
				flipABookUser = flip;
				break;
			}
		}
		String title = req.getParameter("title");
		String isbn = req.getParameter("isbn");
		String tags = req.getParameter("tags"); // TODO change to a usable
												// format
		String description = req.getParameter("description");
		double price = Double.parseDouble(req.getParameter("price"));
		Book book = new Book(title, isbn, tags);
		Post post = new Post(flipABookUser, book, price, title, description);
		boolean postExists = false;
		boolean bookExists = false;
		for (Post curPost : HomePage.posts) {
			if (curPost.getBook().equals(book)) {
				bookExists = true;
				if (curPost.equals(post)) {
					postExists = true;
					break;
				}
			}
		}

		if (postExists) {
			// TODO notify user that he cannot have duplicate posts
			return;
		} else if (!bookExists) {
			ofy().save().entity(book).now();
		}
		ofy().save().entity(post).now();
		resp.sendRedirect("/index.jsp");
	}
}