package com.example.finalsactivity;

public class PaymentModel {

    public int id;
    public String username;
    public String amount;
    public String status;

    public PaymentModel(int id, String username, String amount, String status) {
        this.id = id;
        this.username = username;
        this.amount = amount;
        this.status = status;
    }
}