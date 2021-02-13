package com.dementev_a.trademate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.dementev_a.trademate.json.MerchandiserJson;
import com.dementev_a.trademate.json.OperatorJson;
import com.dementev_a.trademate.json.RequestJson;

import org.jetbrains.annotations.NotNull;

public class ListActivity extends AppCompatActivity {
    private LinearLayout linearLayout;
    private TextView headerTV;

    private enum BtnType {
        COMPANY_TYPE, MERCHANDISER_TYPE, REQUEST_TYPE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        headerTV = findViewById(R.id.list_activity_header_tv);
        linearLayout = findViewById(R.id.list_activity_list);

        String type = getIntent().getStringExtra("type");
        switch (type) {
            case "merchandisers": {
                String header = getString(R.string.list_activity_header_merchandisers_text);
                Parcelable[] merchandisers = getIntent().getParcelableArrayExtra("merchandisers");
                for (Parcelable merchandiser : merchandisers) {
                    MerchandiserJson merchandiserJson = (MerchandiserJson) merchandiser;
                    MyTextView myTextView = new MyTextView(this, merchandiserJson.getName());
                    linearLayout.addView(myTextView.getView());
                }
                headerTV.setText(String.format(header, getIntent().getStringExtra("companyName")));
            } break;
            case "operators": {
                String header = getString(R.string.list_activity_header_operators_text);
                Parcelable[] operators = getIntent().getParcelableArrayExtra("operators");
                for (Parcelable operator : operators) {
                    OperatorJson operatorJson = (OperatorJson) operator;
                    MyTextView myTextView = new MyTextView(this, operatorJson.getName());
                    linearLayout.addView(myTextView.getView());
                }
                headerTV.setText(String.format(header, getIntent().getStringExtra("companyName")));
            } break;
            case "requests": {
                headerTV.setText(R.string.list_activity_header_requests_text);
                Parcelable[] requests = getIntent().getParcelableArrayExtra("requests");
                for (Parcelable request : requests) {
                    RequestJson requestJson = (RequestJson) request;
                    MyTextView myTextView = new MyTextView(this, requestJson.getSubject());
                    myTextView.setOnClick(BtnType.REQUEST_TYPE);
                    linearLayout.addView(myTextView.getView());
                }
            }
        }
    }


    private class MyTextView {
        private final TextView textView;

        public MyTextView(Context context, String text) {
            textView = new TextView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(params);
            textView.setTextColor(getResources().getColor(R.color.black, context.getTheme()));
            textView.setTextSize(20);
            textView.setPadding(20, 10, 20, 10);
            textView.setClickable(true);
            textView.setText(text);
        }

        public void setOnClick(@NotNull BtnType type) {
            switch (type) {
                case REQUEST_TYPE: {
                    textView.setOnClickListener(v -> {
                    });
                } break;
            }
        }

        public View getView() {
            return textView;
        }
    }
}