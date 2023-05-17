package com.fptu.android.financialmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fptu.android.financialmanagement.Category.CrudPage;
import com.fptu.android.financialmanagement.Convertor.CurrencyConvertor;
import com.fptu.android.financialmanagement.Login.LoginActivity;
import com.fptu.android.financialmanagement.analysis.ChooseAnalyticActivity;
import com.fptu.android.financialmanagement.crudBudget.BudgetActivity;
import com.fptu.android.financialmanagement.history.historyActivity;
import com.fptu.android.financialmanagement.spending.MonthSpendingActivity;
import com.fptu.android.financialmanagement.spending.TodaySpendingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String CHANNEL_ID = "CHANNEL_1";
    public static final String CHANNEL_ID_2 = "CHANNEL_2";
    private CardView budgetCardView;
    private CardView cateCardView;
    private CardView analysisCardView;
    private CardView TodaySpending;
    private CardView convertor;
    private CardView monthSpending;
    private TextView budgetTv;
    private CardView history,logout;
    private String onlineUserID = "";

    private FirebaseAuth mAuth;
    private DatabaseReference budgetRef;

    private int totalAmountBudget = 0;
    private int totalAmountBudgetB = 0;
    private int totalAmountBudgetC = 0;

    private void bindingview(){
        cateCardView = findViewById(R.id.cateCardView);
        budgetCardView = findViewById(R.id.budgetCardView);
        analysisCardView = findViewById(R.id.analysisCardView);
        TodaySpending = findViewById(R.id.TodaySpending);
        convertor = findViewById(R.id.convertor);
        history = findViewById(R.id.history);
        logout = findViewById(R.id.logout);
        monthSpending = findViewById(R.id.monthSpending);
        budgetTv = findViewById(R.id.budgetTv);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindingview();
    // create notifi
        createNotificationChannel();

        budgetCardView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BudgetActivity.class);
                startActivity(intent);
            }
        });


        cateCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CrudPage.class);
                startActivity(intent);
            }
        });

        analysisCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChooseAnalyticActivity.class);
                startActivity(intent);
            }
        });

        TodaySpending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TodaySpendingActivity.class);
                startActivity(intent);
            }
        });

        convertor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CurrencyConvertor.class);
                startActivity(intent);
            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,  historyActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                Toast.makeText(MainActivity.this, "logout success", Toast.LENGTH_LONG).show();
            }
        });


        monthSpending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,  MonthSpendingActivity.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        onlineUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        budgetRef = FirebaseDatabase.getInstance().getReference("budget").child(onlineUserID);

        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmountBudgetB += pTotal;
                    }
                    totalAmountBudgetC = totalAmountBudgetB;
                } else {
                    Toast.makeText(MainActivity.this, "Please set a budget", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getBudgetAmount();
    }

    private void getBudgetAmount() {
        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmountBudget += pTotal;
                        budgetTv.setText("$" + String.valueOf(totalAmountBudget));
                    }
                    totalAmountBudgetC = totalAmountBudgetB;
                } else {
                    totalAmountBudget = 0;
                    budgetTv.setText("$ " + String.valueOf(0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_MIN;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            CharSequence name2 = getString(R.string.channel_name2);
            String description2 = getString(R.string.channel_description2);;
            int importance2 = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel2 = new NotificationChannel(CHANNEL_ID_2, name2, importance2);
            channel2.setDescription(description2);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager!=null) {
                notificationManager.createNotificationChannel(channel);
                notificationManager.createNotificationChannel(channel2);
            }
        }
    }
}