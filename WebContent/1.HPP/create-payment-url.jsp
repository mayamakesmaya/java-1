<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title>Adyen - Create Payment URL</title>
	</head>
	<body>
		<!--
		Please note that printing $paymentUrl in the browser does some encoding to the URL causing it not to work,
		it should work putting $paymentUrl in a link.
		-->
		<a href="${paymentUrl}" target="_blank">Pay!</a>
	</body>
</html>