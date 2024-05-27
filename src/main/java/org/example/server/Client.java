package org.example.server;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final String username;
    private final Socket socket;
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;
    private static final Scanner scanner = new Scanner(System.in);

    public Client(Socket socket, String text) {
        this.socket = socket;
        this.username = "user" + text;
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            close();
            throw new RuntimeException("Error creating client", e);
        }
    }

    public void sendMessage() {
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
                    System.out.println(messageFromServer);
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
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            String text = scanner.next();
            Socket socket = new Socket("localhost", 8081);
            Client client = new Client(socket, text);
            client.listenForMessage();
            client.sendMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
