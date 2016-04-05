//http://tyestormyblog.appspot.com

package blog;

import com.googlecode.objectify.ObjectifyService;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class OfySubscribeServlet extends HttpServlet {

	static {
		ObjectifyService.register(Post.class);
		ObjectifyService.register(Subscriber.class);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if(req.getParameter("subscribed")!=null){
			ofy().save().entity(new Subscriber(user)).now();
			resp.sendRedirect("/ofyblog.jsp?subscribed=true");
		}
		else if(req.getParameter("unsubscribed")!=null){
			List<Subscriber> subscribers = ofy().load().type(Subscriber.class).list();
			for(int i = 0; i< subscribers.size(); i++){
				if(subscribers.get(i).getEmail().equals(user.getEmail())){
					ofy().delete().entity(subscribers.get(i)).now();
					break;
				}
			}
			resp.sendRedirect("/ofyblog.jsp?subscribed=false");
		}
	}
}