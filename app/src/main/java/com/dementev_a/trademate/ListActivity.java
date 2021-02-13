package com.dementev_a.trademate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
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
                    MyTextViewForMerchandisers myTextView = new MyTextViewForMerchandisers(this, merchandiserJson);
                    linearLayout.addView(myTextView.getView());
                }
                headerTV.setText(String.format(header, getIntent().getStringExtra("companyName")));
            } break;
            case "operators": {
                String header = getString(R.string.list_activity_header_operators_text);
                Parcelable[] operators = getIntent().getParcelableArrayExtra("operators");
                for (Parcelable operator : operators) {
                    OperatorJson operatorJson = (OperatorJson) operator;
                    MyTextViewForOperators myTextView = new MyTextViewForOperators(this, operatorJson);
                    linearLayout.addView(myTextView.getView());
                }
                headerTV.setText(String.format(header, getIntent().getStringExtra("companyName")));
            } break;
            case "requests": {
                headerTV.setText(R.string.list_activity_header_requests_text);
                Parcelable[] requests = getIntent().getParcelableArrayExtra("requests");
                for (Parcelable request : requests) {
                    RequestJson requestJson = (RequestJson) request;
                    MyTextViewForRequests myTextView = new MyTextViewForRequests(this, requestJson);
                    linearLayout.addView(myTextView.getView());
                }
            }
        }
    }

    private class MyTextViewAbstract {
        protected final TextView textView;
        protected final Context context;

        public MyTextViewAbstract(Context context) {
            this.context = context;
            textView = new TextView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(params);
            textView.setTextColor(getResources().getColor(R.color.black, context.getTheme()));
            textView.setTextSize(20);
            textView.setPadding(20, 10, 20, 10);
            textView.setClickable(true);
        }

        public View getView() {
            return textView;
        }
    }

    private class MyTextViewForMerchandisers extends MyTextViewAbstract {
        private final MerchandiserJson merchandiserJson;

        public MyTextViewForMerchandisers(Context context, @NotNull MerchandiserJson merchandiserJson) {
            super(context);
            this.merchandiserJson = merchandiserJson;
            textView.setText(merchandiserJson.getName());
        }
    }

    private class MyTextViewForOperators extends MyTextViewAbstract {
        private final OperatorJson operatorJson;

        public MyTextViewForOperators(Context context, @NotNull OperatorJson operatorJson) {
            super(context);
            this.operatorJson = operatorJson;
            textView.setText(operatorJson.getName());
        }
    }

    private class MyTextViewForRequests extends MyTextViewAbstract {
        private final RequestJson requestJson;

        public MyTextViewForRequests(Context context, @NotNull RequestJson requestJson) {
            super(context);
            this.requestJson = requestJson;
            textView.setText(requestJson.getSubject());
            textView.setOnClickListener(v -> {
                Intent intent = new Intent(context, AboutRequestActivity.class);
                intent.putExtra("subject", requestJson.getSubject());
                intent.putExtra("text", requestJson.getText());
                intent.putExtra("receiver", requestJson.getOperator());
                intent.putExtra("dateTime", requestJson.getDateTime());
                startActivity(intent);
            });
        }
    }
}