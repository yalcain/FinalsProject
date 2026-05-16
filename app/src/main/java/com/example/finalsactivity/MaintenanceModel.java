package com.example.finalsactivity;

public class MaintenanceModel {

    public int id;
    public String issue;
    public String category;
    public String priority;
    public String staff;
    public String status;
    public String date;

    public MaintenanceModel(int id, String issue, String category,
                            String priority, String staff,
                            String status, String date) {
        this.id = id;
        this.issue = issue;
        this.category = category;
        this.priority = priority;
        this.staff = staff;
        this.status = status;
        this.date = date;
    }
}