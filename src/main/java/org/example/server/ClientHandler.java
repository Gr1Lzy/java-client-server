package org.example.server;

import org.example.model.Message;
import org.example.model.MessageStatistic;
import org.example.model.MessageStatus;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ClientHandler implements Runnable {
    public static final List<ClientHandler> CLIENT_HANDLER_LIST = new CopyOnWriteArrayList<>();
    public static List<MessageStatistic> messageStatistics;

    private final Socket socket;
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;
    private final String clientId;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.clientId = bufferedReader.readLine();
        CLIENT_HANDLER_LIST.add(this);
    }

    @Override
    public void run() {
        String messageFromClient;
        try {
            while ((messageFromClient = bufferedReader.readLine()) != null) {
                broadcastMessage(messageFromClient);
            }
        } catch (IOException e) {
            close();
        }
    }

    public static void sendSpamToClients(int messageCount) {
        messageStatistics = Collections.synchronizedList(new ArrayList<>());
        ExecutorService executor = Executors.newFixedThreadPool(CLIENT_HANDLER_LIST.size());

        for (ClientHandler clientHandler : CLIENT_HANDLER_LIST) {
            executor.execute(() -> {
                for (int i = 0; i < messageCount; i++) {
                    Message message = new Message("SPAM MESSAGE " + (i + 1));
                    try {
                        clientHandler.bufferedWriter.write(message.getText() + " for " + clientHandler.clientId);
                        clientHandler.bufferedWriter.newLine();
                        clientHandler.bufferedWriter.flush();
                        messageStatistics.add(new MessageStatistic(clientHandler.clientId, message, MessageStatus.DELIVERED, LocalDateTime.now()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        messageStatistics.add(new MessageStatistic(clientHandler.clientId, message, MessageStatus.UNDELIVERED, null));
                    }
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMessage(String message) {
        for (ClientHandler clientHandler : CLIENT_HANDLER_LIST) {
            try {
                if (!clientHandler.clientId.equals(clientId)) {
                    clientHandler.bufferedWriter.write(message);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                close();
            }
        }
    }

    public void removeClientHandler() {
        CLIENT_HANDLER_LIST.remove(this);
    }

    public void close() {
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
