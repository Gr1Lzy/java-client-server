package org.example.model;

import java.time.LocalDateTime;

public class MessageStatistic {
    private String userName;
    private Message message;
    private MessageStatus status;
    private LocalDateTime deliveredTime;

    public MessageStatistic(String userName, Message message, MessageStatus status) {
        this.message = message;
        this.userName = userName;

        setStatus(status);
    }

    public void setStatus(MessageStatus status) {
        this.status = status;

        if (status == MessageStatus.DELIVERED) {
            this.deliveredTime = LocalDateTime.now();
        }
    }
}
