<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Collections"%>
<%@ page import="flipabook.Post"%>
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
		Compose Message
	</div>
	
	<form action="/ofypost" method="post">
		<div>
			<h3>Subject:</h3>
			<textarea name="title" rows="1" cols="60" required></textarea>
		</div>
		
		<div>
			<textarea name="content" rows="3" cols="60" required></textarea>
		</div>
		
		<div>
			<input type="submit" value="Send" /> <button type="button" onclick="location = 'home.jsp'">Cancel</button>
		</div>
		
		<input type="hidden" name="blogName" value="${fn:escapeXml(blogName)}" />
	</form>
	
	<h4>
			<img src="FlipABook.png">
	</h4>
	
	<%
		ObjectifyService.register(Post.class);		
	%>


</body>
</html>