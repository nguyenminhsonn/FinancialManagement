package com.fptu.android.financialmanagement.spending;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.android.financialmanagement.R;
import com.fptu.android.financialmanagement.crudBudget.Transaction;

import java.util.List;

public class MonthSpendingAdapter extends RecyclerView.Adapter<MonthSpendingAdapter.ViewHolder>{

    private Context mContext;
    private List<Transaction> myDataList;

    public MonthSpendingAdapter(Context mContext, List<Transaction> myDataList) {
        this.mContext = mContext;
        this.myDataList = myDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.retrieve_layout,
                parent,false);

        return new MonthSpendingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Transaction data = myDataList.get(position);

        holder.item.setText("Item: " +data.getItem());
        holder.amount.setText("Amount: " +data.getAmount());
        holder.date.setText("Date: " +data.getDate());
        holder.notes.setText("Notes: " +data.getNotes());


    }

    @Override
    public int getItemCount() {
        return myDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView item, amount, date, notes;
        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bindingView();
        }

        public void bindingView() {
            item = itemView.findViewById(R.id.item);
            amount = itemView.findViewById(R.id.amount);
            date = itemView.findViewById(R.id.date);
            notes = itemView.findViewById(R.id.note);
            imageView = itemView.findViewById(R.id.imageView);

        }
    }
}