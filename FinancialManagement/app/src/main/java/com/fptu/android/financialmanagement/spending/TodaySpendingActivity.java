package com.fptu.android.financialmanagement.spending;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.android.financialmanagement.R;
import com.fptu.android.financialmanagement.crudBudget.BudgetActivity;
import com.fptu.android.financialmanagement.crudBudget.Transaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;


public class TodaySpendingActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView totalAmountSpendOn;
    private ProgressBar progessBar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ProgressDialog loader;

    private FirebaseAuth mAuth;
    private String onlineUserId ="";
    private DatabaseReference expensesRef;

    private TodayItemAdapter todayItemAdapter;
    private List<Transaction> myDataList;

    private void bindView(){
//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("TodaySpending");
        totalAmountSpendOn = findViewById(R.id.totalAmountSpendOn);
        progessBar = findViewById(R.id.progessBar);

        recyclerView = findViewById(R.id.recyclerView);

        fab = findViewById(R.id.fab);
        loader = new ProgressDialog(this );

        mAuth= FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
    }
    private void bindingAction(){

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        myDataList = new ArrayList<>();
        todayItemAdapter = new TodayItemAdapter(TodaySpendingActivity.this
                ,myDataList);

        recyclerView.setAdapter(todayItemAdapter);

        readItem();

        getSupportActionBar().setTitle("TodaySpending");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemSpendOn();
            }
        });
    }

    private void readItem() {

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());

        DatabaseReference reference = FirebaseDatabase.getInstance().
                getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDataList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Transaction data = dataSnapshot.getValue(Transaction.class);
                    myDataList.add(data);
                }
                todayItemAdapter.notifyDataSetChanged();
                progessBar.setVisibility(View.GONE);

                int totaleAmount =0;
                for (DataSnapshot ds : snapshot.getChildren()){
                    Map<String,Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totaleAmount+=pTotal;

                    totalAmountSpendOn.setText("Total Day's spending: $" +totaleAmount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void addItemSpendOn() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_layout, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final Spinner itemSpinner = myView.findViewById(R.id.itemSpinner);
        final EditText amount = myView.findViewById(R.id.amount);
        final EditText note =myView.findViewById(R.id.note);
        final Button cancel = myView.findViewById(R.id.cancel);
        final Button save = myView.findViewById(R.id.save);

        note.setVisibility(View.VISIBLE);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Amount = amount.getText().toString();
                String Item = itemSpinner.getSelectedItem().toString();
                String notes = note.getText().toString();

                if(TextUtils.isEmpty(Amount)){
                    amount.setError("Amount is required!");
                    return;
                }

                if(Item.equals("Select item")){
                    Toast.makeText(TodaySpendingActivity.this, "Select valid item", Toast.LENGTH_SHORT).show();

                }
                if (TextUtils.isEmpty(notes)){
                    note.setError("note is required");
                    return;
                }
                else {
                    loader.setMessage("Adding a budget item");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String id = expensesRef.push().getKey();
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy");
                    Calendar cal = Calendar.getInstance();
                    String date = dateFormat.format(cal.getTime());

                    MutableDateTime epoch = new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now = new DateTime();
                    Months months = Months.monthsBetween(epoch, now);

                    Transaction transaction = new Transaction(Item, date, id, notes, Integer.parseInt(Amount), months.getMonths());
                    expensesRef.child(id).setValue(transaction).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(TodaySpendingActivity.this, "Add Budget successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(TodaySpendingActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_spending);
        bindView();
        bindingAction();

    }
}