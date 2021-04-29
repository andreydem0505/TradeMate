package com.dementev_a.trademate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.bundle.BundleEngine;
import com.dementev_a.trademate.intent.IntentConstants;
import com.dementev_a.trademate.requests.RequestSender;
import com.dementev_a.trademate.requests.RequestStatus;
import com.dementev_a.trademate.widgets.ReactOnStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class MakeRequestActivity extends AppCompatActivity {
    private Spinner spinner;
    private AutoCompleteTextView nameET;
    private EditText textET;
    private ProgressBar progressBar;
    private TextView errorTV;
    private String[] shops, namesOfOperators, emailsOfOperators;
    private String merchandiserName, accessToken;
    private final int RESULT_SPEECH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_TradeMate);
        setContentView(R.layout.activity_make_request);
        spinner = findViewById(R.id.make_request_activity_spinner);
        nameET = findViewById(R.id.make_request_activity_name_et);
        textET = findViewById(R.id.make_request_activity_text_et);
        progressBar = findViewById(R.id.make_request_activity_progress_bar);
        errorTV = findViewById(R.id.make_request_activity_error_tv);

        merchandiserName = getIntent().getStringExtra(IntentConstants.NAME_OF_MERCHANDISER_INTENT_KEY);
        accessToken = getIntent().getStringExtra(IntentConstants.ACCESS_TOKEN_INTENT_KEY);

        API api = new API(this, handler);
        api.getOperators(accessToken);
        api.getShops(accessToken);
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            new ReactOnStatus(bundle, errorTV) {
                @Override
                public void success() {
                    switch (msg.what) {
                        case API.GET_OPERATORS_HANDLER_NUMBER: {
                            namesOfOperators = bundle.getStringArray(BundleEngine.NAMES_OF_OPERATORS_KEY_BUNDLE);
                            emailsOfOperators = bundle.getStringArray(BundleEngine.EMAILS_OF_OPERATORS_KEY_BUNDLE);
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(MakeRequestActivity.this, android.R.layout.simple_spinner_item, namesOfOperators);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(adapter);
                        } break;
                        case API.GET_SHOPS_HANDLER_NUMBER: {
                            shops = bundle.getStringArray(BundleEngine.SHOPS_KEY_BUNDLE);
                            nameET.setAdapter(new ArrayAdapter<>(MakeRequestActivity.this, android.R.layout.simple_dropdown_item_1line, shops));
                        } break;
                        case API.SEND_EMAIL_HANDLER_NUMBER: {
                            finish();
                        } break;
                    }
                }
            }.execute();
        }
    };

    public void onSpeakBtnClick(View v) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault());
        try {
            startActivityForResult(intent, RESULT_SPEECH);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), getText(R.string.make_request_activity_text_was_not_recognised_error_text), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_SPEECH) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String resultText = text.get(0);
                if (TextUtils.isEmpty(nameET.getText())) {
                    nameET.setText(resultText);
                } else {
                    textET.append(resultText + "\n");
                }
            }
        }
    }

    public void onSendBtnClick(View v) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        if (TextUtils.isEmpty(nameET.getText()) || TextUtils.isEmpty(textET.getText())) {
            Bundle bundle = new Bundle();
            bundle.putInt(BundleEngine.STATUS_KEY_BUNDLE, RequestStatus.STATUS_EMPTY_FIELDS);
            new RequestSender().sendHandlerMessage(bundle, handler, API.SEND_EMAIL_HANDLER_NUMBER);
        } else {
            String shop = nameET.getText().toString();
            if (Arrays.binarySearch(shops, shop) < 0) {
                Bundle bundle = new Bundle();
                BundleEngine.putError(bundle, R.string.make_request_activity_shop_was_not_found_error_text);
                new RequestSender().sendHandlerMessage(bundle, handler, API.SEND_EMAIL_HANDLER_NUMBER);
            } else {
                int operatorPosition = spinner.getSelectedItemPosition();
                String operatorName = namesOfOperators[operatorPosition];
                String operatorEmail = emailsOfOperators[operatorPosition];
                API api = new API(this, handler);
                api.sendEmail(accessToken, nameET.getText().toString(), textET.getText().toString(), merchandiserName, operatorName, operatorEmail);
            }
        }
    }
}