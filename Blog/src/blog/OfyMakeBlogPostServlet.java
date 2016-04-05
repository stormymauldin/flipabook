//http://tyestormyblog.appspot.com

package blog;

import com.googlecode.objectify.ObjectifyService;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class OfyMakeBlogPostServlet extends HttpServlet {

	static {
		ObjectifyService.register(Post.class);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		ofy().save().entity(new Post(user, req.getParameter("title"), req.getParameter("content"))).now();
		resp.sendRedirect("/ofyblog.jsp");
	}
}