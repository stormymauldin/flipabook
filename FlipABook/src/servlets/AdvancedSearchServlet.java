package servlets;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import objects.Facade;
import objects.FlipABookUser;
import objects.HomePage;

@SuppressWarnings("serial")
public class AdvancedSearchServlet extends HttpServlet {


	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = Facade.getCurrentUser(userService);
		HomePage.getInstance();
		FlipABookUser flipABookUser = Facade.getFlipABookUser(user);

		String title = req.getParameter("title").toLowerCase();
		String isbn = req.getParameter("isbn").replaceAll("\\D", "").toLowerCase();
		String author = req.getParameter("author").toLowerCase();
		String keywords = req.getParameter("keywords").toLowerCase();
		boolean nullFields = false;
		if ((title == null || title.equals("")) && (isbn == null || isbn.equals(""))
				&& (author == null || author.equals("")) && (keywords == null || keywords.equals(""))) {
			flipABookUser.setNullFields();
			nullFields = true;
		}

		if (!nullFields) {
			Facade.advancedSearch(title, author, isbn, keywords);
			resp.sendRedirect("results.jsp");
		} else {
			resp.sendRedirect("advancedsearch.jsp");
		}
	}
}