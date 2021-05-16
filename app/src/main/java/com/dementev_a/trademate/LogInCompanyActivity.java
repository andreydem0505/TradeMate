package com.dementev_a.trademate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.bundle.BundleEngine;
import com.dementev_a.trademate.requests.RequestSender;
import com.dementev_a.trademate.requests.RequestStatus;
import com.dementev_a.trademate.widgets.ReactOnStatus;

public class LogInCompanyActivity extends AppCompatActivity {
    private EditText emailET, passwordET;
    private ProgressBar progressBar;
    private TextView errorTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_TradeMate);
        setContentView(R.layout.activity_log_in_company);
        emailET = findViewById(R.id.log_in_company_activity_email_et);
        passwordET = findViewById(R.id.log_in_company_activity_password_et);
        progressBar = findViewById(R.id.log_in_company_activity_progress_bar);
        errorTV = findViewById(R.id.log_in_company_activity_error_tv);
    }

    public void onLogInClickBtn(View v) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        if (TextUtils.isEmpty(emailET.getText().toString().trim()) || TextUtils.isEmpty(passwordET.getText().toString().trim())) {
            Bundle bundle = new Bundle();
            bundle.putInt(BundleEngine.STATUS_KEY_BUNDLE, RequestStatus.STATUS_EMPTY_FIELDS);
            new RequestSender().sendHandlerMessage(bundle, handler, API.LOG_IN_COMPANY_HANDLER_NUMBER);
        } else {
            API api = new API(this, handler);
            api.logInCompany(emailET.getText().toString(), passwordET.getText().toString());
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
            }.execute();
        }
    };
}