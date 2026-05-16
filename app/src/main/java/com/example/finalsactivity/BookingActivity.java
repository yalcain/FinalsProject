package com.example.finalsactivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class BookingActivity extends AppCompatActivity {

    EditText etName, etContact, etCheckIn, etCheckOut;
    TextView txtTotal, txtLocation;
    CheckBox cbTerms;
    Button btnBook;

    int rate = 550;
    int total = 0;

    Calendar checkInCal = Calendar.getInstance();
    Calendar checkOutCal = Calendar.getInstance();

    Database dbHelper;
    SQLiteDatabase db;

    String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        etName = findViewById(R.id.etName);
        etContact = findViewById(R.id.etContact);
        etCheckIn = findViewById(R.id.etCheckIn);
        etCheckOut = findViewById(R.id.etCheckOut);

        txtTotal = findViewById(R.id.txtTotal);
        txtLocation = findViewById(R.id.txtLocation);
        cbTerms = findViewById(R.id.cbTerms);
        btnBook = findViewById(R.id.btnBook);

        dbHelper = new Database(this);
        db = dbHelper.getReadableDatabase();

        Intent intent = getIntent();

        String property = intent.getStringExtra("property");
        String location = intent.getStringExtra("location");

        username = intent.getStringExtra("username");

        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Username missing!", Toast.LENGTH_LONG).show();
        }

        txtLocation.setText(location != null ? location : "No location");

        loadUserData();

        etCheckIn.setOnClickListener(v -> {
            Calendar today = Calendar.getInstance();

            DatePickerDialog dialog = new DatePickerDialog(this,
                    (view, y, m, d) -> {
                        checkInCal.set(y, m, d);
                        etCheckIn.setText(d + "/" + (m + 1) + "/" + y);
                    },
                    today.get(Calendar.YEAR),
                    today.get(Calendar.MONTH),
                    today.get(Calendar.DAY_OF_MONTH));

            dialog.getDatePicker().setMinDate(System.currentTimeMillis());
            dialog.show();
        });

        etCheckOut.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(this,
                    (view, y, m, d) -> {
                        checkOutCal.set(y, m, d);
                        etCheckOut.setText(d + "/" + (m + 1) + "/" + y);
                        calculateTotal();
                    },
                    checkOutCal.get(Calendar.YEAR),
                    checkOutCal.get(Calendar.MONTH),
                    checkOutCal.get(Calendar.DAY_OF_MONTH));

            dialog.getDatePicker().setMinDate(checkInCal.getTimeInMillis());
            dialog.show();
        });

        btnBook.setOnClickListener(v -> {

            if (!cbTerms.isChecked()) {
                Toast.makeText(this, "Accept terms first", Toast.LENGTH_SHORT).show();
                return;
            }

            int down = (int) (total * 0.30);
            int balance = total - down;

            Intent i = new Intent(this, PaymentActivity.class);

            i.putExtra("name", etName.getText().toString());
            i.putExtra("contact", etContact.getText().toString());
            i.putExtra("property", property);
            i.putExtra("location", location);
            i.putExtra("checkin", etCheckIn.getText().toString());
            i.putExtra("checkout", etCheckOut.getText().toString());
            i.putExtra("total", total);
            i.putExtra("down", down);
            i.putExtra("balance", balance);

            startActivity(i);
            finish();
        });
    }

    private void loadUserData() {

        if (username == null || username.isEmpty()) return;

        Cursor c = db.rawQuery(
                "SELECT fullname, contact FROM users WHERE username=?",
                new String[]{username}
        );

        if (c.moveToFirst()) {
            etName.setText(c.getString(0));
            etContact.setText(c.getString(1));

            etName.setEnabled(false);
            etContact.setEnabled(false);
        } else {
            Toast.makeText(this, "User not found in DB", Toast.LENGTH_SHORT).show();
        }

        c.close();
    }

    private void calculateTotal() {

        long diff = checkOutCal.getTimeInMillis() - checkInCal.getTimeInMillis();
        int days = (int) (diff / (1000 * 60 * 60 * 24));

        if (days <= 0) days = 1;

        total = days * rate;

        txtTotal.setText("Total: ₱" + total);
    }
}