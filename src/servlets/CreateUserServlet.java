package servlets;

import com.googlecode.objectify.ObjectifyService;

import objects.Book;
import objects.FlipABookUser;
import objects.HomePage;
import objects.Post;

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
public class CreateUserServlet extends HttpServlet {

	static {
		ObjectifyService.register(Post.class);
		ObjectifyService.register(FlipABookUser.class);
		ObjectifyService.register(Book.class);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
		HomePage.getInstance();
		UserService userService = UserServiceFactory.getUserService();
		resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));

		resp.sendRedirect("/verifyemail");
	}
}
