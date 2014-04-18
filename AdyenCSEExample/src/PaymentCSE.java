

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
@WebServlet("/PaymentCSE")
public class PaymentCSE {
    
	protected void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {
		
//		String pubKey = "10001|80C7821C961865FB4AD23F172E220F819A5CC7B9956BC3458E2788"
//				 + "F9D725B07536E297B89243081916AAF29E26B7624453FC84CB10FC7DF386"
//				 + "31B3FA0C2C01765D884B0DA90145FCE217335BCDCE4771E30E6E5630E797"
//				 + "EE289D3A712F93C676994D2746CBCD0BEDD6D29618AF45FA6230C1D41FE1"
//				 + "DB0193B8FA6613F1BD145EA339DAC449603096A40DC4BF8FACD84A5D2CA5"
//				 + "ECFC59B90B928F31715A7034E7B674E221F1EB1D696CC8B734DF7DE2E309"
//				 + "E6E8CF94156686558522629E8AF59620CBDE58327E9D84F29965E4CD0FAF"
//				 + "A38C632B244287EA1F7F70DAA445D81C216D3286B09205F6650262CAB415"
//				 + "5F024B3294A933F4DC514DE0B5686F6C2A6A2D"; 
//
//		Encrypter e;
//		
//		try {
//			e = new Encrypter(pubKey);
//
//			JSONCard jsonCard = new JSONCard.Builder(new Date())
//				.number(httpRequest.getParameter("adyen-encrypted-form-number"))
//				.cvc(httpRequest.getParameter("adyen-encrypted-form-cvc"))
//				.expiryMonth(httpRequest.getParameter("adyen-encrypted-form-month"))
//				.expiryYear(httpRequest.getParameter("adyen-encrypted-form-expiry-year"))
//				.holderName(httpRequest.getParameter("adyen-encrypted-form-holder-name"))
//				.build();
//
//			String encryptedCard = e.encrypt(jsonCard.toString());
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
