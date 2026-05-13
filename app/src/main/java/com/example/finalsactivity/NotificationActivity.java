package com.example.finalsactivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    Database db;
    ListView listView;

    int tenantId = 1; // replace with logged-in tenant ID

    ArrayList<String> notifList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        db = new Database(this);
        listView = findViewById(R.id.listNotif);

        loadNotifications();
    }

    private void loadNotifications() {

        notifList = new ArrayList<>();

        SQLiteDatabase database = db.getReadableDatabase();

        Cursor c = database.rawQuery(
                "SELECT title, message, type, date FROM notifications WHERE tenant_id=? ORDER BY id DESC",
                new String[]{String.valueOf(tenantId)}
        );

        if (c.moveToFirst()) {
            do {
                String title = c.getString(0);
                String msg = c.getString(1);
                String type = c.getString(2);
                String date = c.getString(3);

                notifList.add(
                        "📌 " + title +
                                "\n" + msg +
                                "\nType: " + type +
                                "\nDate: " + date + "\n"
                );

            } while (c.moveToNext());
        } else {
            notifList.add("No notifications yet");
        }

        c.close();

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1,
                        notifList);

        listView.setAdapter(adapter);
    }
}