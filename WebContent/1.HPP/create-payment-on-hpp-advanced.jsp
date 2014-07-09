<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title>Adyen - Create Payment On Hosted Payment Page (HPP) Advanced</title>
	</head>
	<body>
		<form method="POST" action="${hppUrl}" target="_blank">
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
			<input type="hidden" name="recurringContract" value="${recurringContract}">
			<input type="hidden" name="allowedMethods" value="${allowedMethods}">
			<input type="hidden" name="blockedMethods" value="${blockedMethods}">
			<input type="hidden" name="shopperStatement" value="${shopperStatement}">
			<input type="hidden" name="merchantReturnData" value="${merchantReturnData}">
			<input type="hidden" name="offset" value="${offset}">
			<input type="hidden" name="brandCode" value="${brandCode}">
			<input type="hidden" name="issuerId" value="${issuerId}">
			
			<!-- Billing address -->
			<input type="hidden" name="billingAddress.street" value="${billingAddressStreet}">
			<input type="hidden" name="billingAddress.houseNumberOrName" value="${billingAddressHouseNumberOrName}">
			<input type="hidden" name="billingAddress.city" value="${billingAddressCity}">
			<input type="hidden" name="billingAddress.postalCode" value="${billingAddressPostalCode}">
			<input type="hidden" name="billingAddress.stateOrProvince" value="${billingAddressStateOrProvince}">
			<input type="hidden" name="billingAddress.country" value="${billingAddressCountry}">
			<input type="hidden" name="billingAddressType" value="${billingAddressType}">
			
			<!-- Delivery address -->
			<input type="hidden" name="deliveryAddress.street" value="${deliveryAddressStreet}">
			<input type="hidden" name="deliveryAddress.houseNumberOrName" value="${deliveryAddressHouseNumberOrName}">
			<input type="hidden" name="deliveryAddress.city" value="${deliveryAddressCity}">
			<input type="hidden" name="deliveryAddress.postalCode" value="${deliveryAddressPostalCode}">
			<input type="hidden" name="deliveryAddress.stateOrProvince" value="${deliveryAddressStateOrProvince}">
			<input type="hidden" name="deliveryAddress.country" value="${deliveryAddressCountry}">
			<input type="hidden" name="deliveryAddressType" value="${deliveryAddressType}">
			
			<!-- Shopper -->
			<input type="hidden" name="shopper.firstName" value="${shopperFirstName}">
			<input type="hidden" name="shopper.infix" value="${shopperInfix}">
			<input type="hidden" name="shopper.lastName" value="${shopperLastName}">
			<input type="hidden" name="shopper.gender" value="${shopperGender}">
			<input type="hidden" name="shopper.dateOfBirthDayOfMonth" value="${shopperDateOfBirthDayOfMonth}">
			<input type="hidden" name="shopper.dateOfBirthMonth" value="${shopperDateOfBirthMonth}">
			<input type="hidden" name="shopper.dateOfBirthYear" value="${shopperDateOfBirthYear}">
			<input type="hidden" name="shopper.telephoneNumber" value="${shopperTelephoneNumber}">
			<input type="hidden" name="shopperType" value="${shopperType}">
			
			<!-- Signatures -->
			<input type="hidden" name="billingAddressSig" value="${billingAddressSig}">
			<input type="hidden" name="deliveryAddressSig" value="${deliveryAddressSig}">
			<input type="hidden" name="shopperSig" value="${shopperSig}">
			<input type="hidden" name="merchantSig" value="${merchantSig}">
			
			<input type="submit" value="Create payment">
		</form>
	</body>
</html>