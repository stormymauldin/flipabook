<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Collections"%>
<%@ page import="blog.Post"%>
<%@ page import="blog.Subscriber"%>
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
			Stormy the Marmot's Blog de Graphic Design
		</h1>
		<h1>
			<img src="marmot.jpg"> <img src="marmot2.jpg">
		</h1>
		
	</div>
	<p>
			Hello! Welcome to Stormy the Marmot's Graphic Design blog.
			This is a place to come together to discuss and share ideas
			about graphic design. Obviously we are very good, but if you 
			have any ideas or tips to share we'd love to hear them!
	</p>
	
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
		<input type="button" value="Create a post" 
			onClick="window.location='createPost.jsp';">
		<p>
			<a href="<%=userService.createLogoutURL(request.getRequestURI())%>"
			> or sign out</a>
		</p>	
	<%
	///////////////////////////CRON JOB
			ObjectifyService.register(Subscriber.class);
			List<Subscriber> subscribers = ObjectifyService.ofy().load().type(Subscriber.class).list();
			boolean isSubscribed = false;
			for(int i = 0; i < subscribers.size(); i++){
				if(subscribers.get(i).getEmail().equals(user.getEmail())){
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
			 		<div><input type="submit" name="subscribed" value="Subscribe" /></div>
				</form>
				<%
			}
	///////////////////////////CRON JOB	
		} else {
	%>
	
	<p>
		<a href="<%=userService.createLoginURL(request.getRequestURI())%>"
			>Sign in</a> to make posts.
	</p>
	<%
		}	
		ObjectifyService.register(Post.class);
		List<Post> posts = ObjectifyService.ofy().load().type(Post.class).list();
		Collections.sort(posts);
		Collections.reverse(posts);
		if (posts.isEmpty()) {
	%>
	<p>There are no recent posts.</p>
	<%
		} else {
	%>
	
	<p>Recent posts:</p>
	
	<%
		for (Post post : posts) {
				if (post.getUser() != null) {
					pageContext.setAttribute("post_title", post.getTitle());
					pageContext.setAttribute("post_content", post.getContent());
					pageContext.setAttribute("post_user", post.getUser());
					pageContext.setAttribute("post_date", post.getDate());
	
				}
	%>
	
	<h2 style="color:01FF38">
			<b>${fn:escapeXml(post_title)}</b>
	</h2>
		
	<div style="color:97831F"
		class="content-box-gold">
		
		<blockquote>${fn:escapeXml(post_content)}</blockquote>
	</div>
	<blockquote>Posted by: ${fn:escapeXml(post_user.nickname)} on ${fn:escapeXml(post_date)}</blockquote>

	<%
		}
	}
	%>
	<input type="button" value="Back to Home" onClick="window.location='ofyblog.jsp';">
	<h1><img src="marmot3.jpg"></h1>
</body>
</html>