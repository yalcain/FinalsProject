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

    Database dbHelper;
    SQLiteDatabase db;

    EditText etIssue, etCategory, etPriority;
    ImageView imgPreview;
    TextView txtList;

    String imagePath = "";

    int tenantId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);

        // DATABASE
        dbHelper = new Database(this);
        db = dbHelper.getWritableDatabase();

        // UI
        etIssue = findViewById(R.id.etIssue);
        etCategory = findViewById(R.id.etCategory);
        etPriority = findViewById(R.id.etPriority);

        imgPreview = findViewById(R.id.imgPreview);
        txtList = findViewById(R.id.txtList);

        Button btnImage = findViewById(R.id.btnImage);
        Button btnSubmit = findViewById(R.id.btnSubmit);

        // IMAGE PLACEHOLDER
        btnImage.setOnClickListener(v -> {

            imagePath = "sample.jpg";

            imgPreview.setImageResource(
                    android.R.drawable.ic_menu_camera
            );
        });

        // SUBMIT
        btnSubmit.setOnClickListener(v -> submitRequest());

        loadRequests();
    }

    private void submitRequest() {

        try {

            String issue =
                    etIssue.getText().toString().trim();

            String category =
                    etCategory.getText().toString().trim();

            String priority =
                    etPriority.getText().toString().trim();

            if (issue.isEmpty()
                    || category.isEmpty()
                    || priority.isEmpty()) {

                Toast.makeText(
                        this,
                        "Fill all fields",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            String date = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()
            ).format(new Date());

            ContentValues values = new ContentValues();

            values.put("tenant_id", tenantId);
            values.put("issue", issue);
            values.put("category", category);
            values.put("priority", priority);
            values.put("image", imagePath);
            values.put("status", "Pending");
            values.put("date_created", date);

            long result =
                    db.insert("maintenance", null, values);

            if (result != -1) {

                Toast.makeText(
                        this,
                        "Request Submitted",
                        Toast.LENGTH_SHORT
                ).show();

                etIssue.setText("");
                etCategory.setText("");
                etPriority.setText("");

                loadRequests();

            } else {

                Toast.makeText(
                        this,
                        "Insert Failed",
                        Toast.LENGTH_SHORT
                ).show();
            }

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "ERROR: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

            e.printStackTrace();
        }
    }

    private void loadRequests() {

        StringBuilder sb = new StringBuilder();

        Cursor c = null;

        try {

            c = db.rawQuery(
                    "SELECT issue, category, priority, " +
                            "status, date_created " +
                            "FROM maintenance " +
                            "WHERE tenant_id=? " +
                            "ORDER BY id DESC",
                    new String[]{
                            String.valueOf(tenantId)
                    }
            );

            if (c.moveToFirst()) {

                do {

                    sb.append("Issue: ")
                            .append(c.getString(0))
                            .append("\n");

                    sb.append("Category: ")
                            .append(c.getString(1))
                            .append("\n");

                    sb.append("Priority: ")
                            .append(c.getString(2))
                            .append("\n");

                    sb.append("Status: ")
                            .append(c.getString(3))
                            .append("\n");

                    sb.append("Date: ")
                            .append(c.getString(4))
                            .append("\n\n");

                } while (c.moveToNext());

            } else {

                sb.append("No maintenance requests yet");
            }

        } catch (Exception e) {

            sb.append("ERROR: ").append(e.getMessage());

            e.printStackTrace();

        } finally {

            if (c != null) {
                c.close();
            }
        }

        txtList.setText(sb.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (db != null) {
            db.close();
        }
    }
}