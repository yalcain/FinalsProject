package com.example.finalsactivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminMaintenance extends AppCompatActivity {

    Database dbHelper;
    SQLiteDatabase db;
    android.widget.ListView listRequests;
    ArrayList<Map<String, String>> requestList;
    MaintenanceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintenance);

        listRequests = findViewById(R.id.listMaintenance);
        dbHelper = new Database(this);
        db = dbHelper.getReadableDatabase();

        loadRequests();

        // CLICK TO UPDATE STATUS
        listRequests.setOnItemClickListener((parent, view, position, id) -> {
            Map<String, String> selected = requestList.get(position);
            String reqId = selected.get("id");
            String currentStatus = selected.get("status");
            String tenantId = selected.get("tenant_id");

            showStatusDialog(reqId, tenantId, currentStatus);
        });
    }

    // ✅ LOAD ALL MAINTENANCE REQUESTS
    private void loadRequests() {
        requestList = new ArrayList<>();
        Cursor c = null;

        try {
            c = db.rawQuery(
                    "SELECT m.id, m.tenant_id, u.name, u.username, m.issue, m.description, m.status, m.date " +
                            "FROM " + Database.TABLE_MAINTENANCE + " m " +
                            "JOIN " + Database.TABLE_USERS + " u ON m.tenant_id = u.id " +
                            "ORDER BY m.id DESC",
                    null
            );

            if (c.moveToFirst()) {
                do {
                    Map<String, String> req = new HashMap<>();
                    req.put("id", c.getString(0));
                    req.put("tenant_id", c.getString(1));
                    req.put("name", c.getString(2));
                    req.put("username", c.getString(3));
                    req.put("issue", c.getString(4));
                    req.put("description", c.getString(5));
                    req.put("status", c.getString(6));
                    req.put("date", c.getString(7));
                    requestList.add(req);

                } while (c.moveToNext());
            } else {
                Map<String, String> empty = new HashMap<>();
                empty.put("issue", "No maintenance requests found");
                empty.put("description", "");
                empty.put("status", "");
                requestList.add(empty);
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (c != null) c.close();
        }

        adapter = new MaintenanceAdapter();
        listRequests.setAdapter(adapter);
    }

    // ✅ SHOW STATUS OPTIONS
    private void showStatusDialog(String reqId, String tenantId, String currentStatus) {
        final String[] statuses = {"Pending", "In Progress", "Fixed / Resolved", "Cancelled"};

        new AlertDialog.Builder(this)
                .setTitle("Update Request Status")
                .setMessage("Current: " + currentStatus)
                .setItems(statuses, (dialog, which) -> {
                    String newStatus = statuses[which];
                    updateStatus(reqId, tenantId, newStatus);
                })
                .setNegativeButton("Close", null)
                .show();
    }

    // ✅ UPDATE IN DB + SEND NOTIFICATION
    private void updateStatus(String reqId, String tenantId, String newStatus) {
        SQLiteDatabase wDb = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Database.COL_MAINTENANCE_STATUS, newStatus);
        wDb.update(Database.TABLE_MAINTENANCE, values, Database.COL_MAINTENANCE_ID + "=?", new String[]{reqId});
        wDb.close();

        // ✅ AUTO-NOTIFY TENANT
        String title = "Maintenance Update";
        String message = "Your request has been updated to: " + newStatus;
        dbHelper.addNotification(Integer.parseInt(tenantId), title, message, "maintenance");

        Toast.makeText(this, "Status updated ✅", Toast.LENGTH_SHORT).show();
        loadRequests();
    }

    // ✅ CUSTOM ADAPTER
    private class MaintenanceAdapter extends BaseAdapter {
        @Override public int getCount() { return requestList.size(); }
        @Override public Object getItem(int position) { return requestList.get(position); }
        @Override public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(AdminMaintenance.this)
                        .inflate(R.layout.item_maintenance, parent, false);
            }

            Map<String, String> req = requestList.get(position);

            TextView tvTenant = convertView.findViewById(R.id.tvTenant);
            TextView tvIssue = convertView.findViewById(R.id.tvIssue);
            TextView tvDesc = convertView.findViewById(R.id.tvDesc);
            TextView tvDate = convertView.findViewById(R.id.tvDate);
            TextView tvStatus = convertView.findViewById(R.id.tvStatus);
            View dot = convertView.findViewById(R.id.statusDot);

            tvTenant.setText(req.get("name") + " (@" + req.get("username") + ")");
            tvIssue.setText(req.get("issue"));
            tvDesc.setText(req.get("description"));
            tvDate.setText(req.get("date"));
            tvStatus.setText(req.get("status"));

            // ✅ COLOR CODE STATUS
            String status = req.get("status");
            int color;
            if (status == null || status.isEmpty()) {
                color = ContextCompat.getColor(AdminMaintenance.this, android.R.color.darker_gray);
            } else switch (status) {
                case "Pending":
                    color = ContextCompat.getColor(AdminMaintenance.this, android.R.color.holo_orange_dark);
                    break;
                case "In Progress":
                    color = ContextCompat.getColor(AdminMaintenance.this, android.R.color.holo_blue_dark);
                    break;
                case "Fixed / Resolved":
                    color = ContextCompat.getColor(AdminMaintenance.this, android.R.color.holo_green_dark);
                    break;
                case "Cancelled":
                    color = ContextCompat.getColor(AdminMaintenance.this, android.R.color.holo_red_dark);
                    break;
                default:
                    color = ContextCompat.getColor(AdminMaintenance.this, android.R.color.darker_gray);
                    break;
            }

            dot.setBackgroundColor(color);
            tvStatus.setTextColor(color);

            return convertView;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) db.close();
    }
}