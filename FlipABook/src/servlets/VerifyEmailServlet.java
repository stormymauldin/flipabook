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
public class VerifyEmailServlet extends HttpServlet {

	static {
		ObjectifyService.register(Post.class);
		ObjectifyService.register(FlipABookUser.class);
		ObjectifyService.register(Book.class);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
		HomePage.getInstance();
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		int index = HomePage.users.indexOf(user);
		String[] parsedEmail = user.getEmail().split("@");
		if(parsedEmail.length != 2 || !parsedEmail[1].equals("utexas.edu")){
			req.setAttribute("utexasemail", false);
		} else {
			HomePage.users.add(user);
			FlipABookUser flipABookUser = new FlipABookUser(user);
			ofy().save().entity(flipABookUser).now();
			HomePage.flipABookUsers.add(flipABookUser);
			//TODO: change redirect locaiton to user's home
			resp.sendRedirect("/index.jsp");
		}
	}
}
