package com.dementev_a.trademate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.dementev_a.trademate.api.RequestStatus;
import com.dementev_a.trademate.json.OperatorJson;
import com.dementev_a.trademate.messages.EmailSending;
import com.dementev_a.trademate.messages.MessageSender;
import com.dementev_a.trademate.messages.StrategyMessage;
import com.dementev_a.trademate.requests.RequestEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
                    textET.append(resultText);
                }
            }
        }
    }

    public void onSendBtnClick(View v) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        new SendEmail().execute();
    }

    private class SendEmail extends AsyncTask<Void, Void, Bundle> {
        @Override
        protected Bundle doInBackground(Void... voids) {
            Bundle bundle = new Bundle();

            if (TextUtils.isEmpty(nameET.getText()) || TextUtils.isEmpty(textET.getText())) {
                bundle.putInt("status", RequestStatus.STATUS_EMPTY_FIELDS);
                return bundle;
            }

            if (!RequestEngine.isConnectedToInternet(MakeRequestActivity.this)) {
                bundle.putInt("status", RequestStatus.STATUS_INTERNET_ERROR);
                return bundle;
            }

            try {
                String to = operatorsMap.get(spinner.getSelectedItem().toString());
                MessageSender sender = new MessageSender();
                sender.setMethod(new EmailSending());
                sender.send(new StrategyMessage(to, nameET.getText().toString(), textET.getText().toString()));
                bundle.putInt("status", RequestStatus.STATUS_OK);
            } catch (Exception e) {
                bundle.putInt("status", RequestStatus.STATUS_SERVER_ERROR);
            }

            return bundle;
        }

        @Override
        protected void onPostExecute(Bundle bundle) {
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
        }
    }
}