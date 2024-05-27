package org.example.model.message;

import java.util.concurrent.atomic.AtomicInteger;

public class Message {
    private final int id;
    private final String text;

    private static final AtomicInteger COUNTER = new AtomicInteger();

    public Message(String text) {
        id = COUNTER.incrementAndGet();
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public int getId() {
        return id;
    }
}
