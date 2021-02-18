package com.dementev_a.trademate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class AboutMerchandiserActivity extends AppCompatActivity {
    private TextView headerTV, emailTV, passwordTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_merchandiser);
        headerTV = findViewById(R.id.about_merchandiser_activity_header_tv);
        emailTV = findViewById(R.id.about_merchandiser_activity_email_tv);
        passwordTV = findViewById(R.id.about_merchandiser_activity_password_tv);


    }
}