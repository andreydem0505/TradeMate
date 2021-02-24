package com.dementev_a.trademate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ChoiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
    }

    public void onSignUpCompanyClickBtn(View v) {
        Intent intent = new Intent(ChoiceActivity.this, SignUpCompanyActivity.class);
        startActivity(intent);
    }

    public void onLogInCompanyClickBtn(View v) {
        Intent intent = new Intent(ChoiceActivity.this, LogInCompanyActivity.class);
        startActivity(intent);
    }

    public void onLogInMerchandiserClickBtn(View v) {
        Intent intent = new Intent(ChoiceActivity.this, LogInMerchandiserActivity.class);
        startActivity(intent);
    }
}