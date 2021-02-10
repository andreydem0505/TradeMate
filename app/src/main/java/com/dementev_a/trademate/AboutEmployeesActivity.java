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

public class AboutEmployeesActivity extends AppCompatActivity {
    private LinearLayout linearLayout;
    private TextView headerTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_employees);
        headerTV = findViewById(R.id.about_employees_activity_header_tv);
        linearLayout = findViewById(R.id.about_merchandisers_activity_list);

        String header;
        String type = getIntent().getStringExtra("type");
        if (type.equals("merchandisers")) {
            header = getString(R.string.about_employees_activity_header_merchandisers_text);
            Parcelable[] merchandisers = getIntent().getParcelableArrayExtra("merchandisers");
            for (Parcelable merchandiser : merchandisers) {
                MerchandiserJson merchandiserJson = (MerchandiserJson) merchandiser;
                MyTextView myTextView = new MyTextView(this, merchandiserJson.getName());
                linearLayout.addView(myTextView.getView());
            }
        } else {
            header = getString(R.string.about_employees_activity_header_operators_text);
            Parcelable[] operators = getIntent().getParcelableArrayExtra("operators");
            for (Parcelable operator : operators) {
                OperatorJson operatorJson = (OperatorJson) operator;
                MyTextView myTextView = new MyTextView(this, operatorJson.getName());
                linearLayout.addView(myTextView.getView());
            }
        }
        headerTV.setText(String.format(header, getIntent().getStringExtra("companyName")));
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
            textView.setOnClickListener(v -> {
            });
            textView.setText(text);
        }

        public View getView() {
            return textView;
        }
    }
}