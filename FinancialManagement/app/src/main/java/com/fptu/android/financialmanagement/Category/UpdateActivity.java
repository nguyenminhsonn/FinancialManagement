package com.fptu.android.financialmanagement.Category;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fptu.android.financialmanagement.Login.Account;
import com.fptu.android.financialmanagement.R;
import com.fptu.android.financialmanagement.crudBudget.BudgetActivity;
import com.fptu.android.financialmanagement.crudBudget.Transaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UpdateActivity extends AppCompatActivity {

    EditText cName_input, level_input;
    Spinner PID_input;
    Button updatebutton,deletebutton;

    String cId, cName, PID, level;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String uID= mAuth.getCurrentUser().getUid();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        bindingView();
        bindingAction();
        getAndSetIntentData();
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(cId);
        }
    }
    private void bindingView() {
        cName_input= findViewById(R.id.categoryName_input1);
        PID_input= findViewById(R.id.parentId);
        level_input = findViewById(R.id.level_input);
        updatebutton = findViewById(R.id.UpDatebutton);
        deletebutton = findViewById(R.id.deletebutton);

    }

    private void bindingAction() {
        updatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //And only then we call this
                mydbhelper myDB = new mydbhelper(UpdateActivity.this);


                cName = cName_input.getText().toString().trim();
                PID = PID_input.getSelectedItem().toString();
                level = level_input.getText().toString().trim();
                myDB.updateData(cId, cName, PID, level,uID);
                Category user = new Category(Account.USER.getUid(), cId,cName,PID,level);
                mDatabase.child("Categories").child(uID).child(cId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(UpdateActivity.this, "Update successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UpdateActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        deletebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mydbhelper myDB = new mydbhelper(UpdateActivity.this);

                myDB.deleteOneRow(cId);
                mDatabase.child("Categories").child(uID).child(cId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UpdateActivity.this, "Delete successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UpdateActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }



    void getAndSetIntentData(){
        if(getIntent().hasExtra("Cid") && getIntent().hasExtra("cName") &&
                getIntent().hasExtra("PId") && getIntent().hasExtra("level")){
            //Getting Data from Intent
            cId = getIntent().getStringExtra("Cid");
            cName = getIntent().getStringExtra("cName");
            PID = getIntent().getStringExtra("PId");
            System.out.println(PID);
            level = getIntent().getStringExtra("level");

            //Setting Intent Data
            cName_input.setText(cName);
            String type = getIntent().getStringExtra("PId");
            PID_input.setSelection(type == "Income"? 0 : 1);
            level_input.setText(level);
            Log.d("dienpt", cName+" "+PID+" "+level);
        }else{
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
        }
    }

}