package servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.ObjectifyService;

import objects.Book;
import objects.FlipABookUser;
import objects.HomePage;
import objects.Post;

@SuppressWarnings("serial")
public class CreatePostServlet extends HttpServlet {

	static {
		ObjectifyService.register(Post.class);
		ObjectifyService.register(FlipABookUser.class);
		ObjectifyService.register(Book.class);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		HomePage.getInstance();
		int index = -1;
		for(int i = 0; i < HomePage.users.size(); i++){
			if(HomePage.users.get(i).compareTo(user) == 0){
				index = i;
				break;
			}
		}
		FlipABookUser flipABookUser = HomePage.flipABookUsers.get(index);
		
		String title = req.getParameter("title");
		String isbn = req.getParameter("isbn");
		String author = req.getParameter("author"); // TODO change to a usable
												// format
		String description = req.getParameter("description");
		String price = req.getParameter("price");
		Book book = new Book(title, author, isbn);
		Post post = new Post(flipABookUser, book, price, description);
		boolean postExists = false;
		boolean bookExists = false;
		for (Post curPost : HomePage.posts) {
			if (curPost.getBook().compareTo(book) == 0) {
				bookExists = true;
				if (curPost.compareTo(post) == 0) {
					postExists = true;
					break;
				}
			}
		}

		if (postExists) {
			resp.sendRedirect("/createpost.jsp?exists=true");
			return;
		} else if (!bookExists) {
			ofy().save().entity(book).now();
		}
		HomePage.posts.add(post);
		ofy().save().entity(post).now();
		
		resp.sendRedirect("/index.jsp");
	}
}