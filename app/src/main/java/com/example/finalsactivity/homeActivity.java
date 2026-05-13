package com.example.finalsactivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class homeActivity extends AppCompatActivity {

    CardView card1, card2, card3, card4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);
        card4 = findViewById(R.id.card4);

        card1.setOnClickListener(v -> openDorm("SD Dorm", "19 Blueberry ext. St.,Aranai Village, Brgy Ususan"));
        card2.setOnClickListener(v -> openDorm("Loft 22", "15 Blue Falcon St., Brgy.Rizal"));
        card3.setOnClickListener(v -> openDorm("The Dormitory", "C2 Mt.Apo St., Palar Village, Brgy.Pinagsama"));
        card4.setOnClickListener(v -> openDorm("Milflores", "B70 L30 Milflores St., Brgy.Rizal"));
    }

    private void openDorm(String name, String location) {

        Toast.makeText(this, "Opening " + name, Toast.LENGTH_SHORT).show();

        Intent i = new Intent(homeActivity.this, DormDetailsActivity.class);

        i.putExtra("property", name);
        i.putExtra("location", location);
        i.putExtra("room_type", "Transient");
        i.putExtra("price", "550");

        startActivity(i);
    }
}