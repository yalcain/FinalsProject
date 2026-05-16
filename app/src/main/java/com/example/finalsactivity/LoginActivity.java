package com.example.finalsactivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    Button btnLogin;
    TextView txtCreate;
    EditText etEmail, etPassword;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtCreate = findViewById(R.id.txtCreate);

        db = openOrCreateDatabase("DormDB", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, fullname TEXT, age INTEGER, gender TEXT, contact TEXT, email TEXT UNIQUE, password TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS payments (id INTEGER PRIMARY KEY AUTOINCREMENT, userName TEXT, property TEXT, amount INTEGER, method TEXT, status TEXT, date TEXT)");

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if(email.equals("admin") && password.equals("admin123")) {
                startActivity(new Intent(LoginActivity.this, AdminDashboard.class));
                return;
            }

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email=? AND password=?", new String[]{email, password});
            if (cursor.moveToFirst()) {
                startActivity(new Intent(this, MainActivity2.class));
                finish();
            } else {
                Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        });

        txtCreate.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }
}