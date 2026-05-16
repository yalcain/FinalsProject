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

public class TenantAdapter extends RecyclerView.Adapter<TenantAdapter.ViewHolder> {

    Context context;
    ArrayList<TenantModel> list;
    Database dbHelper;

    public TenantAdapter(Context context, ArrayList<TenantModel> list) {
        this.context = context;
        this.list = list;
        this.dbHelper = new Database(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_tenant, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        TenantModel t = list.get(position);

        holder.txtName.setText(t.username);
        holder.txtRoom.setText("Room: " + t.room);
        holder.txtRent.setText("₱" + t.rent);
        holder.txtContact.setText(t.contact);

        holder.btnDelete.setOnClickListener(v -> deleteTenant(t.id, position));

        holder.btnEdit.setOnClickListener(v ->
                Toast.makeText(context, "Edit feature next step", Toast.LENGTH_SHORT).show()
        );
    }

    private void deleteTenant(int id, int position) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int result = db.delete(
                "tenants",
                "id=?",
                new String[]{String.valueOf(id)}
        );

        if (result > 0) {

            list.remove(position);
            notifyItemRemoved(position);

            Toast.makeText(context, "Tenant Deleted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtRoom, txtRent, txtContact;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            txtRoom = itemView.findViewById(R.id.txtRoom);
            txtRent = itemView.findViewById(R.id.txtRent);
            txtContact = itemView.findViewById(R.id.txtContact);

            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}