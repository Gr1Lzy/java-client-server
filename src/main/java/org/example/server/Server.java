package org.example.server;

import com.sun.net.httpserver.HttpServer;
import org.example.handlers.HtmlHandler;
import org.example.handlers.MessageStatisticsHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    private final ServerSocket serverSocket;
    private final Scanner scanner = new Scanner(System.in);

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    private void startHttpServer() throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8082), 0);
        httpServer.createContext("/messageStatistics", new MessageStatisticsHandler());
        httpServer.createContext("/", new HtmlHandler());
        httpServer.setExecutor(null);
        httpServer.start();
    }

    public void startServer() {
        System.out.println("Server started on port " + serverSocket.getLocalPort());

        new Thread(this::getCodeWord).start();

        try {
            startHttpServer();
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("Client joined the session!");
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getCodeWord() {
        String command = scanner.nextLine();

        if (command.equals("CODEWORD")) {
            ClientHandler.sendSpamToClients(1000);
        }
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8081);
            Server server = new Server(serverSocket);
            server.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
