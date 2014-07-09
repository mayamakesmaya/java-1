<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title>Adyen - Create Payment On Hosted Payment Page (HPP)</title>
	</head>
	<body>
		<form method="GET" action="https://test.adyen.com/hpp/pay.shtml" target="_blank">
			<input type="hidden" name="merchantReference" value="${merchantReference}">
			<input type="hidden" name="paymentAmount" value="${paymentAmount}">
			<input type="hidden" name="currencyCode" value="${currencyCode}">
			<input type="hidden" name="shipBeforeDate" value="${shipBeforeDate}">
			<input type="hidden" name="skinCode" value="${skinCode}">
			<input type="hidden" name="merchantAccount" value="${merchantAccount}">
			<input type="hidden" name="sessionValidity" value="${sessionValidity}">
			<input type="hidden" name="shopperLocale" value="${shopperLocale}">
			<input type="hidden" name="orderData" value="${orderData}">
			<input type="hidden" name="countryCode" value="${countryCode}">
			<input type="hidden" name="shopperEmail" value="${shopperEmail}">
			<input type="hidden" name="shopperReference" value="${shopperReference}">
			<input type="hidden" name="allowedMethods" value="${allowedMethods}">
			<input type="hidden" name="blockedMethods" value="${blockedMethods}">
			<input type="hidden" name="offset" value="${offset}">
			<input type="hidden" name="merchantSig" value="${merchantSig}">	
			<input type="submit" value="Create payment">
		</form>
	</body>
</html>