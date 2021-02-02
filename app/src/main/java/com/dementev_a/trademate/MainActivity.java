package com.dementev_a.trademate;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.fragment.app.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;

import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.json.JsonEngine;
import com.dementev_a.trademate.json.MerchandiserJson;
import com.dementev_a.trademate.json.OperatorJson;
import com.dementev_a.trademate.preferences.SharedPreferencesEngine;
import com.dementev_a.trademate.requests.RequestEngine;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        update(true);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        update(false);
    }

    private void update(boolean restart) {
        SharedPreferencesEngine spe = new SharedPreferencesEngine(this, getString(R.string.shared_preferences_user));
        if (spe.count() == 0) {
            if (restart) {
                transaction = fragmentManager.beginTransaction();
                Fragment descriptionFragment = new DescriptionFragment();
                transaction.replace(R.id.fragment, descriptionFragment);
                transaction.commit();
            }
        } else {
            String type = spe.getString("type");
            if (type.equals(getString(R.string.shared_preferences_type_company))) {
                if (restart) {
                    transaction = fragmentManager.beginTransaction();
                    Fragment progressFragment = new ProgressFragment();
                    transaction.replace(R.id.fragment, progressFragment);
                    transaction.commit();
                }
                new SetCompanyFragment().execute(spe);
            } else if (type.equals(getString(R.string.shared_preferences_type_merchandiser))) {
                transaction = fragmentManager.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("name", spe.getString("name"));
                Fragment merchandiserFragment = new MerchandiserFragment();
                merchandiserFragment.setArguments(bundle);
                transaction.replace(R.id.fragment, merchandiserFragment);
                transaction.commit();
            }
        }
    }

    private class SetCompanyFragment extends AsyncTask<SharedPreferencesEngine, Void, Bundle> {
        private final int STATUS_OK = 0;
        private final int STATUS_SERVER_ERROR = 1;
        private final int STATUS_INTERNET_ERROR = 2;

        @Override
        protected Bundle doInBackground(SharedPreferencesEngine... spe) {
            Bundle bundle = new Bundle();
            JsonEngine jsonEngine = new JsonEngine();

            if (!RequestEngine.isConnectedToInternet(MainActivity.this)) {
                bundle.putInt("status", STATUS_INTERNET_ERROR);
                return bundle;
            }
            bundle.putString("companyName", spe[0].getString("name"));
            bundle.putString("accessToken", spe[0].getString("accessToken"));

            String url = API.MAIN_URL + API.ALL_MERCHANDISERS_URL;
            Map<String, String> headers = new HashMap<>();
            headers.put("access_token", spe[0].getString("accessToken"));
            try {
                String response = RequestEngine.makeGetRequest(url, headers);
                String message = jsonEngine.getStringFromJson(response, "message");
                switch (message) {
                    case "Success": {
                        int total = jsonEngine.getIntegerFromJson(response, "total");
                        bundle.putInt("total_merchandisers", total);

                        MerchandiserJson[] merchandisersArray = jsonEngine.getMerchandisersArrayFromJson(response, "merchandisers");
                        bundle.putParcelableArray("merchandisers", merchandisersArray);
                    } break;
                    case "Access token is wrong": {
                        bundle.putInt("status", STATUS_SERVER_ERROR);
                        return bundle;
                    }
                }
            } catch (IOException e) {
                bundle.putInt("status", STATUS_SERVER_ERROR);
                return bundle;
            }

            url = API.MAIN_URL + API.ALL_OPERATORS_URL;
            try {
                String response = RequestEngine.makeGetRequest(url, headers);
                String message = jsonEngine.getStringFromJson(response, "message");
                switch (message) {
                    case "Success": {
                        int total = jsonEngine.getIntegerFromJson(response, "total");
                        bundle.putInt("total_operators", total);

                        OperatorJson[] operatorsArray = jsonEngine.getOperatorsArrayFromJson(response, "operators");
                        bundle.putParcelableArray("operators", operatorsArray);

                        bundle.putInt("status", STATUS_OK);
                    } break;
                    case "Access token is wrong": {
                        bundle.putInt("status", STATUS_SERVER_ERROR);
                    } break;
                }
            } catch (IOException e) {
                bundle.putInt("status", STATUS_SERVER_ERROR);
            }

            return bundle;
        }

        @Override
        protected void onPostExecute(Bundle bundle) {
            transaction = fragmentManager.beginTransaction();
            int status = bundle.getInt("status");
            if (status == STATUS_OK) {
                bundle.remove("status");
                Fragment companyFragment = new CompanyFragment();
                companyFragment.setArguments(bundle);
                transaction.replace(R.id.fragment, companyFragment);
            } else {
                bundle.remove("status");
                switch (status) {
                    case STATUS_SERVER_ERROR:
                        bundle.putString("error", getString(R.string.global_errors_server_error_text));
                        break;
                    case STATUS_INTERNET_ERROR:
                        bundle.putString("error", getString(R.string.global_errors_internet_connection_error_text));
                        break;
                }
                ErrorFragment errorFragment = new ErrorFragment();
                errorFragment.setArguments(bundle);
                transaction.replace(R.id.fragment, errorFragment);
            }
            transaction.commit();
        }
    }
}