<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Collections"%>
<%@ page import="objects.*"%>
<%@ page import="servlets.*"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
<meta name="description" content="">
<meta name="author" content="">
<link rel="icon" href="../favicon.ico">

<title>FlipABook</title>

<!-- Bootstrap core CSS -->
<link href="bootstrap/css/bootstrap.min.css" rel="stylesheet">

<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
<link href="bootstrap/assets/css/ie10-viewport-bug-workaround.css"
	rel="stylesheet">

<!-- Custom styles for this template -->
<link href="bootstrap/css/blog.css" rel="stylesheet">

<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>

<body>
	<%
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		HomePage.getInstance();
		boolean valid = Facade.verifyEmail(user);

		ArrayList<Conversation> conversations = new ArrayList<Conversation>();
		if (HomePage.conversations != null && user != null) {
			for (Conversation convo : HomePage.conversations) {
				if (convo.getConvoID().contains(user.getEmail())) {
					conversations.add(convo); //Only if you're in the convoID would you be involved in this conversation
				}
			}
		}
	%>
	<div class="blog-masthead">
		<div class="blog-masthead">
			<div class="container">
				<nav class="blog-nav"> <a class="blog-nav-item"
					href="../index.jsp">Home</a> <%
 	if (user != null && valid) {
 %> <a class="blog-nav-item" href="../advancedsearch.jsp">Advanced
					Search</a> <a class="blog-nav-item" href="../posts.jsp">Your Posts</a>
				<a class="blog-nav-item active" href="../messages.jsp">Messages</a>
				<a class="blog-nav-item" href="../account.jsp">Account Info</a> <a
					class="blog-nav-item"
					href="<%=userService.createLogoutURL(request.getRequestURI())%>">Log
					Out</a> <%
 	} else {
 %> <a class="blog-nav-item"
					href="<%=userService.createLoginURL(request.getRequestURI())%>">Log
					In</a> <%
 	}
 %> </nav>

			</div>
		</div>
	</div>

	<div class="container">
		<div class="blog-header">
			<h1 class="blog-title">
				<img src="bootstrap/assets/img/FlipABook.png">
			</h1>
			<h2 class="lead blog-description">
				<%
					if (user != null && valid) {
				%>Messages<%
					} else if(user != null && !valid) {
						%>You must be a UT student to use FlipABook.<%
					}
					else {
				%>You must be logged in to use this feature.<%
					}
				%>
			</h2>
			<%
				if (user != null && valid) {
					if (conversations.isEmpty()) {
			%>
			<p>You have no active conversations.</p>
			<%
				} else {
						//Back end is implemented! 
						//Please implement front-end asap
						for (Conversation current_convo : conversations) {
							pageContext.setAttribute("title", current_convo.getPost().getTitle());
							String seller = current_convo.getPost().getSeller().getEmail();
							String buyer = current_convo.getBuyer().getEmail();
							pageContext.setAttribute("seller", current_convo.getPost().getSeller().getEmail());
							pageContext.setAttribute("buyer", current_convo.getBuyer().getEmail());
							pageContext.setAttribute("convoID", current_convo.getConvoID());
							if (user.getEmail().equals(seller)) {
								pageContext.setAttribute("buyer_or_seller", "SELLER");
								pageContext.setAttribute("other_user", buyer);
							} else {
								pageContext.setAttribute("buyer_or_seller", "BUYER");
								pageContext.setAttribute("other_user", seller);
							}
			%>

			<!-- <div class="row"> -->

			<div class="blog-main">

				<div class="blog-post">
					<h2 class="blog-post-title">Conversation:
						${fn:escapeXml(title)}</h2>
					<p class="blog-post-meta">
						with <a href="#">${fn:escapeXml(other_user)}</a>
					</p>
					<p>***You are the ${fn:escapeXml(buyer_or_seller)}***</p>

					<%
						ArrayList<Message> messages = current_convo.getMessages();
									Collections.sort(messages); //Sorts messages by date
									for (Message message : messages) {
										pageContext.setAttribute("message_content", message.getContent());
										pageContext.setAttribute("message_sender", message.getSender().getUserInfo());
										//FlipABookUser sender = message.getSender();
					%>
					<p>${fn:escapeXml(message_sender)}said:
						${fn:escapeXml(message_content)}</p>


					<%
						if (message.getSender().getUserInfo().equals(user)) {
											//Messages should be displayed one way if you are sender
					%>



					<%
						} else {
											//Messages should be displayed a different way if you are receiver

										}

									}
					%>
					<form action="/message" method="get">
						<div>
							<textarea name="content" rows="1" cols="60" required></textarea>
						</div>
						<p></p>
						<div>
							<input type="submit" value="Send Message" align="middle" />
						</div>
						<input type="hidden" name="conversation"
							value="${fn:escapeXml(convoID)}" /> <input type="hidden"
							name="sender" value="${fn:escapeXml(user)}" />

					</form>

				</div>


				<!-- /.blog-post -->


			</div>
			<%
				}
			%>
			<!-- /.blog-main -->
			<!--</div>-->
			<!-- /.row -->
			<%
				}
				} else {
			%>
			<div class="blog-main">

				<div class="blog-post">
					<h3>
						<a href="../index.jsp">Return home</a> or <a
							href="<%=userService.createLoginURL(request.getRequestURI())%>">Log
							back in</a>
					</h3>
				</div>
			</div>
			<%
				}
			%>

		</div>
		<!-- /.container -->

		<footer class="blog-footer">
		<p>Created by Tye Macon, William "Stormy" Mauldin, Daniel
			Officewala, and Daniel Zhang.</p>
		<p>
			Blog template built for <a href="http://getbootstrap.com">Bootstrap</a>
			by <a href="https://twitter.com/mdo">@mdo</a>.
		</p>
		<p>
			<a href="#">Back to top</a>
		</p>
		</footer>


		<!-- Bootstrap core JavaScript
    ================================================== -->
		<!-- Placed at the end of the document so the pages load faster -->
		<script
			src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
		<script>
			window.jQuery
					|| document
							.write('<script src="bootstrap/assets/js/vendor/jquery.min.js"><\/script>')
		</script>
		<script src="bootstrap/js/bootstrap.min.js"></script>
		<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
		<script src="bootstrap/assets/js/ie10-viewport-bug-workaround.js"></script>
</body>
</html>
