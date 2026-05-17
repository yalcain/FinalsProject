package com.example.finalsactivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {

    Database dbHelper;
    SQLiteDatabase db;
    ListView listView;
    ArrayList<Map<String, String>> notifList;
    NotificationAdapter adapter;

    // GET THIS FROM LOGIN / CURRENT USER
    int currentTenantId = 1; // ⚠️ CHANGE THIS DYNAMICALLY LATER

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // GET TENANT ID FROM INTENT (from Login/MainActivity)
        if (getIntent().hasExtra("TENANT_ID")) {
            currentTenantId = getIntent().getIntExtra("TENANT_ID", 1);
        }

        listView = findViewById(R.id.listNotif);

        // INIT DB
        dbHelper = new Database(this);
        db = dbHelper.getReadableDatabase();

        loadNotifications();
    }

    private void loadNotifications() {
        notifList = new ArrayList<>();
        Cursor c = null;

        try {
            c = db.rawQuery(
                    "SELECT id, title, message, type, date, is_read " +
                            "FROM " + Database.TABLE_NOTIFICATIONS + " " +
                            "WHERE " + Database.COL_NOTIF_TENANT_ID + "=? " +
                            "ORDER BY id DESC", // NEWEST FIRST
                    new String[]{String.valueOf(currentTenantId)}
            );

            if (c.moveToFirst()) {
                do {
                    Map<String, String> notif = new HashMap<>();
                    notif.put("id", c.getString(0));
                    notif.put("title", c.getString(1));
                    notif.put("message", c.getString(2));
                    notif.put("type", c.getString(3));
                    notif.put("date", c.getString(4));
                    notif.put("is_read", c.getString(5)); // 0 or 1
                    notifList.add(notif);

                } while (c.moveToNext());
            } else {
                Map<String, String> empty = new HashMap<>();
                empty.put("id", "0");
                empty.put("title", "📭 No Notifications");
                empty.put("message", "You don't have any updates yet.");
                empty.put("type", "info");
                empty.put("date", "");
                empty.put("is_read", "1");
                notifList.add(empty);
            }

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("id", "-1");
            error.put("title", "⚠️ Error");
            error.put("message", "Failed to load: " + e.getMessage());
            error.put("type", "error");
            error.put("date", "");
            error.put("is_read", "1");
            notifList.add(error);
            e.printStackTrace();

        } finally {
            if (c != null && !c.isClosed()) c.close();
        }

        adapter = new NotificationAdapter();
        listView.setAdapter(adapter);

        // ✅ MARK AS READ WHEN CLICKED — FIXED TYPO HERE
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Map<String, String> selected = notifList.get(position);
            String notifId = selected.get("id");
            String isRead = selected.get("is_read");

            if (isRead != null && isRead.equals("0")) {
                markAsRead(Integer.parseInt(notifId));
                selected.put("is_read", "1");
                adapter.notifyDataSetChanged();
            }
        });
    }

    // ✅ UPDATE DB: SET is_read = 1
    private void markAsRead(int notifId) {
        SQLiteDatabase wDb = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Database.COL_NOTIF_IS_READ, 1);
        wDb.update(
                Database.TABLE_NOTIFICATIONS,
                values,
                Database.COL_NOTIF_ID + "=?",
                new String[]{String.valueOf(notifId)}
        );
        wDb.close();
    }

    // ✅ CUSTOM ADAPTER — FIXED ALL SYMBOLS HERE
    private class NotificationAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return notifList.size();
        }

        @Override
        public Object getItem(int position) {
            return notifList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(NotificationActivity.this)
                        .inflate(R.layout.item_notification, parent, false);
            }

            Map<String, String> notif = notifList.get(position);

            // ✅ FIXED: Correct view IDs
            TextView tvNotifTitle = convertView.findViewById(R.id.tvNotifTitle);
            TextView tvNotifMessage = convertView.findViewById(R.id.tvNotifMessage);
            TextView tvNotifDate = convertView.findViewById(R.id.tvNotifDate);
            View dotIndicator = convertView.findViewById(R.id.dotIndicator);

            String title = notif.get("title");
            String message = notif.get("message");
            String type = notif.get("type");
            String date = notif.get("date");
            String isRead = notif.get("is_read");

            tvNotifTitle.setText(title);
            tvNotifMessage.setText(message);
            tvNotifDate.setText(date);

            // ✅ UNREAD = BOLD TEXT + SHOW DOT
            if (isRead.equals("0")) {
                tvNotifTitle.setTypeface(null, android.graphics.Typeface.BOLD);
                dotIndicator.setVisibility(View.VISIBLE);
            } else {
                tvNotifTitle.setTypeface(null, android.graphics.Typeface.NORMAL);
                dotIndicator.setVisibility(View.GONE);
            }

            // ✅ COLOR CODING BY TYPE
            int color;
            if (type == null) type = "";
            switch (type.toLowerCase()) {
                case "payment":
                    color = ContextCompat.getColor(NotificationActivity.this, android.R.color.holo_green_dark);
                    break;
                case "maintenance":
                    color = ContextCompat.getColor(NotificationActivity.this, android.R.color.holo_orange_dark);
                    break;
                case "alert":
                case "error":
                    color = ContextCompat.getColor(NotificationActivity.this, android.R.color.holo_red_dark);
                    break;
                case "info":
                default:
                    color = ContextCompat.getColor(NotificationActivity.this, android.R.color.darker_gray);
                    break;
            }

            tvNotifTitle.setTextColor(color);
            dotIndicator.setBackgroundColor(color);

            return convertView;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) db.close();
    }
}