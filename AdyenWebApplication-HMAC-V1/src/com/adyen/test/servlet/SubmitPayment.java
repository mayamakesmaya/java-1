package com.adyen.test.servlet;


import java.io.IOException;
import java.util.Enumeration;

import com.adyen.test.util.HMACTools;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SubmitPayment
 */
@WebServlet("/submitPayment")
public class SubmitPayment extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String[] parametersToSign = {
														"paymentAmount",
														"currencyCode",
														"shipBeforeDate",
														"merchantReference",
														"skinCode",
														"merchantAccount",
														"sessionValidity",
														"shopperEmail",
														"shopperReference",
														"recurringContract",
														"allowedMethods",
														"blockedMethods",
														"shopperStatement",
														"merchantReturnData",
														"billingAddressType",
														"deliveryAddressType",
														"shopperType",
														"offset"
														};
	
	private static final String[] billingAddressToSign = {
														"billingAddress.street",
														"billingAddress.houseNumberOrName",
														"billingAddress.city",
														"billingAddress.postalCode",
														"billingAddress.stateOrProvince",
														"billingAddress.country"
														};
	
	private static final String[] deliveryAddressToSign = {
														"billingAddress.street",
														"billingAddress.houseNumberOrName",
														"billingAddress.city",
														"billingAddress.postalCode",
														"billingAddress.stateOrProvince",
														"billingAddress.country"
														};
	
	private static final String[] shopperToSign = {
														"shopper.firstName",
														"shopper.infix",
														"shopper.lastName",
														"shopper.gender",
														"shopper.dateOfBirthDayOfMonth",
														"shopper.dateOfBirthMonth",
														"shopper.dateOfBirthYear",
														"shopper.telephoneNumber",
														//"shopper.socialSecurityNumber"
														};

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SubmitPayment() {
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
		// Printing headers
		Enumeration<String> names = request.getHeaderNames();
		while ( names.hasMoreElements()){
			String name = names.nextElement();
			String header = request.getHeader( name);
			System.out.println( name + ": " + header);
		}
		System.out.println( request.getCharacterEncoding());
		request.setCharacterEncoding( "UTF-8");
		
		// Generating merchantSig
		StringBuffer strbToSign = new StringBuffer();
		for ( String param: parametersToSign){
			strbToSign.append( nullToEmpty( request.getParameter( param)));
		}
		System.out.println( strbToSign);
		String merchantSig = HMACTools.getBase64EncodedSignature( request.getParameter( "secret"), strbToSign.toString());
		System.out.println( merchantSig);
		request.setAttribute( "merchantSig", merchantSig);
		
		// Generating billingAddressSig
		strbToSign.setLength( 0);
		for ( String param: billingAddressToSign){
			strbToSign.append( nullToEmpty( request.getParameter( param)));
		}
		if ( strbToSign.length() > 0){
			System.out.println( strbToSign);	
			String billingAddressSig = HMACTools.getBase64EncodedSignature( request.getParameter( "secret"), strbToSign.toString());
			System.out.println( billingAddressSig);	
			request.setAttribute( "billingAddressSig", billingAddressSig);
		}
		
		// Generating deliveryAddressSig
		strbToSign.setLength( 0);
		for ( String param: deliveryAddressToSign){
			strbToSign.append( nullToEmpty( request.getParameter( param)));
		}
		if ( strbToSign.length() > 0){
			System.out.println( strbToSign);	
			String deliveryAddressSig = HMACTools.getBase64EncodedSignature( request.getParameter( "secret"), strbToSign.toString());
			System.out.println( deliveryAddressSig);	
			request.setAttribute( "deliveryAddressSig", deliveryAddressSig);
		}
		
		// Generating shopperSig
		strbToSign.setLength( 0);
		for ( String param: shopperToSign){
			strbToSign.append( nullToEmpty( request.getParameter( param)));
		}
		if ( strbToSign.length() > 0){
			System.out.println( strbToSign);	
			String shopperSig = HMACTools.getBase64EncodedSignature( request.getParameter( "secret"), strbToSign.toString());
			System.out.println( shopperSig);	
			request.setAttribute( "shopperSig", shopperSig);
		}
		
		// Dispatche to form auto-submit
		RequestDispatcher dispatcher = request.getRequestDispatcher("/orderSubmit.jsp");
		dispatcher.forward(request,response);
	}
	
	private static String nullToEmpty( String str){
		if ( str == null)
			return "";
		else
			return str;
	}

}
