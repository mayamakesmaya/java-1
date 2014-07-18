package com.adyen.examples.recurring;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
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
 * Retrieve recurring contract details (HTTP Post)
 * 
 * Once a shopper has stored RECURRING details with Adyen you are able to process a RECURRING payment. This file shows
 * you how to retrieve the RECURRING contract(s) for a shopper using HTTP Post.
 * 
 * Please note: using our API requires a web service user. Set up your Webservice user:
 * Adyen CA >> Settings >> Users >> ws@Company. >> Generate Password >> Submit
 * 
 * @link /5.Recurring/HttpPost/RetrieveRecurringContract
 * @author Created by Adyen - Payments Made Easy
 */

@WebServlet(urlPatterns = { "/5.Recurring/HttpPost/RetrieveRecurringContract" })
public class RetrieveRecurringContractHttpPost extends HttpServlet {

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
		 * The recurring details request should contain the following variables:
		 * 
		 * <pre>
		 * - action                   : Recurring.listRecurringDetails
		 * - recurringDetailsRequest
		 *   - merchantAccount        : Your merchant account.
		 *   - shopperReference       : The reference to the shopper. This shopperReference must be the same as the
		 *                            shopperReference used in the initial payment.
		 *   - recurring
		 *       - contract           : This should be the same value as recurringContract in the payment where the
		 *                              recurring contract was created. However if ONECLICK,RECURRING was specified
		 *                              initially then this field can be either ONECLICK or RECURRING.
		 * </pre>
		 */
		List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		Collections.addAll(postParameters,
			new BasicNameValuePair("action", "Recurring.listRecurringDetails"),

			new BasicNameValuePair("recurringDetailsRequest.merchantAccount", "YourMerchantAccount"),
			new BasicNameValuePair("recurringDetailsRequest.shopperReference", "TheShopperReference"),
			new BasicNameValuePair("recurringDetailsRequest.recurring.contract", "ONECLICK")
			);

		/**
		 * Send the HTTP Post request with the specified variables.
		 */
		HttpPost httpPost = new HttpPost(apiUrl);
		httpPost.setEntity(new UrlEncodedFormEntity(postParameters));

		HttpResponse httpResponse = client.execute(httpPost);
		String recurringResponse = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

		/**
		 * Keep in mind that you should handle errors correctly.
		 * If the Adyen platform does not accept or store a submitted request, you will receive a HTTP response with
		 * status code 500 Internal Server Error. The fault string can be found in the recurringResponse.
		 */
		if (httpResponse.getStatusLine().getStatusCode() == 500) {
			throw new ServletException(recurringResponse);
		}
		else if (httpResponse.getStatusLine().getStatusCode() != 200) {
			throw new ServletException(httpResponse.getStatusLine().toString());
		}

		/**
		 * The recurring details response will contain the following fields:
		 * 
		 * <pre>
		 * - creationDate
		 * - lastKnownShopperEmail
		 * - shopperReference
		 * - recurringDetail              : A list of zero or more details, containing:
		 *     - recurringDetailReference : The reference the details are stored under.
		 *     - variant                  : The payment method (e.g. mc, visa, elv, ideal, paypal).
		 *                                  For some variants, like iDEAL, the sub-brand is returned like idealrabobank.
		 *     - creationDate             : The date when the recurring details were created.
		 *     - card                     : A container for credit card data.
		 *     - elv                      : A container for ELV data.
		 *     - bank                     : A container for BankAccount data.
		 * </pre>
		 * 
		 * The recurring contracts are stored in the same object types as you would have submitted in the initial
		 * payment. Depending on the payment method one or more fields may be blank or incomplete (e.g. CVC for
		 * card). Only one of the detail containers (card/elv/bank) will be returned per detail block, the others will
		 * be null. For PayPal there is no detail container.
		 */
		Map<String, String> recurringResult = parseQueryString(recurringResponse);
		PrintWriter out = response.getWriter();

		out.println("Recurring Details Result:");

		for (Map.Entry<String, String> entry : recurringResult.entrySet()) {
			out.println("- " + entry.getKey() + " : " + entry.getValue());
		}
		
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
