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
public class VerifyEmailServlet extends HttpServlet {


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
			//ofy().save().entity(flipABookUser).now();
			HomePage.flipABookUsers.add(flipABookUser);
			//TODO: change redirect locaiton to user's home
			//TODO: add to datastore
			resp.sendRedirect("/index.jsp");
		}
	}
}
