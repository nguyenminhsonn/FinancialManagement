package com.fptu.android.financialmanagement.analysis;

import androidx.appcompat.app.AppCompatActivity;

import com.fptu.android.financialmanagement.R;
import android.os.Bundle;

public class ChooseAnalyticActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_analytic);
        getSupportActionBar().setTitle("Analytics");
    }
}