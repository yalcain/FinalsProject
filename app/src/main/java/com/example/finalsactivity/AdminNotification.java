package com.example.finalsactivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AdminNotification extends AppCompatActivity {

    EditText etTitle, etMessage;
    Button btnPost;

    RadioGroup radioType;

    RecyclerView recyclerView;

    Database dbHelper;
    SQLiteDatabase db;

    ArrayList<NotificationModel> list;
    NotificationAdapter adapter;

    String selectedType = "Announcement";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_notification);

        etTitle = findViewById(R.id.etTitle);
        etMessage = findViewById(R.id.etMessage);
        btnPost = findViewById(R.id.btnPost);
        radioType = findViewById(R.id.radioType);

        recyclerView = findViewById(R.id.recyclerNotifications);

        dbHelper = new Database(this);
        db = dbHelper.getWritableDatabase();

        list = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadNotifications();

        adapter = new NotificationAdapter(this, list);
        recyclerView.setAdapter(adapter);

        radioType.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == R.id.radioReminder) {
                selectedType = "Reminder";
            } else if (checkedId == R.id.radioMaintenance) {
                selectedType = "Maintenance";
            } else if (checkedId == R.id.radioEmergency) {
                selectedType = "Emergency";
            } else {
                selectedType = "Announcement";
            }
        });

        btnPost.setOnClickListener(v -> postNotification());
    }

    private void postNotification() {

        String title = etTitle.getText().toString();
        String message = etMessage.getText().toString();

        String date = new SimpleDateFormat("MMM dd, yyyy",
                Locale.getDefault()).format(new Date());

        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("message", message);
        cv.put("type", selectedType);
        cv.put("date", date);

        long result = db.insert("notifications", null, cv);

        if (result != -1) {

            Toast.makeText(this, "Posted Successfully", Toast.LENGTH_SHORT).show();

            list.clear();
            loadNotifications();
            adapter.notifyDataSetChanged();

        } else {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadNotifications() {

        Cursor c = db.rawQuery(
                "SELECT id, title, message, type, date FROM notifications",
                null
        );

        while (c.moveToNext()) {

            list.add(new NotificationModel(
                    c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3),
                    c.getString(4)
            ));
        }

        c.close();
    }
}