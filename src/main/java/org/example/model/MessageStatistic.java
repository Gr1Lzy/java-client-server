package org.example.model;

import java.time.LocalDateTime;

public record MessageStatistic(String clientName, Message message, MessageStatus status, LocalDateTime deliveredTime) {

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
