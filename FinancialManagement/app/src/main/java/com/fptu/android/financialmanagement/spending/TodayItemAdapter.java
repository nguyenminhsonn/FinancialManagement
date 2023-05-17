package com.fptu.android.financialmanagement.spending;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.android.financialmanagement.R;
import com.fptu.android.financialmanagement.crudBudget.BudgetActivity;
import com.fptu.android.financialmanagement.crudBudget.Transaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class TodayItemAdapter extends RecyclerView.Adapter<TodayItemAdapter.ViewHolder>{

    private Context mContext;
    private List<Transaction> myDataList;

    private String post_key = "";
    private String item = "";
    private String note ="";
    private int amount = 0;

    public TodayItemAdapter(Context mContext, List<Transaction> myDataList) {
        this.mContext = mContext;
        this.myDataList = myDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.retrieve_layout,
                parent,false);

        return new TodayItemAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Transaction data = myDataList.get(position);

        holder.item.setText("item: " +data.getItem());
        holder.amount.setText("amount: " +data.getAmount());
        holder.date.setText("date: " +data.getDate());
        holder.notes.setText("notes: " +data.getNotes());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_key = data.getId();
                item = data.getItem();
                amount = data.getAmount();
                note = data.getNotes();
                updateData();
            }

        
        });
    }

    private void updateData() {


        AlertDialog.Builder myDialog = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View mView = inflater.inflate(R.layout.update_layout, null);

        myDialog.setView(mView);
        final AlertDialog dialog = myDialog.create();
        final TextView mItem = mView.findViewById(R.id.itemName);
        final EditText mAmount = mView.findViewById(R.id.amount);
        final EditText mNotes = mView.findViewById(R.id.note);



        mItem.setText(item);

        mAmount.setText(String.valueOf(amount));
        mAmount.setSelection(String.valueOf(amount).length());

        mNotes.setText(note);
        mNotes.setSelection(note.length());
        Button btnDelete = mView.findViewById(R.id.btnDelete);
        Button btnUpdate = mView.findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount = Integer.parseInt(mAmount.getText().toString());
                note= mNotes.getText().toString();

                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy");
                Calendar cal = Calendar.getInstance();
                String date = dateFormat.format(cal.getTime());

                MutableDateTime epoch = new MutableDateTime();
                epoch.setDate(0);
                DateTime now = new DateTime();
                Months months = Months.monthsBetween(epoch, now);

                Transaction transaction = new Transaction(item, date, post_key, note, amount, months.getMonths());

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").
                        child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                reference.child(post_key).setValue(transaction).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(mContext, "Update successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.dismiss();

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").
                    child(FirebaseAuth.getInstance().getCurrentUser().getUid());


            @Override
            public void onClick(View view) {
                reference.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(mContext, "Delete successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.dismiss();

            }
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return myDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView item, amount, date, notes;
        public ImageView imageView;

        public void bindingView(){
        item = itemView.findViewById(R.id.item);
        amount = itemView.findViewById(R.id.amount);
        date = itemView.findViewById(R.id.date);
        notes = itemView.findViewById(R.id.note);
        imageView = itemView.findViewById(R.id.imageView);

        }

        public void bindingAction(){

        }
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bindingView();
        }
    }
}
