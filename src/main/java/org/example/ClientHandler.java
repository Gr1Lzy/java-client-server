package org.example;

import org.example.model.Message;
import org.example.model.MessageStatistic;
import org.example.model.MessageStatus;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler implements Runnable {
    public static List<ClientHandler> clientHandlerList = new CopyOnWriteArrayList<>();
    private final Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = bufferedReader.readLine();
            clientHandlerList.add(this);
            broadcastMessage(username + " has connected!");
        } catch (IOException e) {
            close(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                close(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public List<MessageStatistic> sendSpamToClients(int messageCount) {
        ExecutorService executor = Executors.newFixedThreadPool(clientHandlerList.size());
        List<MessageStatistic> messageStatistic = new ArrayList<>();
        for (ClientHandler clientHandler : clientHandlerList) {
            executor.execute(() -> {
                for (int i = 0; i < messageCount; i++) {
                    Message message = new Message("SPAM MESSAGE " + (i + 1));
                    try {
                        clientHandler.bufferedWriter.write(message +  " for " + clientHandler.username);
                        clientHandler.bufferedWriter.newLine();
                        clientHandler.bufferedWriter.flush();
                        messageStatistic.add(new MessageStatistic(clientHandler.username, message, MessageStatus.DELIVERED));

                    } catch (IOException e) {
                        e.printStackTrace();
                        messageStatistic.add(new MessageStatistic(clientHandler.username, message, MessageStatus.UNDELIVERED));
                    }
                }
            });
        }

        executor.close();
        executor.shutdown();

        return messageStatistic;
    }

    public void broadcastMessage(String message) {
        for (ClientHandler clientHandler : clientHandlerList) {
            try {
                if (!clientHandler.username.equals(username)) {
                    clientHandler.bufferedWriter.write(message);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                close(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void removeClientHandler() {
        clientHandlerList.remove(this);
        broadcastMessage(username + " has disconnected.");
    }

    public void close(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
