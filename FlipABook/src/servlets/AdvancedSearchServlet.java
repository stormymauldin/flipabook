package servlets;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import objects.FlipABookUser;
import objects.HomePage;

@SuppressWarnings("serial")
public class AdvancedSearchServlet extends HttpServlet {


	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		HomePage.getInstance();
		FlipABookUser flipABookUser = HomePage.getUser(user);

		String title = req.getParameter("title");
		String isbn = req.getParameter("isbn").replaceAll("\\D", "");
		String author = req.getParameter("author");
		String keywords = req.getParameter("keywords");
		boolean nullFields = false;
		if ((title == null || title.equals("")) && (isbn == null || isbn.equals(""))
				&& (author == null || author.equals("")) && (keywords == null || keywords.equals(""))) {
			flipABookUser.setNullFields();
			nullFields = true;
		}

		if (!nullFields) {
			HomePage.advancedSearch(title, author, isbn, keywords);
			resp.sendRedirect("results.jsp");
		} else {
			resp.sendRedirect("advancedsearch.jsp");
		}
	}
}