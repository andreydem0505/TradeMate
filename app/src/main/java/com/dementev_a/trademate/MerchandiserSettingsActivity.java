package com.dementev_a.trademate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.preferences.SharedPreferencesEngine;
import com.dementev_a.trademate.widgets.ReactOnStatus;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MerchandiserSettingsActivity extends AppCompatActivity {
    private LinearLayout panel1;
    private ProgressBar progressBar;
    private RadioButton radioButton1, radioButton2;
    private SharedPreferencesEngine spe;
    private boolean layoutHasBtn, layoutHasET;
    private String keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_TradeMate);
        setContentView(R.layout.activity_merchandiser_settings);
        progressBar = findViewById(R.id.settings_merchandiser_activity_progress_bar);
        panel1 = findViewById(R.id.settings_merchandiser_activity_panel_1);
        radioButton1 = findViewById(R.id.settings_merchandiser_activity_radio_btn_1);
        radioButton2 = findViewById(R.id.settings_merchandiser_activity_radio_btn_2);
        spe = new SharedPreferencesEngine(this, getString(R.string.shared_preferences_user));
        layoutHasBtn = false;
        layoutHasET = false;
        if (spe.containsKey(SharedPreferencesEngine.KEYWORD_KEY)) {
            radioButton2.setChecked(true);
            keyword = spe.getString(SharedPreferencesEngine.KEYWORD_KEY);
            radioButton2.setText(String.format(getString(R.string.settings_merchandiser_activity_radio_2_text), keyword));
            addBtnToChangeKeyword();
        } else {
            radioButton1.setChecked(true);
        }
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

    public void onRadioBtn1Click(View v) {
        if (spe.containsKey(SharedPreferencesEngine.KEYWORD_KEY)) {
            spe.removeKey(SharedPreferencesEngine.KEYWORD_KEY);
        }
    }

    public void onRadioBtn2Click(View v) {
        if (!layoutHasBtn && !layoutHasET) {
            layoutHasET = true;
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            LayoutInflater inflater = LayoutInflater.from(this);
            EditText editText = (EditText) inflater.inflate(R.layout.merchandiser_settings_panel_1_edit_text, linearLayout, false);
            linearLayout.addView(editText);
            FloatingActionButton floatingActionButton = (FloatingActionButton) inflater.inflate(R.layout.merchandiser_settings_panel_1_floating_action_btn, linearLayout, false);
            linearLayout.addView(floatingActionButton);
            panel1.addView(linearLayout);
            radioButton1.setOnClickListener(v1 -> {
                layoutHasET = false;
                panel1.removeView(linearLayout);
            });
            floatingActionButton.setOnClickListener(v1 -> {
                if (TextUtils.isEmpty(editText.getText().toString().trim())) {
                    Toast.makeText(this, R.string.settings_merchandiser_activity_panel_1_no_text_error, Toast.LENGTH_SHORT).show();
                } else {
                    keyword = editText.getText().toString();
                    spe.putString(SharedPreferencesEngine.KEYWORD_KEY, keyword);
                    panel1.removeView(linearLayout);
                    radioButton1.setOnClickListener(this::onRadioBtn1Click);
                    radioButton2.setText(String.format(getString(R.string.settings_merchandiser_activity_radio_2_text), keyword));
                    addBtnToChangeKeyword();
                }
            });
        } else {
            spe.putString(SharedPreferencesEngine.KEYWORD_KEY, keyword);
        }
    }

    private void addBtnToChangeKeyword() {
        layoutHasBtn = true;
        LayoutInflater inflater = LayoutInflater.from(this);
        Button button = (Button) inflater.inflate(R.layout.merchandiser_settings_panel_1_button, panel1, false);
        panel1.addView(button);
        button.setOnClickListener(v -> {
            layoutHasBtn = false;
            layoutHasET = false;
            onRadioBtn2Click(v);
            panel1.removeView(button);
        });
    }
}