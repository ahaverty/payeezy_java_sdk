package com.firstdata.payeezy;

import com.firstdata.payeezy.api.APIResourceConstants;
import com.firstdata.payeezy.api.PayeezyRequestOptions;
import com.firstdata.payeezy.api.RequestMethod;
import com.firstdata.payeezy.client.PayeezyClient;
import com.firstdata.payeezy.models.exception.ApplicationRuntimeException;
import com.firstdata.payeezy.models.transaction.PayeezyResponse;
import com.firstdata.payeezy.models.transaction.TransactionRequest;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import static com.firstdata.payeezy.api.APIResourceConstants.PropertyConstants.URL_TAG;

/**
 * Helper class that provides convenient methods to execute primary, secondary, getToken and dynamic pricing API's
 * the URL should always point to the host.
 * when making the GetToken call do not pass the protocol as the helper sets up the protocol
 */
public class PayeezyClientHelper {

    private PayeezyClient payeezyClient;
    private Properties properties;
    private JSONHelper jsonHelper;

    final private static String PROPERTIES_FILE_LOCATION = "properties.json";

    public PayeezyClientHelper(Properties properties) {
        if (properties != null && !properties.isEmpty()) {
            payeezyClient = new PayeezyClient(properties);
        } else {
            throw new ApplicationRuntimeException("SDK Properties should be configured to use the Client. Please provide the required properties based on the type of transaction.");
        }
        jsonHelper = new JSONHelper();
        this.properties = properties;
    }

    public PayeezyClientHelper() {
        this(getProperties());
    }

    public static Properties getProperties() {
        Properties properties;

        try {
            URL resource = PayeezyClientHelper.class.getClassLoader().getResource(PROPERTIES_FILE_LOCATION);
            File propertiesFile = Paths.get(resource.toURI()).toFile();

            JsonReader reader = new JsonReader(new FileReader(propertiesFile));
            Gson gson = new Gson();
            properties = gson.fromJson(reader, Properties.class);
        } catch (URISyntaxException | FileNotFoundException e) {
            throw new ApplicationRuntimeException(PROPERTIES_FILE_LOCATION + " was not found: " + e.getMessage());
        }
        return properties;
    }

    /**
     * Use this for primary transactions like Authorize, Purchase
     *
     * @param transactionRequest
     * @return
     * @throws Exception
     */
    public PayeezyResponse doPrimaryTransaction(TransactionRequest transactionRequest) throws Exception {
        String payload = jsonHelper.getJSONObject(transactionRequest);
        String url = properties.getProperty(URL_TAG);
        url = url + APIResourceConstants.PRIMARY_TRANSACTIONS;
        return payeezyClient.execute(url, RequestMethod.POST, getRequestOptions(), payload);
    }

    /**
     * Use this for Secondary transactions like void, refund, capture etc
     */

    public PayeezyResponse doSecondaryTransaction(String id, TransactionRequest transactionRequest) throws Exception {
        String url = properties.getProperty(URL_TAG);
        url = url + APIResourceConstants.PRIMARY_TRANSACTIONS + "/" + id;
        String payload = jsonHelper.getJSONObject(transactionRequest);
        return payeezyClient.execute(url, RequestMethod.POST, getRequestOptions(), payload);
    }

    /**
     * Get Token Call to tokenize a credit card
     *
     * @param queryMap
     * @return
     * @throws Exception
     */
    public PayeezyResponse doGetTokenCall(Map<String, String> queryMap) throws Exception {
        String url = properties.getProperty(URL_TAG) + APIResourceConstants.SECURE_TOKEN_URL;
        if (url.contains("http://")) {
            url = url.replace("https://", "");
        }
        if (!queryMap.containsKey(APIResourceConstants.SecurityConstants.APIKEY)) {
            String apikey = properties.getProperty(APIResourceConstants.SecurityConstants.APIKEY);
            queryMap.put(APIResourceConstants.SecurityConstants.APIKEY, apikey);
        }

        if (!queryMap.containsKey(APIResourceConstants.SecurityConstants.JS_SECURITY_KEY)) {
            String jsSecurityKey = properties.getProperty(APIResourceConstants.SecurityConstants.JS_SECURITY_KEY);
            queryMap.put(APIResourceConstants.SecurityConstants.JS_SECURITY_KEY, jsSecurityKey);
        }
        return payeezyClient.execute(url, RequestMethod.GET, null, queryMap);
    }

    /**
     * Dynamic Pricing look up
     *
     * @param transactionRequest
     * @return
     * @throws Exception
     */
    public PayeezyResponse doExchangeRate(TransactionRequest transactionRequest) throws Exception {
        String url = properties.getProperty(URL_TAG) + APIResourceConstants.EXCHANGE_RATE;
        String payload = jsonHelper.getJSONObject(transactionRequest);
        return payeezyClient.execute(url, RequestMethod.POST, getRequestOptions(), payload);
    }


    /**
     * API Call to search for events
     *
     * @param url
     * @param queryMap
     * @param requestOptions
     * @return
     * @throws Exception
     */
    public PayeezyResponse getEvents(String url, Map<String, String> queryMap, PayeezyRequestOptions requestOptions) throws Exception {
        url = url + APIResourceConstants.SECURE_TOKEN_URL;
        return payeezyClient.execute(url, RequestMethod.GET, requestOptions, queryMap);
    }

    private PayeezyRequestOptions getRequestOptions() {
        String apikey = properties.getProperty(APIResourceConstants.SecurityConstants.APIKEY);
        String secret = properties.getProperty(APIResourceConstants.SecurityConstants.APISECRET);
        String token = properties.getProperty(APIResourceConstants.SecurityConstants.TOKEN);
        return new PayeezyRequestOptions(apikey, token, secret);
    }
}
