package com.example.finalsactivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManageUsersActivity extends AppCompatActivity {

    Database dbHelper;
    SQLiteDatabase db;
    ListView listTenants;
    ArrayList<Map<String, String>> tenantList;
    TenantAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        listTenants = findViewById(R.id.listTenants);
        FloatingActionButton fabAdd = findViewById(R.id.fabAddTenant);

        dbHelper = new Database(this);
        db = dbHelper.getReadableDatabase();

        loadTenants();

        // ADD NEW TENANT
        fabAdd.setOnClickListener(v -> showAddTenantDialog());

        // CLICK TENANT → OPTIONS
        listTenants.setOnItemClickListener((parent, view, position, id) -> {
            Map<String, String> selected = tenantList.get(position);
            String userId = selected.get("id");
            showTenantOptions(userId, selected);
        });
    }

    // ✅ LOAD ALL USERS/TENANTS
    private void loadTenants() {
        tenantList = new ArrayList<>();
        Cursor c = null;

        try {
            c = db.rawQuery(
                    "SELECT id, name, username, contact, room_id, status " +
                            "FROM " + Database.TABLE_USERS + " " +
                            "WHERE role = 'tenant' " + // ONLY TENANTS
                            "ORDER BY name",
                    null
            );

            if (c.moveToFirst()) {
                do {
                    Map<String, String> tenant = new HashMap<>();
                    tenant.put("id", c.getString(0));
                    tenant.put("name", c.getString(1));
                    tenant.put("username", c.getString(2));
                    tenant.put("contact", c.getString(3));
                    tenant.put("room_id", c.getString(4));
                    tenant.put("status", c.getString(5)); // Active / Inactive
                    tenantList.add(tenant);

                } while (c.moveToNext());
            } else {
                Map<String, String> empty = new HashMap<>();
                empty.put("name", "No tenants registered yet");
                empty.put("username", "");
                empty.put("contact", "");
                empty.put("status", "");
                tenantList.add(empty);
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (c != null) c.close();
        }

        adapter = new TenantAdapter();
        listTenants.setAdapter(adapter);
    }

    // ✅ ADD NEW TENANT DIALOG
    private void showAddTenantDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_tenant, null);
        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etUser = dialogView.findViewById(R.id.etUsername);
        EditText etPass = dialogView.findViewById(R.id.etPassword);
        EditText etContact = dialogView.findViewById(R.id.etContact);
        EditText etRoomId = dialogView.findViewById(R.id.etRoomId);
        Spinner spnStatus = dialogView.findViewById(R.id.spnStatus);

        new AlertDialog.Builder(this)
                .setTitle("Add New Tenant")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String user = etUser.getText().toString().trim();
                    String pass = etPass.getText().toString().trim();
                    String contact = etContact.getText().toString().trim();
                    String room = etRoomId.getText().toString().trim();
                    String status = spnStatus.getSelectedItem().toString();

                    if (name.isEmpty() || user.isEmpty() || pass.isEmpty() || contact.isEmpty()) {
                        Toast.makeText(this, "Fill all required fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SQLiteDatabase wDb = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(Database.COL_USER_NAME, name);
                    values.put(Database.COL_USER_USERNAME, user);
                    values.put(Database.COL_USER_PASSWORD, pass);
                    values.put(Database.COL_USER_CONTACT, contact);
                    values.put(Database.COL_USER_ROOM_ID, room.isEmpty() ? null : room);
                    values.put(Database.COL_USER_STATUS, status);
                    values.put(Database.COL_USER_ROLE, "tenant"); // ALWAYS TENANT

                    wDb.insert(Database.TABLE_USERS, null, values);
                    wDb.close();

                    Toast.makeText(this, "Tenant Added ✅", Toast.LENGTH_SHORT).show();
                    loadTenants();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ✅ TENANT OPTIONS
    private void showTenantOptions(String userId, Map<String, String> data) {
        String[] options = {"Edit Details", "Change Status", "View Payments", "Delete Tenant"};

        new AlertDialog.Builder(this)
                .setTitle("Tenant: " + data.get("name"))
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            showEditTenantDialog(userId, data);
                            break;
                        case 1:
                            toggleStatus(userId, data.get("status"));
                            break;
                        case 2:
                            viewTenantPayments(data.get("username"));
                            break;
                        case 3:
                            deleteTenant(userId);
                            break;
                    }
                })
                .show();
    }

    // ✅ EDIT TENANT
    private void showEditTenantDialog(String userId, Map<String, String> data) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_tenant, null);
        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etUser = dialogView.findViewById(R.id.etUsername);
        EditText etPass = dialogView.findViewById(R.id.etPassword);
        EditText etContact = dialogView.findViewById(R.id.etContact);
        EditText etRoomId = dialogView.findViewById(R.id.etRoomId);
        Spinner spnStatus = dialogView.findViewById(R.id.spnStatus);

        // Fill existing data
        etName.setText(data.get("name"));
        etUser.setText(data.get("username"));
        etContact.setText(data.get("contact"));
        etRoomId.setText(data.get("room_id"));
        etPass.setHint("Leave blank to keep same password");

        new AlertDialog.Builder(this)
                .setTitle("Edit Tenant")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String user = etUser.getText().toString().trim();
                    String pass = etPass.getText().toString().trim();
                    String contact = etContact.getText().toString().trim();
                    String room = etRoomId.getText().toString().trim();
                    String status = spnStatus.getSelectedItem().toString();

                    if (name.isEmpty() || user.isEmpty() || contact.isEmpty()) {
                        Toast.makeText(this, "Fill all required fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SQLiteDatabase wDb = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(Database.COL_USER_NAME, name);
                    values.put(Database.COL_USER_USERNAME, user);
                    values.put(Database.COL_USER_CONTACT, contact);
                    values.put(Database.COL_USER_ROOM_ID, room.isEmpty() ? null : room);
                    values.put(Database.COL_USER_STATUS, status);
                    if (!pass.isEmpty()) values.put(Database.COL_USER_PASSWORD, pass); // only update if changed

                    wDb.update(Database.TABLE_USERS, values, Database.COL_USER_ID + "=?", new String[]{userId});
                    wDb.close();

                    Toast.makeText(this, "Tenant Updated ✅", Toast.LENGTH_SHORT).show();
                    loadTenants();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ✅ ACTIVE / INACTIVE
    private void toggleStatus(String userId, String currentStatus) {
        String newStatus = currentStatus.equals("Active") ? "Inactive" : "Active";

        SQLiteDatabase wDb = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Database.COL_USER_STATUS, newStatus);
        wDb.update(Database.TABLE_USERS, values, Database.COL_USER_ID + "=?", new String[]{userId});
        wDb.close();

        Toast.makeText(this, "Status: " + newStatus + " ✅", Toast.LENGTH_SHORT).show();
        loadTenants();
    }

    // ✅ VIEW THIS TENANT'S PAYMENTS
    private void viewTenantPayments(String username) {
        Cursor c = db.rawQuery("SELECT COUNT(*), SUM(amount) FROM payments WHERE username=?", new String[]{username});
        if (c.moveToFirst()) {
            int count = c.getInt(0);
            String total = c.getString(1);
            new AlertDialog.Builder(this)
                    .setTitle("Payment Summary")
                    .setMessage("Total Payments: " + count + "\nTotal Amount: ₱" + (total == null ? "0" : total))
                    .setPositiveButton("OK", null)
                    .show();
        }
        c.close();
    }

    // ✅ DELETE TENANT
    private void deleteTenant(String userId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Tenant")
                .setMessage("This will remove all their data. Cannot undo.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SQLiteDatabase wDb = dbHelper.getWritableDatabase();
                    wDb.delete(Database.TABLE_USERS, Database.COL_USER_ID + "=?", new String[]{userId});
                    wDb.close();
                    Toast.makeText(this, "Tenant Deleted ❌", Toast.LENGTH_SHORT).show();
                    loadTenants();
                })
                .setNegativeButton("No", null)
                .show();
    }

    // ✅ ADAPTER FOR LIST
    private class TenantAdapter extends BaseAdapter {
        @Override public int getCount() { return tenantList.size(); }
        @Override public Object getItem(int position) { return tenantList.get(position); }
        @Override public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ManageUsersActivity.this)
                        .inflate(R.layout.item_tenant, parent, false);
            }

            Map<String, String> t = tenantList.get(position);

            TextView tvName = convertView.findViewById(R.id.tvTenantName);
            TextView tvUser = convertView.findViewById(R.id.tvUsername);
            TextView tvContact = convertView.findViewById(R.id.tvContact);
            TextView tvRoom = convertView.findViewById(R.id.tvRoom);
            TextView tvStatus = convertView.findViewById(R.id.tvStatus);
            View dot = convertView.findViewById(R.id.statusDot);

            tvName.setText(t.get("name"));
            tvUser.setText("@" + t.get("username"));
            tvContact.setText(t.get("contact"));
            tvRoom.setText(t.get("room_id") == null ? "No Room" : "Room ID: " + t.get("room_id"));

            // Status Color
            if (t.get("status") == null) return convertView;
            if (t.get("status").equals("Active")) {
                tvStatus.setText("ACTIVE");
                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                dot.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
            } else {
                tvStatus.setText("INACTIVE");
                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                dot.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
            }

            return convertView;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) db.close();
    }
}