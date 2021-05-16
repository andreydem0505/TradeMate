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
import com.dementev_a.trademate.intent.IntentConstants;
import com.dementev_a.trademate.requests.RequestSender;
import com.dementev_a.trademate.requests.RequestStatus;
import com.dementev_a.trademate.widgets.ReactOnStatus;

public class AddOperatorActivity extends AppCompatActivity {
    private EditText nameET, emailET;
    private ProgressBar progressBar;
    private TextView errorTV;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_TradeMate);
        setContentView(R.layout.activity_add_operator);
        nameET = findViewById(R.id.add_operator_activity_name_et);
        emailET = findViewById(R.id.add_operator_activity_email_et);
        progressBar = findViewById(R.id.add_operator_activity_progress_bar);
        errorTV = findViewById(R.id.add_operator_activity_error_tv);
        accessToken = getIntent().getStringExtra(IntentConstants.ACCESS_TOKEN_INTENT_KEY);
    }

    public void onAddClickBtn(View v) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        if (TextUtils.isEmpty(nameET.getText().toString().trim()) || TextUtils.isEmpty(emailET.getText().toString().trim())) {
            Bundle bundle = new Bundle();
            bundle.putInt(BundleEngine.STATUS_KEY_BUNDLE, RequestStatus.STATUS_EMPTY_FIELDS);
            new RequestSender().sendHandlerMessage(bundle, handler, API.ADD_OPERATOR_HANDLER_NUMBER);
        } else {
            API api = new API(this, handler);
            api.addOperator(accessToken, nameET.getText().toString(), emailET.getText().toString());
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