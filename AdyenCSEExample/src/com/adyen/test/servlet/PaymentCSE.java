package com.adyen.test.servlet;


import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.axis.client.Call;
import org.apache.axis.client.Stub;

import com.adyen.services.common.Address;
import com.adyen.services.common.Amount;
import com.adyen.services.payment.Card;
import com.adyen.services.payment.PaymentLocator;
import com.adyen.services.payment.PaymentPortType;
import com.adyen.services.payment.PaymentRequest;
import com.adyen.services.payment.PaymentResult;

/**
 * Servlet implementation class PaymentCSE
 */
@WebServlet("/paymentCSE")
public class PaymentCSE {
    
	protected void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {
		
		try {
		
			PaymentPortType ws = new PaymentLocator().getPaymentHttpPort( new java.net.URL( Credentials.test_address) );
			
			// Basic HTTP Authentication
			( (Stub) ws )._setProperty( Call.USERNAME_PROPERTY, Credentials.ws_user );
			( (Stub) ws )._setProperty( Call.PASSWORD_PROPERTY, Credentials.pwd_user );
			
			// Payment data
			PaymentRequest request = new PaymentRequest();
			request.setMerchantAccount( Credentials.merchant_account );
			
			// Your order number
			request.setReference( "Payment Test -"+System.currentTimeMillis() );
			
			Amount amount = new Amount( 199L,"GBP");
			request.setAmount(amount);
			
			// Payment method details
			Map<String,String> additionalData = new TreeMap<String,String>();
			additionalData.put( "card.encrypted.json", httpRequest.getParameter("data-encrypted-name") );
			request.setAdditionalData(additionalData);
			
			// Additional fields could be handy
			request.setShopperEmail( "shopperemail@server.com" );
			request.setShopperReference( "shopperreference" );
			request.setFraudOffset( -100 );
			request.setShopperIP( "1.1.1.1" );
			
			// Billing address
			Card card = new Card();
			Address billingAddress = new Address();
			billingAddress.setCity( "Amsterdam" );
			billingAddress.setCountry( "NL" );
			billingAddress.setHouseNumberOrName( "100" );
			billingAddress.setPostalCode( "1088TT" );
			billingAddress.setStateOrProvince( "NH" );
			billingAddress.setStreet( "Simon Carmiggelstraat" );
			card.setBillingAddress( billingAddress );
			
			// Send the request
			PaymentResult result = ws.authorise( request );
			
			System.out.println( new Date() );
			
			System.out.println( result.getPspReference() );
			System.out.println( result.getResultCode() );
			System.out.println( result.getAuthCode() );
			System.out.println( result.getRefusalReason() );

		}
		catch ( Exception ex ) {
			ex.printStackTrace();
		}

	}

}
