package com.dementev_a.trademate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.bundle.BundleEngine;
import com.dementev_a.trademate.intent.IntentConstants;
import com.dementev_a.trademate.requests.RequestSender;
import com.dementev_a.trademate.requests.RequestStatus;
import com.dementev_a.trademate.widgets.ReactOnStatus;

public class AddMerchandiserActivity extends AppCompatActivity {
    private EditText nameET, emailET, passwordET;
    private Button button;
    private ProgressBar progressBar;
    private TextView errorTV;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_TradeMate);
        setContentView(R.layout.activity_add_merchandiser);
        nameET = findViewById(R.id.add_merchandiser_activity_name_et);
        emailET = findViewById(R.id.add_merchandiser_activity_email_et);
        passwordET = findViewById(R.id.add_merchandiser_activity_password_et);
        progressBar = findViewById(R.id.add_merchandiser_activity_progress_bar);
        errorTV = findViewById(R.id.add_merchandiser_activity_error_tv);
        button = findViewById(R.id.add_merchandiser_activity_button);
        accessToken = getIntent().getStringExtra(IntentConstants.ACCESS_TOKEN_INTENT_KEY);
    }

    public void onAddMerchandiserClickBtn(View v) {
        button.setOnClickListener(null);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        if (TextUtils.isEmpty(nameET.getText().toString().trim()) || TextUtils.isEmpty(emailET.getText().toString().trim()) || TextUtils.isEmpty(passwordET.getText().toString().trim())) {
            Bundle bundle = new Bundle();
            bundle.putInt(BundleEngine.STATUS_KEY_BUNDLE, RequestStatus.STATUS_EMPTY_FIELDS);
            new RequestSender().sendHandlerMessage(bundle, handler, API.ADD_MERCHANDISER_HANDLER_NUMBER);
        } else {
            API api = new API(this, handler);
            api.addMerchandiser(accessToken, nameET.getText().toString(), emailET.getText().toString(), passwordET.getText().toString());
        }
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            Bundle bundle = msg.getData();
            new ReactOnStatus(bundle, errorTV) {
                @Override
                public void success() {
                    finish();
                }

                @Override
                public void failure() {
                    super.failure();
                    button.setOnClickListener(AddMerchandiserActivity.this::onAddMerchandiserClickBtn);
                }
            }.execute();
        }
    };
}