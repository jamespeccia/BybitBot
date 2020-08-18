import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AlertHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String alert = br.readLine();
        handleAlert(alert);
        exchange.close();
    }

    public void handleAlert(String alert) {
        if (alert.contains("Enter Long")) {
            JSONObject response = placeOrder("Buy");
            handleResponse(response);
        } else if (alert.contains("Enter Short")) {
            JSONObject response = placeOrder("Sell");
            handleResponse(response);
        }
    }

    public JSONObject placeOrder(String side) {
        double lastPrice = MarketData.getLastPrice("BTCUSD");
        double delta = side.equals("Buy") ? -0.5 : 0.5;
        try {
            return Client.placeOrder("BTCUSD", side, "Limit", lastPrice + delta, 1, 0, 0, false);
        } catch (Exception e) {
            Error.handle(e);
            return null;
        }
    }

    public void handleResponse(JSONObject response) {
        try {
            JSONObject result = response.getJSONObject("result");
            if (result.getString("order_status").equals("Created")) {
                OrderMonitor.add(new ActiveOrder("BTCUSD", result.getString("order_id"), result.getString("side"), result.getDouble("price"), result.getInt("qty")));
            } else {
                Logger.Log("Limit order failed. Retrying.");
                placeOrder(result.getString("side"));
            }
        } catch (Exception e) {
            Error.handle(e);
        }
    }

}
