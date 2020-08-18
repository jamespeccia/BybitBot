public class TEMA {

    public static double[] getTEMA(String symbol, int interval, String src, int length) {

        Candle[] candles = null;
        try {
            candles = MarketData.getCandles(symbol, interval);
        } catch (Exception e) {
            e.printStackTrace();
        }

        double[] ema1 = new double[200];
        double[] ema2 = new double[200];
        double[] ema3 = new double[200];

        double multiplier = 2.0 / (1 + length);

        ema1[0] = candles[0].get(src);
        ema1[1] = ((candles[1].get(src) - candles[0].get(src)) * multiplier) + candles[0].get(src);

        int j = 1;
        for (int i = 2; i < ema1.length; i++) {
            ema1[i] = ((candles[i].get(src) - ema1[j]) * multiplier) + ema1[j];
            j++;
        }

        ema2[0] = ema1[0];
        ema2[1] = ((ema1[1] - ema1[0]) * multiplier) + ema1[0];

        j = 1;
        for (int i = 2; i < ema2.length; i++) {
            ema2[i] = ((ema1[i] - ema2[j]) * multiplier) + ema2[j];
            j++;
        }

        ema3[0] = ema2[0];
        ema3[1] = ((ema2[1] - ema2[0]) * multiplier) + ema2[0];

        j = 1;
        for (int i = 2; i < ema3.length; i++) {
            ema3[i] = ((ema2[i] - ema3[j]) * multiplier) + ema3[j];
            j++;
        }

        double[] last = new double[2];

        last[0] = 3 * ema1[198] - 3 * ema2[198] + ema3[198];
        last[1] = 3 * ema1[199] - 3 * ema2[199] + ema3[199];

        return last;

    }

}
