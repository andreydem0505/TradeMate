package com.dementev_a.trademate.widgets;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dementev_a.trademate.AboutRequestActivity;
import com.dementev_a.trademate.R;
import com.dementev_a.trademate.json.RequestJson;

import org.jetbrains.annotations.NotNull;

public class WidgetsEngine {
    public static void setRequestsOnListView(@NotNull Parcelable[] requests, ListView listView, Context context, TextView errorTV) {
        if (requests.length > 0) {
            RequestJson[] arrayOfRequestJson = new RequestJson[requests.length];
            String[] arrayNames = new String[requests.length];
            for (int i = 0; i < requests.length; i++) {
                RequestJson requestJson = (RequestJson) requests[i];
                arrayOfRequestJson[i] = requestJson;
                arrayNames[i] = requestJson.getSubject();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, arrayNames);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Intent intent = new Intent(context, AboutRequestActivity.class);
                intent.putExtra("subject", arrayOfRequestJson[position].getSubject());
                intent.putExtra("text", arrayOfRequestJson[position].getText());
                intent.putExtra("receiver", arrayOfRequestJson[position].getOperator());
                intent.putExtra("sender", arrayOfRequestJson[position].getSender());
                intent.putExtra("dateTime", arrayOfRequestJson[position].getDateTime());
                context.startActivity(intent);
            });
        } else
            errorTV.setText(R.string.list_activity_error_tv_requests_text);
    }
}
