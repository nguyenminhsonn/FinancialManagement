package com.fptu.android.financialmanagement.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fptu.android.financialmanagement.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class changePass extends AppCompatActivity {
    private EditText edtUserName;
    private Button changBtn;
    private FirebaseAuth mAuth;

    public void bindingView() {
        edtUserName = findViewById(R.id.edtUserName);
        changBtn = findViewById(R.id.changBtn);
        mAuth = FirebaseAuth.getInstance();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        bindingView();
        changBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        String email = edtUserName.getText().toString().trim();
        if (email.isEmpty()) {
            edtUserName.setError("Email required");
            edtUserName.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtUserName.setError("Please provide a valid email!");
            edtUserName.requestFocus();
            return;
        }
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(changePass.this, "Please check your email", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(changePass.this, LoginActivity.class));
                } else
                    Toast.makeText(changePass.this, "some thing went wrong!Please try again!", Toast.LENGTH_LONG).show();
            }
        });
    }
}