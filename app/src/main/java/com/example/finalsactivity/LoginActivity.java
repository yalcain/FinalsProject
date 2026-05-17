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
    TextView txtCreateAccount;
    EditText etEmail, etPassword;
    Database dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views — MATCH YOUR XML IDS
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtCreateAccount = findViewById(R.id.txtCreate);

        // Initialize database
        dbHelper = new Database(this);
        db = dbHelper.getReadableDatabase();

        // Click Listeners
        btnLogin.setOnClickListener(v -> loginUser());
        txtCreateAccount.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }

    // Normal Email/Password Login
    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        // ✅ ADMIN LOGIN (keep your code)
        if (email.equalsIgnoreCase("admin@dorm.com") && password.equals("admin123")) {
            Toast.makeText(this, "Welcome Admin", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, AdminDashboard.class));
            finish();
            return;
        }

        // ✅ TENANT LOGIN — FIXED: No MainActivity2, go to TenantDashboard
        Cursor cursor = db.query(
                Database.TABLE_USERS,
                new String[]{Database.COL_USER_ID, Database.COL_USER_FULLNAME},
                Database.COL_USER_EMAIL + "=? AND " + Database.COL_USER_PASSWORD + "=?",
                new String[]{email, password},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            String fullName = cursor.getString(cursor.getColumnIndexOrThrow(Database.COL_USER_FULLNAME));
            Toast.makeText(this, "Welcome " + fullName, Toast.LENGTH_SHORT).show();

            // ✅ FIXED: GO TO TENANT DASHBOARD (NOT MainActivity2)
            Intent intent = new Intent(this, TenantsProfileActivity.class);
            intent.putExtra("username", email); // pass user data
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}