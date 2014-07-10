<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title>Adyen - Client-Side Encryption (SOAP)</title>
	</head>
	<body>
		<% if (request.getAttribute("paymentResult") == null) { %>
			<form method="POST" action="#handler" target="_blank" id="adyen-encrypted-form">
				<fieldset>
					<legend>Card Details</legend>
					
					<div class="field">
						<label for="adyen-encrypted-form-number">Card Number
							<input type="text" id="adyen-encrypted-form-number" value="5555444433331111" size="20" autocomplete="off" data-encrypted-name="number">
						</label>
					</div>
					
					<div class="field">
						<label for="adyen-encrypted-form-holder-name">Card Holder Name
							<input type="text" id="adyen-encrypted-form-holder-name" value="John Doe" size="20" autocomplete="off" data-encrypted-name="holderName">
						</label>
					</div>
					
					<div class="field">
						<label for="adyen-encrypted-form-cvc">CVC
							<input type="text" id="adyen-encrypted-form-cvc" value="737" size="4" autocomplete="off" data-encrypted-name="cvc">
						</label>
					</div>
					
					<div class="field">
						<label for="adyen-encrypted-form-expiry-month">Expiration Month (MM)
							<input type="text" value="06" id="adyen-encrypted-form-expiry-month" size="2" autocomplete="off" data-encrypted-name="expiryMonth"> /
						</label>
						<label for="adyen-encrypted-form-expiry-year">Expiration Year (YYYY)
							<input type="text" value="2016" id="adyen-encrypted-form-expiry-year" size="4" autocomplete="off" data-encrypted-name="expiryYear">
						</label>
					</div>
					
					<div class="field">
						<input type="hidden" id="adyen-encrypted-form-expiry-generationtime" value="${generationTime}" data-encrypted-name="generationtime">
						<input type="submit" value="Create payment">
					</div>
				</fieldset>
			</form>
		
			<script type="text/javascript" src="../../js/adyen.encrypt.min.js"></script>
			<script type="text/javascript">
				var form = document.getElementById('adyen-encrypted-form');
				
				// Put your WS users' CSE key here
				// Adyen CA -> Settings -> Users -> Choose the WS user -> Copy CSE key
				var key = "YOUR-KEY-HERE-YOUR-KEY-HERE-YOUR-KEY-HERE-YOUR-KEY-HERE"
						+ "YOUR-KEY-HERE-YOUR-KEY-HERE-YOUR-KEY-HERE-YOUR-KEY-HERE"
						+ "YOUR-KEY-HERE-YOUR-KEY-HERE-YOUR-KEY-HERE-YOUR-KEY-HERE"
						+ "YOUR-KEY-HERE-YOUR-KEY-HERE-YOUR-KEY-HERE-YOUR-KEY-HERE"
						+ "YOUR-KEY-HERE-YOUR-KEY-HERE-YOUR-KEY-HERE-YOUR-KEY-HERE"
						+ "YOUR-KEY-HERE-YOUR-KEY-HERE-YOUR-KEY-HERE-YOUR-KEY-HERE"
						+ "YOUR-KEY-HERE-YOUR-KEY-HERE-YOUR-KEY-HERE-YOUR-KEY-HERE"
						+ "YOUR-KEY-HERE-YOUR-KEY-HERE-YOUR-KEY-HERE-YOUR-KEY-HERE"
						+ "YOUR-KEY-HERE-YOUR-KEY-HERE-YOUR-KEY-HERE-YOUR-KEY-HERE";
				
				adyen.encrypt.createEncryptedForm(form, key, {});
			</script>
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