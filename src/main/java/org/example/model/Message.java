package org.example.model;

import java.util.concurrent.atomic.AtomicInteger;

public class Message {
    private final int id;
    private final String text;

    private static AtomicInteger counter = new AtomicInteger();

    public Message(String text) {
        this.id = counter.incrementAndGet();
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
