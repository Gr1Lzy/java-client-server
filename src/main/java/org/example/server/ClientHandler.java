package org.example.server;

import org.example.model.message.Message;
import org.example.model.message.MessageStatistic;
import org.example.model.message.MessageStatus;

import java.io.*;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler  {
    public static List<ClientHandler> clientHandlers = new CopyOnWriteArrayList<>();
    public static List<MessageStatistic> messageStatistics;

    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());
    private final Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientId;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.clientId = bufferedReader.readLine();
        } catch (IOException e) {
            close();
            logger.log(Level.SEVERE, "Error initializing ClientHandler", e);
        }
        clientHandlers.add(this);
    }

    public static void sendSpamToClients(int messageCount) {
        messageStatistics = Collections.synchronizedList(new ArrayList<>());

        try (ExecutorService executor = Executors.newFixedThreadPool(clientHandlers.size())) {
            for (ClientHandler clientHandler : clientHandlers) {
                executor.execute(() -> {
                    for (int i = 0; i < messageCount; i++) {
                        Message message = new Message("SPAM MESSAGE " + (i + 1));

                        try {
                            clientHandler.bufferedWriter.write(message.getText() + " for " + clientHandler.clientId);
                            clientHandler.bufferedWriter.newLine();
                            clientHandler.bufferedWriter.flush();
                            messageStatistics.add(new MessageStatistic(clientHandler.clientId, message,
                                    MessageStatus.DELIVERED, LocalDateTime.now()));
                        } catch (IOException e) {
                            logger.log(Level.SEVERE, "Error initializing ClientHandler", e);
                            messageStatistics.add(new MessageStatistic(clientHandler.clientId, message,
                                    MessageStatus.UNDELIVERED, null));
                        }
                    }
                });
            }
        }

        Duration duration = Duration.between(messageStatistics.getFirst().deliveredTime(),
                messageStatistics.getLast().deliveredTime());

        logger.log(Level.INFO, "Spam messages sent in {0} milliseconds", duration);
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
    }

    public void close() {
        removeClientHandler();
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error closing ClientHandler", e);
        }
    }
}
