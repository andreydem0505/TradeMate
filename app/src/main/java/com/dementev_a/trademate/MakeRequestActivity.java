package com.dementev_a.trademate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Parcelable;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.dementev_a.trademate.json.OperatorJson;

import java.util.Arrays;

public class MakeRequestActivity extends AppCompatActivity {
    private Spinner spinner;
    private String[] namesOfOperators, emailsOfOperators;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_request);
        spinner = findViewById(R.id.make_request_activity_spinner);

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
}