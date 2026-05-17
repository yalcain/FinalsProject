package com.example.finalsactivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class TenantsProfileActivity extends AppCompatActivity {

    // Profile Fields
    EditText etFullname, etUsername, etAge, etGender, etContact, etEmail;

    // Change Password Fields
    EditText etCurrentPass, etNewPass, etConfirmPass;

    Button btnSave, btnCancel;

    Database dbHelper;
    SQLiteDatabase db;
    String username; // current logged-in user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenants_profile);

        // Initialize Profile Views
        etFullname = findViewById(R.id.etFullname);
        etUsername = findViewById(R.id.etUsername);
        etAge = findViewById(R.id.etAge);
        etGender = findViewById(R.id.etGender);
        etContact = findViewById(R.id.etContact);
        etEmail = findViewById(R.id.etEmail);

        // Initialize Password Views
        etCurrentPass = findViewById(R.id.etCurrentPass);
        etNewPass = findViewById(R.id.etNewPass);
        etConfirmPass = findViewById(R.id.etConfirmPass);

        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        dbHelper = new Database(this);
        db = dbHelper.getWritableDatabase();

        // Get username from intent (passed from MainActivity2)
        username = getIntent().getStringExtra("username");

        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Error: No user data found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load user data from database
        loadUserProfile();

        // Save changes (Profile + Password)
        btnSave.setOnClickListener(v -> {
            if (validateAllInputs()) {
                updateUserProfile();
                // Only update password if user filled any password field
                if (!etCurrentPass.getText().toString().isEmpty() ||
                        !etNewPass.getText().toString().isEmpty() ||
                        !etConfirmPass.getText().toString().isEmpty()) {
                    changePassword();
                }
            }
        });

        // Cancel and go back
        btnCancel.setOnClickListener(v -> finish());
    }

    // Load current user data
    private void loadUserProfile() {
        Cursor cursor = db.rawQuery(
                "SELECT fullname, username, age, gender, contact, email FROM users WHERE username=?",
                new String[]{username}
        );

        if (cursor.moveToFirst()) {
            etFullname.setText(cursor.getString(0));
            etUsername.setText(cursor.getString(1));
            etAge.setText(cursor.getString(2));
            etGender.setText(cursor.getString(3));
            etContact.setText(cursor.getString(4));
            etEmail.setText(cursor.getString(5));

            // Username cannot be changed
            etUsername.setEnabled(false);
        } else {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            finish();
        }
        cursor.close();
    }

    // ✅ Full Validation (Profile + Password)
    private boolean validateAllInputs() {
        String fullname = etFullname.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String gender = etGender.getText().toString().trim();
        String contact = etContact.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        // --- Validate Profile Fields ---
        if (fullname.isEmpty()) {
            etFullname.setError("Full name required");
            etFullname.requestFocus();
            return false;
        }
        if (ageStr.isEmpty()) {
            etAge.setError("Age required");
            etAge.requestFocus();
            return false;
        }
        try {
            int age = Integer.parseInt(ageStr);
            if (age < 12 || age > 100) {
                etAge.setError("Enter valid age (12-100)");
                etAge.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etAge.setError("Age must be a number");
            etAge.requestFocus();
            return false;
        }
        if (gender.isEmpty()) {
            etGender.setError("Gender required");
            etGender.requestFocus();
            return false;
        }
        if (contact.isEmpty() || contact.length() != 11 || !contact.matches("\\d+")) {
            etContact.setError("Enter valid 11-digit contact");
            etContact.requestFocus();
            return false;
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter valid email address");
            etEmail.requestFocus();
            return false;
        }

        // --- Validate Password Fields (only if user wants to change) ---
        String currentPass = etCurrentPass.getText().toString().trim();
        String newPass = etNewPass.getText().toString().trim();
        String confirmPass = etConfirmPass.getText().toString().trim();

        // If ANY password field is filled, ALL must be filled
        if (!currentPass.isEmpty() || !newPass.isEmpty() || !confirmPass.isEmpty()) {
            if (currentPass.isEmpty()) {
                etCurrentPass.setError("Enter current password");
                etCurrentPass.requestFocus();
                return false;
            }
            if (newPass.isEmpty()) {
                etNewPass.setError("Enter new password");
                etNewPass.requestFocus();
                return false;
            }
            if (newPass.length() < 6) {
                etNewPass.setError("Password must be at least 6 characters");
                etNewPass.requestFocus();
                return false;
            }
            if (confirmPass.isEmpty()) {
                etConfirmPass.setError("Confirm new password");
                etConfirmPass.requestFocus();
                return false;
            }
            if (!newPass.equals(confirmPass)) {
                etConfirmPass.setError("New passwords do not match");
                etConfirmPass.requestFocus();
                return false;
            }
            // Check if current password is correct
            if (!isCurrentPasswordCorrect(currentPass)) {
                etCurrentPass.setError("Current password is wrong");
                etCurrentPass.requestFocus();
                return false;
            }
        }

        return true;
    }

    // Check if entered current password matches DB
    private boolean isCurrentPasswordCorrect(String currentPass) {
        Cursor c = db.rawQuery(
                "SELECT password FROM users WHERE username=? AND password=?",
                new String[]{username, currentPass}
        );
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    // Update profile info
    private void updateUserProfile() {
        ContentValues values = new ContentValues();
        values.put(Database.COL_USER_FULLNAME, etFullname.getText().toString().trim());
        values.put(Database.COL_USER_AGE, etAge.getText().toString().trim());
        values.put(Database.COL_USER_GENDER, etGender.getText().toString().trim());
        values.put(Database.COL_USER_CONTACT, etContact.getText().toString().trim());
        values.put(Database.COL_USER_EMAIL, etEmail.getText().toString().trim());

        db.update(
                Database.TABLE_USERS,
                values,
                Database.COL_USER_USERNAME + "=?",
                new String[]{username}
        );
    }

    // Update password
    private void changePassword() {
        String newPass = etNewPass.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(Database.COL_USER_PASSWORD, newPass);

        int result = db.update(
                Database.TABLE_USERS,
                values,
                Database.COL_USER_USERNAME + "=?",
                new String[]{username}
        );

        if (result > 0) {
            Toast.makeText(this, "Profile & Password updated successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Update failed. Try again.", Toast.LENGTH_SHORT).show();
        }
    }
}