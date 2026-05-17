package com.example.finalsactivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class AdminDashboard extends AppCompatActivity {

    // Cards instead of plain buttons (better design)
    CardView cardViewPayments, cardReports, cardAnnouncements, cardManageRooms, cardManageUsers, cardMaintenance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // INITIALIZE ALL CARDS/FEATURES
        cardViewPayments = findViewById(R.id.cardViewPayments);
        cardReports = findViewById(R.id.cardReports);
        cardAnnouncements = findViewById(R.id.cardAnnouncements);
        cardManageRooms = findViewById(R.id.cardManageRooms);
        cardManageUsers = findViewById(R.id.cardManageUsers);
        cardMaintenance = findViewById(R.id.cardMaintenance);

        // ---------------- CLICK ACTIONS ----------------
        // 1. View Payments
        cardViewPayments.setOnClickListener(v ->
                startActivity(new Intent(AdminDashboard.this, UserPayment.class))
        );

        // 2. Reports & Analytics
        //cardReports.setOnClickListener(v ->
                //startActivity(new Intent(AdminDashboard.this, AdminReport.class))
        //);

        // 3. Post Announcements → Opens your Notification system
        cardAnnouncements.setOnClickListener(v ->
                startActivity(new Intent(AdminDashboard.this, PostAnnouncementActivity.class))
        );

        // 4. Manage Rooms
        cardManageRooms.setOnClickListener(v ->
                // Replace with your actual Activity
                startActivity(new Intent(AdminDashboard.this, ManageRoomsActivity.class))
        );

        // 5. Manage Users / Tenants
        cardManageUsers.setOnClickListener(v ->
                // Replace with your actual Activity
                startActivity(new Intent(AdminDashboard.this, ManageUsersActivity.class))
        );

        // 6. Maintenance Requests
        cardMaintenance.setOnClickListener(v ->
                // Replace with your actual Activity
                startActivity(new Intent(AdminDashboard.this, AdminMaintenance.class))
        );

    }
}