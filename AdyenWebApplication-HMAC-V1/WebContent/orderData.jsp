<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Adyen - Sample - Order Data</title>
</head>
<body>
<form action="<%= request.getContextPath()%>/servlet/submitPayment" method="POST">

	<br>
	URL to post to
	<select id="Environment" name="environment">
		<option value="test-onepage">test-onepage</option>
		<option value="test-multipage">test-multipage</option>
		<option value="test-checkhmac">test-checkhmac</option>
		<option value="test-directory">test-directory</option>
		<option value="test-skip">test-skip</option>
		<option value="live-onepage">live-onepage</option>
		<option value="live-multipage">live-multipage</option>
		<option value="live-checkhmac">live-checkhmac</option>
		<option value="live-directory">live-directory</option>
		<option value="live-skip">test-skip</option>
	</select>
	<br>
	<br>
	<b>Parameters</b>
	<br>
	<br>
	skinCode*
	<br>
	<input type="text" name="skinCode" value="" />
	<br>
	secret* (make sure is not in the production form)
	<br>
	<input type="text" name="secret" value="" />
	<br>
	merchantAccount*
	<br>
	<input type="text" name="merchantAccount" value="[MerchantAccountCode]" /><br>
	shopperLocale
	<br>
	<input type="text" name="shopperLocale" value="en_GB" />
	<br>
	countryCode (Required for directory.shtml)
	<br>
	<input type="text" name="countryCode" value="NL" /><br>
	<br>	
	merchantReference*
	<br>
	<input type="text" name="merchantReference" value="" />
	<br>
	currencyCode*
	<br>
	<input type="text" name="currencyCode" value="EUR" />
	<br>
	paymentAmount*
	<br>
	<input type="text" name="paymentAmount" value="1000" />
	<br>
	shipBeforeDate*
	<br>
	<input type="text" name="shipBeforeDate" value="2014-11-10" />
	<br>
	sessionValidity*
	<br>
	<input type="text" name="sessionValidity" value="2014-12-31T11:00:00Z" />
	<br>
	offset
	<br>
	<input type="text" name="offset" value="0" />
	<br>
	
	<br>
	offerEmail
	<br>
	<input type="text" name="offerEmail" value="prompt"/>
	<br>

	<br>
	shopperEmail
	<br>
	<input type="text" name="shopperEmail" value="test@email.com" />
	<br>
	boletobancario.sendEmail
	<br>
	<input type="text" name="boletobancario.sendByEmail" value="false" />
	<br>
	shopperReference
	<br>
	<input type="text" name="shopperReference" value="hmacTestShopperRef" />
	<br>
	recurringContract
	<br>
	<input type="text" name="recurringContract" value="ONECLICK" />
	<br>
	shopperStatement
	<br>	
	<textarea rows="4" cols="50" name="shopperStatement">Order 082236</textarea>
	<br>
	merchantReturnData
	<br>
	<input type="text" name="merchantReturnData" value="Testing merchantReturnData field" />
	<br>
	orderData
	<br>
	<input type="text" name="orderData" value="" /><!--E.g.: H4sIAAAAAAAAALMpsOPlCkssyswvLVZIz89PKVZIzEtRKE4tKstMTi3W4+Wy0S+wAwDOGUCXJgAAAA==-->
	<br>
	<br>
	
	<b>Shopper Details</b>
	<br>
	shopperType
	<select id="shopperType" name="shopperType">
		<option value="">modifiable/visible</option>
		<option value="1">unmodifiable/visible</option>
		<option value="2">unmodifiable/invisible</option>
	</select>
	<br>
	shopper.firstName
	<br>
	<input type="text" name="shopper.firstName" value="Test" />
	<br>
	shopper.infix
	<br>
	<input type="text" name="shopper.infix" value="" />
	<br>
	shopper.lastName
	<br>
	<input type="text" name="shopper.lastName" value="Shopper" />
	<br>
	shopper.socialSecurityNumber
	<br>
	<input type="text" name="shopper.socialSecurityNumber" value="44552272888" />
	<br>
	<br>
	
	<b>Delivery Address Details</b>
	<br>
	deliveryAddressType
	<select id="deliveryAddressType" name="deliveryAddressType">
		<option value="">modifiable/visible</option>
		<option value="1">unmodifiable/visible</option>
		<option value="2">unmodifiable/invisible</option>
	</select>
	<br>
	deliveryAddress.street
	<br>
	<input type="text" name="deliveryAddress.street" value="Test Street" />
	<br>
	deliveryAddress.houseNumberOrName
	<br>
	<input type="text" name="deliveryAddress.street.houseNumberOrName" value="999" />
	<br>
	deliveryAddress.city
	<br>
	<input type="text" name="deliveryAddress.city" value="Amsterdam" />
	<br>
	deliveryAddress.postalCode
	<br>
	<input type="text" name="deliveryAddress.postalCode" value="1000BB" /><!-- Without dash -->
	<br> 
	deliveryAddress.stateOrProvince<br>
	<input type="text" name="deliveryAddress.stateOrProvince" value="NH" /><!-- 2 letters -->
	<br> 
	deliveryAddress.country
	<br>
	<input type="text" name="deliveryAddress.country" value="NL" />
	<br>
	<br>
	
	<b>Billing Address Details</b>
	<br>
	billingAddressType
	<select id="billingAddressType" name="billingAddressType">
		<option value="">modifiable/visible</option>
		<option value="1">unmodifiable/visible</option>
		<option value="2">unmodifiable/invisible</option>
	</select>
	<br>
	billingAddress.street
	<br>
	<input type="text" name="billingAddress.street" value="Test Street" />
	<br>
	billingAddress.houseNumberOrName
	<br>
	<input type="text" name="billingAddress.houseNumberOrName" value="999" />
	<br>
	billingAddress.city
	<br>
	<input type="text" name="billingAddress.city" value="Amsterdam" />
	<br>
	billingAddress.postalCode
	<br>
	<input type="text" name="billingAddress.postalCode" value="1000BB" /><!-- Without dash -->
	<br> 
	billingAddress.stateOrProvince<br>
	<input type="text" name="billingAddress.stateOrProvince" value="NH" /><!-- 2 letters -->
	<br> 
	billingAddress.country
	<br>
	<input type="text" name="billingAddress.country" value="NL" />
	<br>
	<br>
	
	
	
	<b>Restricting available payment methods</b> (Recommendation:use in combination with the results of directory.shtml URL)
	<br>
	allowedMethods
	<br>
	<input type="text" name="allowedMethods" value="visa,mc,amex" />
	<br>
	blockedMethods
	<br>
	<input type="text" name="blockedMethods" value="" />
	<br>
	
	<br>
	<b>Pre-selection of the payment method</b>(Just valid for details.shtml URL)
	<br>
	brandCode
	<br>
	<input type="text" name="brandCode" value="visa" />
	<br>
	issuerId
	<br>
	<input type="text" name="issuerId" value="" />
	<br>
	<br>
	
	<input type="submit" name="Submit">

</form>
</body>
</html>