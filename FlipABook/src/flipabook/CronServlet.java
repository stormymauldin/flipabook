//http://tyestormyblog.appspot.com

package flipabook;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.ObjectifyService;

@SuppressWarnings("serial")
public class CronServlet extends HttpServlet {
	// Resources:
	// http://rominirani.com/2009/11/16/episode-9-using-the-cron-service-to-run-scheduled-tasks/
	// https://cloud.google.com/appengine/docs/java/mail/

	private static final Logger _logger = Logger.getLogger(CronServlet.class.getName());

	static {
		ObjectifyService.register(Post.class);
		ObjectifyService.register(Subscriber.class);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		String msgBody = "***************************Posts:***************************";

		try {
			_logger.info("Cron Job has been executed");
			List<Post> posts = ObjectifyService.ofy().load().type(Post.class).list();
			List<Subscriber> subscribers = ObjectifyService.ofy().load().type(Subscriber.class).list();
			Collections.sort(posts);
			Collections.reverse(posts);
			for (int i = 0; i < posts.size(); i++) {
				msgBody += "\n\n\n" + posts.get(i).getEmailablePost() + "\n____________________________________________________";
				Message msg = new MimeMessage(session);
				msg.setFrom(new InternetAddress("string@flipabook-1242.appspotmail.com", "The FlipABook Team"));
				msg.addRecipient(Message.RecipientType.TO,
						new InternetAddress(subscribers.get(i).getFlipABookUser().getEmail(), subscribers.get(i).getFlipABookUser().getNickname()));
				msg.setSubject("Daily Email Digest for FlipABook Posts");
				msg.setText(msgBody);
				Transport.send(msg);
			}

		} catch (Exception e) {
			System.out.println("Failed to send.");
		}
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
}
