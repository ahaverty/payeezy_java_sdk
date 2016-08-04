package com.firstdata.payeezy.samples;

import com.firstdata.payeezy.JSONHelper;
import com.firstdata.payeezy.PayeezyClientHelper;
import com.firstdata.payeezy.models.transaction.*;

/**
 * Example to illustrate an authorization and reversal(void) using a credit card
 */
public class CreditCardPurchaseAndVoidSample {

    public static void main(String [] args){
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAmount("100"); // always set the amouunt in cents
        transactionRequest.setTransactionType(TransactionType.PURCHASE.name().toLowerCase());
        transactionRequest.setPaymentMethod(PaymentMethod.CREDIT_CARD.getValue());
        transactionRequest.setReferenceNo(""+System.currentTimeMillis()); // this is your order number
        transactionRequest.setCurrency("USD");
        // set the credit card info
        Card card = new Card();
        card.setName("Not Provided");
        card.setNumber("");// credit card number
        card.setType("visa");
        card.setCvv("123");
        card.setExpiryDt("1020");
        card.setNumber("4012000033330026");
        transactionRequest.setCard(card);
        // this picks the properties from the .payeezy.properties files
        // alternatively you can populate the properties and pass it to the constructor
        PayeezyClientHelper clientHelper = new PayeezyClientHelper();
        try{
            PayeezyResponse payeezyResponse = clientHelper.doPrimaryTransaction(transactionRequest);
            JSONHelper jsonHelper = new JSONHelper();
            TransactionResponse transactionResponse = jsonHelper.fromJson(payeezyResponse.getResponseBody(), TransactionResponse.class);
            // void the credit card authorization
            // refer to https://developer.payeezy.com for payload properties
            TransactionRequest voidRequest = new TransactionRequest();
            voidRequest.setTransactionTag(transactionResponse.getTransactionTag());
            voidRequest.setTransactionType(TransactionType.VOID.name().toLowerCase());
            voidRequest.setPaymentMethod(PaymentMethod.CREDIT_CARD.getValue());
            voidRequest.setAmount("100"); // should always match the request amount
            voidRequest.setCurrency("USD");
            PayeezyResponse voidPayeezyResponse = clientHelper.doSecondaryTransaction(transactionResponse.getTransactionId(), voidRequest);
            System.out.println(voidPayeezyResponse.getResponseBody());
       }catch (Exception e){
            e.printStackTrace();
       }
       System.exit(0);
    }
}
