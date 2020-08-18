import org.json.JSONObject;

public class ActiveOrder {

    private String symbol, orderID, side;
    private double orderCreationPrice, currentPrice, executedPrice;
    private int quantity, expiration;
    private boolean isLong;

    /**
     * Constructor for class ActiveOrder
     *
     * @param symbol    this order's symbol
     * @param side      this order's side
     * @param orderType "Limit" or "Market"
     * @param price     the price of the asset on order creation
     * @param quantity  the quantity of contracts
     */
    public ActiveOrder(String symbol, String side, String orderType, double price, int quantity) {

        JSONObject response = Client.placeOrder(symbol, side, orderType, price, quantity, -1, -1, false);

        if (response.getInt("ret_code") != 0)
            Logger.Log(response.getInt("ret_code") + " Error");

        else {
            JSONObject result = response.getJSONObject("result");

            this.symbol = result.getString("symbol");
            this.orderID = result.getString("order_id");
            this.side = result.getString("side");
            this.orderCreationPrice = this.currentPrice = result.getDouble("price");
            this.quantity = result.getInt("qty");

            if (side.equals("Buy"))
                this.isLong = true;
            else if (side.equals("Sell"))
                this.isLong = false;
            else
                Logger.Log("Error, active order is not Buy or Sell");
        }
    }

    /**
     * Checks to see if last action was successful
     *
     * @return false if order was cancelled or rejected, otherwise true
     */
    public boolean wasSuccessfullyPlaced() {
        JSONObject response = Client.getOrder(symbol, orderID);

        if (response.getInt("ret_code") != 0) {
            Logger.Log(response.toString());
            return false;
        }

        JSONObject result = response.getJSONObject("result");

        String orderStatus = result.getString("order_status");

        return !orderStatus.equals("Cancelled") && !orderStatus.equals("Rejected");
    }

    /**
     * Gets update of current order
     *
     * @return response from exchange
     */
    public JSONObject getUpdate() {
        return Client.getOrder(symbol, orderID);
    }

    public void forceLimitIn() {

        double lastPrice = MarketData.getLastPrice(symbol);

        if (isLong && lastPrice - 0.5 > currentPrice) {
            try {
                Client.editOrder(symbol, orderID, lastPrice - 0.5, -1);
                if (wasSuccessfullyPlaced())
                    currentPrice = lastPrice - 0.5;
            } catch (Exception e) {
                Error.handle(e);
            }
        } else if (!isLong && lastPrice + 0.5 < currentPrice) {
            try {
                Client.editOrder(symbol, orderID, lastPrice + 0.5, -1);
                currentPrice = lastPrice + 0.5;
            } catch (Exception e) {
                Error.handle(e);
            }
        }
    }

    public String getSide() {
        return side;
    }
}
