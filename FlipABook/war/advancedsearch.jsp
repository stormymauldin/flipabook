<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
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
		User user = Facade.getCurrentUser(userService);
		HomePage.getInstance();
		FlipABookUser flipABookUser = null;
	%>
	<div class="blog-masthead">
		<div class="blog-masthead">
			<div class="container">
				<nav class="blog-nav"> <a class="blog-nav-item"
					href="../index.jsp">Home</a> <%
 	if (user != null) {
 		if (!Facade.verifyEmail(user)) {
			response.sendRedirect(userService.createLogoutURL(request.getRequestURI()));
		}
 		pageContext.setAttribute("user", user);
 		flipABookUser = Facade.getFlipABookUser(user);
 %> <a class="blog-nav-item active" href="../advancedsearch.jsp">Advanced
					Search</a> <a class="blog-nav-item" href="../posts.jsp">Your Posts</a>
				<a class="blog-nav-item" href="../messages.jsp">Messages</a> <a
					class="blog-nav-item" href="../account.jsp">Account Info</a> <a
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
			<h2 class="lead blog-description">Advanced search</h2>


			<%
				if (flipABookUser != null) {
					if (flipABookUser.nullFields()) {
						flipABookUser.removeNullFields();
			%>
			<h2 class="lead blog-description">
				<font color="red">ERROR: Please fill out at least one field.</font>
			</h2>
			<%
				}
			%>

		</div>

		<!-- <div class="row"> -->

		<div class="blog-main">
			<form action="/advancedsearch" method="post">
				<div>
					<h4>Search by title...</h4>
					<textarea name="title" rows="1" cols="60"></textarea>
				</div>

				<div>
					<h4>Search by author...</h4>
					<textarea name="author" rows="1" cols="60"></textarea>
				</div>

				<div>
					<h4>Search by ISBN...</h4>
					<textarea name="isbn" rows="1" cols="60"></textarea>
				</div>

				<div>
					<h4>Search by key words...</h4>
					<textarea name="keywords" rows="3" cols="60"></textarea>
				</div>

				<div>
					<input type="submit" value="Search" /> <input type="button"
						onclick="location = 'home'" value="Cancel" />
				</div>
			</form>
		</div>
		<!-- /.blog-main -->
		<!--</div>-->
		<!-- /.row -->
		<%
			} else {
		%>
		<div class="blog-main">

			<div class="blog-post">
				<h3>
					<a href="<%=userService.createLoginURL(request.getRequestURI())%>">Log
						in</a> (with a valid UT email) to use FlipABook.
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
