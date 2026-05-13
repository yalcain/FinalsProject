package com.example.finalsactivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DormDetailsActivity extends AppCompatActivity {

    ImageView imgRoom;
    TextView txtRoomName, txtPrice, txtType, txtDetails, txtLocation;
    Button btnBookNow;

    String property, location;
    int imageRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dorm_details);

        imgRoom = findViewById(R.id.imgRoom);
        txtRoomName = findViewById(R.id.txtRoomName);
        txtPrice = findViewById(R.id.txtPrice);
        txtType = findViewById(R.id.txtType);
        txtDetails = findViewById(R.id.txtDetails);
        txtLocation = findViewById(R.id.txtLocation);
        btnBookNow = findViewById(R.id.btnBookNow);

        Intent intent = getIntent();
        property = intent.getStringExtra("property");
        location = intent.getStringExtra("location");
        imageRes = intent.getIntExtra("image", R.drawable.loft1);

        txtRoomName.setText(property);
        txtLocation.setText(location);
        txtType.setText("Transient");
        txtPrice.setText("₱550 / day");
        txtDetails.setText("All rooms are available for transient stay.\nRate: ₱550 per day.");

        imgRoom.setImageResource(imageRes);

        btnBookNow.setOnClickListener(v -> {
            Intent bookIntent = new Intent(DormDetailsActivity.this, BookingActivity.class);

            bookIntent.putExtra("property", property);
            bookIntent.putExtra("location", location);
            bookIntent.putExtra("room_type", "Transient");
            bookIntent.putExtra("price", "550");
            bookIntent.putExtra("image", imageRes);

            startActivity(bookIntent);
        });
    }
}