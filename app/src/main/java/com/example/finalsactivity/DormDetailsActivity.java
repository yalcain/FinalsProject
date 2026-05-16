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

        // INTENT SAFETY
        Intent intent = getIntent();
        property = intent.getStringExtra("property");
        location = intent.getStringExtra("location");
        imageRes = intent.getIntExtra("image", R.drawable.loft1);

        if (property == null) property = "Unknown Dorm";
        if (location == null) location = "Unknown Location";

        // SET UI
        txtRoomName.setText(property);
        txtLocation.setText(location);
        imgRoom.setImageResource(imageRes);

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

    private void updateDetails(String type) {

        if ("Transient".equals(type)) {
            txtType.setText("Transient Stay");
            txtPrice.setText("₱550 / day");
            txtDetails.setText("Daily stay rooms (check-in required)");
        } else {
            txtType.setText("Bedspace");
            txtPrice.setText("₱3500 / month");
            txtDetails.setText("Long-term occupancy (NO check-in)");
        }
    }

    private String getPrice(String type) {
        return "Transient".equals(type) ? "550" : "3500";
    }
}