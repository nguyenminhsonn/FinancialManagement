package com.fptu.android.financialmanagement.crudBudget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter_LifecycleAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fptu.android.financialmanagement.Category.Category;
import com.fptu.android.financialmanagement.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class BudgetActivity extends AppCompatActivity {

    private TextView totalBudgetAmountTextView;
    private RecyclerView recyclerView;
    private Spinner cateSpinner;

    private FloatingActionButton fab;

    private DatabaseReference budgetRef;
    private DatabaseReference cateRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loader;
    private ValueEventListener listener;
    private ArrayList<Category> list;
    private ArrayAdapter<Category> adapter;

    private String post_key = "";
    private String item = "";
    private int amount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);
        getSupportActionBar().setTitle("My Budget");

        mAuth = FirebaseAuth.getInstance();
        budgetRef = FirebaseDatabase.getInstance().getReference().child("budget").child(mAuth.getCurrentUser().getUid());
        cateRef = FirebaseDatabase.getInstance().getReference().child("Categories").child(mAuth.getCurrentUser().getUid());
        loader = new ProgressDialog(this);

        totalBudgetAmountTextView = findViewById(R.id.totalBudgetAmountTextView);
        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalAmount = 0;

                for (DataSnapshot snap: snapshot.getChildren()) {
                    Transaction transaction = snap.getValue(Transaction.class);
                    totalAmount += transaction.getAmount();
                    String sTotal = String.valueOf("Total Budget: $" + totalAmount);
                    totalBudgetAmountTextView.setText(sTotal);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });

//        cateSpinner = findViewById(R.id.itemSpinner);
//
//        list = new ArrayList<Category>();
//        adapter = new ArrayAdapter<>(this, R.layout.simple_spinner_dropdown_item, list);
//        cateSpinner.setAdapter(adapter);
    }

    private void addItem() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_layout, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final Spinner itemSpinner = myView.findViewById(R.id.itemSpinner);
        final EditText amount = myView.findViewById(R.id.amount);
        final Button cancel = myView.findViewById(R.id.cancel);
        final Button save = myView.findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String budgetAmount = amount.getText().toString();
                String budgetItem = itemSpinner.getSelectedItem().toString();

                if(TextUtils.isEmpty(budgetAmount)){
                    amount.setError("Amount is required!");
                    return;
                }

                if(budgetItem.equals("Select item")){
                    Toast.makeText(BudgetActivity.this, "Select valid item", Toast.LENGTH_SHORT).show();

                }
                else {
                    loader.setMessage("Adding a budget item");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String id = budgetRef.push().getKey();
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy");
                    Calendar cal = Calendar.getInstance();
                    String date = dateFormat.format(cal.getTime());

                    MutableDateTime epoch = new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now = new DateTime();
                    Months months = Months.monthsBetween(epoch, now);

                    Transaction transaction = new Transaction(budgetItem, date, id, null, Integer.parseInt(budgetAmount), months.getMonths());
                    budgetRef.child(id).setValue(transaction).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(BudgetActivity.this, "Add Budget successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(BudgetActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                            loader.dismiss();
                        }
                    });
                }
                dialog.dismiss();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Transaction> options = new FirebaseRecyclerOptions.Builder<Transaction>()
                .setQuery(budgetRef, Transaction.class).build();

        FirebaseRecyclerAdapter<Transaction, MyViewHolder> mAdapter = new FirebaseRecyclerAdapter<Transaction, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Transaction model) {
                holder.setItemAmount("Allocated amount: $" + model.getAmount());
                holder.setItemDate("On: " + model.getDate());
                holder.setItemName("Transaction's Item: " + model.getItem());

                holder.notes.setVisibility(View.GONE);

//                switch (model.getItem()) {
//                    case
//                }
                holder.imageView.setImageResource(R.drawable.ic_cash);

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        post_key = getRef(holder.getAbsoluteAdapterPosition()).getKey();
                        item = model.getItem();
                        amount = model.getAmount();
                        updateData();
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_layout, parent, false);
                return new MyViewHolder(view);
            }
        };
        recyclerView.setAdapter(mAdapter);
        mAdapter.startListening();
        mAdapter.notifyDataSetChanged();
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public ImageView imageView;
        public TextView notes;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            imageView = itemView.findViewById(R.id.imageView);
            notes = itemView.findViewById(R.id.note);
//            date = itemView.findViewById(R.id.date);
        }

        public void setItemName (String itemName) {
            TextView item = mView.findViewById(R.id.item);
            item.setText(itemName);
        }

        public void setItemAmount (String itemAmount) {
            TextView item = mView.findViewById(R.id.amount);
            item.setText(itemAmount);
        }

        public void setItemDate (String itemDate) {
            TextView item = mView.findViewById(R.id.date);
            item.setText(itemDate);
        }
    }

    private void updateData() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View mView = inflater.inflate(R.layout.update_layout, null);

        myDialog.setView(mView);
        final AlertDialog dialog = myDialog.create();
        final TextView mItem = mView.findViewById(R.id.itemName);
        final EditText mAmount = mView.findViewById(R.id.amount);
        final EditText mNotes = mView.findViewById(R.id.note);

        mNotes.setVisibility(View.GONE);

        mItem.setText(item);

        mAmount.setText(String.valueOf(amount));
        mAmount.setSelection(String.valueOf(amount).length());

        Button btnDelete = mView.findViewById(R.id.btnDelete);
        Button btnUpdate = mView.findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount = Integer.parseInt(mAmount.getText().toString());

                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy");
                Calendar cal = Calendar.getInstance();
                String date = dateFormat.format(cal.getTime());

                MutableDateTime epoch = new MutableDateTime();
                epoch.setDate(0);
                DateTime now = new DateTime();
                Months months = Months.monthsBetween(epoch, now);

                Transaction transaction = new Transaction(item, date, post_key, null, amount, months.getMonths());
                budgetRef.child(post_key).setValue(transaction).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(BudgetActivity.this, "Update successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BudgetActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.dismiss();

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                budgetRef.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(BudgetActivity.this, "Delete successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BudgetActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.dismiss();

            }
        });

        dialog.show();
    }
}
