package com.adyen.examples.openinvoice;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

/**
 * Open Invoice Server (HTTP Post)
 * 
 * Open Invoice lowers the barrier substantially for shoppers to buy products on a merchant's website. By providing
 * their personal data, like address and date of birth, an assessment is made if we allow the shopper to receive the
 * product before the payment is made. The invoice is shipped along with the product. The shopper is expected to either
 * pay the invoice by initiating a bank transfer or to return the goods. When the invoice is not paid and the goods are
 * not returned the merchant has the option to turn the invoice over to a debt collection agency.
 * 
 * After the shoppers has entered their details Adyen will perform a call-back to a web service hosted by the merchant
 * requesting the order line specification of the invoice. The line specification is required by Adyen to send detailed
 * letters to the shopper requesting the payment when the initial invoice has not been paid. Including the line
 * specification details in written communication to the shopper is required by law. This example shows how such an
 * OpenInvoice web service can be implemented using HTTP Post.
 * 
 * Please note that the order line information server needs to be available during the lifecycle of a payment, i.e.
 * during Authorisation (1), Capture (2) and Refund (3). See the example below how to handle these cases.
 * 
 * @link /7.OpenInvoice/HttpPost/OpenInvoiceServer
 * @author Created by Adyen - Payments Made Easy
 */

@WebServlet(urlPatterns = { "/7.OpenInvoice/HttpPost/OpenInvoiceServer" })
public class OpenInvoiceServer extends HttpServlet {

	@SuppressWarnings("unused")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/**
		 * Check authentication
		 * 
		 * We recommend you to secure your OpenInvoice server. You can secure it using a username/password which can be
		 * configured in the CA. The username and password will be available in the Authorization header of the request.
		 * Alternatively, is to allow only traffic that comes from Adyen servers.
		 */
		String notificationUser = "TestUser";
		String notificationPassword = "TestPassword";

		String authHeader = request.getHeader("Authorization");

		if (authHeader == null) {
			// Return 401 Unauthorized if Authorization header is not available
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		else {
			// Decode username and password from Authorization header
			String encodedAuth = authHeader.split(" ")[1];
			String decodedAuth = new String(Base64.decodeBase64(encodedAuth));

			String requestUser = decodedAuth.split(":")[0];
			String requestPassword = decodedAuth.split(":")[1];

			// Return 403 Forbidden if username and/or password are incorrect
			if (!notificationUser.equals(requestUser) || !notificationPassword.equals(requestPassword)) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
		}

		/**
		 * Line Specification Request by Adyen
		 * 
		 * The line specification request by Adyen contains the following fields:
		 * 
		 * <pre>
		 * - openInvoiceDetailRequest
		 *     - merchantAccount       : The merchant account the payment was processed with.
		 *     - reference             : The original reference specified by the merchant when initiating this payment.
		 *     - amount
		 *       - currency            : The three character ISO currency code.
		 *       - value               : The original amount that was authorised.
		 * </pre>
		 */
		String merchantAccount = request.getParameter("openInvoiceDetailRequest.merchantAccount");
		String reference = request.getParameter("openInvoiceDetailRequest.reference");
		String currency = request.getParameter("openInvoiceDetailRequest.amount.currency");
		int value = Integer.parseInt(request.getParameter("openInvoiceDetailRequest.amount.value"));

		/**
		 * Line Specification Response from the Merchant
		 * 
		 * The line specification response should contain the following fields:
		 * 
		 * <pre>
		 * - openInvoiceDetailResult
		 *     - lines               : The order holding; one ore more lines summing up to the authorisation amount.
		 *        - lineReference    : A control number starting at 1 at the first line and summing up to the total
		 *                             number of lines (items are displayed in this order).
		 *        - itemPrice        : The price of one individual item of this line's product in minor units.
		 *        - itemVAT          : The VAT of one individual item of this line's product in minor units.
		 *        - vatCategory      : The country specific VAT category this product falls under
		 *                             In the Netherlands this can be High, Low and None (if not applicable).
		 *        - numberOfItems    : The number of units purchased for this specific product.
		 *        - currency         : The currency in which this product is charged (must be the same for all lines).
		 *        - description      : A text description of this line's product.
		 * </pre>
		 * 
		 * Let's assume an open invoice payment of is made of EUR 19,50. After the payment is authorised and captured
		 * the shopper returns part of the order. Therefore a refund of EUR 3,50 must be initiated as well.
		 */
		PrintWriter out = response.getWriter();

		/**
		 * 1. Authorisation / 2. Capture
		 * 
		 * When the value of the openInvoiceDetailRequest is positive, this indicates an authorisation or capture. The
		 * following invoice lines are returned in the openInvoiceDetailResult.
		 * 
		 * Please note that the total value of the invoice lines should be equal to the value specified in the request
		 * and in the same currency. In this example: (3,25 + 0,75) * 4 + (3,50 + 0,00) * 1 = EUR 19,50.
		 * If the total value of the retrieved invoice lines is not equal to the amount that was requested, an error
		 * will be displayed to the shopper.
		 */
		if (value > 0) {
			out.print("openInvoiceDetailResult.lines.0.lineReference=1&");
			out.print("openInvoiceDetailResult.lines.0.itemPrice=325&");
			out.print("openInvoiceDetailResult.lines.0.itemVAT=75&");
			out.print("openInvoiceDetailResult.lines.0.vatCategory=High&");
			out.print("openInvoiceDetailResult.lines.0.numberOfItems=4&");
			out.print("openInvoiceDetailResult.lines.0.currency=EUR&");
			out.print("openInvoiceDetailResult.lines.0.description=Product 1&");

			out.print("openInvoiceDetailResult.lines.1.lineReference=2&");
			out.print("openInvoiceDetailResult.lines.1.itemPrice=350&");
			out.print("openInvoiceDetailResult.lines.1.itemVAT=0&");
			out.print("openInvoiceDetailResult.lines.1.vatCategory=None&");
			out.print("openInvoiceDetailResult.lines.1.numberOfItems=1&");
			out.print("openInvoiceDetailResult.lines.1.currency=EUR&");
			out.print("openInvoiceDetailResult.lines.1.description=Product 2&");
		}

		/**
		 * 3. Refund
		 * 
		 * When the value of the openInvoiceDetailRequest is negative, this indicates a refund. In this example we
		 * assume that the shopper has returned Product 2 of his order: EUR 3,50 must be refunded to the shopper.
		 * 
		 * There are 2 options for the response of the retrieveDetails request:
		 * - Reverse the line of the product that is refunded.
		 * - Create the same response as with the authorisation, but with an additional Refund line.
		 * 
		 * The openInvoiceDetailResult below illustrates the first option. Note that numberOfItems is negative in this
		 * case. For both options the total of the returned invoice lines should be equal to the amount that is
		 * requested.
		 */
		else if (value < 0) {
			out.print("openInvoiceDetailResult.lines.0.lineReference=1&");
			out.print("openInvoiceDetailResult.lines.0.itemPrice=350&");
			out.print("openInvoiceDetailResult.lines.0.itemVAT=0&");
			out.print("openInvoiceDetailResult.lines.0.vatCategory=None");
			out.print("openInvoiceDetailResult.lines.0.numberOfItems=-1&");
			out.print("openInvoiceDetailResult.lines.0.currency=EUR&");
			out.print("openInvoiceDetailResult.lines.0.description=Product 2&");
		}

	}

}
