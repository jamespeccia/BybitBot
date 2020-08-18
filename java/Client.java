import org.apache.commons.codec.binary.Hex;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Client {

    private static String API_KEY = "";
    private static String SECRET = "";
    private static String BASE_ENDPOINT = "api-testnet.bybit.com";

    /**
     * Places an order on Bybit exchange
     * @param symbol Trading pair
     * @param side "Buy" for long, "Sell" for short
     * @param orderType "Market" or "Limit"
     * @param price Price to buy or sell at
     * @param quantity Amount to buy or sell
     * @param takeProfit Price to take profit
     * @param stopLoss Price to exit trade
     * @param reduceOnly True if trade is to reduce the size of a current position
     * @return response from Bybit API
     */
    public static JSONObject placeOrder(String symbol, String side, String orderType, double price, int quantity, double takeProfit, double stopLoss, boolean reduceOnly) {

        try {
            List<NameValuePair> payload = new ArrayList<>();
            payload.add(new BasicNameValuePair("api_key", API_KEY));
            payload.add(new BasicNameValuePair("order_type", orderType));
            payload.add(new BasicNameValuePair("price", String.valueOf(price)));
            payload.add(new BasicNameValuePair("qty", String.valueOf(quantity)));
            payload.add(new BasicNameValuePair("side", side));
            payload.add(new BasicNameValuePair("symbol", symbol));
            payload.add(new BasicNameValuePair("time_in_force", "PostOnly"));
            payload.add(new BasicNameValuePair("timestamp", String.valueOf(System.currentTimeMillis())));

            URI uri = new URIBuilder()
                    .setScheme("https")
                    .setHost(BASE_ENDPOINT)
                    .setPath("/v2/private/order/create")
                    .setParameters(payload)
                    .build();

            payload.add(new BasicNameValuePair("sign", sign(uri.getRawQuery())));

            uri = new URIBuilder()
                    .setScheme("https")
                    .setHost(BASE_ENDPOINT)
                    .setPath("/v2/private/order/create")
                    .setParameters(payload)
                    .build();

            return Utility.post(uri);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Edits order on Bybit exchange
     * @param symbol Trading pair
     * @param orderID order_id of order to edit
     * @param newPrice New price to execute order, -1 if no change
     * @param newQuantity New quantity to exit order, -1 if no change
     * @return response from Bybit API
     */
    public static JSONObject editOrder(String symbol, String orderID, double newPrice, int newQuantity) {

        try {
            List<NameValuePair> payload = new ArrayList<>();

            payload.add(new BasicNameValuePair("api_key", API_KEY));
            payload.add(new BasicNameValuePair("order_id", orderID));

            if (newPrice != -1)
                payload.add(new BasicNameValuePair("p_r_price", String.valueOf(newPrice)));

            if (newQuantity != -1)
                payload.add(new BasicNameValuePair("p_r_qty", String.valueOf(newQuantity)));

            payload.add(new BasicNameValuePair("symbol", symbol));
            payload.add(new BasicNameValuePair("timestamp", String.valueOf(System.currentTimeMillis())));

            URI uri = new URIBuilder()
                    .setScheme("https")
                    .setHost(BASE_ENDPOINT)
                    .setPath("/open-api/order/replace")
                    .setParameters(payload)
                    .build();

            payload.add(new BasicNameValuePair("sign", sign(uri.getRawQuery())));

            uri = new URIBuilder()
                    .setScheme("https")
                    .setHost(BASE_ENDPOINT)
                    .setPath("/open-api/order/replace")
                    .setParameters(payload)
                    .build();

            return Utility.post(uri);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets order on Bybit exchange
     * @param symbol Trading pair
     * @param orderID order_id of order to fetch
     * @return response from Bybit API
     */
    public static JSONObject getOrder(String symbol, String orderID) {

        try {
            List<NameValuePair> payload = new ArrayList<>();

            payload.add(new BasicNameValuePair("api_key", API_KEY));
            payload.add(new BasicNameValuePair("order_id", orderID));
            payload.add(new BasicNameValuePair("symbol", symbol));
            payload.add(new BasicNameValuePair("timestamp", String.valueOf(System.currentTimeMillis())));

            URI uri = new URIBuilder()
                    .setScheme("https")
                    .setHost(BASE_ENDPOINT)
                    .setPath("/v2/private/order")
                    .setParameters(payload)
                    .build();

            payload.add(new BasicNameValuePair("sign", sign(uri.getRawQuery())));

            uri = new URIBuilder()
                    .setScheme("https")
                    .setHost(BASE_ENDPOINT)
                    .setPath("/v2/private/order")
                    .setParameters(payload)
                    .build();

            return Utility.get(uri);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Signs a request for authentication
     * @param parameters parameters of the request
     * @return a signed string
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    private static String sign(String parameters) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(SECRET.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return Hex.encodeHexString(sha256_HMAC.doFinal(parameters.getBytes()));
    }

}

