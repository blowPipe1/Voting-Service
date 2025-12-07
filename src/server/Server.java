package server;

import com.sun.net.httpserver.HttpServer;
import handlers.CandidatesHandler;
import handlers.StaticFileHandler;
import handlers.ThankYouHandler;
import handlers.VoteHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {

    public Server(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", new CandidatesHandler());
        server.createContext("/vote", new VoteHandler());
        server.createContext("/thankyou", new ThankYouHandler());
        server.createContext("/css/", new StaticFileHandler("src/data"));
        server.createContext("/images/", new StaticFileHandler("src/data"));

        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port " + port);

    }
}
