package org.example.model.message;

public record MessageStatistic(String clientName, Message message, MessageStatus status, java.time.LocalDateTime deliveredTime) {

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
