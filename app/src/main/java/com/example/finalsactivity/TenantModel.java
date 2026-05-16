package com.example.finalsactivity;

public class TenantModel {

    public int id;
    public String username;
    public String room;
    public String rent;
    public String dueDate;
    public String contact;

    public TenantModel(int id, String username, String room,
                       String rent, String dueDate, String contact) {
        this.id = id;
        this.username = username;
        this.room = room;
        this.rent = rent;
        this.dueDate = dueDate;
        this.contact = contact;
    }
}