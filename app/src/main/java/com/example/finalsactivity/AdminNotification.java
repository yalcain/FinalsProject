package com.example.finalsactivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;
import java.util.ArrayList;

public class AdminNotification extends AppCompatActivity {

    Database dbHelper;
    SQLiteDatabase db;
    RecyclerView recyclerView;
    NotificationAdapter adapter;
    ArrayList<NotificationModel> notifList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_notification);

        recyclerView = findViewById(R.id.recyclerNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new Database(this);
        db = dbHelper.getReadableDatabase();

        loadAllNotifications();
    }

    private void loadAllNotifications() {
        notifList = new ArrayList<>();
        Cursor c = null;

        try {
            c = db.rawQuery(
                    "SELECT id, title, message, date, type FROM " + Database.TABLE_NOTIFICATIONS + " ORDER BY id DESC",
                    null
            );

            if (c.moveToFirst()) {
                do {
                    notifList.add(new NotificationModel(
                            c.getInt(0),
                            c.getString(1),
                            c.getString(2),
                            c.getString(3),
                            c.getString(4)
                    ));
                } while (c.moveToNext());
            } else {
                Toast.makeText(this, "No notifications found", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (c != null) c.close();
        }

        adapter = new NotificationAdapter(this, notifList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) db.close();
    }
}