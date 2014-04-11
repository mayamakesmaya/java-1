<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.Collection"%>
<%@page import="java.util.List"%>
<%@page import="java.util.LinkedList"%>
<%@page import="java.util.Enumeration"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Adyen - Sample - Order Submit</title>
</head>
<body>
<%
	String url = "https://test.adyen.com/hpp/pay.shtml";
	String environment = request.getParameter( "environment");
	
	if (environment.equals ( "test-multipage"))
		url = "https://test.adyen.com/hpp/select.shtml";
	if ( environment.equals( "test-checkhmac"))
		url = "https://backoffice-test.adyen.com/psp/ca/skin/testhmac.shtml";
	if ( environment.equals( "test-skip"))
		url = "https://test.adyen.com/hpp/details.shtml";
	if ( environment.equals( "test-directory"))
		url = "https://test.adyen.com/hpp/directory.shtml";
	
	if ( environment.equals( "live-onepage"))
		url = "https://live.adyen.com/hpp/pay.shtml";
	if ( environment.equals( "live-multipage"))
		url = "https://live.adyen.com/hpp/select.shtml";
	if ( environment.equals( "live-skip"))
		url = "https://live.adyen.com/hpp/details.shtml";
	if ( environment.equals( "live-directory"))
		url = "https://live.adyen.com/hpp/directory.shtml";
%>

<form action="<%= url %>" method="post">

<% 
	Enumeration<String> names = request.getParameterNames();
	List<String> namesList = new LinkedList<String>();
	while ( names.hasMoreElements())
		namesList.add( names.nextElement());
	String urlCompleta = url + "?merchantSig=" + URLEncoder.encode( (String)request.getAttribute( "merchantSig"), "UTF-8") + "&";
	Collections.sort( namesList);
	for ( String name : namesList){
		if ( name.equals( "secret") || name.equals( "Assinar") || name.equals( "environment"))
			continue;
		urlCompleta += name + "=" + URLEncoder.encode( request.getParameter( name), "UTF-8") + "&";
		if ( name.equals("shopperStatement")){
		%>
			<textarea rows="4" cols="50" name="<%= name%>"><%= request.getParameter( name)%></textarea>
		<% } else { %>
			<input type="hidden" name="<%= name%>" value="<%= request.getParameter( name)%>" />
		<% } %>	
	<% } %>
	<br>
	merchantSig<br>
	<input type="text" name="merchantSig" value="<%= request.getAttribute( "merchantSig") %>" /><br>
	billingAddressSig<br>
	<input type="text" name="billingAddressSig" value="<%= request.getAttribute( "billingAddressSig") %>" /><br>
	shopperSig<br>
	<input type="text" name="shopperSig" value="<%= request.getAttribute( "shopperSig") %>" /><br>
	
	<input type="submit" name="Enviar">

</form>
<form>
urlCompleta<br>
<input type="text" name="urlCompleta" value="<%= urlCompleta%>" /><br>
</form>

</body>
</html>