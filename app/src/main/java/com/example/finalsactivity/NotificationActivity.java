package com.example.finalsactivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    Database dbHelper;
    SQLiteDatabase db;

    ListView listView;

    ArrayList<String> notifList;

    int tenantId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        listView = findViewById(R.id.listNotif);

        // DATABASE
        dbHelper = new Database(this);
        db = dbHelper.getReadableDatabase();

        loadNotifications();
    }

    private void loadNotifications() {

        notifList = new ArrayList<>();

        Cursor c = null;

        try {

            c = db.rawQuery(
                    "SELECT title, message, type, date " +
                            "FROM notifications " +
                            "WHERE tenant_id=? " +
                            "ORDER BY id DESC",
                    new String[]{String.valueOf(tenantId)}
            );

            if (c.moveToFirst()) {

                do {

                    String title = c.getString(0);
                    String message = c.getString(1);
                    String type = c.getString(2);
                    String date = c.getString(3);

                    notifList.add(
                            "📌 " + title +
                                    "\n\n" + message +
                                    "\n\nType: " + type +
                                    "\nDate: " + date
                    );

                } while (c.moveToNext());

            } else {

                notifList.add("No notifications yet");
            }

        } catch (Exception e) {

            notifList.add("ERROR: " + e.getMessage());
            e.printStackTrace();

        } finally {

            if (c != null) {
                c.close();
            }
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        notifList
                );

        listView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (db != null) {
            db.close();
        }
    }
}