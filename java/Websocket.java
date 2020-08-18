import java.net.URI;
import java.net.URISyntaxException;


public class Websocket {

    public static void start() {
        try {
            // open websocket
            final WebsocketClientEndpoint clientEndPoint = new WebsocketClientEndpoint(new URI("wss://stream.bybit.com/realtime"));

            // add listener
            clientEndPoint.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
                public void handleMessage(String message) {
                    System.out.println(message);
                }
            });

            // send message to websocket
            clientEndPoint.sendMessage("{\"op\": \"subscribe\", \"args\": [\"instrument_info.100ms.BTCUSD\"]}");

        } catch (URISyntaxException ex) {
            System.err.println("URISyntaxException exception: " + ex.getMessage());
        }
    }
}
