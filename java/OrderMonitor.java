import org.json.JSONObject;

import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

public class OrderMonitor {

    public static HashSet<ActiveOrder> activeOrders = new HashSet<>();

    public static void monitor() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (ActiveOrder order : activeOrders) {
                    JSONObject response = order.getUpdate().getJSONObject("result");
                    String orderStatus = response.getString("order_status");

                    switch (orderStatus) {
                        case "New": {
                            try {
                                order.forceLimitIn();
                            } catch (Exception e) {
                                Error.handle(e);
                            }
                            break;
                        }

                        case "Filled": {
                            Logger.Log(" order executed");
                            activeOrders.remove(order);
                            break;
                        }

                        case "Cancelled": {
                            Logger.Log("Order cancelled for unknown reason");
                            order.forceLimitIn();
                            break;
                        }
                    }
                }
            }
        }, 0, 1000);
    }

    public static void add(ActiveOrder activeOrder) {
        Logger.Log(activeOrder.getSide() + " order was created");
        activeOrders.add(activeOrder);
    }
}


