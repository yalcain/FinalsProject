package com.example.finalsactivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class PaymentHistoryActivity extends AppCompatActivity {

    ListView listPayments;

    Database dbHelper;
    SQLiteDatabase db;

    ArrayList<String> paymentList;

    String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);

        listPayments = findViewById(R.id.listPayments);

        dbHelper = new Database(this);
        db = dbHelper.getReadableDatabase();

        username = getIntent().getStringExtra("username");

        paymentList = new ArrayList<>();

        loadPayments();

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1,
                        paymentList);

        listPayments.setAdapter(adapter);
    }

    private void loadPayments() {

        Cursor cursor = null;

        try {
            cursor = db.rawQuery(
                    "SELECT amount, method, date, status FROM payments WHERE username=? ORDER BY id DESC",
                    new String[]{username}
            );

            if (cursor.moveToFirst()) {
                do {

                    String amount = cursor.getString(0);
                    String method = cursor.getString(1);
                    String date = cursor.getString(2);
                    String status = cursor.getString(3);

                    String item =
                            "💰 ₱" + amount +
                                    "\nMethod: " + method +
                                    "\nDate: " + date +
                                    "\nStatus: " + status;

                    paymentList.add(item);

                } while (cursor.moveToNext());
            } else {
                paymentList.add("No payment history found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            paymentList.add("Error loading payments.");
        } finally {
            if (cursor != null) cursor.close();
        }
    }
}