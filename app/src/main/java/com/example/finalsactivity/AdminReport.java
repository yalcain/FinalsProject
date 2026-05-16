package com.example.finalsactivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AdminReport extends AppCompatActivity {

    TextView txtTotalTenants;

    TextView txtTotalRooms, txtOccupied, txtAvailable;

    TextView txtPaid, txtPending;

    TextView txtMaintTotal, txtMaintPending, txtMaintProgress, txtMaintDone;

    Database dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_report);

        dbHelper = new Database(this);
        db = dbHelper.getReadableDatabase();

        initViews();
        loadReports();
    }

    private void initViews() {

        txtTotalTenants = findViewById(R.id.txtTotalTenants);

        txtTotalRooms = findViewById(R.id.txtTotalRooms);
        txtOccupied = findViewById(R.id.txtOccupied);
        txtAvailable = findViewById(R.id.txtAvailable);

        txtPaid = findViewById(R.id.txtPaid);
        txtPending = findViewById(R.id.txtPending);

        txtMaintTotal = findViewById(R.id.txtMaintTotal);
        txtMaintPending = findViewById(R.id.txtMaintPending);
        txtMaintProgress = findViewById(R.id.txtMaintProgress);
        txtMaintDone = findViewById(R.id.txtMaintDone);
    }

    private void loadReports() {

        // TENANTS
        txtTotalTenants.setText("Total: " + getCount("tenants"));

        // ROOMS
        txtTotalRooms.setText("Total: " + getCount("rooms"));

        txtOccupied.setText("Occupied: " + getWhereCount("rooms", "occupied=1"));
        txtAvailable.setText("Available: " + getWhereCount("rooms", "occupied=0"));

        // PAYMENTS
        txtPaid.setText("Paid: " + getWhereCount("payments", "status='Paid'"));
        txtPending.setText("Pending: " + getWhereCount("payments", "status='Pending'"));

        // MAINTENANCE
        txtMaintTotal.setText("Total: " + getCount("maintenance"));

        txtMaintPending.setText("Pending: " + getWhereCount("maintenance", "status='Pending'"));
        txtMaintProgress.setText("In Progress: " + getWhereCount("maintenance", "status='In Progress'"));
        txtMaintDone.setText("Done: " + getWhereCount("maintenance", "status='Done'"));
    }

    // TOTAL COUNT
    private int getCount(String table) {

        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + table, null);

        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);

        c.close();
        return count;
    }

    // WHERE COUNT
    private int getWhereCount(String table, String condition) {

        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM " + table + " WHERE " + condition,
                null
        );

        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);

        c.close();
        return count;
    }
}