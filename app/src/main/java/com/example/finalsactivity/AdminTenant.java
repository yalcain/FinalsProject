package com.example.finalsactivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdminTenant extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<TenantModel> list;
    TenantAdapter adapter;

    Database dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_tenant);

        recyclerView = findViewById(R.id.recyclerTenants);

        dbHelper = new Database(this);
        db = dbHelper.getReadableDatabase();

        list = new ArrayList<>();

        loadTenants();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TenantAdapter(this, list);

        recyclerView.setAdapter(adapter);
    }

    private void loadTenants() {

        Cursor c = db.rawQuery(
                "SELECT id, username, room, rent, due_date, contact FROM tenants",
                null
        );

        while (c.moveToNext()) {

            list.add(new TenantModel(
                    c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3),
                    c.getString(4),
                    c.getString(5)
            ));
        }

        c.close();
    }
}