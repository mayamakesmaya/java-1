

import java.util.Date;

import org.apache.axis.client.Call;
import org.apache.axis.client.Stub;

import com.adyen.services.common.Address;
import com.adyen.services.common.Amount;
import com.adyen.services.common.Installments;
import com.adyen.services.payment.Card;
import com.adyen.services.payment.PaymentLocator;
import com.adyen.services.payment.PaymentPortType;
import com.adyen.services.payment.PaymentRequest;
import com.adyen.services.payment.PaymentResult;


public class Payment {
    
	public static void main ( String[] args ) {
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
			
			Amount amount = new Amount( "GBP", 199 );
			request.setAmount( amount );
			
			// Payment method details
			Card card = new Card();
			card.setHolderName( "John Smith" );
			card.setCvc( "737" );
			card.setExpiryMonth( "06" );
			card.setExpiryYear( "2016" );
			card.setNumber( "4111111111111111" );
			request.setCard( card );
			
			// Billing address
			Address billingAddress = new Address ("Amsterdam", "NL", "100", "1088TT", "NH", "Simon Carmiggeltstraat");
			card.setBillingAddress(billingAddress);
			
			// Additional fields could be handy
			request.setInstallments( new Installments( (short) 2) );
			request.setShopperEmail( "shopperemail@server.com" );
			request.setShopperReference( "shopperreference" );
			request.setFraudOffset( -100 );
			request.setShopperIP("1.1.1.1");
			
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
