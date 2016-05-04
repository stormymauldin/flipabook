package servlets;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import objects.Facade;
import objects.HomePage;

@SuppressWarnings("serial")
public class BasicSearchServlet extends HttpServlet {

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		HomePage.getInstance();

		String keywords = req.getParameter("keywords").toLowerCase();

		Facade.basicSearch(keywords);
		resp.sendRedirect("results.jsp");
	}
}