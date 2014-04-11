package com.adyen.test.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

/**
 * Servlet implementation class ReceiveNotification
 */
@WebServlet("/ReceiveNotification")

/** 
 * It can be tested posting something like that on your browser:
 * http://localhost:8080/AdyenWebApplication-HMAC/ReceiveNotification?pspReference=8513972259418824&eventDate=2014-04-11T14%3A19%3A01.57Z&merchantAccountCode=CristinaMerchant&reason=18021%3A1111%3A6%2F2016&originalReference=&value=1000&eventCode=AUTHORISATION&merchantReference=Reference+1&operations=CANCEL%2CCAPTURE%2CREFUND&success=true&paymentMethod=mc&currency=EUR&live=false
**/

public class ReceiveNotification extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    private static SimpleDateFormat formatData = new SimpleDateFormat( "dd-MM-yyyy HH:mm:ss Z");
	
	/**
     * @see HttpServlet#HttpServlet()
     */
    public ReceiveNotification() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost( request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Log received info
		System.out.println();
		System.out.println( "***** Received Notification: " + formatData.format( new Date()));
				
		// Check user and password
		String authHeader = request.getHeader("authorization");
		if ( authHeader != null){
			String encodedValue = authHeader.split(" ")[1];
			String decodedValue = new String(Base64.decodeBase64(encodedValue.getBytes()));
			System.out.println( "user:password: " + decodedValue);
		}
		else
			System.out.println( "Without BASIC authentication");
		
		
		System.out.println( "HEADERS");
		Enumeration<String> headerNames = request.getHeaderNames();
		while ( headerNames.hasMoreElements()){
			String headerName = headerNames.nextElement();
			System.out.println( headerName + ": " + request.getHeader( headerName));
		}
		
		System.out.println( "PARAMS");
		Enumeration<String> paramNames = request.getParameterNames();
		while ( paramNames.hasMoreElements()){
			String paramName = paramNames.nextElement();
			System.out.println( paramName + ": " + request.getParameter( paramName));
		}
		
		System.out.println( "****************************************************");
		System.out.println();
		
		// return [accepted]
		ServletOutputStream out = response.getOutputStream();
		out.write( "[accepted]".getBytes());
		out.flush();
		out.close();
	}

}
