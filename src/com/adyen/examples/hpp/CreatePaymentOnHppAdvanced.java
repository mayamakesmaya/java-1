package com.adyen.examples.hpp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

/**
 * Create Payment On Hosted Payment Page (HPP) Advanced
 * 
 * The Adyen Hosted Payment Pages (HPPs) provide a flexible, secure and easy way to allow shoppers to pay for goods or
 * services. Rather than submitting a simple request containing the required fields we offer a possibility to post even
 * more variables to our HPP. This code example will show you which variables can be posted and why.
 * 
 * @link /1.HPP/CreatePaymentOnHppAdvanced
 * @author Created by Adyen - Payments Made Easy
 */

@WebServlet(urlPatterns = { "/1.HPP/CreatePaymentOnHppAdvanced" })
public class CreatePaymentOnHppAdvanced extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/**
		 * Defining variables
		 * The HPP requires certain variables to be posted in order to create a payment possibility for the shopper.
		 * 
		 * The variables that you can post to the HPP are the following:
		 * 
		 * <pre>
		 * merchantReference    : The merchant reference is your reference for the payment
		 * paymentAmount        : Amount specified in minor units EUR 1,00 = 100
		 * currencyCode         : The three-letter capitalised ISO currency code to pay in i.e. EUR
		 * shipBeforeDate       : The date by which the goods or services are shipped.
		 *                        Format: YYYY-MM-DD
		 * skinCode             : The skin code that should be used for the payment
		 * merchantAccount      : The merchant account you want to process this payment with.
		 * sessionValidity      : The final time by which a payment needs to have been made. 
		 *                        Format: YYYY-MM-DDThh:mm:ssTZD
		 * shopperLocale        : A combination of language code and country code to specify 
		 *                        the language used in the session i.e. en_GB.
		 * orderData            : A fragment of HTML/text that will be displayed on the HPP, for example to show the basket (optional)
		 * countryCode          : Country code according to ISO_3166-1_alpha-2 standard  (optional)
		 * shopperEmail         : The e-mailaddress of the shopper (optional)
		 * shopperReference     : The shopper reference, i.e. the shopper ID (optional)
		 * recurringContract    : Can be "ONECLICK","RECURRING" or "ONECLICK,RECURRING", this allows you to store
		 *                        the payment details as a ONECLICK and/or RECURRING contract. Please note that if you supply
		 *                        recurringContract, shopperEmail and shopperReference become mandatory. Please
		 *                        view the recurring examples in the repository as well. 
		 * allowedMethods       : Allowed payment methods separated with a , i.e. "ideal,mc,visa" (optional)
		 * blockedMethods       : Blocked payment methods separated with a , i.e. "ideal,mc,visa" (optional)
		 * shopperStatement     : To submit a variable shopper statement you can set the shopperStatement field in the payment request.
		 * merchantReturnData   : This field willl be passed back as-is on the return URL when the shopper completes (or abandons) the 
		 *                        payment and returns to your shop. (optional)
		 * offset               : Numeric value that will be added to the fraud score (optional)
		 * brandCode            : The payment method the shopper likes to pay with, i.e. ideal (optional)
		 * issuerId             : If brandCode specifies a redirect payment method, the issuer can be 
		 *                        defined here forcing the HPP to redirect directly to the payment method. (optional)
		 * merchantSig          : The HMAC signature used by Adyen to test the validy of the form;
		 * </pre>
		 */

		// Generate dates
		Calendar calendar = Calendar.getInstance();
		Date currentDate = calendar.getTime(); // current date
		calendar.add(Calendar.DATE, 1);
		Date sessionDate = calendar.getTime(); // current date + 1 day
		calendar.add(Calendar.DATE, 2);
		Date shippingDate = calendar.getTime(); // current date + 3 days

		// Define variables
		String merchantReference = "TEST-PAYMENT-" + new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(currentDate);
		String paymentAmount = "199";
		String currencyCode = "EUR";
		String shipBeforeDate = new SimpleDateFormat("yyyy-MM-dd").format(shippingDate);
		String skinCode = "YourSkinCode";
		String merchantAccount = "YourMerchantAccount";
		String sessionValidity = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(sessionDate);
		String shopperLocale = "en_US";
		String orderData = compressString("Orderdata to display on the HPP can be put here");
		String countryCode = "NL";
		String shopperEmail = "";
		String shopperReference = "";
		String recurringContract = "";
		String allowedMethods = "";
		String blockedMethods = "";
		String shopperStatement = "";
		String merchantReturnData = "";
		String offset = "";

		// By providing the brandCode and issuerId the HPP will redirect the shopper directly to the redirect payment method.
		// Please note: the form should be posted to https://test.adyen.com/hpp/details.shtml rather than pay.shtml.
		// While posting to details.shtml countryCode becomes a required as well.
		String brandCode = "";
		String issuerId = "";

		/**
		 * Collecting Shopper Information
		 * 
		 * Address Verification System (AVS) is a security feature that verifies the billing address and/or
		 * delivery address and/or shopper information of the card holder. To enable AVS the Billing Address Fields
		 * (AVS) field must be checked under Skin Options for each skin you wish to use. The following variables
		 * can be send to the HPP:
		 * 
		 * 1. Billing address;
		 * - billingAddress.street: The street name
		 * - billingAddress.houseNumberOrName: The house number
		 * - billingAddress.city: The city.
		 * - billingAddress.postalCode: The postal/zip code.
		 * - billingAddress.stateOrProvince: The state or province.
		 * - billingAddress.country: The country in ISO 3166-1 alpha-2 format i.e. NL.
		 * - billingAddressType: You can specify whether the shopper is allowed to view and/or modify these personal details.
		 * - billingAddressSig: A separate merchant signature that is required for these fields.
		 * 
		 * 2. Delivery address;
		 * - deliveryAddress.street: The street name
		 * - deliveryAddress.houseNumberOrName: The house number
		 * - deliveryAddress.city: The city.
		 * - deliveryAddress.postalCode: The postal/zip code.
		 * - deliveryAddress.stateOrProvince: The state or province.
		 * - deliveryAddress.country: The country in ISO 3166-1 alpha-2 format i.e. NL.
		 * - deliveryAddressType: You can specify whether the shopper is allowed to view and/or modify these personal details.
		 * - deliveryAddressSig: A separate merchant signature that is required for these fields.
		 * 
		 * 3. Shopper information
		 * - shopper.firstName: First name of the shopper.
		 * - shopper.infix: The shopper infix.
		 * - shopper.lastName: The shopper lastname.
		 * - shopper.gender: The shopper gender: MALE/FEMALE
		 * - shopper.dateOfBirthDayOfMonth: The day of the month of the shopper's birth.
		 * - shopper.dateOfBirthMonth: The month of the shopper's birth.
		 * - shopper.dateOfBirthYear: The year of the shopper's birth.
		 * - shopper.telephoneNumber: The shopper's telephone number.
		 * - shopperType: This field can be used if validation of the shopper fiels are desired.
		 * - shopperSig: A separate merchant signature that is required for these fields.
		 * 
		 * Please note: billingAddressType, deliveryAddressType and shopperType can have the following values:
		 * - Not supplied: modifiable / visible;
		 * - 1: unmodifiable / visible
		 * - 2: unmodifiable / invisible
		 */

		String billingAddressStreet = "Simon Carmiggeltstraat";
		String billingAddressHouseNumberOrName = "6-50";
		String billingAddressCity = "Amsterdam";
		String billingAddressPostalCode = "1011 DJ";
		String billingAddressStateOrProvince = "";
		String billingAddressCountry = "NL";
		String billingAddressType = "";

		String deliveryAddressStreet = "Simon Carmiggeltstraat";
		String deliveryAddressHouseNumberOrName = "6-50";
		String deliveryAddressCity = "Amsterdam";
		String deliveryAddressPostalCode = "1011 DJ";
		String deliveryAddressStateOrProvince = "";
		String deliveryAddressCountry = "NL";
		String deliveryAddressType = "1";

		String shopperFirstName = "John";
		String shopperInfix = "";
		String shopperLastName = "Doe";
		String shopperGender = "MALE";
		String shopperDateOfBirthDayOfMonth = "05";
		String shopperDateOfBirthMonth = "10";
		String shopperDateOfBirthYear = "1990";
		String shopperTelephoneNumber = "+31612345678";
		String shopperType = "1";

		/**
		 * Signing the form
		 * 
		 * The signatures are used by Adyen to verify if the posted data is not altered by the shopper. The signatures
		 * must be encrypted according to the procedures below.
		 */

		// HMAC Key is a shared secret KEY used to encrypt the signature. Set up the HMAC key:
		// Adyen Test CA >> Skins >> Choose your Skin >> Edit Tab >> Edit HMAC key for Test and Live
		String hmacKey = "YourHmacSecretKey";
		String signingString;

		// Compute the merchantSig
		String merchantSig;
		try {
			signingString = paymentAmount + currencyCode + shipBeforeDate + merchantReference + skinCode + merchantAccount
					+ sessionValidity + shopperEmail + shopperReference + recurringContract + allowedMethods + blockedMethods
					+ shopperStatement + merchantReturnData + billingAddressType + deliveryAddressType + shopperType + offset;
			merchantSig = calculateHMAC(hmacKey, signingString);
		} catch (GeneralSecurityException e) {
			throw new ServletException(e);
		}

		// Compute the billingAddressSig
		String billingAddressSig;
		try {
			signingString = billingAddressStreet + billingAddressHouseNumberOrName + billingAddressCity
					+ billingAddressPostalCode + billingAddressStateOrProvince + billingAddressCountry;
			billingAddressSig = calculateHMAC(hmacKey, signingString);
		} catch (GeneralSecurityException e) {
			throw new ServletException(e);
		}

		// Compute the deliveryAddressSig
		String deliveryAddressSig;
		try {
			signingString = deliveryAddressStreet + deliveryAddressHouseNumberOrName + deliveryAddressCity
					+ deliveryAddressPostalCode + deliveryAddressStateOrProvince + deliveryAddressCountry;
			deliveryAddressSig = calculateHMAC(hmacKey, signingString);
		} catch (GeneralSecurityException e) {
			throw new ServletException(e);
		}

		// Compute the shopperSig
		String shopperSig;
		try {
			signingString = shopperFirstName + shopperInfix + shopperLastName + shopperGender + shopperDateOfBirthDayOfMonth
					+ shopperDateOfBirthMonth + shopperDateOfBirthYear + shopperTelephoneNumber;
			shopperSig = calculateHMAC(hmacKey, signingString);
		} catch (GeneralSecurityException e) {
			throw new ServletException(e);
		}

		// Set request parameters for use on the JSP page
		request.setAttribute("merchantReference", merchantReference);
		request.setAttribute("paymentAmount", paymentAmount);
		request.setAttribute("currencyCode", currencyCode);
		request.setAttribute("shipBeforeDate", shipBeforeDate);
		request.setAttribute("skinCode", skinCode);
		request.setAttribute("merchantAccount", merchantAccount);
		request.setAttribute("sessionValidity", sessionValidity);
		request.setAttribute("shopperLocale", shopperLocale);
		request.setAttribute("orderData", orderData);
		request.setAttribute("countryCode", countryCode);
		request.setAttribute("shopperEmail", shopperEmail);
		request.setAttribute("shopperReference", shopperReference);
		request.setAttribute("recurringContract", recurringContract);
		request.setAttribute("allowedMethods", allowedMethods);
		request.setAttribute("blockedMethods", blockedMethods);
		request.setAttribute("shopperStatement", shopperStatement);
		request.setAttribute("merchantReturnData", merchantReturnData);
		request.setAttribute("offset", offset);
		request.setAttribute("brandCode", brandCode);
		request.setAttribute("issuerId", issuerId);

		request.setAttribute("billingAddressStreet", billingAddressStreet);
		request.setAttribute("billingAddressHouseNumberOrName", billingAddressHouseNumberOrName);
		request.setAttribute("billingAddressCity", billingAddressCity);
		request.setAttribute("billingAddressPostalCode", billingAddressPostalCode);
		request.setAttribute("billingAddressStateOrProvince", billingAddressStateOrProvince);
		request.setAttribute("billingAddressCountry", billingAddressCountry);
		request.setAttribute("billingAddressType", billingAddressType);

		request.setAttribute("deliveryAddressStreet", deliveryAddressStreet);
		request.setAttribute("deliveryAddressHouseNumberOrName", deliveryAddressHouseNumberOrName);
		request.setAttribute("deliveryAddressCity", deliveryAddressCity);
		request.setAttribute("deliveryAddressPostalCode", deliveryAddressPostalCode);
		request.setAttribute("deliveryAddressStateOrProvince", deliveryAddressStateOrProvince);
		request.setAttribute("deliveryAddressCountry", deliveryAddressCountry);
		request.setAttribute("deliveryAddressType", deliveryAddressType);

		request.setAttribute("shopperFirstName", shopperFirstName);
		request.setAttribute("shopperInfix", shopperInfix);
		request.setAttribute("shopperLastName", shopperLastName);
		request.setAttribute("shopperGender", shopperGender);
		request.setAttribute("shopperDateOfBirthDayOfMonth", shopperDateOfBirthDayOfMonth);
		request.setAttribute("shopperDateOfBirthMonth", shopperDateOfBirthMonth);
		request.setAttribute("shopperDateOfBirthYear", shopperDateOfBirthYear);
		request.setAttribute("shopperTelephoneNumber", shopperTelephoneNumber);
		request.setAttribute("shopperType", shopperType);

		request.setAttribute("merchantSig", merchantSig);
		request.setAttribute("billingAddressSig", billingAddressSig);
		request.setAttribute("deliveryAddressSig", deliveryAddressSig);
		request.setAttribute("shopperSig", shopperSig);

		// Forward request data to corresponding JSP page
		request.getRequestDispatcher("/1.HPP/create-payment-on-hpp-advanced.jsp").forward(request, response);
	}

	/**
	 * Generates GZIP compressed and Base64 encoded string.
	 */
	private String compressString(String input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(output);

		gzip.write(input.getBytes());
		gzip.close();
		output.close();

		return Base64.encodeBase64String(output.toByteArray());
	}

	/**
	 * Computes the Base64 encoded signature using the HMAC algorithm with the SHA-1 hashing function.
	 */
	private String calculateHMAC(String hmacKey, String signingString) throws GeneralSecurityException {
		SecretKeySpec keySpec = new SecretKeySpec(hmacKey.getBytes(), "HmacSHA1");
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(keySpec);

		byte[] result = mac.doFinal(signingString.getBytes());
		return Base64.encodeBase64String(result);
	}

}
