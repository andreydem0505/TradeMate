package com.dementev_a.trademate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.preferences.SharedPreferencesEngine;
import com.dementev_a.trademate.widgets.ReactOnStatus;

public class MerchandiserSettingsActivity extends AppCompatActivity {
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_TradeMate);
        setContentView(R.layout.activity_merchandiser_settings);
        progressBar = findViewById(R.id.settings_merchandiser_activity_progress_bar);
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            Bundle bundle = msg.getData();
            new ReactOnStatus(bundle) {
                @Override
                public void success() {
                    finish();
                }

                @Override
                public void failure() {
                    Toast.makeText(MerchandiserSettingsActivity.this, R.string.settings_merchandiser_activity_no_company_error, Toast.LENGTH_SHORT).show();
                }
            }.execute();
        }
    };

    public void onBecomeMerchandiserBtnClick(View v) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        API api = new API(this, handler);
        SharedPreferencesEngine spe = new SharedPreferencesEngine(this, getString(R.string.shared_preferences_user));
        String email = spe.getString(SharedPreferencesEngine.EMAIL_KEY);
        String password = spe.getString(SharedPreferencesEngine.PASSWORD_KEY);
        api.logInCompany(email, password);
    }
}