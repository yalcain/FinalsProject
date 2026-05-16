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

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {

    Context context;
    ArrayList<PaymentModel> list;
    Database dbHelper;

    // ✅ FIXED CONSTRUCTOR (THIS IS IMPORTANT)
    public PaymentAdapter(Context context, ArrayList<PaymentModel> list) {
        this.context = context;
        this.list = list;
        this.dbHelper = new Database(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_payment, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        PaymentModel model = list.get(position);

        holder.txtTenantName.setText(model.username);
        holder.txtAmount.setText("₱" + model.amount);
        holder.txtStatus.setText(model.status);

        holder.btnApprove.setOnClickListener(v -> updateStatus(model.id, "Approved", holder));
        holder.btnReject.setOnClickListener(v -> updateStatus(model.id, "Rejected", holder));
    }

    private void updateStatus(int id, String status, ViewHolder holder) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("status", status);

        int result = db.update(
                "payments",
                cv,
                "id=?",
                new String[]{String.valueOf(id)}
        );

        if (result > 0) {
            holder.txtStatus.setText(status);

            Toast.makeText(context,
                    "Payment " + status,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtTenantName, txtAmount, txtStatus;
        Button btnApprove, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTenantName = itemView.findViewById(R.id.txtTenantName);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtStatus = itemView.findViewById(R.id.txtStatus);

            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}