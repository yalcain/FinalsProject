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

public class MaintenanceAdapter extends RecyclerView.Adapter<MaintenanceAdapter.ViewHolder> {

    Context context;
    ArrayList<MaintenanceModel> list;
    Database dbHelper;

    public MaintenanceAdapter(Context context, ArrayList<MaintenanceModel> list) {
        this.context = context;
        this.list = list;
        this.dbHelper = new Database(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_maintenance, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MaintenanceModel m = list.get(position);

        holder.txtIssue.setText(m.issue);
        holder.txtCategory.setText(m.category);
        holder.txtPriority.setText(m.priority);
        holder.txtStaff.setText("Staff: " + m.staff);
        holder.txtStatus.setText(m.status);
        holder.txtDate.setText(m.date);

        holder.btnNext.setOnClickListener(v -> updateStatus(m, holder));
        holder.btnAssign.setOnClickListener(v -> assignStaff(m, holder));
    }

    private void updateStatus(MaintenanceModel m, ViewHolder holder) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String newStatus = m.status.equals("Pending") ? "In Progress"
                : m.status.equals("In Progress") ? "Done"
                : "Done";

        ContentValues cv = new ContentValues();
        cv.put("status", newStatus);

        db.update(
                "maintenance",
                cv,
                "id=?",
                new String[]{String.valueOf(m.id)}
        );

        m.status = newStatus;
        holder.txtStatus.setText(newStatus);

        Toast.makeText(context, "Status Updated", Toast.LENGTH_SHORT).show();
    }

    private void assignStaff(MaintenanceModel m, ViewHolder holder) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String staffName = "Maintenance Staff A"; // you can replace with dialog later

        ContentValues cv = new ContentValues();
        cv.put("staff", staffName);

        db.update(
                "maintenance",
                cv,
                "id=?",
                new String[]{String.valueOf(m.id)}
        );

        m.staff = staffName;
        holder.txtStaff.setText("Staff: " + staffName);

        Toast.makeText(context, "Staff Assigned", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtIssue, txtCategory, txtPriority, txtStaff, txtStatus, txtDate;
        Button btnNext, btnAssign;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtIssue = itemView.findViewById(R.id.txtIssue);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            txtPriority = itemView.findViewById(R.id.txtPriority);
            txtStaff = itemView.findViewById(R.id.txtStaff);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtDate = itemView.findViewById(R.id.txtDate);

            btnNext = itemView.findViewById(R.id.btnNext);
            btnAssign = itemView.findViewById(R.id.btnAssign);
        }
    }
}