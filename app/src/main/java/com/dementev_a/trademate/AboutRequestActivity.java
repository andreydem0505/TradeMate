package com.dementev_a.trademate;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextClock;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class AboutRequestActivity extends AppCompatActivity {
    private TextView headerTV, textTV, receiverTV, textTime;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_request);
        headerTV = findViewById(R.id.about_request_activity_header);
        textTV = findViewById(R.id.about_request_activity_text);
        receiverTV = findViewById(R.id.about_request_activity_receiver);
        textTime = findViewById(R.id.about_request_activity_time);

        headerTV.setText(getIntent().getStringExtra("subject"));
        textTV.setText(getIntent().getStringExtra("text"));
        String receiverText = getString(R.string.about_request_activity_receiver_text);
        receiverTV.setText(String.format(receiverText, getIntent().getStringExtra("receiver")));
        String dateTimeString = getIntent().getStringExtra("dateTime");
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