package com.adyen.examples.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * Create Payment through the API (HTTP Post)
 * 
 * Payments can be created through our API, however this is only possible if you are PCI Compliant. HTTP Post payments
 * are submitted using the Payment.authorise action. We will explain a simple credit card submission.
 * 
 * Please note: using our API requires a web service user. Set up your Webservice user:
 * Adyen CA >> Settings >> Users >> ws@Company. >> Generate Password >> Submit
 * 
 * @link /2.API/Soap/CreatePaymentAPI
 * @author Created by Adyen - Payments Made Easy
 */

@WebServlet(urlPatterns = { "/2.API/HttpPost/CreatePaymentAPI" })
public class CreatePaymentAPIHttpPost extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/**
		 * HTTP Post settings
		 * - apiUrl: URL of the Adyen API you are using (Test/Live)
		 * - wsUser: your web service user
		 * - wsPassword: your web service user's password
		 */
		String apiUrl = "https://pal-test.adyen.com/pal/adapter/httppost";
		String wsUser = "YourWSUser";
		String wsPassword = "YourWSPassword";

		/**
		 * Create HTTP Client (using Apache HttpComponents library) and set up Basic Authentication
		 */
		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(wsUser, wsPassword);
		provider.setCredentials(AuthScope.ANY, credentials);

		HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();

		/**
		 * A payment can be submitted with a HTTP Post request to the API, containing the following variables:
		 * 
		 * <pre>
		 * - action: Payment.authorise
		 * - paymentRequest
		 *   - merchantAccount: the merchant account the payment was processed with
		 *   - reference: your reference
		 *   - shopperIP: the IP address of the shopper (recommended)
		 *   - shopperEmail: the e-mail address of the shopper 
		 *   - shopperReference: the shopper reference, i.e. the shopper ID
		 *   - fraudOffset: numeric value that will be added to the fraud score (optional)
		 *   - amount: the amount of the payment
		 *       - currency: the currency of the payment
		 *       - amount: the amount of the payment
		 *   - card
		 *       - billingAddress: we advice you to submit billingAddress data if available for risk checks;
		 *           - street: the street name
		 *           - postalCode: the postal/zip code
		 *           - city: the city
		 *           - houseNumberOrName: the house number/name
		 *           - stateOrProvince: the state or province
		 *           - country: the country
		 *       - expiryMonth: the expiration month of the card, written as a 2-digit string, padded with 0 if required
		 *                      (e.g. 03 or 12)
		 *       - expiryYear: the expiration year of the card, full-written (e.g. 2016)
		 *       - holderName: the card holder's name, as embossed on the card
		 *       - number: the card number
		 *       - cvc: the card validation code, which is the CVC2 (MasterCard), CVV2 (Visa) or CID (American Express)
		 * </pre>
		 */
		List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		Collections.addAll(postParameters,
			new BasicNameValuePair("action", "Payment.authorise"),

			new BasicNameValuePair("paymentRequest.merchantAccount", "YourMerchantAccount"),
			new BasicNameValuePair("paymentRequest.reference", "TEST-PAYMENT-" + new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date())),
			new BasicNameValuePair("paymentRequest.shopperIP", "123.123.123.123"),
			new BasicNameValuePair("paymentRequest.shopperEmail", "test@example.com"),
			new BasicNameValuePair("paymentRequest.shopperReference", "YourReference"),
			new BasicNameValuePair("paymentRequest.fraudOffset", "0"),

			new BasicNameValuePair("paymentRequest.amount.currency", "EUR"),
			new BasicNameValuePair("paymentRequest.amount.value", "199"),

			new BasicNameValuePair("paymentRequest.card.billingAddress.street", "Simon Carmiggeltstraat"),
			new BasicNameValuePair("paymentRequest.card.billingAddress.postalCode", "1011 DJ"),
			new BasicNameValuePair("paymentRequest.card.billingAddress.city", "Amsterdam"),
			new BasicNameValuePair("paymentRequest.card.billingAddress.houseNumberOrName", "6-50"),
			new BasicNameValuePair("paymentRequest.card.billingAddress.stateOrProvince", ""),
			new BasicNameValuePair("paymentRequest.card.billingAddress.country", "NL"),

			new BasicNameValuePair("paymentRequest.card.expiryMonth", "06"),
			new BasicNameValuePair("paymentRequest.card.expiryYear", "2016"),
			new BasicNameValuePair("paymentRequest.card.holderName", "John Doe"),
			new BasicNameValuePair("paymentRequest.card.number", "5555444433331111"),
			new BasicNameValuePair("paymentRequest.card.cvc", "737")
		);

		/**
		 * Send the HTTP Post request with the specified variables.
		 */
		HttpPost httpPost = new HttpPost(apiUrl);
		httpPost.setEntity(new UrlEncodedFormEntity(postParameters));

		HttpResponse httpResponse = client.execute(httpPost);
		String paymentResponse = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

		/**
		 * Keep in mind that you should handle errors correctly.
		 * If the Adyen platform does not accept or store a submitted request, you will receive a HTTP response with
		 * status code 500 Internal Server Error. The fault string can be found in the paymentResponse.
		 */
		if (httpResponse.getStatusLine().getStatusCode() == 500) {
			throw new ServletException(paymentResponse);
		}
		else if (httpResponse.getStatusLine().getStatusCode() != 200) {
			throw new ServletException(httpResponse.getStatusLine().toString());
		}

		/**
		 * If the payment passes validation a risk analysis will be done and, depending on the outcome, an authorisation
		 * will be attempted. You receive a payment response with the following fields:
		 * - pspReference: The reference we assigned to the payment;
		 * - resultCode: The result of the payment. One of Authorised, Refused or Error;
		 * - authCode: An authorisation code if the payment was successful, or blank otherwise;
		 * - refusalReason: If the payment was refused, the refusal reason.
		 */
		Map<String, String> paymentResult = parseQueryString(paymentResponse);
		PrintWriter out = response.getWriter();

		out.println("Payment Result:");
		out.println("- pspReference: " + paymentResult.get("paymentResult.pspReference"));
		out.println("- resultCode: " + paymentResult.get("paymentResult.resultCode"));
		out.println("- authCode: " + paymentResult.get("paymentResult.authCode"));
		out.println("- refusalReason: " + paymentResult.get("paymentResult.refusalReason"));
		
	}

	/**
	 * Parse the result of the HTTP Post request (will be returned in the form of a query string)
	 */
	private Map<String, String> parseQueryString(String queryString) throws UnsupportedEncodingException {
		Map<String, String> parameters = new HashMap<String, String>();
		String[] pairs = queryString.split("&");

		for (String pair : pairs) {
			String[] keyval = pair.split("=");
			parameters.put(URLDecoder.decode(keyval[0], "UTF-8"), URLDecoder.decode(keyval[1], "UTF-8"));
		}

		return parameters;
	}

}
