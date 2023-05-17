package com.fptu.android.financialmanagement.Category;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fptu.android.financialmanagement.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CrudPage extends AppCompatActivity {
    RecyclerView recyclerView;
    FloatingActionButton add_button;
    mydbhelper FinancialManagement;

    ArrayList<String> cId, categoryName, categoryPID, level;
    MyAdapter myAdapter;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ArrayList<Category>list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crud_page);
        getSupportActionBar().setTitle("My Category");
        bindingView();
        bindingAction();
        FinancialManagement = new mydbhelper(CrudPage.this);
        cId = new ArrayList<>();
        categoryName = new ArrayList<>();
        categoryPID = new ArrayList<>();
        level = new ArrayList<>();
        storeDataInArrays();
        myAdapter = new MyAdapter(CrudPage.this,this, cId,categoryName, categoryPID,level);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(CrudPage.this));
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            recreate();
        }
    }

    void storeDataInArrays() {
        Cursor cursor = FinancialManagement.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                cId.add(cursor.getString(0));
                categoryName.add(cursor.getString(1));
                categoryPID.add(cursor.getString(2));
                level.add(cursor.getString(3));
            }

        }
    }

    private void bindingView() {
        recyclerView = findViewById(R.id.recyclerView);
        add_button = findViewById(R.id.addbutton);
    }

    private void bindingAction() {
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CrudPage.this, AddActivity.class);
                startActivity(intent);
            }
        });

    }
}