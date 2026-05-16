package com.example.finalsactivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {

    Context context;
    ArrayList<RoomModel> list;
    Database dbHelper;

    public RoomAdapter(Context context, ArrayList<RoomModel> list) {
        this.context = context;
        this.list = list;
        this.dbHelper = new Database(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_room, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        RoomModel r = list.get(position);

        holder.txtRoomName.setText(r.roomName);
        holder.txtProperty.setText(r.property);
        holder.txtType.setText(r.type);
        holder.txtCapacity.setText("Capacity: " + r.capacity);

        updateStatusText(holder, r.occupied);

        holder.btnToggle.setOnClickListener(v -> {

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            int newStatus = (r.occupied == 0) ? 1 : 0;

            ContentValues cv = new ContentValues();
            cv.put("occupied", newStatus);

            db.update("rooms", cv, "id=?",
                    new String[]{String.valueOf(r.id)});

            r.occupied = newStatus;

            updateStatusText(holder, newStatus);

            Toast.makeText(context, "Status Updated", Toast.LENGTH_SHORT).show();
        });

        holder.btnAssign.setOnClickListener(v ->
                Toast.makeText(context, "Assign feature coming soon", Toast.LENGTH_SHORT).show()
        );
    }

    private void updateStatusText(ViewHolder holder, int status) {

        if (status == 0) {
            holder.txtStatus.setText("Available");
        } else {
            holder.txtStatus.setText("Occupied");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtRoomName, txtProperty, txtType, txtCapacity, txtStatus;
        Button btnAssign, btnToggle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtRoomName = itemView.findViewById(R.id.txtRoomName);
            txtProperty = itemView.findViewById(R.id.txtProperty);
            txtType = itemView.findViewById(R.id.txtType);
            txtCapacity = itemView.findViewById(R.id.txtCapacity);
            txtStatus = itemView.findViewById(R.id.txtStatus);

            btnAssign = itemView.findViewById(R.id.btnAssign);
            btnToggle = itemView.findViewById(R.id.btnToggle);
        }
    }
}