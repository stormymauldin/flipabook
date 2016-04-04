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
		for(FlipABookUser flip : HomePage.users)
		{
			if(flip.getEmail().equals(user.getEmail())){
				flipABookUser = flip;
				break;
			}
		}
		Book book = new Book(req.getParameter("title"), req.getParameter("isbn"), req.getParameter("tags"));
		double price = Double.parseDouble(req.getParameter("price"));
		ofy().save().entity(book).now();
		ofy().save().entity(new Post(flipABookUser, book, price, book.getTitle(), req.getParameter("description"))).now();
		resp.sendRedirect("/index.jsp");
	}
}