package com.example.finalsactivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserPayment extends AppCompatActivity {

    Spinner spnMethod;
    EditText etAmount, etRefNumber;
    ImageView ivProof;
    String selectedMethod = "GCash";
    Uri imageUri;
    Database dbHelper;

    // ✅ CHANGE THIS TO CURRENT LOGGED-IN USER
    String currentUsername = "user123"; // ⚠️ Replace with actual logged-in user

    private static final int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        dbHelper = new Database(this);

        spnMethod = findViewById(R.id.spnMethod);
        etAmount = findViewById(R.id.etAmount);
        etRefNumber = findViewById(R.id.etRefNumber);
        ivProof = findViewById(R.id.ivProof);

        // ✅ PAYMENT METHODS LIST
        String[] methods = {
                "GCash",
                "Maya / PayMaya",
                "Bank Transfer (BDO/BPI/Metrobank)",
                "Cash Payment",
                "Remittance (Cebuana/Palawan)"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, methods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMethod.setAdapter(adapter);

        spnMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMethod = methods[position];
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // UPLOAD PROOF BUTTON
        findViewById(R.id.btnUpload).setOnClickListener(v -> openGallery());

        // SUBMIT PAYMENT BUTTON
        findViewById(R.id.btnSubmit).setOnClickListener(v -> submitPayment());

        // ✅ GET AMOUNT FROM BOOKING SCREEN (if coming from BookingActivity)
        if (getIntent().hasExtra("amount")) {
            String amount = getIntent().getStringExtra("amount");
            etAmount.setText(amount);
            etAmount.setEnabled(false); // lock amount if from booking
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            ivProof.setImageURI(imageUri); // show preview
            ivProof.setVisibility(View.VISIBLE);
        }
    }

    private void submitPayment() {
        String amount = etAmount.getText().toString().trim();
        String ref = etRefNumber.getText().toString().trim();

        // VALIDATION
        if (amount.isEmpty()) {
            etAmount.setError("Enter amount");
            return;
        }
        if (!selectedMethod.equals("Cash Payment") && ref.isEmpty()) {
            etRefNumber.setError("Enter Reference / Transaction ID");
            return;
        }
        if (!selectedMethod.equals("Cash Payment") && imageUri == null) {
            Toast.makeText(this, "Please upload proof of payment (screenshot)", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ SAVE TO DATABASE
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Database.COL_PAYMENT_USERNAME, currentUsername);
        values.put(Database.COL_PAYMENT_AMOUNT, amount);
        values.put(Database.COL_PAYMENT_METHOD, selectedMethod);
        values.put(Database.COL_PAYMENT_REFERENCE, ref);
        values.put(Database.COL_PAYMENT_IMAGE, (imageUri != null) ? imageUri.toString() : ""); // save image path
        values.put(Database.COL_PAYMENT_STATUS, "Pending"); // default
        values.put(Database.COL_PAYMENT_DATE, new SimpleDateFormat("MMM dd, yyyy · hh:mm a", Locale.getDefault()).format(new Date()));

        long result = db.insert(Database.TABLE_PAYMENTS, null, values);
        db.close();

        if (result != -1) {
            Toast.makeText(this, "Payment Submitted ✅ Waiting for approval", Toast.LENGTH_LONG).show();

            // Send notification to admin
            dbHelper.addNotification(1, "New Payment", currentUsername + " paid ₱" + amount, "payment");

            finish(); // go back
        } else {
            Toast.makeText(this, "Error submitting payment", Toast.LENGTH_SHORT).show();
        }
    }
}