package com.fptu.android.financialmanagement.Login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fptu.android.financialmanagement.Category.Category;
import com.fptu.android.financialmanagement.Category.mydbhelper;
import com.fptu.android.financialmanagement.MainActivity;
import com.fptu.android.financialmanagement.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUserName,edtPassword;
    private Button btnLogin;
    private TextView loginQn, TvChangePass;
    ArrayList<Category>list = new ArrayList<>();

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    mydbhelper FinancialManagement;

    private void bindingView(){
        edtUserName = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.LoginBtn);
        loginQn = findViewById(R.id.LoginQn);
        TvChangePass = findViewById(R.id.TvChangePass);

    }

    private void bindingAction() {
        loginQn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        btnLogin.setOnClickListener(this::onClick);
        TvChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, changePass.class));
            }
        });

    }

    private void onClick(View view) {
        userLogin();
    }

    private void userLogin() {
        String usernameString = edtUserName.getText().toString();
        String passwordString = edtPassword.getText().toString();
        if (TextUtils.isEmpty(usernameString)){
            edtUserName.setError("Email is required");
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(usernameString).matches()) {
            edtUserName.setError("please provide correct email");
            edtUserName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(passwordString)){
            edtPassword.setError("Password is required");
        }if (passwordString.length() < 6) {
            edtPassword.setError("Password should longer than 6 bro");
            edtPassword.requestFocus();
            return;

        }
        else{
            mAuth.signInWithEmailAndPassword(usernameString,passwordString).
                    addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Cursor cursor =  FinancialManagement.readAllData();
                                if (cursor!=null){
                                    while (cursor.moveToNext()){
                                        Log.i("dd",cursor.getString(0));
                                        list.add(new Category(cursor.getString(4),cursor.getString(0)
                                                ,cursor.getString(1),cursor.getString(2),cursor.getString(3)));
                                    }
                                }
                                syncFromFirebase(list);

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    }

                                } ,4000);

                            }else{
                                Toast.makeText(LoginActivity.this, "Fail to login", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FinancialManagement= new mydbhelper(LoginActivity.this);
        bindingView();
        bindingAction();
    }
    boolean isAdd ;
    private void syncFromFirebase(ArrayList<Category>list){

        mDatabase.child("Categories").child(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Category category = snapshot.getValue(Category.class);
                isAdd = true;
                Log.i("dienpt", "onCreate: "+category.toString());
                for (Category category1 :list){
                    Log.i("dienpt", "onCreate: "+category1.toString());
                    if (Integer.parseInt(category.getCategoryID())==Integer.parseInt(category1.getCategoryID())){

                        isAdd = false;
                    }
                }
                if (isAdd) {

                    FinancialManagement.addDB(category.getCategoryName(), category.getParentID(), category.getLevel(), category.getUserID());
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}