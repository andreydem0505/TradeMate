package com.dementev_a.trademate;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.fragment.app.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;

import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.json.JsonEngine;
import com.dementev_a.trademate.preferences.SharedPreferencesEngine;
import com.dementev_a.trademate.requests.RequestEngine;

import java.io.IOException;
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        update(true);
    }

    private void update(boolean progress) {
        SharedPreferencesEngine spe = new SharedPreferencesEngine(this, getString(R.string.shared_preferences_user));
        if (spe.count() == 0) {
            transaction = fragmentManager.beginTransaction();
            Fragment descriptionFragment = new DescriptionFragment();
            transaction.replace(R.id.fragment, descriptionFragment);
            transaction.commit();
        } else {
            String type = spe.getString("type");
            if (type.equals(getString(R.string.shared_preferences_type_company))) {
                if (progress) {
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
            if (!RequestEngine.isConnectedToInternet(MainActivity.this)) {
                bundle.putInt("status", STATUS_INTERNET_ERROR);
                return bundle;
            }
            bundle.putString("companyName", spe[0].getString("name"));
            bundle.putString("accessToken", spe[0].getString("accessToken"));
            String url = API.API_URL + "/merchandisers";
            Map<String, String> headers = new HashMap<>();
            headers.put("access_token", spe[0].getString("accessToken"));
            try {
                String response = RequestEngine.makeGetRequest(url, headers);
                JsonEngine jsonEngine = new JsonEngine();
                String message = jsonEngine.getStringFromJson(response, "message");
                switch (message) {
                    case "Success": {
                        int total = jsonEngine.getIntegerFromJson(response, "total");
                        bundle.putInt("total", total);
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
                if (status == STATUS_SERVER_ERROR)
                    bundle.putString("error", getString(R.string.global_errors_server_error_text));
                else if (status == STATUS_INTERNET_ERROR)
                    bundle.putString("error", getString(R.string.global_errors_internet_connection_error_text));
                ErrorFragment errorFragment = new ErrorFragment();
                errorFragment.setArguments(bundle);
                transaction.replace(R.id.fragment, errorFragment);
            }
            transaction.commit();
        }
    }
}