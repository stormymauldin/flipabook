<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Collections"%>
<%@ page import="objects.*"%>
<%@ page import="servlets.*"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ page
	import="com.google.appengine.api.datastore.DatastoreServiceFactory"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreService"%>
<%@ page import="com.google.appengine.api.datastore.Query"%>
<%@ page import="com.google.appengine.api.datastore.Entity"%>
<%@ page import="com.google.appengine.api.datastore.FetchOptions"%>
<%@ page import="com.google.appengine.api.datastore.Key"%>
<%@ page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@ page import="java.util.Date"%>
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
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		HomePage.getInstance();
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		FlipABookUser flipABookUser = null;
		boolean nullUser = true;
		boolean blockedUser = true;
		int userIndex = -1;
		if (user != null) {
			nullUser = false;
			userIndex = HomePage.users.indexOf(user);
			if (userIndex == -1) {
				new FlipABookUser(user);
				userIndex = HomePage.users.size() - 1;
			}

			if (!HomePage.flipABookUsers.get(userIndex).validEmail) {
				blockedUser = true;
			} else {
				flipABookUser = HomePage.flipABookUsers.get(userIndex);
				blockedUser = false;
			}
		} else {
			nullUser = true;
		}
	%>
	<div class="blog-masthead">
		<div class="blog-masthead">
			<div class="container">
				<nav class="blog-nav"> <a class="blog-nav-item"
					href="../index.jsp">Home</a> <%
 	if (!nullUser && !blockedUser) {
 %> <a class="blog-nav-item" href="../advancedsearch.jsp">Advanced
					Search</a> <a class="blog-nav-item" href="../posts.jsp">Your Posts</a>
				<a class="blog-nav-item" href="../messages.jsp">Messages</a> <a
					class="blog-nav-item" href="../scheduledmeetings.jsp">Scheduled
					Meetings</a> <a class="blog-nav-item active" href="../account.jsp">Account
					Info</a> <a class="blog-nav-item"
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
					if (!nullUser) {
				%>Account<%
					} else {
				%>You have been logged out.<%
					}
				%>
			</h2>
		</div>
		<%
			if (!nullUser) {
		%>

		<!-- <div class="row"> -->

		<div class="blog-main">

			<div class="blog-post">
				<h2 class="blog-post-title">Panel</h2>

				<p>Your information...</p>

			</div>

			<nav>
			<ul class="pager">
				<li><a href="#">Previous</a></li>
				<li><a href="#">Next</a></li>
			</ul>
			</nav>

		</div>
		<!-- /.blog-main -->
		<!--</div>-->
		<!-- /.row -->
		<%
			} else if (nullUser) {
		%>
		<div class="blog-main">

			<div class="blog-post">
				<h3>
					<a href="<%=userService.createLoginURL(request.getRequestURI())%>">Log
						in </a> to use FlipABook.
				</h3>
			</div>
		</div>
		<%
			} else if (blockedUser) {
		%>
		<div class="blog-main">

			<div class="blog-post">
				<h3>
					<font color="red">ERROR: Only those with valid @utexas.edu
						emails are allowed to use FlipABook.</font>
				</h3>
				<h3>
					<a href="<%=userService.createLoginURL(request.getRequestURI())%>">Log
						in with a valid @utexas.edu email</a> to use FlipABook.
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
