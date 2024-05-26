package org.example.model;

import java.time.LocalDateTime;

public class MessageStatistic {
    private final String clientName;
    private final Message message;
    private final MessageStatus status;
    private final LocalDateTime deliveredTime;

    public MessageStatistic(String clientName, Message message, MessageStatus status, LocalDateTime deliveredTime) {
        this.clientName = clientName;
        this.message = message;
        this.status = status;
        this.deliveredTime = deliveredTime;
    }

    public String getClientName() {
        return clientName;
    }

    public Message getMessage() {
        return message;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public LocalDateTime getDeliveredTime() {
        return deliveredTime;
    }

    @Override
    public String toString() {
        return "MessageStatistic{" +
                "messageId=" + message.getId() +
                ",clientId=" + clientName +
                ", message='" + message.getText() + "'" +
                ", status=" + status +
                ", deliveredTime=" + deliveredTime +
                '}';
    }
}
