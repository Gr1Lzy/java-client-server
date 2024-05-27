package org.example.server;

import com.sun.net.httpserver.HttpServer;
import org.example.handlers.HtmlHandler;
import org.example.handlers.MessageStatisticsHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private final ServerSocket serverSocket;
    private final Scanner scanner = new Scanner(System.in);
    private final Logger logger  = Logger.getLogger(Server.class.getName());

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startHttpServer() throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8082), 0);
        httpServer.createContext("/messageStatistics", new MessageStatisticsHandler());
        httpServer.createContext("/", new HtmlHandler());
        httpServer.setExecutor(null);
        httpServer.start();
    }

    public void startServer() {
        logger.log(java.util.logging.Level.INFO, "Server started on port {0}",
                String.valueOf(serverSocket.getLocalPort()));

        new Thread(this::getCodeWord).start();

        try {
            startHttpServer();
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                logger.log(java.util.logging.Level.INFO, "Client connected!");
                new ClientHandler(socket);
            }
        } catch (IOException e){
            logger.log(Level.SEVERE, "Error occurred while starting server or handling client", e);
        }
    }

    public void getCodeWord() {
        String command = scanner.next();

        if (command.equals("CODEWORD") && !ClientHandler.clientHandlers.isEmpty()) {
            ClientHandler.sendMessagesToClients(1000);
        } else {
            getCodeWord();
        }
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8081);
            Server server = new Server(serverSocket);
            server.startServer();
        } catch (IOException e) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Failed to start the server", e);
        }
    }
}
