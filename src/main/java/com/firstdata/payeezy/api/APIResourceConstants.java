package com.firstdata.payeezy.api;

/**
 * Contains all the resource constants and security constants
 * LIVE_URL is the production URL
 */
public class APIResourceConstants {
    public static final String SANDBOX_URL = "https://api-cert.payeezy.com";
    public static final String LIVE_URL = "https://api-cert.payeezy.com";
    public static final String PRIMARY_TRANSACTIONS = "/v1/transactions";
    public static final String EXCHANGE_RATE = "/v1/transactions/exchange_rate";
    public static final String SECURE_TOKEN_URL = "/v1/securitytokens";

    public class SecurityConstants {
        public static final String NONCE = "nonce";
        public static final String APIKEY = "apikey";
        public static final String APISECRET = "pzsecret";
        public static final String TOKEN = "token";
        public static final String TIMESTAMP = "timestamp";
        public static final String AUTHORIZE = "Authorization";
        public static final String PAYLOAD = "payload";
        public static final String JS_SECURITY_KEY = "js_security_key";
    }

    public class PropertyConstants {
        public static final String JS_SECURITY_KEY = SecurityConstants.JS_SECURITY_KEY;
        public static final String URL_TAG = "url";
        public static final String PZ_SECRET = SecurityConstants.APISECRET;
        public static final String TOKEN = SecurityConstants.TOKEN;
        public static final String API_KEY = SecurityConstants.APIKEY;
        public static final String PRINT_JSON = "printjson";
        public static final String TIMEOUT = "timeout";
        public static final String PROXY_HOST = "proxyHost";
        public static final String PROXY_PORT = "proxyPort";
    }
}
