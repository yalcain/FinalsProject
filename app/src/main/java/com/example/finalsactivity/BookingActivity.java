package com.example.finalsactivity;

import android.app.DatePickerDialog;
import android.content.Intent;
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

        String property = getIntent().getStringExtra("property");
        String location = getIntent().getStringExtra("location");

        txtLocation.setText(location);

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

            if (etName.getText().toString().isEmpty()
                    || etContact.getText().toString().length() != 11) {
                Toast.makeText(this, "Fill all fields correctly", Toast.LENGTH_SHORT).show();
                return;
            }

            int days = getDays();
            int down = (int) (total * 0.30);
            int balance = total - down;

            Intent intent = new Intent(this, PaymentActivity.class);

            intent.putExtra("name", etName.getText().toString());
            intent.putExtra("contact", etContact.getText().toString());
            intent.putExtra("property", property);
            intent.putExtra("location", location);
            intent.putExtra("checkin", etCheckIn.getText().toString());
            intent.putExtra("checkout", etCheckOut.getText().toString());
            intent.putExtra("total", total);
            intent.putExtra("down", down);
            intent.putExtra("balance", balance);

            startActivity(intent);
            finish();
        });
    }

    private void calculateTotal() {

        long diff = checkOutCal.getTimeInMillis() - checkInCal.getTimeInMillis();
        int days = (int) (diff / (1000 * 60 * 60 * 24));

        if (days <= 0) days = 1;

        total = days * rate;

        txtTotal.setText("Total: ₱" + total);
    }

    private int getDays() {
        long diff = checkOutCal.getTimeInMillis() - checkInCal.getTimeInMillis();
        return Math.max((int) (diff / (1000 * 60 * 60 * 24)), 1);
    }
}