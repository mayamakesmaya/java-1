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
				<li>pspReference: ${paymentResult.pspReference}</li>
				<li>resultCode: ${paymentResult.resultCode}</li>
				<li>authCode: ${paymentResult.authCode}</li>
				<li>refusalReason: ${paymentResult.refusalReason}</li>
			</ul>
		<% } %>
	</body>
</html>