package com.adyen.examples.recurring;

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
 * Create Recurring Payment (HTTP Post)
 * 
 * You can submit a recurring payment using a specific recurringDetails record or by using the last created
 * recurringDetails record. The request for the recurring payment is done using a paymentRequest. This file shows how a
 * recurring payment can be submitted using our HTTP Post API.
 * 
 * Please note: using our API requires a web service user. Set up your Webservice user:
 * Adyen CA >> Settings >> Users >> ws@Company. >> Generate Password >> Submit
 * 
 * @link /5.Recurring/HttpPost/CreateRecurringPayment
 * @author Created by Adyen - Payments Made Easy
 */

@WebServlet(urlPatterns = { "/5.Recurring/HttpPost/CreateRecurringPayment" })
public class CreateRecurringPaymentHttpPost extends HttpServlet {

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
		 * A recurring payment can be submitted with a HTTP Post request to the API, containing the following variables:
		 * 
		 * <pre>
		 * - action               : Payment.authorise
		 * - paymentRequest
		 *   - selectedRecurringDetailReference : The recurringDetailReference you want to use for this payment.
		 *                          The value LATEST can be used to select the most recently used recurring detail.
		 *   - recurring
		 *       - contract       : This should be the same value as recurringContract in the payment where the recurring
		 *                          contract was created. However if ONECLICK,RECURRING was specified initially then this
		 *                          field can be either ONECLICK or RECURRING.
		 *   - shopperInteraction : Set to ContAuth if the contract value is RECURRING, or Ecommerce if the contract
		 *                          value is ONECLICK.
		 * 
		 *   - merchantAccount    : The merchant account for which you want to process the payment.
		 *   - amount
		 *       - currency       : The three character ISO currency code.
		 *       - value          : The transaction amount in minor units (e.g. EUR 1,00 = 100).
		 *   - reference          : Your reference for this payment.
		 *   - shopperEmail       : The email address of the shopper. This does not have to match the email address
		 *                          supplied with the initial payment since it may have changed in the mean time.
		 *   - shopperReference   : The reference to the shopper. This shopperReference must be the same as the
		 *                          shopperReference used in the initial payment.
		 *   - shopperIP          : The shopper's IP address. (recommended)
		 *   - fraudOffset        : An integer that is added to the normal fraud score. (optional)
		 *   - card
		 *       - CVC            : The card validation code. (only required for OneClick card payments)
		 * </pre>
		 */
		List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		Collections.addAll(postParameters,
			new BasicNameValuePair("action", "Payment.authorise"),

			new BasicNameValuePair("paymentRequest.merchantAccount", "YourMerchantAccount"),
			new BasicNameValuePair("paymentRequest.reference",
				"TEST-PAYMENT-" + new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date())),
			new BasicNameValuePair("paymentRequest.shopperIP", "123.123.123.123"),
			new BasicNameValuePair("paymentRequest.shopperEmail", "test@example.com"),
			new BasicNameValuePair("paymentRequest.shopperReference", "TheShopperReference"),
			new BasicNameValuePair("paymentRequest.fraudOffset", "0"),

			new BasicNameValuePair("paymentRequest.selectedRecurringDetailReference", "LATEST"),
			new BasicNameValuePair("paymentRequest.recurring.contract", "ONECLICK"),
			new BasicNameValuePair("paymentRequest.shopperInteraction", "Ecommerce"),

			new BasicNameValuePair("paymentRequest.amount.currency", "EUR"),
			new BasicNameValuePair("paymentRequest.amount.value", "199"),

			// CVC is only required for OneClick card payments
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
		 * If the recurring payment passes validation a risk analysis will be done and, depending on the outcome, an
		 * authorisation will be attempted. You receive a payment response with the following fields:
		 * 
		 * <pre>
		 * - pspReference    : Adyen's unique reference that is associated with the payment.
		 * - resultCode      : The result of the payment. Possible values: Authorised, Refused, Error or Received.
		 * - authCode        : The authorisation code if the payment was successful. Blank otherwise.
		 * - refusalReason   : Adyen's mapped refusal reason, populated if the payment was refused.
		 * </pre>
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
