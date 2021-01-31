package com.dementev_a.trademate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;

import com.dementev_a.trademate.json.MerchandiserJson;
import com.dementev_a.trademate.json.OperatorJson;

public class AboutEmployeesActivity extends AppCompatActivity {
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_employees);
        linearLayout = findViewById(R.id.about_merchandisers_activity_list);

        String type = getIntent().getStringExtra("type");
        if (type.equals("merchandisers")) {
            Parcelable[] merchandisers = getIntent().getParcelableArrayExtra("merchandisers");
            for (Parcelable merchandiser : merchandisers) {
                MerchandiserJson merchandiserJson = (MerchandiserJson) merchandiser;
                MyButton myButton = new MyButton(this);
                myButton.setText(merchandiserJson.getName());
                linearLayout.addView(myButton.getView());
            }
        } else {
            Parcelable[] operators = getIntent().getParcelableArrayExtra("operators");
            for (Parcelable operator : operators) {
                OperatorJson operatorJson = (OperatorJson) operator;
                MyButton myButton = new MyButton(this);
                myButton.setText(operatorJson.getName());
                linearLayout.addView(myButton.getView());
            }
        }
    }


    private class MyButton {
        private Button button;

        public MyButton(Context context) {
            button = new Button(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            button.setLayoutParams(params);
            button.setBackgroundTintList(getResources().getColorStateList(R.color.white, context.getTheme()));
            button.setTextColor(getResources().getColor(R.color.black, context.getTheme()));
            button.setTransformationMethod(null);
            button.setGravity(Gravity.START);
            button.setStateListAnimator(null);
            button.setTextSize(20);
        }

        public void setText(String text) {
            button.setText(text);
        }

        public View getView() {
            return button;
        }
    }
}