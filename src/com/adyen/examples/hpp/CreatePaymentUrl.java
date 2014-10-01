package com.adyen.examples.hpp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
 * Create Payment URL
 * 
 * The Adyen Hosted Payment Pages (HPPs) provide a flexible, secure and easy way to allow shoppers to pay for goods or
 * services. This example shows a page which creates a payment by a URL. The link provided by this example can for
 * instance be send by e-mail to create a payment.
 * 
 * @link /1.HPP/CreatePaymentUrl
 * @author Created by Adyen - Payments Made Easy
 */

@WebServlet(urlPatterns = { "/1.HPP/CreatePaymentUrl" })
public class CreatePaymentUrl extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/**
		 * General HPP settings
		 * - hppUrl: URL of the Adyen HPP to submit the form to
		 * - hmacKey: shared secret key used to encrypt the signature
		 * 
		 * Both variables are dependent on the environment which should be used (Test/Live).
		 * HMAC key can be set up: Adyen CA >> Skins >> Choose your Skin >> Edit Tab >> Edit HMAC key for Test & Live.
		 */
		String hppUrl = "https://test.adyen.com/hpp/pay.shtml";
		String hmacKey = "YourHmacSecretKey";

		/**
		 * Defining variables
		 * The HPP requires certain variables to be posted in order to create a payment possibility for the shopper.
		 * 
		 * The variables that you can post to the HPP are the following:
		 * 
		 * <pre>
		 * merchantReference    : Your reference for this payment.
		 * paymentAmount        : The transaction amount in minor units (e.g. EUR 1,00 = 100).
		 * currencyCode         : The three character ISO currency code.
		 * shipBeforeDate       : The date by which the goods or services specifed in the order must be shipped.
		 *                        Format: YYYY-MM-DD
		 * skinCode             : The code of the skin to be used for the payment.
		 * merchantAccount      : The merchant account for which you want to process the payment.
		 * sessionValidity      : The time by which a payment needs to have been made.
		 *                        Format: YYYY-MM-DDThh:mm:ssTZD
		 * shopperLocale        : A combination of language code and country code used to specify the language to be
		 *                        used in the payment session (e.g. en_GB).
		 * orderData            : A fragment of HTML/text that will be displayed on the HPP. (optional)
		 * countryCode          : Country code according to ISO_3166-1_alpha-2 standard. (optional)
		 * shopperEmail         : The shopper's email address. (recommended)
		 * shopperReference     : An ID that uniquely identifes the shopper, such as a customer id. (recommended)
		 * allowedMethods       : A comma-separated list of allowed payment methods, i.e. "ideal,mc,visa". (optional)
		 * blockedMethods       : A comma-separated list of blocked payment methods, i.e. "ideal,mc,visa". (optional)
		 * offset               : An integer that is added to the normal fraud score. (optional)
		 * merchantSig          : The HMAC signature used by Adyen to test the validy of the form.
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
		String allowedMethods = "";
		String blockedMethods = "";
		String offset = "";

		/**
		 * Signing the form
		 * 
		 * The merchant signature is used by Adyen to verify if the posted data is not altered by the shopper. The
		 * signature must be encrypted according to the procedure below.
		 * 
		 * Please note: the signature does contain more variables, in this example they are NOT required since they are
		 * empty. Please have a look at the advanced HPP example for a comprehensive overview on what should be part of
		 * the signature.
		 */

		String signingString = paymentAmount + currencyCode + shipBeforeDate + merchantReference + skinCode
				+ merchantAccount + sessionValidity + shopperEmail + shopperReference + allowedMethods + blockedMethods
				+ offset;

		String merchantSig;
		try {
			merchantSig = calculateHMAC(hmacKey, signingString);
		} catch (GeneralSecurityException e) {
			throw new ServletException(e);
		}

		/**
		 * Generating the payment URL
		 * 
		 * All variables are appended to the query string of the provided hppUrl. Please note that not all browsers are
		 * capable of handling large URLs, and all parameters and their values should be URL-encoded using UTF-8
		 * character encoding.
		 */
		String paymentUrl = hppUrl
				+ "?merchantReference=" + URLEncoder.encode(merchantReference, "UTF-8")
				+ "&paymentAmount=" + URLEncoder.encode(paymentAmount, "UTF-8")
				+ "&currencyCode=" + URLEncoder.encode(currencyCode, "UTF-8")
				+ "&shipBeforeDate=" + URLEncoder.encode(shipBeforeDate, "UTF-8")
				+ "&skinCode=" + URLEncoder.encode(skinCode, "UTF-8")
				+ "&merchantAccount=" + URLEncoder.encode(merchantAccount, "UTF-8")
				+ "&sessionValidity=" + URLEncoder.encode(sessionValidity, "UTF-8")
				+ "&shopperLocale=" + URLEncoder.encode(shopperLocale, "UTF-8")
				+ "&orderData=" + URLEncoder.encode(orderData, "UTF-8")
				+ "&countryCode=" + URLEncoder.encode(countryCode, "UTF-8")
				+ "&shopperEmail=" + URLEncoder.encode(shopperEmail, "UTF-8")
				+ "&allowedMethods=" + URLEncoder.encode(allowedMethods, "UTF-8")
				+ "&blockedMethods=" + URLEncoder.encode(blockedMethods, "UTF-8")
				+ "&offset=" + URLEncoder.encode(offset, "UTF-8")
				+ "&merchantSig=" + URLEncoder.encode(merchantSig, "UTF-8");
		
		// Set correct character encoding
		response.setCharacterEncoding("UTF-8");

		// Set payment URL in request data and forward it to corresponding JSP page
		request.setAttribute("paymentUrl", paymentUrl.toString());
		request.getRequestDispatcher("/1.HPP/create-payment-url.jsp").forward(request, response);
	}

	/**
	 * Generates GZIP compressed and Base64 encoded string.
	 */
	private String compressString(String input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(output);

		gzip.write(input.getBytes("UTF-8"));
		gzip.close();
		output.close();

		return Base64.encodeBase64String(output.toByteArray());
	}

	/**
	 * Computes the Base64 encoded signature using the HMAC algorithm with the SHA-1 hashing function.
	 */
	private String calculateHMAC(String hmacKey, String signingString) throws GeneralSecurityException, UnsupportedEncodingException {
		SecretKeySpec keySpec = new SecretKeySpec(hmacKey.getBytes(), "HmacSHA1");
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(keySpec);

		byte[] result = mac.doFinal(signingString.getBytes("UTF-8"));
		return Base64.encodeBase64String(result);
	}

}
