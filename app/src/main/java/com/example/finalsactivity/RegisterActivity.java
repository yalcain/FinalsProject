package com.example.finalsactivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText etFullname, etUsername, etAge,
            etContact, etEmail,
            etPassword, etConfirm;

    Spinner spinnerGender;
    Button btnRegister;

    Database dbHelper;
    SQLiteDatabase db;

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

        spinnerGender = findViewById(R.id.spinnerGender);
        btnRegister = findViewById(R.id.btnRegister);

        // SPINNER DATA
        String[] genderList = {"Male", "Female"};

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

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        String fullname =
                etFullname.getText().toString().trim();

        String username =
                etUsername.getText().toString().trim();

        String ageText =
                etAge.getText().toString().trim();

        String contact =
                etContact.getText().toString().trim();

        String email =
                etEmail.getText().toString().trim();

        String password =
                etPassword.getText().toString().trim();

        String confirm =
                etConfirm.getText().toString().trim();

        String gender =
                spinnerGender.getSelectedItem().toString();

        // VALIDATION
        if (fullname.isEmpty() ||
                username.isEmpty() ||
                ageText.isEmpty() ||
                contact.isEmpty() ||
                email.isEmpty() ||
                password.isEmpty() ||
                confirm.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // PASSWORD CHECK
        if (!password.equals(confirm)) {

            Toast.makeText(
                    this,
                    "Passwords do not match",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // AGE CHECK
        int age;

        try {
            age = Integer.parseInt(ageText);

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Invalid age",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // CHECK DUPLICATE
        Cursor c = db.rawQuery(
                "SELECT * FROM users WHERE username=? OR email=?",
                new String[]{username, email}
        );

        if (c.moveToFirst()) {

            Toast.makeText(
                    this,
                    "Username or Email already exists",
                    Toast.LENGTH_SHORT
            ).show();

            c.close();
            return;
        }

        c.close();

        // INSERT DATA
        ContentValues cv = new ContentValues();

        cv.put("fullname", fullname);
        cv.put("username", username);
        cv.put("age", age);
        cv.put("gender", gender);
        cv.put("contact", contact);
        cv.put("email", email);
        cv.put("password", password);

        long result = db.insert("users", null, cv);

        if (result != -1) {

            Toast.makeText(
                    this,
                    "Registered Successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Registration Failed",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (db != null) {
            db.close();
        }
    }
}