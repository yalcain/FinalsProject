package com.example.finalsactivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdminMaintenance extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<MaintenanceModel> list;
    MaintenanceAdapter adapter;

    Database dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintenance);

        recyclerView = findViewById(R.id.recyclerMaintenance);

        dbHelper = new Database(this);
        db = dbHelper.getReadableDatabase();

        list = new ArrayList<>();

        loadMaintenance();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MaintenanceAdapter(this, list);

        recyclerView.setAdapter(adapter);
    }

    private void loadMaintenance() {

        Cursor c = db.rawQuery(
                "SELECT id, issue, category, priority, staff, status, date_created FROM maintenance",
                null
        );

        while (c.moveToNext()) {

            list.add(new MaintenanceModel(
                    c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3),
                    c.getString(4),
                    c.getString(5),
                    c.getString(6)
            ));
        }

        c.close();
    }
}