<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Collections"%>
<%@ page import="objects.*"%>
<%@ page import="servlets.*"%>
<%@ page import="com.googlecode.objectify.*"%>
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
		HomePage.getInstance();
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
	%>
	<div class="blog-masthead">
		<div class="blog-masthead">
			<div class="container">
				<nav class="blog-nav"> <a class="blog-nav-item active"
					href="../index.jsp">Home</a> <%
 	if (user != null) {
 		int index = -1;
		for(int i = 0; i < HomePage.users.size(); i++){
			if(HomePage.users.get(i).compareTo(user) == 0){
				index = i;
				break;
			}
		}
 		ObjectifyService.register(FlipABookUser.class);
 		FlipABookUser flipABookUser = null;
 		if (index == -1) {
 			HomePage.users.add(user);
 			flipABookUser = new FlipABookUser(user);
 			ObjectifyService.ofy().save().entity(flipABookUser).now();
 			HomePage.flipABookUsers.add(flipABookUser);
 		} else { flipABookUser = HomePage.flipABookUsers.get(index);}
 		pageContext.setAttribute("user", user);
 		pageContext.setAttribute("flipabookuser", flipABookUser);
 %> <a class="blog-nav-item" href="../advancedsearch.jsp">Advanced
					Search</a> <a class="blog-nav-item" href="../posts.jsp">Your Posts</a>
				<a class="blog-nav-item" href="../messages.jsp">Messages</a> <a
					class="blog-nav-item" href="../scheduledmeetings.jsp">Scheduled
					Meetings</a> <a class="blog-nav-item" href="../account.jsp">Account
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
			<h2 class="lead blog-description">The University of Texas'
				Premier Book Exchange Service</h2>
			<%
				if (user != null) {
			%>
			<input type="button" value="Create a post"
				onClick="window.location='createPost.jsp';">
			<form class="navbar-form navbar-CENTER" role="search">
				<div class="input-group">
					<input type="text" class="form-control"
						placeholder="Search for a book..."> <span
						class="input-group-btn">
						
						<button type="submit" class="btn btn-default">
							<span class="glyphicon glyphicon-search"></span>
						</button>
					</span>
				</div>
			</form>
			<%
				}
			%>
		</div>
		<!-- <div class="row"> -->

		<div class="blog-main">
			<%
				if (user != null) {
					ObjectifyService.register(Post.class);
					ObjectifyService.register(Book.class);
					List<Post> posts = ObjectifyService.ofy().load().type(Post.class).list();
					Collections.sort(posts);
					Collections.reverse(posts);
					if (posts.isEmpty()) {
			%>
			<p>There are no recent posts.</p>
			<%
				} else {
						for (int i = 0; i < posts.size(); i++) {
							pageContext.setAttribute("title", posts.get(i).getTitle());
							pageContext.setAttribute("seller", posts.get(i).getSeller());
							pageContext.setAttribute("date", posts.get(i).getDate());
							pageContext.setAttribute("author", posts.get(i).getAuthor());
							pageContext.setAttribute("isbn", posts.get(i).getIsbn());
							pageContext.setAttribute("price", posts.get(i).getPrice());
							pageContext.setAttribute("description", posts.get(i).getDescription());
			%>
			<div class="blog-post">
				<h2 class="blog-post-title">${fn:escapeXml(title)}</h2>
				<p class="blog-post-meta">
					${fn:escapeXml(date)} by <a href="#">${fn:escapeXml(seller)}</a>
				</p>
				<ul style="text-align: left">
					<li>Author: ${fn:escapeXml(author)}</li>
					<li>ISBN: ${fn:escapeXml(isbn)}</li>
					<li>Asking Price: $ ${fn:escapeXml(price)}</li>
					<li>Description: ${fn:escapeXml(description)}</li>
				</ul>
			</div>
			<!-- /.blog-post -->
			<%
				}
			%>
		</div>
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
					<a href="<%=userService.createLoginURL(request.getRequestURI())%>">Log
						in</a> to use FlipABook.
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
