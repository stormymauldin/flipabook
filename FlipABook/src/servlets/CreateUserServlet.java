package servlets;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import objects.*;

@SuppressWarnings("serial")
public class CreateUserServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
		HomePage.getInstance();
		UserService userService = UserServiceFactory.getUserService();
		resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
	//	User user = userService.getCurrentUser();
	//	new FlipABookUser(user);
	//	current.verifyEmail();
		resp.sendRedirect("/verifyemail");
	}
}
