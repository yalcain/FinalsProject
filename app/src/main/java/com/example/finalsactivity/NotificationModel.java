package com.example.finalsactivity;

public class NotificationModel {

    public int id;
    public String title;
    public String message;
    public String type;
    public String date;

    public NotificationModel(int id, String title, String message,
                             String type, String date) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.type = type;
        this.date = date;
    }
}