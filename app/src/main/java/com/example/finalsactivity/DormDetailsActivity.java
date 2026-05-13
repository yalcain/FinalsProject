package com.example.finalsactivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DormDetailsActivity extends AppCompatActivity {

    ImageView imgRoom;
    TextView txtRoomName, txtPrice, txtType, txtDetails, txtLocation;
    Button btnBookNow;

    RadioGroup radioStayType;
    RadioButton rbTransient, rbLongStay, rbBedspace;
    CheckBox cbAdvancePay;

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

        radioStayType = findViewById(R.id.radioStayType);
        rbTransient = findViewById(R.id.rbTransient);
        rbBedspace = findViewById(R.id.rbBedspace);
        cbAdvancePay = findViewById(R.id.cbAdvancePay);

        Intent intent = getIntent();
        property = intent.getStringExtra("property");
        location = intent.getStringExtra("location");
        imageRes = intent.getIntExtra("image", R.drawable.loft1);

        txtRoomName.setText(property);
        txtLocation.setText(location);
        imgRoom.setImageResource(imageRes);

        updateDetails("Transient");

        radioStayType.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == R.id.rbTransient) {
                updateDetails("Transient");
                cbAdvancePay.setVisibility(CheckBox.GONE);
            }

            else if (checkedId == R.id.rbBedspace) {
                updateDetails("Bedspace");
                cbAdvancePay.setVisibility(CheckBox.VISIBLE);
            }
        });

        btnBookNow.setOnClickListener(v -> {

            String stayType = "";

            if (rbTransient.isChecked()) {
                stayType = "Transient";
            } else if (rbLongStay.isChecked()) {
                stayType = "Long Stay";
            } else if (rbBedspace.isChecked()) {
                stayType = "Bedspace";
            }

            boolean advance = cbAdvancePay.isChecked();

            // VALIDATION
            if ((stayType.equals("Long Stay") || stayType.equals("Bedspace")) && !advance) {
                Toast.makeText(this,
                        "Advance payment required for long stay/bedspace",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Intent bookIntent = new Intent(DormDetailsActivity.this, BookingActivity.class);

            bookIntent.putExtra("property", property);
            bookIntent.putExtra("location", location);
            bookIntent.putExtra("room_type", stayType);
            bookIntent.putExtra("price", getPrice(stayType));
            bookIntent.putExtra("advance_payment", advance);

            startActivity(bookIntent);
        });
    }

    private void updateDetails(String type) {

        if (type.equals("Transient")) {
            txtType.setText("Transient Stay");
            txtPrice.setText("₱550 / day");
            txtDetails.setText("Daily stay for short-term visitors.");
        }

        else if (type.equals("Long Stay")) {
            txtType.setText("Long Stay");
            txtPrice.setText("₱6,000 / month");
            txtDetails.setText("Monthly rental with advance payment required.");
        }

        else if (type.equals("Bedspace")) {
            txtType.setText("Bedspace");
            txtPrice.setText("₱3,500 / month");
            txtDetails.setText("Shared room accommodation for long-term stay.");
        }
    }

    private String getPrice(String type) {
        if (type.equals("Transient")) return "550";
        if (type.equals("Long Stay")) return "6000";
        return "3500";
    }
}