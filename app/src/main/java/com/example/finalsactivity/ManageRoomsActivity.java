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
            if (roomId != null) {
                showRoomOptionsDialog(roomId, selected);
            }
        });
    }

    // ✅ LOAD ALL ROOMS FROM DB
    private void loadRooms() {
        roomList = new ArrayList<>();
        Cursor c = null;

        try {
            c = db.rawQuery(
                    "SELECT id, property, room_name, type, capacity, occupied " +
                            "FROM rooms " +
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
                    values.put("property", property);
                    values.put("room_name", name);
                    values.put("type", type);
                    values.put("capacity", Integer.parseInt(capacity));
                    values.put("occupied", 0); // default = Available

                    wDb.insert("rooms", null, values);
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
                    values.put("property", property);
                    values.put("room_name", name);
                    values.put("type", type);
                    values.put("capacity", Integer.parseInt(capacity));

                    wDb.update("rooms", values, "id=?", new String[]{roomId});
                    wDb.close();

                    Toast.makeText(this, "Room Updated ✅", Toast.LENGTH_SHORT).show();
                    loadRooms();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ✅ CHANGE AVAILABILITY
    private void toggleOccupiedStatus(String roomId, String currentStatus) {
        int newStatus = (currentStatus != null && currentStatus.equals("0")) ? 1 : 0;

        SQLiteDatabase wDb = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("occupied", newStatus);
        wDb.update("rooms", values, "id=?", new String[]{roomId});
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
                    wDb.delete("rooms", "id=?", new String[]{roomId});
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

            // ✅ INAYOS NA MGA IDS: Binago mula 'tv' papuntang 'txt' para mag-match sa item_room.xml
            TextView txtName = convertView.findViewById(R.id.txtRoomName);
            TextView txtProperty = convertView.findViewById(R.id.txtProperty);
            TextView txtType = convertView.findViewById(R.id.txtType);
            TextView txtCapacity = convertView.findViewById(R.id.txtCapacity);
            TextView txtStatus = convertView.findViewById(R.id.txtStatus);
            View statusDot = convertView.findViewById(R.id.statusDot);

            if (txtName != null) txtName.setText(room.get("room_name"));
            if (txtProperty != null) txtProperty.setText(room.get("property"));

            if (txtType != null) {
                if (room.get("type") != null && !room.get("type").isEmpty()) {
                    txtType.setText("Type: " + room.get("type"));
                } else {
                    txtType.setText("");
                }
            }

            if (txtCapacity != null) {
                if (room.get("capacity") != null && !room.get("capacity").isEmpty()) {
                    txtCapacity.setText("Capacity: " + room.get("capacity"));
                } else {
                    txtCapacity.setText("");
                }
            }

            // Status Color & Text Handling
            String occupied = room.get("occupied");
            if (occupied == null || occupied.isEmpty()) {
                if (txtStatus != null) txtStatus.setText("");
                if (statusDot != null) statusDot.setVisibility(View.INVISIBLE);
                return convertView;
            }

            if (statusDot != null) statusDot.setVisibility(View.VISIBLE);

            if (occupied.equals("0")) {
                if (txtStatus != null) {
                    txtStatus.setText("AVAILABLE");
                    txtStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                }
                if (statusDot != null) {
                    statusDot.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                }
            } else {
                if (txtStatus != null) {
                    txtStatus.setText("OCCUPIED");
                    txtStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }
                if (statusDot != null) {
                    statusDot.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                }
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
