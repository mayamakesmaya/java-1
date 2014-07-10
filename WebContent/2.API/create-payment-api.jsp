<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title>Adyen - Create Payment through the API (SOAP)</title>
	</head>
	<body>
		<% if (request.getAttribute("paymentResult") == null) { %>
			<form method="POST" action="#handler" target="_blank">
				<input type="submit" value="Create payment">
			</form>
		<% } else { %>
			<p><strong>Payment result:</strong></p>
			<ul>
				<li>pspReference: ${pspReference}</li>
				<li>resultCode: ${resultCode}</li>
				<li>authCode: ${authCode}</li>
				<li>refusalReason: ${refusalReason}</li>
			</ul>
		<% } %>
	</body>
</html>