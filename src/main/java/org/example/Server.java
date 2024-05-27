package org.example;

import org.example.model.MessageStatistic;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {
    Scanner scanner = new Scanner(System.in);
    private final ServerSocket serverSocket;
    private final List<ClientHandler> clientHandlers = new ArrayList<>();

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        System.out.println("Server started on port " + serverSocket.getLocalPort());
        new Thread(this::getCodeWord).start();

        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("Client joined the session!");
                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandlers.add(clientHandler);

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
        for (MessageStatistic messageStatistic : ClientHandler.messageStatistics) {
            System.out.println(messageStatistic);
        }

        getCodeWord();
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