package com.dementev_a.trademate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.bundle.BundleEngine;
import com.dementev_a.trademate.intent.IntentConstants;
import com.dementev_a.trademate.preferences.SharedPreferencesEngine;
import com.dementev_a.trademate.widgets.ReactOnStatus;
import com.dementev_a.trademate.widgets.WidgetsEngine;

public class AboutMerchandiserActivity extends AppCompatActivity {
    private TextView passwordTV, errorTV;
    private Button passwordBtn;
    private ListView listView;
    private ProgressBar progressBar;
    private boolean passwordIsShowed;
    private String password, merchandiserName, accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_merchandiser);
        TextView headerTV = findViewById(R.id.about_merchandiser_activity_header_tv);
        TextView emailTV = findViewById(R.id.about_merchandiser_activity_email_tv);
        passwordTV = findViewById(R.id.about_merchandiser_activity_password_tv);
        passwordBtn = findViewById(R.id.about_merchandiser_activity_password_btn);
        listView = findViewById(R.id.about_merchandiser_activity_list_view);
        progressBar = findViewById(R.id.about_merchandiser_activity_progress_bar);
        errorTV = findViewById(R.id.about_merchandiser_activity_error_tv);

        accessToken = new SharedPreferencesEngine(this, getString(R.string.shared_preferences_user)).getString(SharedPreferencesEngine.ACCESS_TOKEN_KEY);
        merchandiserName = getIntent().getStringExtra(IntentConstants.NAME_OF_MERCHANDISER_INTENT_KEY);
        headerTV.setText(merchandiserName);
        String emailText = getString(R.string.about_merchandiser_activity_email_tv_text);
        emailTV.setText(String.format(emailText, getIntent().getStringExtra(IntentConstants.EMAIL_OF_MERCHANDISER_INTENT_KEY)));
        password = getIntent().getStringExtra(IntentConstants.PASSWORD_OF_MERCHANDISER_INTENT_KEY);
        passwordIsShowed = false;
        showPassword();
    }

    @Override
    protected void onResume() {
        super.onResume();
        errorTV.setText("");
        progressBar.setVisibility(ProgressBar.VISIBLE);
        API api = new API(this, handler);
        api.getRequestsToday(accessToken, merchandiserName);
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

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            Bundle bundle = msg.getData();
            new ReactOnStatus(bundle, errorTV) {
                @Override
                public void success() {
                    WidgetsEngine.setRequestsOnListView(bundle.getParcelableArray(BundleEngine.REQUESTS_KEY_BUNDLE), listView, AboutMerchandiserActivity.this, errorTV);
                }
            }.execute();
        }
    };
}