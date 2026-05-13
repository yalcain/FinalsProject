package com.example.finalsactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    Spinner spPayment;
    EditText etGcashRef, etCardRef;
    Button btnPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        spPayment = findViewById(R.id.spPayment);
        etGcashRef = findViewById(R.id.etGcashRef);
        etCardRef = findViewById(R.id.etCardRef);
        btnPay = findViewById(R.id.btnPay);

        String[] methods = {"Cash", "GCash", "Card"};
        spPayment.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                methods));

        spPayment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selected = parent.getItemAtPosition(position).toString();

                etGcashRef.setVisibility(View.GONE);
                etCardRef.setVisibility(View.GONE);

                if (selected.equals("GCash")) {
                    etGcashRef.setVisibility(View.VISIBLE);
                }

                else if (selected.equals("Card")) {
                    etCardRef.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnPay.setOnClickListener(v -> {

            String payment = spPayment.getSelectedItem().toString();

            Intent fromBooking = getIntent();

            String name = fromBooking.getStringExtra("name");
            String contact = fromBooking.getStringExtra("contact");
            String property = fromBooking.getStringExtra("property");
            String location = fromBooking.getStringExtra("location");
            String checkin = fromBooking.getStringExtra("checkin");
            String checkout = fromBooking.getStringExtra("checkout");

            int total = fromBooking.getIntExtra("total", 0);
            int down = fromBooking.getIntExtra("down", 0);
            int balance = fromBooking.getIntExtra("balance", 0);

            String gcashRef = etGcashRef.getText().toString().trim();
            String cardRef = etCardRef.getText().toString().trim();

            String status;


            if (payment.equals("Cash")) {
                status = "Pay on Arrival";
            }

            else if (payment.equals("GCash")) {

                if (gcashRef.isEmpty()) {
                    etGcashRef.setError("Enter GCash reference number");
                    return;
                }

                if (down <= 0) {
                    Toast.makeText(this, "GCash requires payment", Toast.LENGTH_SHORT).show();
                    return;
                }

                status = "Paid via GCash (Ref: " + gcashRef + ")";
            }

            else if (payment.equals("Card")) {

                if (cardRef.isEmpty()) {
                    etCardRef.setError("Enter Card reference");
                    return;
                }

                if (down < 100) {
                    Toast.makeText(this, "Minimum ₱100 for Card", Toast.LENGTH_SHORT).show();
                    return;
                }

                status = "Paid via Card (Ref: " + cardRef + ")";
            }

            else {
                Toast.makeText(this, "Select payment method", Toast.LENGTH_SHORT).show();
                return;
            }


            Intent i = new Intent(this, ReceiptActivity.class);

            i.putExtra("name", name);
            i.putExtra("contact", contact);
            i.putExtra("property", property);
            i.putExtra("location", location);
            i.putExtra("checkin", checkin);
            i.putExtra("checkout", checkout);

            i.putExtra("total", total);
            i.putExtra("down", down);
            i.putExtra("balance", balance);

            i.putExtra("payment", payment);
            i.putExtra("status", status);

            startActivity(i);
            finish();
        });
    }
}