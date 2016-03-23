<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Collections"%>
<%@ page import="flipabook.Post"%>
<%@ page import="flipabook.Subscriber" %>
<%@ page import="flipabook.FlipABookUser"%>
<%@ page import="com.googlecode.objectify.*"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<html>
	<head>
		<link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
	</head>

<body>	
	
	<div id="top">
		<h1>
			<img src="FlipABook.png">
		</h1>
		<h1>
			UT's Premiere Peer-to-Peer Book Exchange Service
		</h1>
		
	</div>
	<%
		String blogName = request.getParameter("blogName");
		if (blogName == null) {
			blogName = "default";
		}
		pageContext.setAttribute("blogName", blogName);
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user != null) {
			pageContext.setAttribute("user", user);
	%>
		<input type="button" value="Sell a Book!" 
			onClick="window.location='sellABook.jsp';">
			
		<input type="button" value="Your Posts" 
			onClick="window.location='userPosts.jsp';">
			
		<input type="button" value="Messages" 
			onClick="window.location='messages.jsp';">
			
		<p>
			<a href="<%=userService.createLogoutURL(request.getRequestURI())%>"
			> or sign out</a>
		</p>	
	<%
	///////////////////////////CRON JOB
			ObjectifyService.register(FlipABookUser.class);
			ObjectifyService.register(Subscriber.class);
			List<Subscriber> subscribers = ObjectifyService.ofy().load().type(Subscriber.class).list();
			boolean isSubscribed = false;
			for(int i = 0; i < subscribers.size(); i++){
				if(subscribers.get(i).getUser().getEmail().equals(user.getEmail())){
					isSubscribed = true;
					%>
					<form action="/ofysubscribe" method="post">
		 	 		<div><input type="submit" name="unsubscribed" value="Unsubscribe" /></div>
					</form>
					<%
				}
			}
			if(!isSubscribed){
				%>
				<form action="/ofysubscribe" method="post">
			 		<div><input type="submit" name="subscribed" value="Subscribe(or whatever we could need the service for)" /></div>
				</form>
				<%
			}
	///////////////////////////CRON JOB		
			
		} else {
	%>
	
	<p>
		<a href="<%=userService.createLoginURL(request.getRequestURI())%>"
			>Sign in</a> to make and view posts.
	</p>
	
	<%
		}
		
	%>
		<h2>SEARCH BAR HERE MAYHAPS?</h2>
	<% 
		ObjectifyService.register(Post.class);
		List<Post> posts = ObjectifyService.ofy().load().type(Post.class).list();
		Collections.sort(posts);
		Collections.reverse(posts);
		if (posts.isEmpty()) {
	%>
		<p>There are no posts.</p>
	<%
		} else {
	%>
		
	
		<p>Posts: LIST OF ALL CURRENT BOOK ADVERTISEMENTS SORTED BY MOST RECENT</p>
	
	<%
			for (int i = 0; i<posts.size(); i++) {
					if (posts.get(i).getUser() != null) {
						pageContext.setAttribute("post_title", posts.get(i).getTitle());
						pageContext.setAttribute("post_content", posts.get(i).getContent());
						pageContext.setAttribute("post_user", posts.get(i).getUser());
						pageContext.setAttribute("post_date", posts.get(i).getDate());
					}
	%>
		<h2 style="color:01FF38">
				<b>${fn:escapeXml(post_title)}</b>
		</h2>
		
		<div style="color:97831F"
			class="content-box-gold">
			
			<blockquote>${fn:escapeXml(post_content)}</blockquote>
		</div>
		
		<blockquote>Posted by: ${fn:escapeXml(post_user.nickname)} 
			on ${fn:escapeXml(post_date)}</blockquote>

	<%
		}
	}
	%>
	
</body>
</html>