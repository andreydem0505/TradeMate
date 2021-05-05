package com.dementev_a.trademate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;

import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.preferences.SharedPreferencesEngine;
import com.dementev_a.trademate.widgets.ReactOnStatus;

public class CompanySettingsActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private API api;
    private String accessToken, name, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_TradeMate);
        setContentView(R.layout.activity_company_settings);
        progressBar = findViewById(R.id.settings_company_activity_progress_bar);
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case API.ADD_MERCHANDISER_HANDLER_NUMBER: {
                    api.logInMerchandiser(email, password);
                } break;
                case API.LOG_IN_MERCHANDISER_HANDLER_NUMBER: {
                    new ReactOnStatus(bundle) {
                        @Override
                        public void success() {
                            progressBar.setVisibility(ProgressBar.INVISIBLE);
                            finish();
                        }

                        @Override
                        public void failure() {
                            api.addMerchandiser(accessToken, name, email, password);
                        }
                    }.execute();
                } break;
            }
        }
    };

    public void onBecomeMerchandiserBtnClick(View v) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        api = new API(this, handler);
        SharedPreferencesEngine spe = new SharedPreferencesEngine(this, getString(R.string.shared_preferences_user));
        accessToken = spe.getString(SharedPreferencesEngine.ACCESS_TOKEN_KEY);
        name = spe.getString(SharedPreferencesEngine.NAME_KEY);
        email = spe.getString(SharedPreferencesEngine.EMAIL_KEY);
        password = spe.getString(SharedPreferencesEngine.PASSWORD_KEY);
        api.logInMerchandiser(email, password);
    }
}