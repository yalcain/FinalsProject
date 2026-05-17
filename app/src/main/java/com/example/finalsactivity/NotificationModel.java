package com.example.finalsactivity;

public class NotificationModel {
    private int id;
    private String title;
    private String message;
    private String date;
    private String type;

    // Constructor
    public NotificationModel(int id, String title, String message, String date, String type) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.date = date;
        this.type = type;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getDate() { return date; }
    public String getType() { return type; }
}