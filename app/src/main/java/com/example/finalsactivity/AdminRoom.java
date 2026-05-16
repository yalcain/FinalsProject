package com.example.finalsactivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdminRoom extends AppCompatActivity {

    RecyclerView recyclerRooms;
    ArrayList<RoomModel> list;
    RoomAdapter adapter;

    Database dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_room);

        recyclerRooms = findViewById(R.id.recyclerRooms);

        dbHelper = new Database(this);
        db = dbHelper.getReadableDatabase();

        list = new ArrayList<>();

        recyclerRooms.setLayoutManager(new LinearLayoutManager(this));

        loadRooms();

        adapter = new RoomAdapter(this, list); // ✅ FIXED
        recyclerRooms.setAdapter(adapter);
    }

    private void loadRooms() {

        Cursor c = db.rawQuery("SELECT * FROM rooms", null);

        if (c.moveToFirst()) {
            do {
                list.add(new RoomModel(
                        c.getInt(0),
                        c.getString(1),
                        c.getString(2),
                        c.getString(3),
                        c.getInt(4),
                        c.getInt(5)
                ));
            } while (c.moveToNext());
        }

        c.close();
    }
}