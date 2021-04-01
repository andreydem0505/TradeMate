package com.dementev_a.trademate;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.dementev_a.trademate.intent.IntentConstants;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

public class AboutRequestActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_request);
        TextView headerTV = findViewById(R.id.about_request_activity_header);
        TextView textTV = findViewById(R.id.about_request_activity_text);
        TextView receiverTV = findViewById(R.id.about_request_activity_receiver);
        TextView senderTV = findViewById(R.id.about_request_activity_sender);
        TextView textTime = findViewById(R.id.about_request_activity_time);

        headerTV.setText(getIntent().getStringExtra(IntentConstants.SUBJECT_EMAIL_INTENT_KEY));
        textTV.setText(getIntent().getStringExtra(IntentConstants.TEXT_EMAIL_INTENT_KEY));
        String senderText = getString(R.string.about_request_activity_sender_text);
        senderTV.setText(String.format(senderText, getIntent().getStringExtra(IntentConstants.SENDER_EMAIL_INTENT_KEY)));
        String receiverText = getString(R.string.about_request_activity_receiver_text);
        receiverTV.setText(String.format(receiverText, getIntent().getStringExtra(IntentConstants.RECEIVER_EMAIL_INTENT_KEY)));
        String dateTimeString = getIntent().getStringExtra(IntentConstants.DATE_TIME_EMAIL_INTENT_KEY);
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString);
        textTime.setText(formatTime(dateTime.getHour()) + ":" + formatTime(dateTime.getMinute()));
    }

    @NotNull
    private String formatTime(int arg) {
        if (arg < 10)
            return "0" + arg;
        return String.valueOf(arg);
    }
}