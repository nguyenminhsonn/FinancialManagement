package com.fptu.android.financialmanagement.Login;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Account{
    private static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static FirebaseUser USER = firebaseAuth.getCurrentUser();

    public String password;
    public String username;
    private String Phone;
    public Account(){

    }

    public Account(String password, String username, String phone) {
        this.password = password;
        this.username = username;
        Phone = phone;

    }

    public Account(String userName, String Password) {
        password = Password;
        username = userName;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}