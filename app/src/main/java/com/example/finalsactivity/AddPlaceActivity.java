package com.example.finalsactivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddPlaceActivity extends AppCompatActivity {

    EditText etName, etAddress, etPrice, etType, etRating;
    Button btnSave;
    Database dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        dbHelper = new Database(this);

        etName = findViewById(R.id.etName);
        etAddress = findViewById(R.id.etAddress);
        etPrice = findViewById(R.id.etPrice);
        etType = findViewById(R.id.etType);
        etRating = findViewById(R.id.etRating);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> savePlace());
    }

    private void savePlace() {
        String name = etName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String price = etPrice.getText().toString().trim();
        String type = etType.getText().toString().trim();
        String rating = etRating.getText().toString().trim();

        // Validate fields
        if (name.isEmpty() || address.isEmpty() || price.isEmpty() || type.isEmpty() || rating.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save to database
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Database.COL_PLACE_NAME, name);
        values.put(Database.COL_PLACE_ADDRESS, address);
        values.put(Database.COL_PLACE_PRICE, price);
        values.put(Database.COL_PLACE_TYPE, type);
        values.put(Database.COL_PLACE_RATING, rating);
        values.put(Database.COL_PLACE_IMAGE, R.drawable.bedspace); // Default image

        long result = db.insert(Database.TABLE_PLACES, null, values);
        db.close();

        if (result != -1) {
            Toast.makeText(this, "New place added successfully!", Toast.LENGTH_SHORT).show();
            finish(); // Go back to home
        } else {
            Toast.makeText(this, "Failed to add place", Toast.LENGTH_SHORT).show();
        }
    }
}
