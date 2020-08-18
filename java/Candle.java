public class Candle {

    private double high, low, open, close;
    private int openTime;

    public Candle(double high, double low, double open, double close, int openTime) {
        this.high = high;
        this.low = low;
        this.open = open;
        this.close = close;
        this.openTime = openTime;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getOpen() {
        return open;
    }

    public double getClose() {
        return close;
    }

    public int getOpenTime() {
        return openTime;
    }

    public double get(String src) {
        switch (src) {
            case "open":
                return open;
            case "close":
                return close;
            case "high":
                return high;
            case "low":
                return low;
            default:
                return 0;
        }
    }

    @Override
    public String toString() {
        return "Open: " + open;
    }
}
