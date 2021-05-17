package com.dementev_a.trademate.widgets;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dementev_a.trademate.AboutMerchandiserActivity;
import com.dementev_a.trademate.AboutRequestActivity;
import com.dementev_a.trademate.dialogs.DeleteDialog;
import com.dementev_a.trademate.R;
import com.dementev_a.trademate.dialogs.OperatorDialog;
import com.dementev_a.trademate.intent.IntentConstants;
import com.dementev_a.trademate.json.MerchandiserJson;
import com.dementev_a.trademate.json.RequestJson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WidgetsEngine {

    public static class RequestsListView extends ArrayAdapterListView {
        private RequestJson[] arrayOfRequestJson;

        public RequestsListView(@NotNull Parcelable[] requests, ListView listView, Context context, TextView errorTV) {
            setContext(context);
            if (requests.length > 0) {
                arrayOfRequestJson = new RequestJson[requests.length];
                String[] arrayNames = new String[requests.length];
                for (int i = 0; i < requests.length; i++) {
                    RequestJson requestJson = (RequestJson) requests[i];
                    arrayOfRequestJson[i] = requestJson;
                    arrayNames[i] = requestJson.getSubject();
                }
                setNames(new ArrayList<>(Arrays.asList(arrayNames)));
                setListView(listView);
            } else
                errorTV.setText(R.string.list_activity_error_tv_requests_text);
        }

        @Override
        public void onItemClickListener(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getContext(), AboutRequestActivity.class);
            intent.putExtra(IntentConstants.SUBJECT_EMAIL_INTENT_KEY, arrayOfRequestJson[position].getSubject());
            intent.putExtra(IntentConstants.TEXT_EMAIL_INTENT_KEY, arrayOfRequestJson[position].getText());
            intent.putExtra(IntentConstants.RECEIVER_EMAIL_INTENT_KEY, arrayOfRequestJson[position].getOperator());
            intent.putExtra(IntentConstants.DATE_TIME_EMAIL_INTENT_KEY, arrayOfRequestJson[position].getDateTime());
            getContext().startActivity(intent);
        }

        @Override
        public int deleteItem(String name) {
            int index = super.deleteItem(name);
            List<RequestJson> list = new ArrayList<>(Arrays.asList(arrayOfRequestJson));
            list.remove(index);
            arrayOfRequestJson = list.toArray(new RequestJson[0]);
            return index;
        }
    }


    public static class MerchandisersListView extends ArrayAdapterListView {
        private MerchandiserJson[] arrayOfMerchandiserJson;

        public MerchandisersListView(@NotNull Parcelable[] merchandisers, ListView listView, Context context, TextView errorTV) {
            setContext(context);
            if (merchandisers.length > 0) {
                arrayOfMerchandiserJson = new MerchandiserJson[merchandisers.length];
                String[] arrayNames = new String[merchandisers.length];
                for (int i = 0; i < merchandisers.length; i++) {
                    MerchandiserJson merchandiserJson = (MerchandiserJson) merchandisers[i];
                    arrayOfMerchandiserJson[i] = merchandiserJson;
                    arrayNames[i] = merchandiserJson.getName();
                }
                setNames(new ArrayList<>(Arrays.asList(arrayNames)));
                setListView(listView);
            } else
                errorTV.setText(R.string.list_activity_error_tv_merchandisers_text);
        }

        @Override
        public void onItemClickListener(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getContext(), AboutMerchandiserActivity.class);
            intent.putExtra(IntentConstants.NAME_OF_MERCHANDISER_INTENT_KEY, arrayOfMerchandiserJson[position].getName());
            intent.putExtra(IntentConstants.EMAIL_OF_MERCHANDISER_INTENT_KEY, arrayOfMerchandiserJson[position].getEmail());
            intent.putExtra(IntentConstants.PASSWORD_OF_MERCHANDISER_INTENT_KEY, arrayOfMerchandiserJson[position].getPassword());
            getContext().startActivity(intent);
        }

        @Override
        public int deleteItem(String name) {
            int index = super.deleteItem(name);
            List<MerchandiserJson> list = new ArrayList<>(Arrays.asList(arrayOfMerchandiserJson));
            list.remove(index);
            arrayOfMerchandiserJson = list.toArray(new MerchandiserJson[0]);
            return index;
        }
    }


    public static class OperatorsListView extends ArrayAdapterListView {
        private String[] emails;
        private final FragmentManager fragmentManager;

        public OperatorsListView(@NotNull String[] names, String[] emails, ListView listView, Context context, TextView errorTV, FragmentManager fragmentManager) {
            this.fragmentManager = fragmentManager;
            setNames(new ArrayList<>(Arrays.asList(names)));
            setContext(context);
            this.emails = emails;
            if (names.length > 0) {
                setListView(listView);
            } else
                errorTV.setText(R.string.list_activity_error_tv_operators_text);
        }

        @Override
        public void onItemClickListener(AdapterView<?> parent, View view, int position, long id) {
            showOperatorDialog(fragmentManager, getNames().get(position), emails[position]);
        }

        @Override
        public int deleteItem(String name) {
            int index = super.deleteItem(name);
            List<String> list = new ArrayList<>(Arrays.asList(emails));
            list.remove(index);
            emails = list.toArray(new String[0]);
            return index;
        }
    }


    public static class ShopsListView extends ArrayAdapterListView {
        public ShopsListView(@NotNull String[] shops, ListView listView, Context context, TextView errorTV) {
            setNames(new ArrayList<>(Arrays.asList(shops)));
            setContext(context);
            if (getNames().size() > 0) {
                setListView(listView);
            } else
                errorTV.setText(R.string.list_activity_error_tv_shops_text);
        }
    }


    public static class PhotoReportsListView extends ArrayAdapterListView {
        public PhotoReportsListView(@NotNull String[] reports, ListView listView, Context context, TextView errorTV) {
            setNames(new ArrayList<>(Arrays.asList(reports)));
            setContext(context);
            if (getNames().size() > 0) {
                setListView(listView);
            } else
                errorTV.setText(R.string.list_activity_error_tv_photo_reports_text);
        }
    }

    public static void showDeleteDialog(FragmentManager manager, Handler handler, String name) {
        FragmentTransaction transaction = manager.beginTransaction();
        new DeleteDialog(handler, name).show(transaction, "dialog");
    }

    public static void showOperatorDialog(FragmentManager manager, String name, String email) {
        FragmentTransaction transaction = manager.beginTransaction();
        new OperatorDialog(name, email).show(transaction, "dialog");
    }
}
