package com.dementev_a.trademate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AboutMerchandiserActivity extends AppCompatActivity {
    private TextView headerTV, emailTV, passwordTV;
    private Button passwordBtn;
    private boolean passwordIsShowed;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_merchandiser);
        headerTV = findViewById(R.id.about_merchandiser_activity_header_tv);
        emailTV = findViewById(R.id.about_merchandiser_activity_email_tv);
        passwordTV = findViewById(R.id.about_merchandiser_activity_password_tv);
        passwordBtn = findViewById(R.id.about_merchandiser_activity_password_btn);

        headerTV.setText(getIntent().getStringExtra("name"));
        String emailText = getString(R.string.about_merchandiser_activity_email_tv_text);
        emailTV.setText(String.format(emailText, getIntent().getStringExtra("email")));
        password = getIntent().getStringExtra("password");
        passwordIsShowed = false;
        showPassword();
    }

    private void showPassword() {
        String passwordText = getString(R.string.about_merchandiser_activity_password_tv_text);
        if (passwordIsShowed) {
            passwordTV.setText(String.format(passwordText, password));
            passwordBtn.setText(R.string.about_merchandiser_activity_password_btn_hide_text);
        } else {
            StringBuilder dotes = new StringBuilder();
            for (int i = 0; i < password.length(); i++)
                dotes.append("*");
            passwordTV.setText(String.format(passwordText, dotes.toString()));
            passwordBtn.setText(R.string.about_merchandiser_activity_password_btn_show_text);
        }
    }

    public void onPasswordBtnClick(View v) {
        passwordIsShowed = !passwordIsShowed;
        showPassword();
    }
}