import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;

public class MarketData {

    private static String BASE_ENDPOINT = "api-testnet.bybit.com";

    /**
     * Gets last 200 candles on Bybit exchange
     * @param symbol Trading pair
     * @param interval timeframe of candles
     * @return array of last 200 candles
     */
    public static Candle[] getCandles(String symbol, int interval) {

        Candle[] candles;

        try {
            URI uri = new URIBuilder()
                    .setScheme("https")
                    .setHost(BASE_ENDPOINT)
                    .setPath("/v2/public/kline/list")
                    .setParameter("interval", String.valueOf(interval))
                    .setParameter("from", String.valueOf((System.currentTimeMillis() / 1000) - (200 * 60 * interval)))
                    .setParameter("limit", "200")
                    .setParameter("symbol", symbol)
                    .build();

            JSONObject response = Utility.get(uri);
            JSONArray jsonArray = response.getJSONArray("result");

            candles = new Candle[200];

            for (int i = 0; i < 200; i++) {
                JSONObject temp = (JSONObject) jsonArray.get(i);
                candles[i] = new Candle(temp.getDouble("high"), temp.getDouble("low"), temp.getDouble("open"), temp.getDouble("close"), temp.getInt("open_time"));
            }

            return candles;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Gets last price from Bybit exchange
     * @param symbol trading pair
     * @return the last price
     */
    public static double getLastPrice(String symbol) {

        try {
            URI uri = new URIBuilder()
                    .setScheme("https")
                    .setHost(BASE_ENDPOINT)
                    .setPath("/v2/public/tickers")
                    .setParameter("symbol", symbol)
                    .build();

            JSONObject response = Utility.get(uri);

            JSONArray jsonArray = (JSONArray) response.get("result");
            JSONObject result = (JSONObject) jsonArray.get(0);

            return result.getDouble("last_price");
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
