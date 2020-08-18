public class Main {
    public static void main(String[] args) {
        double price = MarketData.getLastPrice("BTCUSD");
        ActiveOrder a = new ActiveOrder("BTCUSD", "Buy", "Limit", price, 1);
        OrderMonitor.add(a);
        OrderMonitor.monitor();
    }

}