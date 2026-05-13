package com.example.finalsactivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText etFullname, etAge, etContact,
            etEmail, etPassword, etConfirm;

    Spinner spinnerGender;

    Button btnRegister;

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFullname = findViewById(R.id.etFullname);
        etAge = findViewById(R.id.etAge);
        etContact = findViewById(R.id.etContact);
        etEmail = findViewById(R.id.etRegEmail);
        etPassword = findViewById(R.id.etRegPassword);
        etConfirm = findViewById(R.id.etConfirmPassword);

        spinnerGender = findViewById(R.id.spinnerGender);

        btnRegister = findViewById(R.id.btnRegister);

        String[] genderList = {"Male", "Female"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                genderList
        );

        spinnerGender.setAdapter(adapter);

        db = openOrCreateDatabase("DormDB",
                MODE_PRIVATE,
                null);

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "fullname TEXT," +
                        "age INTEGER," +
                        "gender TEXT," +
                        "contact TEXT," +
                        "email TEXT," +
                        "password TEXT)"
        );

        btnRegister.setOnClickListener(v -> {

            String fullname = etFullname.getText().toString().trim();
            String ageText = etAge.getText().toString().trim();
            String genderValue = spinnerGender.getSelectedItem().toString();
            String contact = etContact.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirm = etConfirm.getText().toString().trim();

            if (fullname.isEmpty() ||
                    ageText.isEmpty() ||
                    contact.isEmpty() ||
                    email.isEmpty() ||
                    password.isEmpty() ||
                    confirm.isEmpty()) {

                Toast.makeText(
                        RegisterActivity.this,
                        "Please fill all fields",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            int age = Integer.parseInt(ageText);

            if (age < 18) {

                Toast.makeText(
                        RegisterActivity.this,
                        "Age must be 18 and above",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (!password.equals(confirm)) {

                Toast.makeText(
                        RegisterActivity.this,
                        "Password does not match",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            ContentValues cv = new ContentValues();

            cv.put("fullname", fullname);
            cv.put("age", age);
            cv.put("gender", genderValue);
            cv.put("contact", contact);
            cv.put("email", email);
            cv.put("password", password);

            long result = db.insert("users",
                    null,
                    cv);

            if (result != -1) {

                Toast.makeText(
                        RegisterActivity.this,
                        "Registered Successfully",
                        Toast.LENGTH_SHORT
                ).show();

                startActivity(new Intent(
                        RegisterActivity.this,
                        LoginActivity.class
                ));

                finish();

            } else {

                Toast.makeText(
                        RegisterActivity.this,
                        "Registration Failed",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}