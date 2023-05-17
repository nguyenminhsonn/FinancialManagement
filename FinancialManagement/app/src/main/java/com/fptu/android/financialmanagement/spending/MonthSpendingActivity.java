package com.fptu.android.financialmanagement.spending;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fptu.android.financialmanagement.R;
import com.fptu.android.financialmanagement.crudBudget.Transaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MonthSpendingActivity extends AppCompatActivity {


    private TextView totalMonthAmountTextView;
    private ProgressBar progessBar;
    private RecyclerView recyclerView;

    private MonthSpendingAdapter monthSpendingAdapter;
    private List<Transaction> myDataList;

    private FirebaseAuth mAuth;
    private String onlineUserId ="";
    private DatabaseReference expensesRef;



    private void bindingView(){
        totalMonthAmountTextView = findViewById(R.id.totalMonthAmountTextView);
        progessBar = findViewById(R.id.progessBar);
        recyclerView = findViewById(R.id.recyclerView);

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
        monthSpendingAdapter = new MonthSpendingAdapter(MonthSpendingActivity.this,myDataList);
        recyclerView.setAdapter(monthSpendingAdapter);

        readMonthSpendingItem();
    }

    private void readMonthSpendingItem() {

        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        Query query =expensesRef.orderByChild("month").equalTo(months.getMonths());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDataList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Transaction data = dataSnapshot.getValue(Transaction.class);
                    myDataList.add(data);

                }
                monthSpendingAdapter.notifyDataSetChanged();
                progessBar.setVisibility(View.GONE);
                int totaleAmount =0;
                for (DataSnapshot ds : snapshot.getChildren()){
                    Map<String,Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totaleAmount+=pTotal;

                    totalMonthAmountTextView.setText("Total Month's spending: $" +totaleAmount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_spending);
        getSupportActionBar().setTitle("MonthSpending");
        bindingView();
        bindingAction();
    }
}