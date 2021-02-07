package com.dementev_a.trademate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.dementev_a.trademate.json.OperatorJson;

import java.util.ArrayList;
import java.util.Locale;

public class MakeRequestActivity extends AppCompatActivity {
    private Spinner spinner;
    private EditText nameET, textET;
    private String[] namesOfOperators, emailsOfOperators;
    private static final int RESULT_SPEECH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_request);
        spinner = findViewById(R.id.make_request_activity_spinner);
        nameET = findViewById(R.id.make_request_activity_name_et);
        textET = findViewById(R.id.make_request_activity_text_et);

        Parcelable[] operators = getIntent().getParcelableArrayExtra("operators");
        namesOfOperators = new String[operators.length];
        emailsOfOperators = new String[operators.length];
        for (int i = 0; i < operators.length; i++) {
            OperatorJson operatorJson = (OperatorJson) operators[i];
            namesOfOperators[i] = operatorJson.getName();
            emailsOfOperators[i] = operatorJson.getEmail();
        }

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
                    textET.setText(resultText);
                }
            }
        }
    }
}