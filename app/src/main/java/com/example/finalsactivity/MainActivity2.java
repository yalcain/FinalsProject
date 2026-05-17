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
import com.google.android.material.bottomnavigation.BottomNavigationView; // ✅ Missing import

public class MainActivity2 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView; // ✅ Added
    Toolbar toolbar;

    CardView cardSD, cardLoft, cardPalar, cardMilflores;
    TextView txtWelcome;

    SQLiteDatabase db;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);

        db = openOrCreateDatabase("apartment.db", MODE_PRIVATE, null);

        username = getIntent().getStringExtra("username");

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        bottomNavigationView = findViewById(R.id.bottomNav); // ✅ Initialize bottom nav
        toolbar = findViewById(R.id.toolbar);
        txtWelcome = findViewById(R.id.txtWelcome);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Find Your Dorm");
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.open, R.string.close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        // ✅ Bottom Navigation Click Listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                // Already on home
                return true;
            } else if (id == R.id.nav_search) {
                // Add your SearchActivity here when ready
                // startActivity(new Intent(this, SearchActivity.class));
                return true;
            } else if (id == R.id.nav_message) {
                // Add your MessageActivity here when ready
                // startActivity(new Intent(this, MessageActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                Intent intent = new Intent(this, TenantsProfileActivity.class);
                intent.putExtra("username", username); // pass current user
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_settings) {  // ✅ NEW: Settings
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.putExtra("username", username); // optional — pass user if needed
                startActivity(intent);
                return true;
            }
            return false;
        });

        showUser();

        // INIT CARDS
        cardSD = findViewById(R.id.cardSD);
        cardLoft = findViewById(R.id.cardLoft);
        cardPalar = findViewById(R.id.cardPalar);
        cardMilflores = findViewById(R.id.cardMilflores);

        // ✅ EXACT ADDRESSES YOU PROVIDED
        cardSD.setOnClickListener(v -> openDorm(
                "SD Dorm 2",
                "19 Blueberry ext., St. Aranai Village, Brgy. Ususan City",
                R.drawable.bedspace,
                "Bedspace • ₱2,500/month",
                "4.8"
        ));

        cardLoft.setOnClickListener(v -> openDorm(
                "Loft 22",
                "15 Blue Falcon St., Rizal Taguig City",
                R.drawable.trans,
                "Transient • Aircon • ₱500/night",
                "4.9"
        ));

        cardPalar.setOnClickListener(v -> openDorm(
                "The Dormitory",
                "C2 Mt.Apo St., Palar Village, Brgy.Pinagsama Taguig City",
                R.drawable.bedspace1,
                "Bedspace • ₱2,200/month",
                "4.7"
        ));

        cardMilflores.setOnClickListener(v -> openDorm(
                "Milflores Boarding House",
                "B70 L30 Milflores St., Brgy. Taguig City",
                R.drawable.bedspace2,
                "Bedspace • ₱2,300/month",
                "4.6"
        ));
    }

    private void openDorm(String name, String address, int image, String details, String rating) {
        Intent intent = new Intent(this, DormDetailsActivity.class);
        intent.putExtra("property", name);
        intent.putExtra("location", address);
        intent.putExtra("image", image);
        intent.putExtra("details", details);
        intent.putExtra("rating", rating);
        startActivity(intent);
    }

    private void showUser() {
        if (username == null) {
            txtWelcome.setText("Hello!");
            return;
        }

        Cursor cursor = db.rawQuery(
                "SELECT fullname FROM users WHERE username=?",
                new String[]{username}
        );

        if (cursor.moveToFirst()) {
            txtWelcome.setText("Hi, " + cursor.getString(0) + "!");
        } else {
            txtWelcome.setText("Hi there!");
        }
        cursor.close();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(this, MainActivity2.class));
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, TenantsProfileActivity.class);
            intent.putExtra("username", username); // pass current user
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_payment) {
            startActivity(new Intent(this, PaymentHistoryActivity.class));
        } else if (id == R.id.nav_receipt) {
            startActivity(new Intent(this, ReceiptActivity.class));
        } else if (id == R.id.nav_notif) {
            startActivity(new Intent(this, NotificationActivity.class));
        } else if (id == R.id.nav_add_place) { // ✅ This now works
            startActivity(new Intent(this, AddPlaceActivity.class));
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