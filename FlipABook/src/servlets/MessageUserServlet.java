package servlets;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import objects.Conversation;
import objects.FlipABookUser;
import objects.HomePage;
import objects.Post;

public class MessageUserServlet extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5038565026519203051L;
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		//This method simply adds a message to an existing conversation. 
		String convo = req.getParameter("conversation");
		String content = req.getParameter("content");
		HomePage.getInstance();
		UserService userService = UserServiceFactory.getUserService();
		User sender = userService.getCurrentUser();		

		Conversation conversation = HomePage.getConversation(convo);
		if (conversation == null){
			System.out.println("Requested Conversation not found");
			resp.sendRedirect("index.jsp");
		}
		else {
			System.out.println("Message: \"" + req.getParameter("content") + "\"\nConversation: " + req.getParameter("conversation"));
			conversation.newMessage(content, sender);
		}
		resp.sendRedirect("messages.jsp");
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		//This method instantiates a conversation. 
		UserService userService = UserServiceFactory.getUserService();
		User buyer = userService.getCurrentUser();		
		User seller = userService.getCurrentUser(); //needs to be instantiated, please ignore if fail
		String isbn = req.getParameter("message_isbn");
		String temp_seller = req.getParameter("message_seller"); //Deal with this later
		HomePage.getInstance();
		for (FlipABookUser temp_user: HomePage.flipABookUsers){
			if (temp_user.getEmail().equals(temp_seller)) {
				seller = temp_user.getUserInfo(); 
			}
		}
		Post reqPost = null; 
		for (Post temp_post: HomePage.posts){
			if (temp_post.getIsbn().equals(isbn) && temp_post.getSeller().getEmail().equals(temp_seller)){
				reqPost = temp_post; 
				break; 
			}
		}
		FlipABookUser temp_flip_user = HomePage.getUser(buyer);
		if (reqPost == null){
			System.out.println("Requested Post not found");
			resp.sendRedirect("index.jsp");
			return;
		}
		
		
		Conversation convo = new Conversation(reqPost, temp_flip_user);
		HomePage.conversations.add(convo);
		
		System.out.println("Message sent to user: " + req.getParameter("message_seller"));
		resp.sendRedirect("messages.jsp");
	}
}
