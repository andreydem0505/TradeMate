package com.dementev_a.trademate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
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
import com.dementev_a.trademate.requests.AsyncRequest;
import com.dementev_a.trademate.requests.RequestStatus;
import com.dementev_a.trademate.json.JsonEngine;
import com.dementev_a.trademate.messages.EmailSending;
import com.dementev_a.trademate.messages.MessageSender;
import com.dementev_a.trademate.messages.StrategyMessage;
import com.dementev_a.trademate.preferences.SharedPreferencesEngine;
import com.dementev_a.trademate.requests.RequestEngine;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MakeRequestActivity extends AppCompatActivity {
    private Spinner spinner;
    private AutoCompleteTextView nameET;
    private EditText textET;
    private ProgressBar progressBar;
    private TextView errorTV;
    private String[] shops, namesOfOperators, emailsOfOperators;
    private String merchandiserName;
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

        merchandiserName = getIntent().getStringExtra("merchandiserName");
        namesOfOperators = getIntent().getStringArrayExtra("namesOfOperators");
        emailsOfOperators = getIntent().getStringArrayExtra("emailsOfOperators");
        shops = getIntent().getStringArrayExtra("shops");
        nameET.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, shops));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, namesOfOperators);
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


    private class ConcurrentSendEmail extends AsyncRequest {

        protected ConcurrentSendEmail() {
            super();
        }

        @Override
        public void UIWork() {
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            int status = getBundle().getInt("status");
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
                case RequestStatus.STATUS_ERROR_TEXT: {
                    errorTV.setText(getBundle().getInt("error_text"));
                } break;
            }
        }

        @Override
        public void sendRequest() {
            if (TextUtils.isEmpty(nameET.getText()) || TextUtils.isEmpty(textET.getText())) {
                getBundle().putInt("status", RequestStatus.STATUS_EMPTY_FIELDS);
                return;
            }

            if (!RequestEngine.isConnectedToInternet(MakeRequestActivity.this)) {
                getBundle().putInt("status", RequestStatus.STATUS_INTERNET_ERROR);
                return;
            }

            try {
                String shop = nameET.getText().toString();
                String text = textET.getText().toString();
                int operatorPosition = spinner.getSelectedItemPosition();

                if (Arrays.binarySearch(shops, shop) < 0) {
                    BundleEngine.putError(getBundle(), R.string.make_request_activity_shop_was_not_found_error_text);
                    return;
                }

                String operatorName = namesOfOperators[operatorPosition];
                String operatorEmail = emailsOfOperators[operatorPosition];
                System.out.println(operatorName + " " + operatorEmail);
                MessageSender sender = new MessageSender();
                sender.setMethod(new EmailSending());
                sender.send(new StrategyMessage(operatorEmail, shop, text, String.format(getString(R.string.make_request_activity_letter_merchandiser_text), merchandiserName)));

                String url = API.MAIN_URL + API.CREATE_REQUEST_URL;
                String json = String.format("{\"subject\": \"%s\"," +
                                "\"text\": \"%s\"," +
                                "\"operator\": \"%s\"," +
                                "\"dateTime\": \"%s\"}",
                        shop, text.replaceAll("\n", "\r\n"), operatorName, LocalDateTime.now().toString());
                String accessToken = new SharedPreferencesEngine(MakeRequestActivity.this, getString(R.string.shared_preferences_user)).getString("accessToken");
                Map<String, String> headers = new HashMap<>();
                headers.put("access_token", accessToken);
                String response = RequestEngine.makePostRequestWithJson(url, json, headers);
                if (response != null) {
                    String message = JsonEngine.getStringFromJson(response, "message");
                    if ("Success".equals(message)) {
                        getBundle().putInt("status", RequestStatus.STATUS_OK);
                    } else {
                        getBundle().putInt("status", RequestStatus.STATUS_SERVER_ERROR);
                    }
                } else
                    getBundle().putInt("status", RequestStatus.STATUS_SERVER_ERROR);
            } catch (Exception e) {
                getBundle().putInt("status", RequestStatus.STATUS_SERVER_ERROR);
            }
        }
    }
}