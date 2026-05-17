package com.example.finalsactivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class DormDetailsActivity extends AppCompatActivity {

    ImageView imgRoom;
    TextView txtRoomName, txtPrice, txtType, txtDetails, txtLocation, txtAvailability;
    Button btnBookNow;

    RadioGroup radioStayType;
    RadioButton rbTransient, rbBedspace;
    CheckBox cbAdvancePay;

    String property = "";
    String location = "";
    String detailsFromIntent = ""; // ✅ Added
    String ratingFromIntent = "";  // ✅ Added
    int imageRes;

    Database dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dorm_details);

        dbHelper = new Database(this);
        db = dbHelper.getReadableDatabase();

        // INIT VIEWS
        imgRoom = findViewById(R.id.imgRoom);
        txtRoomName = findViewById(R.id.txtRoomName);
        txtPrice = findViewById(R.id.txtPrice);
        txtType = findViewById(R.id.txtType);
        txtDetails = findViewById(R.id.txtDetails);
        txtLocation = findViewById(R.id.txtLocation);
        txtAvailability = findViewById(R.id.txtAvailability);
        btnBookNow = findViewById(R.id.btnBookNow);

        radioStayType = findViewById(R.id.radioStayType);
        rbTransient = findViewById(R.id.rbTransient);
        rbBedspace = findViewById(R.id.rbBedspace);
        cbAdvancePay = findViewById(R.id.cbAdvancePay);

        // ✅ GET ALL DATA FROM INTENT
        Intent intent = getIntent();
        property = intent.getStringExtra("property");
        location = intent.getStringExtra("location");
        detailsFromIntent = intent.getStringExtra("details");
        ratingFromIntent = intent.getStringExtra("rating");
        imageRes = intent.getIntExtra("image", R.drawable.bedspace);

        if (property == null) property = "Unknown Dorm";
        if (location == null) location = "Unknown Location";
        if (detailsFromIntent == null) detailsFromIntent = "";

        // ✅ SET UI WITH CORRECT DATA
        txtRoomName.setText(property);
        txtLocation.setText(location);
        imgRoom.setImageResource(imageRes);
        txtDetails.setText(detailsFromIntent); // Shows exact text from home card

        rbTransient.setChecked(true);
        cbAdvancePay.setVisibility(CheckBox.GONE);

        updateDetails("Transient");
        checkAvailability("Transient");

        // TYPE SWITCH
        radioStayType.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == R.id.rbTransient) {
                updateDetails("Transient");
                cbAdvancePay.setVisibility(CheckBox.GONE);
                checkAvailability("Transient");

            } else if (checkedId == R.id.rbBedspace) {
                updateDetails("Bedspace");
                cbAdvancePay.setVisibility(CheckBox.VISIBLE);
                checkAvailability("Bedspace");

                Toast.makeText(this,
                        "Bedspace = long-term (NO check-in required)",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // BOOK BUTTON
        btnBookNow.setOnClickListener(v -> {

            String type = rbTransient.isChecked() ? "Transient" : "Bedspace";

            if (!isAvailable(property, type)) {
                Toast.makeText(this, "No available rooms!", Toast.LENGTH_SHORT).show();
                return;
            }

            if ("Bedspace".equals(type) && !cbAdvancePay.isChecked()) {
                Toast.makeText(this, "Advance payment required!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent i = new Intent(this, BookingActivity.class);
            i.putExtra("property", property);
            i.putExtra("location", location);
            i.putExtra("room_type", type);
            i.putExtra("price", getPrice(type));

            // 🔥 IMPORTANT FLAG
            i.putExtra("skip_checkin", "Bedspace".equals(type));

            startActivity(i);
        });
    }

    // CHECK AVAILABILITY
    private boolean isAvailable(String property, String type) {
        Cursor c = db.rawQuery(
                "SELECT capacity, occupied FROM rooms WHERE property=? AND type=?",
                new String[]{property, type}
        );

        if (c.moveToFirst()) {
            int capacity = c.getInt(0);
            int occupied = c.getInt(1);
            c.close();
            return occupied < capacity;
        }

        c.close();
        return false;
    }

    private void checkAvailability(String type) {
        txtAvailability.setText(
                isAvailable(property, type) ? "AVAILABLE" : "FULL"
        );
    }

    // ✅ UPDATED: Uses actual prices from your dorms
    private void updateDetails(String type) {
        if (property.equals("SD Dorm 2")) {
            if ("Transient".equals(type)) {
                txtType.setText("Transient Stay");
                txtPrice.setText("₱500 / night");
                txtDetails.setText("Daily stay • Electric fan only • Check-in required");
            } else {
                txtType.setText("Bedspace");
                txtPrice.setText("₱2,500 / month");
                txtDetails.setText("Long-term • Shared room • No check-in needed");
            }
        } else if (property.equals("Loft 22")) {
            if ("Transient".equals(type)) {
                txtType.setText("Transient Stay");
                txtPrice.setText("₱500 / night");
                txtDetails.setText("Daily stay • Aircon • Check-in required");
            } else {
                txtType.setText("Bedspace");
                txtPrice.setText("₱3,000 / month");
                txtDetails.setText("Long-term • Private • No check-in needed");
            }
        } else if (property.equals("The Dormitory")) {
            if ("Transient".equals(type)) {
                txtType.setText("Transient Stay");
                txtPrice.setText("₱450 / night");
                txtDetails.setText("Daily stay • Electric fan • Check-in required");
            } else {
                txtType.setText("Bedspace");
                txtPrice.setText("₱2,200 / month");
                txtDetails.setText("Long-term • Shared room • No check-in needed");
            }
        } else if (property.equals("Milflores Boarding House")) {
            if ("Transient".equals(type)) {
                txtType.setText("Transient Stay");
                txtPrice.setText("₱400 / night");
                txtDetails.setText("Daily stay • Electric fan • Check-in required");
            } else {
                txtType.setText("Bedspace");
                txtPrice.setText("₱2,300 / month");
                txtDetails.setText("Long-term • Shared room • No check-in needed");
            }
        } else {
            // For newly added places
            if ("Transient".equals(type)) {
                txtType.setText("Transient Stay");
                txtPrice.setText("₱500 / night");
                txtDetails.setText("Daily stay • Check-in required");
            } else {
                txtType.setText("Bedspace");
                txtPrice.setText("₱2,500 / month");
                txtDetails.setText("Long-term • No check-in needed");
            }
        }
    }

    private String getPrice(String type) {
        return txtPrice.getText().toString().replace("₱", "").replace(" / month", "").replace(" / night", "").trim();
    }
}