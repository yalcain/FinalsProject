package com.example.finalsactivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ReceiptActivity extends AppCompatActivity {
    TextView txtReceipt;
    Button btnHome; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        txtReceipt = findViewById(R.id.txtReceipt);
        btnHome = findViewById(R.id.btnHome); 

        Intent i = getIntent();
        String display = "DORM RECEIPT\n" +
                "--------------------\n" +
                "Name: " + i.getStringExtra("name") + "\n" +
                "Property: " + i.getStringExtra("property") + "\n" +
                "Amount Paid: ₱" + i.getIntExtra("down", 0) + "\n" +
                "Status: " + i.getStringExtra("status") + "\n" +
                "Method: " + i.getStringExtra("payment") + "\n\n" +
                "Thank you for booking!";

        txtReceipt.setText(display);

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(ReceiptActivity.this, MainActivity2.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);
        finish();
    }
}
