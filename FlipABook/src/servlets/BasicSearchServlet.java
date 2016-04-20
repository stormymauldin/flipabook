package servlets;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.ObjectifyService;

import objects.FlipABookUser;
import objects.HomePage;
import objects.Post;

@SuppressWarnings("serial")
public class BasicSearchServlet extends HttpServlet {

	static {
		ObjectifyService.register(FlipABookUser.class);
		ObjectifyService.register(Post.class);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		HomePage.getInstance();

		String keywords = req.getParameter("keywords");

		HomePage.basicSearch(keywords);
		resp.sendRedirect("results.jsp");
	}
}