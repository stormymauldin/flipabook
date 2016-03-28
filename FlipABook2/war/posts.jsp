<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
<meta name="description" content="">
<meta name="author" content="">
<link rel="icon" href="bootstrap/assets/favicon.ico">

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

	<div class="blog-masthead">
		<div class="container">
			<nav class="blog-nav"> <a class="blog-nav-item"
				href="../index.jsp">Home</a> <a class="blog-nav-item"
				href="../advancedsearch.jsp">Advanced Search</a> <a
				class="blog-nav-item active" href="../posts.jsp">Your Posts</a> <a
				class="blog-nav-item" href="../messages.jsp">Messages</a> <a
				class="blog-nav-item" href="../scheduledmeetings.jsp">Scheduled
				Meetings</a> <a class="blog-nav-item" href="../account.jsp">Account
				Info</a> <a class="blog-nav-item" href="../loginlogout.jsp">Log
				In/Log Out</a> </nav>

		</div>
	</div>

	<div class="container">

		<div class="blog-header">
			<h1 class="blog-title">
				<img src="bootstrap/assets/img/FlipABook.png">
			</h1>
			<h2 class="lead blog-description">Your Posts</h2>
			<form class="navbar-form navbar-CENTER" role="search">
				<div class="input-group">
					<input type="text" class="form-control"
						placeholder="Search your posts..."> <span
						class="input-group-btn">
						<button type="submit" class="btn btn-default">
							<span class="glyphicon glyphicon-search"></span>
						</button>
					</span>
				</div>
			</form>
		</div>

		<!-- <div class="row"> -->

		<div class="blog-main">

			<div class="blog-post">
				<h2 class="blog-post-title">Post C</h2>
				<p class="blog-post-meta">
					on March 27, 2016
				</p>
				<ul style="text-align: left">
					<li>Author: Brandon McCartney</li>
					<li>ISBN: 123-456-789</li>
					<li>Asking Price: $5000.00</li>
					<li>Description: Great book, taught me everything. TYBG</li>
				</ul>
			</div>
			<!-- /.blog-post -->

			<div class="blog-post">
				<h2 class="blog-post-title">Post B</h2>
				<p class="blog-post-meta">
					on March 5, 2016
				</p>
				<ul style="text-align: left">
					<li>Author: Jonatan Aron Leandoer</li>
					<li>ISBN: 144-454-789</li>
					<li>Asking Price: $20.00</li>
					<li>Description: I cried Arizona tears.</li>
				</ul>
			</div>
			<!-- /.blog-post -->

			<div class="blog-post">
				<h2 class="blog-post-title">Post A</h2>
				<p class="blog-post-meta">
					on March 17, 2016
				</p>
				<ul style="text-align: left">
					<li>Author: Pancho Dollier</li>
					<li>ISBN: 414-039-215</li>
					<li>Asking Price: $1.00</li>
					<li>Description: Tastes great.</li>
				</ul>
			</div>
			<!-- /.blog-post -->

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
