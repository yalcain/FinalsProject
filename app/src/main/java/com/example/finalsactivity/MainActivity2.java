package com.example.finalsactivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity2 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    CardView card1, card2, card3, card4;
    TextView txtWelcome;

    SQLiteDatabase db;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);

        db = openOrCreateDatabase("Apartment.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS users(username TEXT, fullname TEXT)");

        username = getIntent().getStringExtra("username");

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        txtWelcome = findViewById(R.id.txtWelcome);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.open, R.string.close
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        showUser();

        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);
        card4 = findViewById(R.id.card4);

        // CLICKABLE CARDS
        card1.setOnClickListener(v -> openDorm("SD Dorm 2", "Santa Ana", R.drawable.bedspace));
        card2.setOnClickListener(v -> openDorm("Loft 22", "Taguig", R.drawable.trans));
        card3.setOnClickListener(v -> openDorm("Palar", "Manila", R.drawable.bedspace1));
        card4.setOnClickListener(v -> openDorm("Milflores", "Pasig", R.drawable.bedspace2));
    }

    // ✅ ONLY ONE openDorm METHOD (IMPORTANT FIX)
    private void openDorm(String name, String location, int image) {
        Intent intent = new Intent(this, DormDetailsActivity.class);
        intent.putExtra("property", name);
        intent.putExtra("location", location);
        intent.putExtra("image", image);
        startActivity(intent);
    }

    private void showUser() {
        if (username == null) {
            txtWelcome.setText("Welcome!");
            return;
        }

        Cursor cursor = db.rawQuery(
                "SELECT fullname FROM users WHERE username=?",
                new String[]{username}
        );

        if (cursor.moveToFirst()) {
            txtWelcome.setText("Welcome, " + cursor.getString(0) + "!");
        } else {
            txtWelcome.setText("Welcome!");
        }

        cursor.close();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(this, MainActivity2.class));

        } else if (id == R.id.nav_tenant) {
            startActivity(new Intent(this, TenantsProfileActivity.class));

        } else if (id == R.id.nav_payment) {
            startActivity(new Intent(this, PaymentHistoryActivity.class));

        } else if (id == R.id.nav_receipt) {
            startActivity(new Intent(this, ReceiptActivity.class));

        } else if (id == R.id.nav_notif) {
            startActivity(new Intent(this, NotificationActivity.class));

        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawers();
        return true;
    }
}