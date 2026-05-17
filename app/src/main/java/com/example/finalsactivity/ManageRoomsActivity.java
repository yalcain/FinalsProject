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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManageRoomsActivity extends AppCompatActivity {

    Database dbHelper;
    SQLiteDatabase db;
    ListView listRooms;
    ArrayList<Map<String, String>> roomList;
    RoomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_rooms);

        listRooms = findViewById(R.id.listRooms);
        FloatingActionButton fabAdd = findViewById(R.id.fabAddRoom);

        dbHelper = new Database(this);
        db = dbHelper.getReadableDatabase();

        loadRooms();

        // ADD NEW ROOM
        fabAdd.setOnClickListener(v -> showAddRoomDialog());

        // CLICK ROOM → EDIT / OPTIONS
        listRooms.setOnItemClickListener((parent, view, position, id) -> {
            Map<String, String> selected = roomList.get(position);
            String roomId = selected.get("id");
            showRoomOptionsDialog(roomId, selected);
        });
    }

    // ✅ LOAD ALL ROOMS FROM DB
    private void loadRooms() {
        roomList = new ArrayList<>();
        Cursor c = null;

        try {
            c = db.rawQuery(
                    "SELECT id, property, room_name, type, capacity, occupied " +
                            "FROM " + Database.TABLE_ROOMS + " " +
                            "ORDER BY property, room_name",
                    null
            );

            if (c.moveToFirst()) {
                do {
                    Map<String, String> room = new HashMap<>();
                    room.put("id", c.getString(0));
                    room.put("property", c.getString(1));
                    room.put("room_name", c.getString(2));
                    room.put("type", c.getString(3));
                    room.put("capacity", c.getString(4));
                    room.put("occupied", c.getString(5)); // 0 = Available, 1 = Occupied
                    roomList.add(room);

                } while (c.moveToNext());
            } else {
                Map<String, String> empty = new HashMap<>();
                empty.put("room_name", "No rooms added yet");
                empty.put("type", "");
                empty.put("capacity", "");
                empty.put("occupied", "");
                roomList.add(empty);
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (c != null) c.close();
        }

        adapter = new RoomAdapter();
        listRooms.setAdapter(adapter);
    }

    // ✅ ADD NEW ROOM DIALOG
    private void showAddRoomDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_room, null);
        EditText etProperty = dialogView.findViewById(R.id.etProperty);
        EditText etRoomName = dialogView.findViewById(R.id.etRoomName);
        EditText etType = dialogView.findViewById(R.id.etType);
        EditText etCapacity = dialogView.findViewById(R.id.etCapacity);

        new AlertDialog.Builder(this)
                .setTitle("Add New Room")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String property = etProperty.getText().toString().trim();
                    String name = etRoomName.getText().toString().trim();
                    String type = etType.getText().toString().trim();
                    String capacity = etCapacity.getText().toString().trim();

                    if (property.isEmpty() || name.isEmpty() || type.isEmpty() || capacity.isEmpty()) {
                        Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SQLiteDatabase wDb = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(Database.COL_ROOM_PROPERTY, property);
                    values.put(Database.COL_ROOM_NAME, name);
                    values.put(Database.COL_ROOM_TYPE, type);
                    values.put(Database.COL_ROOM_CAPACITY, Integer.parseInt(capacity));
                    values.put(Database.COL_ROOM_OCCUPIED, 0); // default = Available

                    wDb.insert(Database.TABLE_ROOMS, null, values);
                    wDb.close();

                    Toast.makeText(this, "Room Added ✅", Toast.LENGTH_SHORT).show();
                    loadRooms(); // refresh
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ✅ ROOM OPTIONS: EDIT / DELETE / CHANGE STATUS
    private void showRoomOptionsDialog(String roomId, Map<String, String> data) {
        String[] options = {"Edit Room", "Mark as Available/Occupied", "Delete Room"};

        new AlertDialog.Builder(this)
                .setTitle("Room: " + data.get("room_name"))
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            showEditRoomDialog(roomId, data);
                            break;
                        case 1:
                            toggleOccupiedStatus(roomId, data.get("occupied"));
                            break;
                        case 2:
                            deleteRoom(roomId);
                            break;
                    }
                })
                .show();
    }

    // ✅ EDIT ROOM DETAILS
    private void showEditRoomDialog(String roomId, Map<String, String> data) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_room, null);
        EditText etProperty = dialogView.findViewById(R.id.etProperty);
        EditText etRoomName = dialogView.findViewById(R.id.etRoomName);
        EditText etType = dialogView.findViewById(R.id.etType);
        EditText etCapacity = dialogView.findViewById(R.id.etCapacity);

        // Fill existing data
        etProperty.setText(data.get("property"));
        etRoomName.setText(data.get("room_name"));
        etType.setText(data.get("type"));
        etCapacity.setText(data.get("capacity"));

        new AlertDialog.Builder(this)
                .setTitle("Edit Room")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String property = etProperty.getText().toString().trim();
                    String name = etRoomName.getText().toString().trim();
                    String type = etType.getText().toString().trim();
                    String capacity = etCapacity.getText().toString().trim();

                    if (property.isEmpty() || name.isEmpty() || type.isEmpty() || capacity.isEmpty()) {
                        Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SQLiteDatabase wDb = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(Database.COL_ROOM_PROPERTY, property);
                    values.put(Database.COL_ROOM_NAME, name);
                    values.put(Database.COL_ROOM_TYPE, type);
                    values.put(Database.COL_ROOM_CAPACITY, Integer.parseInt(capacity));

                    wDb.update(Database.TABLE_ROOMS, values, Database.COL_ROOM_ID + "=?", new String[]{roomId});
                    wDb.close();

                    Toast.makeText(this, "Room Updated ✅", Toast.LENGTH_SHORT).show();
                    loadRooms();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ✅ CHANGE AVAILABILITY
    private void toggleOccupiedStatus(String roomId, String currentStatus) {
        int newStatus = currentStatus.equals("0") ? 1 : 0;

        SQLiteDatabase wDb = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Database.COL_ROOM_OCCUPIED, newStatus);
        wDb.update(Database.TABLE_ROOMS, values, Database.COL_ROOM_ID + "=?", new String[]{roomId});
        wDb.close();

        Toast.makeText(this, "Status updated ✅", Toast.LENGTH_SHORT).show();
        loadRooms();
    }

    // ✅ DELETE ROOM
    private void deleteRoom(String roomId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Room")
                .setMessage("Are you sure? This cannot be undone.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SQLiteDatabase wDb = dbHelper.getWritableDatabase();
                    wDb.delete(Database.TABLE_ROOMS, Database.COL_ROOM_ID + "=?", new String[]{roomId});
                    wDb.close();

                    Toast.makeText(this, "Room Deleted ❌", Toast.LENGTH_SHORT).show();
                    loadRooms();
                })
                .setNegativeButton("No", null)
                .show();
    }

    // ✅ CUSTOM ADAPTER FOR LIST
    private class RoomAdapter extends BaseAdapter {
        @Override public int getCount() { return roomList.size(); }
        @Override public Object getItem(int position) { return roomList.get(position); }
        @Override public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ManageRoomsActivity.this)
                        .inflate(R.layout.item_room, parent, false);
            }

            Map<String, String> room = roomList.get(position);

            TextView tvName = convertView.findViewById(R.id.tvRoomName);
            TextView tvProperty = convertView.findViewById(R.id.tvProperty);
            TextView tvType = convertView.findViewById(R.id.tvType);
            TextView tvCapacity = convertView.findViewById(R.id.tvCapacity);
            TextView tvStatus = convertView.findViewById(R.id.tvStatus);
            View statusDot = convertView.findViewById(R.id.statusDot);

            tvName.setText(room.get("room_name"));
            tvProperty.setText(room.get("property"));
            tvType.setText("Type: " + room.get("type"));
            tvCapacity.setText("Capacity: " + room.get("capacity"));

            // Status Color & Text
            if (room.get("occupied") == null) return convertView;
            if (room.get("occupied").equals("0")) {
                tvStatus.setText("AVAILABLE");
                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                statusDot.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
            } else {
                tvStatus.setText("OCCUPIED");
                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                statusDot.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
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