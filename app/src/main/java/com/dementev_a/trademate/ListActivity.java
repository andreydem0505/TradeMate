package com.dementev_a.trademate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.dementev_a.trademate.intent.IntentConstants;
import com.dementev_a.trademate.widgets.ArrayAdapterListView;
import com.dementev_a.trademate.widgets.WidgetsEngine;


public class ListActivity extends AppCompatActivity {
    private TextView headerTV, errorTV;
    private ListView listView;
    private ArrayAdapterListView arrayAdapterListView;
    private final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_TradeMate);
        setContentView(R.layout.activity_list);
        headerTV = findViewById(R.id.list_activity_header_tv);
        listView = findViewById(R.id.list_activity_list);
        errorTV = findViewById(R.id.list_activity_error_tv);
        int type = getIntent().getIntExtra(IntentConstants.TYPE_INTENT_KEY, IntentConstants.DEFAULT_DATA_TYPE);
        switch (type) {
            case IntentConstants.REQUESTS_DATA_TYPE: {
                headerTV.setText(R.string.list_activity_header_requests_text);
                arrayAdapterListView = new WidgetsEngine.RequestsListView(getIntent().getParcelableArrayExtra(IntentConstants.REQUESTS_INTENT_KEY), listView, this, errorTV);
            } break;
            case IntentConstants.MERCHANDISERS_DATA_TYPE: {
                String header = getString(R.string.list_activity_header_merchandisers_text);
                headerTV.setText(String.format(header, getIntent().getStringExtra(IntentConstants.COMPANY_NAME_INTENT_KEY)));
                arrayAdapterListView = new WidgetsEngine.MerchandisersListView(getIntent().getParcelableArrayExtra(IntentConstants.MERCHANDISERS_INTENT_KEY), listView, this, errorTV);
            } break;
            case IntentConstants.OPERATORS_DATA_TYPE: {
                String header = getString(R.string.list_activity_header_operators_text);
                headerTV.setText(String.format(header, getIntent().getStringExtra(IntentConstants.COMPANY_NAME_INTENT_KEY)));
                arrayAdapterListView = new WidgetsEngine.OperatorsListView(
                        getIntent().getStringArrayExtra(IntentConstants.NAMES_OF_OPERATORS_INTENT_KEY),
                        getIntent().getStringArrayExtra(IntentConstants.EMAILS_OF_OPERATORS_INTENT_KEY),
                        listView, this, errorTV);
            } break;
            case IntentConstants.SHOPS_DATA_TYPE: {
                headerTV.setText(R.string.list_activity_header_shops_text);
                arrayAdapterListView = new WidgetsEngine.ShopsListView(getIntent().getStringArrayExtra(IntentConstants.SHOPS_INTENT_KEY), listView, this, errorTV);
            } break;
            case IntentConstants.PHOTO_REPORTS_DATA_TYPE: {
                headerTV.setText(R.string.list_activity_header_photo_reports_text);
                arrayAdapterListView = new WidgetsEngine.PhotoReportsListView(getIntent().getStringArrayExtra(IntentConstants.PHOTO_REPORTS_INTENT_KEY), listView, this, errorTV) {
                    @Override
                    public void onItemClickListener(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getContext(), PhotoReportActivity.class);
                        intent.putExtra(IntentConstants.PHOTO_REPORT_NAME_INTENT_KEY, getNames().get(position));
                        startActivityForResult(intent, REQUEST_CODE);
                    }
                };
            } break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                if (data.hasExtra(IntentConstants.TODO_INTENT_KEY)) {
                    switch (data.getIntExtra(IntentConstants.TODO_INTENT_KEY, 0)) {
                        case IntentConstants.TODO_DELETE: {
                            arrayAdapterListView.deleteItem(data.getStringExtra(IntentConstants.NAME_INTENT_KEY));
                        } break;
                    }
                }
            }
        }
    }
}