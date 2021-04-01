package com.dementev_a.trademate.widgets;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dementev_a.trademate.AboutMerchandiserActivity;
import com.dementev_a.trademate.AboutRequestActivity;
import com.dementev_a.trademate.R;
import com.dementev_a.trademate.intent.IntentConstants;
import com.dementev_a.trademate.json.MerchandiserJson;
import com.dementev_a.trademate.json.RequestJson;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

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
                intent.putExtra(IntentConstants.SUBJECT_EMAIL_INTENT_KEY, arrayOfRequestJson[position].getSubject());
                intent.putExtra(IntentConstants.TEXT_EMAIL_INTENT_KEY, arrayOfRequestJson[position].getText());
                intent.putExtra(IntentConstants.RECEIVER_EMAIL_INTENT_KEY, arrayOfRequestJson[position].getOperator());
                intent.putExtra(IntentConstants.SENDER_EMAIL_INTENT_KEY, arrayOfRequestJson[position].getSender());
                intent.putExtra(IntentConstants.DATE_TIME_EMAIL_INTENT_KEY, arrayOfRequestJson[position].getDateTime());
                context.startActivity(intent);
            });
        } else
            errorTV.setText(R.string.list_activity_error_tv_requests_text);
    }

    public static void setMerchandisersOnListView(@NotNull Parcelable[] merchandisers, ListView listView, Context context, TextView errorTV) {
        if (merchandisers.length > 0) {
            MerchandiserJson[] arrayOfMerchandiserJson = new MerchandiserJson[merchandisers.length];
            String[] arrayNames = new String[merchandisers.length];
            for (int i = 0; i < merchandisers.length; i++) {
                MerchandiserJson merchandiserJson = (MerchandiserJson) merchandisers[i];
                arrayOfMerchandiserJson[i] = merchandiserJson;
                arrayNames[i] = merchandiserJson.getName();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, arrayNames);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Intent intent = new Intent(context, AboutMerchandiserActivity.class);
                intent.putExtra(IntentConstants.NAME_OF_MERCHANDISER_INTENT_KEY, arrayOfMerchandiserJson[position].getName());
                intent.putExtra(IntentConstants.EMAIL_OF_MERCHANDISER_INTENT_KEY, arrayOfMerchandiserJson[position].getEmail());
                intent.putExtra(IntentConstants.PASSWORD_OF_MERCHANDISER_INTENT_KEY, arrayOfMerchandiserJson[position].getPassword());
                context.startActivity(intent);
            });
        } else
            errorTV.setText(R.string.list_activity_error_tv_merchandisers_text);
    }

    private static int lastOperatorNameEdit = 0;

    public static void setOperatorsOnListView(@NotNull String[] names, String[] emails, ListView listView, Context context, TextView errorTV) {
        if (names.length > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, names);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                names[lastOperatorNameEdit] = names[lastOperatorNameEdit].replaceAll("\n.+", "");
                names[position] = names[position] + "\n" + emails[position];
                lastOperatorNameEdit = position;
                adapter.notifyDataSetChanged();
            });
        } else
            errorTV.setText(R.string.list_activity_error_tv_operators_text);
    }

    public static void setShopsOnListView(@NotNull String[] shops, ListView listView, Context context, TextView errorTV) {
        if (shops.length > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, shops);
            listView.setAdapter(adapter);
        } else
            errorTV.setText(R.string.list_activity_error_tv_shops_text);
    }
}
