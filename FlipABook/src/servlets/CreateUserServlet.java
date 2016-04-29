package servlets;

import objects.Book;
import objects.FlipABookUser;
import objects.HomePage;
import objects.Post;

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

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
		HomePage.getInstance();
		UserService userService = UserServiceFactory.getUserService();
		resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));

		resp.sendRedirect("/verifyemail");
	}
}
