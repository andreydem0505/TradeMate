package com.dementev_a.trademate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dementev_a.trademate.intent.IntentConstants;
import com.dementev_a.trademate.json.MerchandiserJson;
import com.dementev_a.trademate.widgets.WidgetsEngine;


public class ListActivity extends AppCompatActivity {
    private int lastOperatorNameEdit = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        TextView headerTV = findViewById(R.id.list_activity_header_tv);
        ListView listView = findViewById(R.id.list_activity_list);
        TextView errorTV = findViewById(R.id.list_activity_error_tv);

        int type = getIntent().getIntExtra(IntentConstants.TYPE_INTENT_KEY, IntentConstants.DEFAULT_DATA_TYPE);
        switch (type) {
            case IntentConstants.REQUESTS_DATA_TYPE: {
                headerTV.setText(R.string.list_activity_header_requests_text);
                WidgetsEngine.setRequestsOnListView(getIntent().getParcelableArrayExtra("requests"), listView, this, errorTV);
            } break;
            case IntentConstants.MERCHANDISERS_DATA_TYPE: {
                String header = getString(R.string.list_activity_header_merchandisers_text);
                headerTV.setText(String.format(header, getIntent().getStringExtra("companyName")));
                Parcelable[] merchandisers = getIntent().getParcelableArrayExtra("merchandisers");
                if (merchandisers.length > 0) {
                    MerchandiserJson[] arrayOfMerchandiserJson = new MerchandiserJson[merchandisers.length];
                    String[] arrayNames = new String[merchandisers.length];
                    for (int i = 0; i < merchandisers.length; i++) {
                        MerchandiserJson merchandiserJson = (MerchandiserJson) merchandisers[i];
                        arrayOfMerchandiserJson[i] = merchandiserJson;
                        arrayNames[i] = merchandiserJson.getName();
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayNames);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener((parent, view, position, id) -> {
                        Intent intent = new Intent(this, AboutMerchandiserActivity.class);
                        intent.putExtra(IntentConstants.NAME_OF_MERCHANDISER_INTENT_KEY, arrayOfMerchandiserJson[position].getName());
                        intent.putExtra(IntentConstants.EMAIL_OF_MERCHANDISER_INTENT_KEY, arrayOfMerchandiserJson[position].getEmail());
                        intent.putExtra(IntentConstants.PASSWORD_OF_MERCHANDISER_INTENT_KEY, arrayOfMerchandiserJson[position].getPassword());
                        startActivity(intent);
                    });
                } else
                    errorTV.setText(R.string.list_activity_error_tv_merchandisers_text);
            } break;
            case IntentConstants.OPERATORS_DATA_TYPE: {
                String header = getString(R.string.list_activity_header_operators_text);
                headerTV.setText(String.format(header, getIntent().getStringExtra("companyName")));
                String[] names = getIntent().getStringArrayExtra("namesOfOperators");
                String[] emails = getIntent().getStringArrayExtra("emailsOfOperators");
                if (names.length > 0) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener((parent, view, position, id) -> {
                        names[lastOperatorNameEdit] = names[lastOperatorNameEdit].replaceAll("\n.+", "");
                        names[position] = names[position] + "\n" + emails[position];
                        lastOperatorNameEdit = position;
                        adapter.notifyDataSetChanged();
                    });
                } else
                    errorTV.setText(R.string.list_activity_error_tv_operators_text);
            } break;
            case IntentConstants.SHOPS_DATA_TYPE: {
                headerTV.setText(R.string.list_activity_header_shops_text);
                String[] shops = getIntent().getStringArrayExtra("shops");
                if (shops.length > 0) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, shops);
                    listView.setAdapter(adapter);
                } else
                    errorTV.setText(R.string.list_activity_error_tv_shops_text);
            } break;
        }
    }
}