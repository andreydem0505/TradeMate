package com.dementev_a.trademate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.dementev_a.trademate.intent.IntentConstants;
import com.dementev_a.trademate.widgets.WidgetsEngine;


public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_TradeMate);
        setContentView(R.layout.activity_list);
        TextView headerTV = findViewById(R.id.list_activity_header_tv);
        ListView listView = findViewById(R.id.list_activity_list);
        TextView errorTV = findViewById(R.id.list_activity_error_tv);

        int type = getIntent().getIntExtra(IntentConstants.TYPE_INTENT_KEY, IntentConstants.DEFAULT_DATA_TYPE);
        switch (type) {
            case IntentConstants.REQUESTS_DATA_TYPE: {
                headerTV.setText(R.string.list_activity_header_requests_text);
                WidgetsEngine.setRequestsOnListView(getIntent().getParcelableArrayExtra(IntentConstants.REQUESTS_INTENT_KEY), listView, this, errorTV);
            } break;
            case IntentConstants.MERCHANDISERS_DATA_TYPE: {
                String header = getString(R.string.list_activity_header_merchandisers_text);
                headerTV.setText(String.format(header, getIntent().getStringExtra(IntentConstants.COMPANY_NAME_INTENT_KEY)));
                WidgetsEngine.setMerchandisersOnListView(getIntent().getParcelableArrayExtra(IntentConstants.MERCHANDISERS_INTENT_KEY), listView, this, errorTV);
            } break;
            case IntentConstants.OPERATORS_DATA_TYPE: {
                String header = getString(R.string.list_activity_header_operators_text);
                headerTV.setText(String.format(header, getIntent().getStringExtra(IntentConstants.COMPANY_NAME_INTENT_KEY)));
                WidgetsEngine.setOperatorsOnListView(
                        getIntent().getStringArrayExtra(IntentConstants.NAMES_OF_OPERATORS_INTENT_KEY),
                        getIntent().getStringArrayExtra(IntentConstants.EMAILS_OF_OPERATORS_INTENT_KEY),
                        listView, this, errorTV);
            } break;
            case IntentConstants.SHOPS_DATA_TYPE: {
                headerTV.setText(R.string.list_activity_header_shops_text);
                WidgetsEngine.setShopsOnListView(getIntent().getStringArrayExtra(IntentConstants.SHOPS_INTENT_KEY), listView, this, errorTV);
            } break;
            case IntentConstants.PHOTO_REPORTS_DATA_TYPE: {
                headerTV.setText(R.string.list_activity_header_photo_reports_text);
                WidgetsEngine.setPhotoReportsOnListView(getIntent().getStringArrayExtra(IntentConstants.PHOTO_REPORTS_INTENT_KEY), listView, this, errorTV);
            } break;
        }
    }
}