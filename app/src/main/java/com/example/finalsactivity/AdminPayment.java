package com.example.finalsactivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdminPayment extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<PaymentModel> list;
    PaymentAdapter adapter;

    Database dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_payment);

        recyclerView = findViewById(R.id.recyclerPayments);

        dbHelper = new Database(this);
        db = dbHelper.getReadableDatabase();

        list = new ArrayList<>();

        loadPayments();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // ✅ FIXED: correct adapter call
        adapter = new PaymentAdapter(this, list);

        recyclerView.setAdapter(adapter);
    }

    private void loadPayments() {

        Cursor c = db.rawQuery(
                "SELECT id, username, amount, status FROM payments",
                null
        );

        while (c.moveToNext()) {

            list.add(new PaymentModel(
                    c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3)
            ));
        }

        c.close();
    }
}