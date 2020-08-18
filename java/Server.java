import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;


public class Server {

    private HttpServer httpServer;

    public Server(int port) throws IOException {
        this.httpServer = null;

        this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);

        this.httpServer.createContext("/hello", new AlertHandler());

        this.httpServer.setExecutor(null);
    }

    public void start() {
        this.httpServer.start();
    }
}

