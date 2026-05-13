package com.example.finalsactivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

public class ReceiptActivity extends AppCompatActivity {

    TextView txtReceipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        txtReceipt = findViewById(R.id.txtReceipt);

        String receipt =
                "DORM RECEIPT\n\n" +
                        "Name: " + getIntent().getStringExtra("name") + "\n" +
                        "Contact: " + getIntent().getStringExtra("contact") + "\n" +
                        "Property: " + getIntent().getStringExtra("property") + "\n" +
                        "Location: " + getIntent().getStringExtra("location") + "\n" +
                        "Check-In: " + getIntent().getStringExtra("checkin") + "\n" +
                        "Check-Out: " + getIntent().getStringExtra("checkout") + "\n\n" +
                        "Total: ₱" + getIntent().getIntExtra("total", 0) + "\n" +
                        "Downpayment: ₱" + getIntent().getIntExtra("down", 0) + "\n" +
                        "Balance: ₱" + getIntent().getIntExtra("balance", 0) + "\n\n" +
                        "Payment: " + getIntent().getStringExtra("payment") + "\n" +
                        "Status: " + getIntent().getStringExtra("status") + "\n\n" +
                        "STATUS: CONFIRMED";

        txtReceipt.setText(receipt);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, homeActivity.class);

    }
}