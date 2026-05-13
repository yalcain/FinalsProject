package com.example.finalsactivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.HashMap;

public class Reservation extends AppCompatActivity {

    EditText etName, etContact, etDate, etDown;
    Spinner spProperty, spType, spRoom;
    TextView txtFee;
    CheckBox cbAgree;
    Button btnReserve;

    SQLiteDatabase db;

    int currentFee = 0;

    HashMap<String, String[]> propertyMap = new HashMap<>();
    HashMap<String, String[]> typeMap = new HashMap<>();
    HashMap<String, Integer> priceMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        etName = findViewById(R.id.etName);
        etContact = findViewById(R.id.etContact);
        etDate = findViewById(R.id.etDate);
        etDown = findViewById(R.id.etDownpayment);

        spProperty = findViewById(R.id.spProperty);
        spType = findViewById(R.id.spType);
        spRoom = findViewById(R.id.spRoom);

        txtFee = findViewById(R.id.txtFee);
        cbAgree = findViewById(R.id.cbAgree);
        btnReserve = findViewById(R.id.btnReserve);

        db = openOrCreateDatabase("DormDB", MODE_PRIVATE, null);

        db.execSQL("CREATE TABLE IF NOT EXISTS reservation(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT, contact TEXT, property TEXT, type TEXT, room TEXT," +
                "date TEXT, fee INTEGER, downpayment INTEGER, balance INTEGER)");

        seedData();

        setupSpinners();

        etDate.setOnClickListener(v -> {

            Calendar c = Calendar.getInstance();

            DatePickerDialog dialog = new DatePickerDialog(this,
                    (view, year, month, day) -> {
                        etDate.setText(day + "/" + (month + 1) + "/" + year);
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH));

            dialog.show();
        });

        btnReserve.setOnClickListener(v -> reserveRoom());
    }

    private void reserveRoom() {

        String name = etName.getText().toString().trim();
        String contact = etContact.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String property = spProperty.getSelectedItem().toString();
        String type = spType.getSelectedItem().toString();
        String room = spRoom.getSelectedItem().toString();

        if (name.isEmpty() || contact.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contact.length() != 11) {
            etContact.setError("Invalid contact number");
            return;
        }

        if (!cbAgree.isChecked()) {
            Toast.makeText(this, "Accept Terms first", Toast.LENGTH_SHORT).show();
            return;
        }

        int down;

        try {
            down = Integer.parseInt(etDown.getText().toString().trim());
        } catch (Exception e) {
            Toast.makeText(this, "Invalid downpayment", Toast.LENGTH_SHORT).show();
            return;
        }

        if (down < 500) {
            Toast.makeText(this, "Minimum ₱500 required", Toast.LENGTH_SHORT).show();
            return;
        }

        int balance = currentFee - down;

        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("contact", contact);
        cv.put("property", property);
        cv.put("type", type);
        cv.put("room", room);
        cv.put("date", date);
        cv.put("fee", currentFee);
        cv.put("downpayment", down);
        cv.put("balance", balance);

        db.insert("reservation", null, cv);

        Toast.makeText(this,
                "Reserved!\nBalance: ₱" + balance,
                Toast.LENGTH_LONG).show();

        finish();
    }

    private void setupSpinners() {

        String[] properties = {
                "SD Dorm 2",
                "The Dormitory - Palar",
                "Milflores",
                "Loft 22"
        };

        propertyMap.put("SD Dorm 2", new String[]{"Bedspace Floor 2", "Transient Floor 3"});
        propertyMap.put("The Dormitory - Palar", new String[]{"Bedspace Floors 1-3", "Transient Floor 4"});
        propertyMap.put("Milflores", new String[]{"Bedspace Floors"});
        propertyMap.put("Loft 22", new String[]{"Transient Floors 3-5"});

        typeMap.put("Bedspace Floor 2", new String[]{"Room 1", "Room 2", "Room 3", "Room 4"});
        typeMap.put("Transient Floor 3", new String[]{"Room 1", "Room 2", "Room 3", "Room 4"});
        typeMap.put("Bedspace Floors 1-3", new String[]{"Room 1", "Room 2", "Room 3"});
        typeMap.put("Transient Floor 4", new String[]{"Room 1", "Room 2"});
        typeMap.put("Bedspace Floors", new String[]{"Room 1", "Room 2", "Room 3", "Room 4", "Room 5"});
        typeMap.put("Transient Floors 3-5", new String[]{"Room 1", "Room 2", "Room 3"});

        priceMap.put("Bedspace Floor 2", 3300);
        priceMap.put("Bedspace Floors 1-3", 3000);
        priceMap.put("Bedspace Floors", 2500);
        priceMap.put("Transient Floor 3", 550);
        priceMap.put("Transient Floor 4", 600);
        priceMap.put("Transient Floors 3-5", 800);

        ArrayAdapter<String> propertyAdapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item,
                        properties);

        spProperty.setAdapter(propertyAdapter);

        spProperty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String property = properties[position];

                String[] types = propertyMap.get(property);

                ArrayAdapter<String> typeAdapter =
                        new ArrayAdapter<>(Reservation.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                types);

                spType.setAdapter(typeAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String type = spType.getSelectedItem().toString();

                String[] rooms = typeMap.get(type);

                ArrayAdapter<String> roomAdapter =
                        new ArrayAdapter<>(Reservation.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                rooms);

                spRoom.setAdapter(roomAdapter);

                if (priceMap.containsKey(type)) {
                    currentFee = priceMap.get(type);
                    txtFee.setText("Fee: ₱" + currentFee);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void seedData() {

        Cursor c = db.rawQuery("SELECT COUNT(*) FROM reservation", null);

        if (c.moveToFirst() && c.getInt(0) == 0) {

        }

        c.close();
    }
}