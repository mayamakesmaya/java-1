package com.adyen.examples.notifications;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

/**
 * Receive notifcations from Adyen (HTTP Post)
 * 
 * Whenever a payment is made, a modification is processed or a report is available we will notify you. The
 * notifications tell you for instance if an authorisation was performed successfully. Notifications should be used to
 * keep your backoffice systems up to date with the status of each payment and modification. Notifications are sent
 * using a SOAP call or using HTTP POST to a server of your choice. This file describes how HTTP Post notifcations can
 * be received in Java.
 * 
 * @link /3.Notifications/HttpPost/NotificationServer
 * @author Created by Adyen - Payments Made Easy
 */

@WebServlet(urlPatterns = { "/3.Notifications/HttpPost/NotificationServer" })
public class NotificationServer extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/**
		 * Check authentication
		 * 
		 * We recommend you to secure your notification server. You can secure it using a username/password which can be
		 * configured in the CA. The username and password will be available in the Authorization header of the request.
		 * Alternatively, is to allow only traffic that comes from Adyen servers.
		 */
		String notificationUser = "TestUser";
		String notificationPassword = "TestPassword";
		String notificationEmail = "mayaemail";

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
		 * Handle notification
		 * 
		 * The following request parameters are available (see Integration Manual):
		 * - live
		 * - eventCode
		 * - pspReference
		 * - originalReference
		 * - merchantReference
		 * - merchantAccountCode
		 * - eventDate
		 * - success
		 * - paymentMethod
		 * - operations
		 * - reason
		 * - currency
		 * 
		 * We recommend you to handle the notifications based on the eventCode types available, please refer to the
		 * integration manual for a comprehensive list. We also recommend you to save the notification itself.
		 */

		switch (request.getParameter("eventCode")) {
			case "AUTHORISATION":
				// Handle AUTHORISATION notification.
				// Confirms whether the payment was authorised successfully.
				// The authorisation is successful if the "success" field has the value true.
				// In case of an error or a refusal, it will be false and the "reason" field
				// should be consulted for the cause of the authorisation failure.
				break;

			case "CANCELLATION":
				// Handle CANCELLATION notification.
				// Confirms that the payment was cancelled successfully.
				break;

			case "REFUND":
				// Handle REFUND notification.
				// Confirms that the payment was refunded successfully.
				break;

			case "CANCEL_OR_REFUND":
				// Handle CANCEL_OR_REFUND notification.
				// Confirms that the payment was refunded or cancelled successfully.
				break;

			case "CAPTURE":
				// Handle CAPTURE notification.
				// Confirms that the payment was successfully captured.
				break;

			case "REFUNDED_REVERSED":
				// Handle REFUNDED_REVERSED notification.
				// Tells you that the refund for this payment was successfully reversed.
				break;

			case "CAPTURE_FAILED":
				// Handle AUTHORISATION notification.
				// Tells you that the capture on the authorised payment failed.
				break;

			case "REQUEST_FOR_INFORMATION":
				// Handle REQUEST_FOR_INFORMATION notification.
				// Information requested for this payment.
				break;

			case "NOTIFICATION_OF_CHARGEBACK":
				// Handle NOTIFICATION_OF_CHARGEBACK notification.
				// Chargeback is pending, but can still be defended.
				break;

			case "CHARGEBACK":
				// Handle CHARGEBACK notification.
				// Payment was charged back. This is not sent if a REQUEST_FOR_INFORMATION or NOTIFICATION_OF_CHARGEBACK
				// notification has already been sent.
				break;

			case "CHARGEBACK_REVERSED":
				// Handle CHARGEBACK_REVERSED notification.
				// Chargeback has been reversed (cancelled).
				break;

			case "REPORT_AVAILABLE":
				// Handle REPORT_AVAILABLE notification.
				// There is a new report available, the URL of the report is in the "reason" field.
				break;
		}

		// Save the notification in a appropriate way. In this example, the notification is simply logged to System.out.
		boolean notificationSaved = saveNotification(request);

		/**
		 * Return [accepted]
		 * 
		 * Please make sure to return [accepted] to us when you have saved/processed the notification. This is essential
		 * to let us know that you received the notification. If we do NOT receive [accepted] we try to send the
		 * notification again, which will put all other notifications in a queue.
		 */
		if (notificationSaved) {
			PrintWriter out = response.getWriter();
			out.print("[accepted]");
		}

	}

	/**
	 * Print all request headers and parameters of a notification to System.out
	 */
	private boolean saveNotification(HttpServletRequest request) {
		System.out.println("***** Received Notification: "
				+ new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS").format(new Date()));

		System.out.println("Headers:");
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			System.out.println("- " + headerName + ": " + request.getHeader(headerName));
		}

		System.out.println("Parameters:");
		Enumeration<String> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			System.out.println("- " + paramName + ": " + request.getParameter(paramName));
		}

		System.out.println("****************************************************");
		System.out.println();

		// Indicate that the notification is saved correctly
		return true;
	}

}
