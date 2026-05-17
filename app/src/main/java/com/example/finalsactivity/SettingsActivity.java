package com.example.finalsactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    LinearLayout layoutAccount, layoutNotification, layoutDisplay, layoutPrivacy,
            layoutPayment, layoutLanguage, layoutHelp, layoutLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // back arrow
            toolbar.setNavigationOnClickListener(v -> finish()); // go back when click arrow
        }

        // Link all clickable sections
        layoutAccount = findViewById(R.id.layoutAccount);
        layoutNotification = findViewById(R.id.layoutNotification);
        layoutDisplay = findViewById(R.id.layoutDisplay);
        layoutPrivacy = findViewById(R.id.layoutPrivacy);
        layoutPayment = findViewById(R.id.layoutPayment);
        layoutLanguage = findViewById(R.id.layoutLanguage);
        layoutHelp = findViewById(R.id.layoutHelp);
        layoutLogout = findViewById(R.id.layoutLogout);

        // --- CLICK ACTIONS ---
        layoutAccount.setOnClickListener(v -> {
            // Open Profile / Account Edit screen
            startActivity(new Intent(SettingsActivity.this, TenantsProfileActivity.class));
        });

        layoutNotification.setOnClickListener(v -> {
            // You can create NotificationSettingsActivity later
            // For now: Toast or open new screen
        });

        layoutDisplay.setOnClickListener(v -> {
            // Display / Theme settings
        });

        layoutPrivacy.setOnClickListener(v -> {
            // Privacy / Change Password
        });

        layoutPayment.setOnClickListener(v -> {
            // ✅ NAITAMA NA: Ginamit ang PaymentActivity.class na mayroon ka sa project mo
            startActivity(new Intent(SettingsActivity.this, PaymentActivity.class));
        });

        layoutLanguage.setOnClickListener(v -> {
            // Language selection
        });

        layoutHelp.setOnClickListener(v -> {
            // Help / FAQ
        });

        layoutLogout.setOnClickListener(v -> {
            // LOGOUT - go back to Login screen
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // clear history
            startActivity(intent);
            finish();
        });
    }
}
