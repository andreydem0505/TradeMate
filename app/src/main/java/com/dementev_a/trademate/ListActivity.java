package com.dementev_a.trademate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dementev_a.trademate.json.MerchandiserJson;
import com.dementev_a.trademate.json.OperatorJson;
import com.dementev_a.trademate.json.RequestJson;


public class ListActivity extends AppCompatActivity {
    private ListView listView;
    private TextView headerTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        headerTV = findViewById(R.id.list_activity_header_tv);
        listView = findViewById(R.id.list_activity_list);

        String type = getIntent().getStringExtra("type");
        switch (type) {
            case "requests": {
                headerTV.setText(R.string.list_activity_header_requests_text);
                Parcelable[] requests = getIntent().getParcelableArrayExtra("requests");
                RequestJson[] arrayOfRequestJson = new RequestJson[requests.length];
                String[] arrayNames = new String[requests.length];
                for (int i = 0; i < requests.length; i++) {
                    RequestJson requestJson = (RequestJson) requests[i];
                    arrayOfRequestJson[i] = requestJson;
                    arrayNames[i] = requestJson.getSubject();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayNames);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener((parent, view, position, id) -> {
                    Intent intent = new Intent(this, AboutRequestActivity.class);
                    intent.putExtra("subject", arrayOfRequestJson[position].getSubject());
                    intent.putExtra("text", arrayOfRequestJson[position].getText());
                    intent.putExtra("receiver", arrayOfRequestJson[position].getOperator());
                    intent.putExtra("dateTime", arrayOfRequestJson[position].getDateTime());
                    startActivity(intent);
                });
            } break;
            case "merchandisers": {
                String header = getString(R.string.list_activity_header_merchandisers_text);
                headerTV.setText(String.format(header, getIntent().getStringExtra("companyName")));
                Parcelable[] merchandisers = getIntent().getParcelableArrayExtra("merchandisers");
                MerchandiserJson[] arrayOfMerchandiserJson = new MerchandiserJson[merchandisers.length];
                String[] arrayNames = new String[merchandisers.length];
                for (int i = 0; i < merchandisers.length; i++) {
                    MerchandiserJson merchandiserJson = (MerchandiserJson) merchandisers[i];
                    arrayOfMerchandiserJson[i] = merchandiserJson;
                    arrayNames[i] = merchandiserJson.getName();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayNames);
                listView.setAdapter(adapter);
            } break;
            case "operators": {
                String header = getString(R.string.list_activity_header_operators_text);
                headerTV.setText(String.format(header, getIntent().getStringExtra("companyName")));
                Parcelable[] operators = getIntent().getParcelableArrayExtra("operators");
                OperatorJson[] arrayOfOperatorJson = new OperatorJson[operators.length];
                String[] arrayNames = new String[operators.length];
                for (int i = 0; i < operators.length; i++) {
                    OperatorJson operatorJson = (OperatorJson) operators[i];
                    arrayOfOperatorJson[i] = operatorJson;
                    arrayNames[i] = operatorJson.getName();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayNames);
                listView.setAdapter(adapter);
            } break;
        }
    }
}