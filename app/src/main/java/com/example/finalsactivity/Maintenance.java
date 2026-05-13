package com.example.finalsactivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Maintenance extends AppCompatActivity {

    Database db;

    EditText etIssue, etCategory, etPriority;
    ImageView imgPreview;
    TextView txtList;

    String imagePath = "";
    int tenantId = 1; // replace with logged-in user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);

        db = new Database(this);

        etIssue = findViewById(R.id.etIssue);
        etCategory = findViewById(R.id.etCategory);
        etPriority = findViewById(R.id.etPriority);

        imgPreview = findViewById(R.id.imgPreview);
        txtList = findViewById(R.id.txtList);

        Button btnImage = findViewById(R.id.btnImage);
        Button btnSubmit = findViewById(R.id.btnSubmit);

        // SIMPLE IMAGE PLACEHOLDER (can upgrade later to gallery/camera)
        btnImage.setOnClickListener(v -> {
            imagePath = "sample.jpg";
            imgPreview.setImageResource(android.R.drawable.ic_menu_camera);
        });

        btnSubmit.setOnClickListener(v -> submitRequest());

        loadRequests();
    }

    private void submitRequest() {

        SQLiteDatabase database = db.getWritableDatabase();

        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()).format(new Date());

        ContentValues values = new ContentValues();
        values.put("tenant_id", tenantId);
        values.put("issue", etIssue.getText().toString());
        values.put("category", etCategory.getText().toString());
        values.put("priority", etPriority.getText().toString());
        values.put("image", imagePath);
        values.put("status", "Pending");
        values.put("date_created", date);

        database.insert("maintenance", null, values);

        Toast.makeText(this, "Request Submitted", Toast.LENGTH_SHORT).show();

        etIssue.setText("");
        etCategory.setText("");
        etPriority.setText("");

        loadRequests();
    }

    private void loadRequests() {

        SQLiteDatabase database = db.getReadableDatabase();

        Cursor c = database.rawQuery(
                "SELECT issue, category, priority, status, date_created " +
                        "FROM maintenance WHERE tenant_id=?",
                new String[]{String.valueOf(tenantId)}
        );

        StringBuilder sb = new StringBuilder();

        if (c.moveToFirst()) {
            do {
                sb.append("Issue: ").append(c.getString(0)).append("\n")
                        .append("Category: ").append(c.getString(1)).append("\n")
                        .append("Priority: ").append(c.getString(2)).append("\n")
                        .append("Status: ").append(c.getString(3)).append("\n")
                        .append("Date: ").append(c.getString(4)).append("\n\n");
            } while (c.moveToNext());
        } else {
            sb.append("No maintenance requests yet");
        }

        txtList.setText(sb.toString());
        c.close();
    }
}