package com.example.finalsactivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TenantProfileActivity extends AppCompatActivity {

    EditText etName, etAge, etGender, etContact, etEmail, etRoom, etRent;
    Spinner spinnerStatus;
    Button btnSave, btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenants_profile);

        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etGender = findViewById(R.id.etGender);
        etContact = findViewById(R.id.etContact);
        etEmail = findViewById(R.id.etEmail);
        etRoom = findViewById(R.id.etRoom);
        etRent = findViewById(R.id.etRent);

        spinnerStatus = findViewById(R.id.spinnerStatus);

        btnSave = findViewById(R.id.btnSave);
        btnClear = findViewById(R.id.btnClear);

        String[] status = {"Paid", "Unpaid"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                status
        );

        spinnerStatus.setAdapter(adapter);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = etName.getText().toString();
                String age = etAge.getText().toString();
                String gender = etGender.getText().toString();
                String contact = etContact.getText().toString();
                String email = etEmail.getText().toString();
                String room = etRoom.getText().toString();
                String rent = etRent.getText().toString();
                String status = spinnerStatus.getSelectedItem().toString();

                if(name.isEmpty() || age.isEmpty() || gender.isEmpty()
                        || contact.isEmpty() || email.isEmpty()
                        || room.isEmpty() || rent.isEmpty()) {

                    Toast.makeText(TenantProfileActivity.this,
                            "Please fill all fields",
                            Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(TenantProfileActivity.this,
                            "Tenant Profile Saved",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields();
            }
        });
    }

    private void clearFields() {

        etName.setText("");
        etAge.setText("");
        etGender.setText("");
        etContact.setText("");
        etEmail.setText("");
        etRoom.setText("");
        etRent.setText("");
        spinnerStatus.setSelection(0);
    }
}