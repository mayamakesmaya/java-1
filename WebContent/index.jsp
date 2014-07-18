<%@ page import="java.util.*"%>

<%
	// Collect all registered servlets
	TreeSet<String> servlets = new TreeSet<String>();

	for (ServletRegistration servlet : request.getServletContext().getServletRegistrations().values())
		servlets.addAll(servlet.getMappings());
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Adyen Java Examples</title>
</head>
<body>
	<h1>Adyen Java Examples</h1>
	<ul>
		<%
			// Display a list of all example servlets
			for (String servlet : servlets) {
				if (servlet.matches("(.*)\\d\\.(.*)")) {
					String servletURL = servlet.substring(1);
					out.println("<li><a href=\"" + servletURL + "\" target=\"_blank\">" + servletURL + "</a></li>");
				}
			}
		%>
	</ul>
</body>
</html>