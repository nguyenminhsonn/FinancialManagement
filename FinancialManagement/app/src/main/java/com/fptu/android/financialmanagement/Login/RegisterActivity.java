package com.fptu.android.financialmanagement.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextUtils;
import android.util.Patterns;

import com.fptu.android.financialmanagement.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtUserName,edtPassword,edtCustomerName,edtPhone;
    private Button btnRegister;
    private TextView RegistQn;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private void bindingView(){
        edtUserName = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);
//        edtCustomerName = findViewById(R.id.edtCustomerName);
        edtPhone = findViewById(R.id.edtPhone);
        btnRegister = findViewById(R.id.btnRegister);
        RegistQn = findViewById(R.id.RegistQn);

    }

    private void bindingAction() {
        RegistQn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(this::onClick);
    }

    public void onClick(View v){
        userRegister();

    }

    private void userRegister() {
        String usernameString = edtUserName.getText().toString();
        String passwordString = edtPassword.getText().toString();
        String phoneString = edtPhone.getText().toString();
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
            edtPassword.setError("Password should longer than 6 ");
            edtPassword.requestFocus();
            return;

        }
        if (TextUtils.isEmpty(phoneString)){
            edtPhone.setError("Phone is required");
        }if (phoneString.length() <10) {
            edtPhone.setError("Phone should have 10 digit");
            edtPhone.requestFocus();
            return;

        }
        else{
            mAuth.createUserWithEmailAndPassword(usernameString,passwordString).
                    addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Account acc = new Account(usernameString,passwordString,phoneString);
                                FirebaseDatabase.getInstance().getReference("Accounts").child("Account").
                                        child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(acc).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(RegisterActivity.this, "signup successfully",
                                                            Toast.LENGTH_LONG).show();
                                                }else{
                                                    Toast.makeText(RegisterActivity.this, "signup fail",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });



                                finish();
                            }
                            else{

                                Toast.makeText(RegisterActivity.this, "signup successfully",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        bindingView();
        bindingAction();
    }


}