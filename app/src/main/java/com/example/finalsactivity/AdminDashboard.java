package com.example.finalsactivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class AdminDashboard extends AppCompatActivity {

    CardView cardPayments, cardMaintenance, cardTenants,
            cardRooms, cardReports, cardAnnouncement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        cardPayments = findViewById(R.id.cardPayments);
        cardMaintenance = findViewById(R.id.cardMaintenance);
        cardTenants = findViewById(R.id.cardTenants);
        cardRooms = findViewById(R.id.cardRooms);
        cardReports = findViewById(R.id.cardReports);
        cardAnnouncement = findViewById(R.id.cardAnnouncement);

        // NAVIGATION

        cardPayments.setOnClickListener(v ->
                startActivity(new Intent(this, AdminPayment.class)));

        cardMaintenance.setOnClickListener(v ->
                startActivity(new Intent(this, AdminMaintenance.class)));

        cardTenants.setOnClickListener(v ->
                startActivity(new Intent(this, AdminTenant.class)));

        cardRooms.setOnClickListener(v ->
                startActivity(new Intent(this, AdminRoom.class)));

        cardReports.setOnClickListener(v ->
                startActivity(new Intent(this, AdminReport.class)));

        cardAnnouncement.setOnClickListener(v ->
                startActivity(new Intent(this, NotificationActivity.class)));
    }
}