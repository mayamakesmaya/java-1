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
 * Disable recurring contract (HTTP Post)
 * 
 * Disabling a recurring contract (detail) can be done by calling the disable action on the Recurring service with a
 * request. This file shows how you can disable a recurring contract using HTTP Post.
 * 
 * Please note: using our API requires a web service user. Set up your Webservice user:
 * Adyen CA >> Settings >> Users >> ws@Company. >> Generate Password >> Submit
 * 
 * @link /5.Recurring/HttpPost/DisableRecurringContract
 * @author Created by Adyen - Payments Made Easy
 */

@WebServlet(urlPatterns = { "/5.Recurring/HttpPost/DisableRecurringContract" })
public class DisableRecurringContractHttpPost extends HttpServlet {

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
		 * - action                       : Recurring.disable
		 * - recurringDetailsRequest
		 *   - merchantAccount            : Your merchant account.
		 *   - shopperReference           : The reference to the shopper. This shopperReference must be the same as the
		 *                                  shopperReference used in the initial payment.
		 *   - recurringDetailReference   : The recurringDetailReference of the details you wish to disable. If you do
		 *                                  not supply this field all details for the shopper will be disabled including
		 *                                  the contract! This means that you can not add new details anymore.
		 * </pre>
		 */
		List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		Collections.addAll(postParameters,
			new BasicNameValuePair("action", "Recurring.disable"),

			new BasicNameValuePair("disableRequest.merchantAccount", "YourMerchantAccount"),
			new BasicNameValuePair("disableRequest.shopperReference", "TheShopperReference"),
			new BasicNameValuePair("disableRequest.recurringDetailReference", "TheDetailReferenceOfTheContract")
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
		 * The response will be a result object with a single field response. If a single detail was disabled the value
		 * of this field will be [detail-successfully-disabled] or, if all details are disabled, the value is
		 * [all-details-successfully-disabled].
		 */
		Map<String, String> recurringResult = parseQueryString(recurringResponse);
		PrintWriter out = response.getWriter();

		out.println("Disable Recurring Result:");
		out.println("- response: " + recurringResult.get("disableResult.response"));
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
