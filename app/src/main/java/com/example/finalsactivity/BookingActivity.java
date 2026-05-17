package com.example.finalsactivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class BookingActivity extends AppCompatActivity {

    TextView tvRoomName, tvPrice, tvDetails;
    Button btnProceedPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        tvRoomName = findViewById(R.id.tvRoomName);
        tvPrice = findViewById(R.id.tvPrice);
        tvDetails = findViewById(R.id.tvDetails);
        btnProceedPayment = findViewById(R.id.btnProceedPayment);

        // --- EXAMPLE DATA / GET FROM INTENT ---
        String roomName = getIntent().getStringExtra("room_name");
        String price = getIntent().getStringExtra("price");
        String details = getIntent().getStringExtra("details");

        // ✅ FIX: Use proper string resources instead of direct text
        if (roomName != null) tvRoomName.setText(roomName);
        if (price != null) tvPrice.setText(getString(R.string.price_format, price));
        if (details != null) tvDetails.setText(details);

        // ✅ FIX: Was pointing to MISSING "PaymentActivity" — changed to YOUR file: "UserPayment.class"
        btnProceedPayment.setOnClickListener(v -> {
            Intent intent = new Intent(BookingActivity.this, UserPayment.class);
            // Pass data to payment screen
            intent.putExtra("amount", price);
            startActivity(intent);
        });
    }
}