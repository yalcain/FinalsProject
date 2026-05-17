package com.example.finalsactivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    EditText etFullname, etUsername, etAge,
            etContact, etEmail,
            etPassword, etConfirm, etCode;

    Spinner spinnerGender;
    Button btnRegister, btnVerify;
    Database dbHelper;
    SQLiteDatabase db;

    LinearLayout layoutRegister, layoutVerification;
    String generatedCode;

    // Regex for Philippine mobile number: 09XXXXXXXXX
    private static final Pattern PH_CONTACT_PATTERN = Pattern.compile("^09\\d{9}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // INIT VIEWS
        etFullname = findViewById(R.id.etFullname);
        etUsername = findViewById(R.id.etUsername);
        etAge = findViewById(R.id.etAge);
        etContact = findViewById(R.id.etContact);
        etEmail = findViewById(R.id.etRegEmail);
        etPassword = findViewById(R.id.etRegPassword);
        etConfirm = findViewById(R.id.etConfirmPassword);
        etCode = findViewById(R.id.etCode);

        spinnerGender = findViewById(R.id.spinnerGender);
        btnRegister = findViewById(R.id.btnRegister);
        btnVerify = findViewById(R.id.btnVerify);

        layoutRegister = findViewById(R.id.layoutRegister);
        layoutVerification = findViewById(R.id.layoutVerification);

        // SPINNER DATA
        String[] genderList = {"Male", "Female", "Prefer not to say"};
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        genderList
                );
        spinnerGender.setAdapter(adapter);

        // DATABASE
        dbHelper = new Database(this);
        db = dbHelper.getWritableDatabase();

        btnRegister.setOnClickListener(v -> sendVerificationCode());
        btnVerify.setOnClickListener(v -> verifyCodeAndRegister());

        // Back to Login
        findViewById(R.id.txtBackLogin).setOnClickListener(v -> finish());
    }

    private void sendVerificationCode() {
        // Get & trim inputs
        String fullname = etFullname.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String ageText = etAge.getText().toString().trim();
        String contact = etContact.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirm = etConfirm.getText().toString().trim();

        boolean isValid = true;

        // --- VALIDATION ---
        if (fullname.isEmpty()) { etFullname.setError("Required"); isValid = false; }
        if (username.isEmpty()) { etUsername.setError("Required"); isValid = false; }
        if (ageText.isEmpty()) { etAge.setError("Required"); isValid = false; }
        if (!ageText.isEmpty()) {
            try {
                int age = Integer.parseInt(ageText);
                if (age <1 || age>120) { etAge.setError("Invalid age"); isValid=false; }
            } catch (Exception e) { etAge.setError("Must be number"); isValid=false; }
        }
        if (contact.isEmpty()) { etContact.setError("Required"); isValid=false; }
        else if (!PH_CONTACT_PATTERN.matcher(contact).matches()) { etContact.setError("09XXXXXXXXX only"); isValid=false; }
        if (email.isEmpty()) { etEmail.setError("Required"); isValid=false; }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { etEmail.setError("Invalid email"); isValid=false; }
        if (password.isEmpty()) { etPassword.setError("Required"); isValid=false; }
        else if (password.length()<6) { etPassword.setError("Min 6 chars"); isValid=false; }
        if (confirm.isEmpty()) { etConfirm.setError("Required"); isValid=false; }
        else if (!password.equals(confirm)) { etConfirm.setError("Not matching"); isValid=false; }

        if (!isValid) return;

        // --- CHECK DUPLICATE ---
        Cursor c = db.rawQuery("SELECT * FROM users WHERE username=? OR email=?", new String[]{username, email});
        if (c.moveToFirst()) {
            Toast.makeText(this, "Username or Email exists", Toast.LENGTH_SHORT).show();
            c.close();
            return;
        }
        c.close();

    }

    private void verifyCodeAndRegister() {
        String inputCode = etCode.getText().toString().trim();

        if (inputCode.equals(generatedCode)) {
            // --- SAVE TO DATABASE ---
            String fullname = etFullname.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String ageText = etAge.getText().toString().trim();
            String contact = etContact.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String gender = spinnerGender.getSelectedItem().toString();
            int age = Integer.parseInt(ageText);

            ContentValues cv = new ContentValues();
            cv.put("fullname", fullname);
            cv.put("username", username);
            cv.put("age", age);
            cv.put("gender", gender);
            cv.put("contact", contact);
            cv.put("email", email);
            cv.put("password", password);
            cv.put("verified", 1); // ✅ Mark as verified

            long result = db.insert("users", null, cv);
            if (result != -1) {
                Toast.makeText(this, "✅ Registered & Verified! You can login now.", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "❌ Failed to register", Toast.LENGTH_SHORT).show();
            }
        } else {
            etCode.setError("Wrong code. Try again.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) db.close();
    }
}