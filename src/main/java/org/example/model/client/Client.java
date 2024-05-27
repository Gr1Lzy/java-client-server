package org.example.model.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    private final String username;
    private final Socket socket;
    private static final Logger logger = Logger.getLogger(Client.class.getName());
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private static final Scanner scanner = new Scanner(System.in);

    public Client(Socket socket, String text) {
        this.socket = socket;
        this.username = "user" + text;
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            close();
            logger.log(Level.SEVERE, "Error occurred while starting server or handling client", e);
        }
    }

    public void sendUsername() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            close();
        }
    }

    public void listenForMessage() {
        new Thread(() -> {
            String messageFromServer;
            while (socket.isConnected()) {
                try {
                    messageFromServer = bufferedReader.readLine();
                    logger.log(Level.INFO, "Message from server: {0}", messageFromServer);
                } catch (IOException e) {
                    close();
                    break;
                }
            }
        }).start();
    }

    public void close() {
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error closing Client", e);
        }
    }

    public static void main(String[] args) {
        try {
            String text = scanner.next();
            Socket socket = new Socket("localhost", 8081);
            Client client = new Client(socket, text);
            client.listenForMessage();
            client.sendUsername();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error occurred while starting server or handling client", e);
        }
    }
}
