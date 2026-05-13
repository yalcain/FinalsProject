package com.example.finalsactivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TenantsProfileActivity extends AppCompatActivity {

    Database db;

    TextView txtName, txtRoom, txtRent, txtDue;
    EditText etContact;
    Button btnUpdate;

    int tenantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenants_profile);

        db = new Database(this);

        txtName = findViewById(R.id.txtName);
        txtRoom = findViewById(R.id.txtRoom);
        txtRent = findViewById(R.id.txtRent);
        txtDue = findViewById(R.id.txtDue);

        etContact = findViewById(R.id.etContact);
        btnUpdate = findViewById(R.id.btnUpdate);

        // GET TENANT ID FROM INTENT
        tenantId = getIntent().getIntExtra("tenant_id", -1);

        loadTenantProfile();

        btnUpdate.setOnClickListener(v -> updateContact());
    }

    private void loadTenantProfile() {

        SQLiteDatabase database = db.getReadableDatabase();

        Cursor c = database.rawQuery(
                "SELECT * FROM tenants WHERE id=?",
                new String[]{String.valueOf(tenantId)}
        );

        if (c.moveToFirst()) {

            String name = c.getString(1);
            String room = c.getString(2);
            String rent = c.getString(3);
            String due = c.getString(4);
            String contact = c.getString(5);

            txtName.setText(name);
            txtRoom.setText(room);
            txtRent.setText("₱" + rent);
            txtDue.setText(due);
            etContact.setText(contact);
        }

        c.close();
    }

    private void updateContact() {

        SQLiteDatabase database = db.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("contact", etContact.getText().toString());

        database.update(
                "tenants",
                values,
                "id=?",
                new String[]{String.valueOf(tenantId)}
        );

        Toast.makeText(this,
                "Contact Updated Successfully",
                Toast.LENGTH_SHORT).show();
    }
}