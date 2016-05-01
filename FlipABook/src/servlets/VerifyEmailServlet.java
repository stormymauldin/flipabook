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
public class VerifyEmailServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		HomePage.getInstance();
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		int index = HomePage.users.indexOf(user);
		String[] parsedEmail = user.getEmail().split("@");
		if (parsedEmail.length != 2 || !parsedEmail[1].equals("utexas.edu")) {
			req.setAttribute("utexasemail", false);
		} else {
			HomePage.users.add(user);
			FlipABookUser flipABookUser = new FlipABookUser(user);
			HomePage.flipABookUsers.add(flipABookUser);
			resp.sendRedirect("/index.jsp");
		}
	}
}
