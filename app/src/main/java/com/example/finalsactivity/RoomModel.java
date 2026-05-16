package com.example.finalsactivity;

public class RoomModel {

    public int id;
    public String property;
    public String roomName;
    public String type;
    public int capacity;
    public int occupied;

    public RoomModel(int id, String property, String roomName,
                     String type, int capacity, int occupied) {
        this.id = id;
        this.property = property;
        this.roomName = roomName;
        this.type = type;
        this.capacity = capacity;
        this.occupied = occupied;
    }
}