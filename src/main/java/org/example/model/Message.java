package org.example.model;

public class Message {
    private int id;
    private String message;

    private static int counter = 0;

    public Message(String message) {
        this.id = counter++;
        this.message = message;
    }
}
