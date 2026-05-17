package com.example.finalsactivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PostAnnouncementActivity extends AppCompatActivity {

    EditText etTitle, etMessage;
    Spinner spnType;
    String selectedType = "info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_announcement);

        etTitle = findViewById(R.id.etTitle);
        etMessage = findViewById(R.id.etMessage);
        spnType = findViewById(R.id.spnType);

        // Type options
        String[] types = {"info", "payment", "maintenance", "alert"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnType.setAdapter(adapter);

        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedType = types[position];
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Send Button
        findViewById(R.id.btnSend).setOnClickListener(v -> sendAnnouncement());
    }

    private void sendAnnouncement() {
        String title = etTitle.getText().toString().trim();
        String message = etMessage.getText().toString().trim();

        if (title.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Database db = new Database(this);
        // Send to ALL tenants (use tenantId = 0 or loop all users)
        // OR send to specific tenant: db.addNotification(tenantId, ...)
        db.addNotification(1, title, message, selectedType); // ⚠️ Change 1 → send to all

        Toast.makeText(this, "Announcement Sent ✅", Toast.LENGTH_SHORT).show();
        finish();
    }
}