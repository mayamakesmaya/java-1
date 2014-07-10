Adyen Java Integration
==============
The code examples in this repository help you integrate with the Adyen platform using Java. Please go through the code examples and read the documentation in the files itself. Each code example requires you to change some parameters to connect to your Adyen account, such as merchant account and skincode.

## Servlet overview
```
1.HPP (Hosted Payment Page)
  - CreatePaymentOnHpp             : Simple form creating a payment on our HPP
  - CreatePaymentOnHppAdvanced     : Advanced form creating a payment on our HPP
  - CreatePaymentUrl               : Create payment URL on our HPP
2.API
  - HttpPost
    - CreatePaymentAPI             : Create a payment via our API
    - CreatePaymentCSE             : Create a Client-Side Encrypted payment
  - SOAP
    - CreatePaymentOnHpp           : Create a payment via our API
    - CreatePaymentOnHppAdvanced   : Create a Client-Side Encrypted payment
```

## Code structure
```
src                                       : Java implementation of the servlets
  - com.adyen.examples.hpp                
    - CreatePaymentOnHpp.java
    - CreatePaymentOnHppAdvanced.java
  - com.adyen.examples.api                
    - CreatePaymentAPIHttpPost.java
    - CreatePaymentAPISoap.java    
    - CreatePaymentCSEHttpPost.java
    - CreatePaymentCSESoap.java
WebContent                                : JSP pages for the servlets
  - 1.HPP
    - create-payment-on-hpp.jsp
    - create-payment-on-hpp-advanced.jsp
  - 2.API
    - create-payment-api.jsp
    - create-payment-cse.jsp
  - js
    - adyen.encrypt.min.js                : JavaScript file required for encrypting card data
  - WEB-INF/lib/                          : Java libraries (JARs) used in the servlets
  - WEB-INF/web.xml                       : Deployment descriptor
```

## Manuals
The code examples are based on our Integration manual and the API manual which provides rich information on how our platform works. Please find our manuals on the Developers section at www.adyen.com. 

## Support
If you do have any suggestions or questions regarding the code examples please send an e-mail to support@adyen.com.
