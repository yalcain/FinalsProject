package com.example.finalsactivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {
    Button btnPay;
    String name, property, contact, location, checkin, checkout;
    int total, down, balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        btnPay = findViewById(R.id.btnPay);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        property = intent.getStringExtra("property");
        contact = intent.getStringExtra("contact");
        location = intent.getStringExtra("location");
        checkin = intent.getStringExtra("checkin");
        checkout = intent.getStringExtra("checkout");
        total = intent.getIntExtra("total", 0);
        down = intent.getIntExtra("down", 0);
        balance = intent.getIntExtra("balance", 0);

        btnPay.setOnClickListener(v -> {
            try {
                SQLiteDatabase db = openOrCreateDatabase("DormDB", MODE_PRIVATE, null);
                db.execSQL("CREATE TABLE IF NOT EXISTS payments (id INTEGER PRIMARY KEY AUTOINCREMENT, userName TEXT, property TEXT, amount INTEGER, method TEXT, status TEXT, date TEXT)");
                db.execSQL("CREATE TABLE IF NOT EXISTS notifications (id INTEGER PRIMARY KEY AUTOINCREMENT, tenant_id INTEGER, title TEXT, message TEXT, type TEXT, date TEXT)");

                String currentDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());

                ContentValues cv = new ContentValues();
                cv.put("userName", name != null ? name : "Guest");
                cv.put("property", property != null ? property : "N/A");
                cv.put("amount", down);
                cv.put("method", "Gcash/Card");
                cv.put("status", "Paid");
                cv.put("date", currentDate);
                db.insert("payments", null, cv);

                ContentValues nav = new ContentValues();
                nav.put("tenant_id", 1);
                nav.put("title", "Booking Confirmed");
                nav.put("message", "Success! You booked " + property);
                nav.put("type", "Payment");
                nav.put("date", currentDate);
                db.insert("notifications", null, nav);

                db.close();

                Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(PaymentActivity.this, ReceiptActivity.class);
                i.putExtra("name", name);
                i.putExtra("property", property);
                i.putExtra("down", down);
                i.putExtra("status", "Paid");
                i.putExtra("payment", "E-Wallet");
                startActivity(i);
                finish();

            } catch (Exception e) {
                Toast.makeText(this, "DB Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
