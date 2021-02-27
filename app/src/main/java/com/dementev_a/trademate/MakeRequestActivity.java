package com.dementev_a.trademate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.requests.AsyncRequest;
import com.dementev_a.trademate.requests.RequestStatus;
import com.dementev_a.trademate.json.JsonEngine;
import com.dementev_a.trademate.json.OperatorJson;
import com.dementev_a.trademate.messages.EmailSending;
import com.dementev_a.trademate.messages.MessageSender;
import com.dementev_a.trademate.messages.StrategyMessage;
import com.dementev_a.trademate.preferences.SharedPreferencesEngine;
import com.dementev_a.trademate.requests.RequestEngine;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MakeRequestActivity extends AppCompatActivity {
    private Spinner spinner;
    private EditText nameET, textET;
    private ProgressBar progressBar;
    private TextView errorTV;
    private Map<String, String> operatorsMap;
    private static final int RESULT_SPEECH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_request);
        spinner = findViewById(R.id.make_request_activity_spinner);
        nameET = findViewById(R.id.make_request_activity_name_et);
        textET = findViewById(R.id.make_request_activity_text_et);
        progressBar = findViewById(R.id.make_request_activity_progress_bar);
        errorTV = findViewById(R.id.make_request_activity_error_tv);

        Parcelable[] operators = getIntent().getParcelableArrayExtra("operators");
        operatorsMap = new HashMap<>();
        for (Parcelable operator : operators) {
            OperatorJson operatorJson = (OperatorJson) operator;
            operatorsMap.put(operatorJson.getName(), operatorJson.getEmail());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, operatorsMap.keySet().toArray(new String[0]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

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
        new ConcurrentSendEmail().execute();
    }


    private class ConcurrentSendEmail implements AsyncRequest {
        private final Bundle bundle;

        protected ConcurrentSendEmail() {
            bundle = new Bundle();
        }

        @Override
        public void execute() {
            Executor executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                sendRequest();
                handler.post(() -> {
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                    int status = bundle.getInt("status");
                    switch (status) {
                        case RequestStatus.STATUS_OK: {
                            finish();
                        } break;
                        case RequestStatus.STATUS_INTERNET_ERROR: {
                            errorTV.setText(R.string.global_errors_internet_connection_error_text);
                        } break;
                        case RequestStatus.STATUS_SERVER_ERROR: {
                            errorTV.setText(R.string.global_errors_server_error_text);
                        } break;
                        case RequestStatus.STATUS_EMPTY_FIELDS: {
                            errorTV.setText(R.string.global_errors_empty_fields_error_text);
                        } break;
                    }
                });
            });
        }

        @Override
        public void sendRequest() {

            if (TextUtils.isEmpty(nameET.getText()) || TextUtils.isEmpty(textET.getText())) {
                bundle.putInt("status", RequestStatus.STATUS_EMPTY_FIELDS);
                return;
            }

            if (!RequestEngine.isConnectedToInternet(MakeRequestActivity.this)) {
                bundle.putInt("status", RequestStatus.STATUS_INTERNET_ERROR);
                return;
            }

            try {
                String subject = nameET.getText().toString();
                String text = textET.getText().toString();
                String operator = spinner.getSelectedItem().toString();

                String to = operatorsMap.get(operator);
                MessageSender sender = new MessageSender();
                sender.setMethod(new EmailSending());
                sender.send(new StrategyMessage(to, subject, text));

                String url = API.MAIN_URL + API.CREATE_REQUEST_URL;
                String json = String.format("{\"subject\": \"%s\"," +
                                "\"text\": \"%s\"," +
                                "\"operator\": \"%s\"," +
                                "\"dateTime\": \"%s\"}",
                        subject, text.replaceAll("\n", "\r\n"), operator, LocalDateTime.now().toString());
                String accessToken = new SharedPreferencesEngine(MakeRequestActivity.this, getString(R.string.shared_preferences_user)).getString("accessToken");
                Map<String, String> headers = new HashMap<>();
                headers.put("access_token", accessToken);
                String response = RequestEngine.makePostRequestWithJson(url, json, headers);
                if (response != null) {
                    JsonEngine jsonEngine = new JsonEngine();
                    String message = jsonEngine.getStringFromJson(response, "message");
                    if ("Success".equals(message)) {
                        bundle.putInt("status", RequestStatus.STATUS_OK);
                    } else {
                        bundle.putInt("status", RequestStatus.STATUS_SERVER_ERROR);
                    }
                } else
                    bundle.putInt("status", RequestStatus.STATUS_SERVER_ERROR);
            } catch (Exception e) {
                bundle.putInt("status", RequestStatus.STATUS_SERVER_ERROR);
            }
        }
    }
}