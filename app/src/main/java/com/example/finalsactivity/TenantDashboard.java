package com.example.finalsactivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TenantDashboard extends AppCompatActivity {

    Database db;

    TextView txtRoom, txtRent, txtDue, txtAnnouncement;

    int tenantId; // dynamic based on login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_dashboard);

        db = new Database(this);

        txtRoom = findViewById(R.id.txtRoom);
        txtRent = findViewById(R.id.txtRent);
        txtDue = findViewById(R.id.txtDue);
        txtAnnouncement = findViewById(R.id.txtAnnouncement);

        Button btnLoad = findViewById(R.id.btnLoad);

        // 📌 GET TENANT ID FROM LOGIN
        tenantId = getIntent().getIntExtra("tenant_id", -1);

        if (tenantId == -1) {
            Toast.makeText(this, "Error: No tenant logged in", Toast.LENGTH_SHORT).show();
            finish();
        }

        // AUTO LOAD (recommended)
        loadDashboard();

        btnLoad.setOnClickListener(v -> loadDashboard());
    }

    private void loadDashboard() {

        SQLiteDatabase database = db.getReadableDatabase();

        // 🏠 TENANT INFO
        Cursor c = database.rawQuery(
                "SELECT room, rent, due_date FROM tenants WHERE id=?",
                new String[]{String.valueOf(tenantId)}
        );

        if (c.moveToFirst()) {
            txtRoom.setText("Room: " + c.getString(0));
            txtRent.setText("Rent: ₱" + c.getString(1));
            txtDue.setText("Due Date: " + c.getString(2));
        }
        c.close();

        // 📢 ANNOUNCEMENT
        Cursor a = database.rawQuery(
                "SELECT message FROM announcements ORDER BY id DESC LIMIT 1",
                null
        );

        if (a.moveToFirst()) {
            txtAnnouncement.setText("Announcement: " + a.getString(0));
        } else {
            txtAnnouncement.setText("No announcements");
        }

        a.close();
    }
}
