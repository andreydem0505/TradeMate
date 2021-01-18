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
    protected void onResume() {
        super.onResume();
        SharedPreferencesEngine spe = new SharedPreferencesEngine(this, getString(R.string.shared_preferences_user));
        if (spe.count() == 0) {
            Fragment descriptionFragment = new DescriptionFragment();
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment, descriptionFragment);
            transaction.commit();
        } else if (spe.getString("type").equals("company")) {
            Fragment progressFragment = new ProgressFragment();
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment, progressFragment);
            transaction.commit();
            new SetCompanyFragment().execute(spe);
        }
    }

    private class SetCompanyFragment extends AsyncTask<SharedPreferencesEngine, Void, Bundle> {
        private final int STATUS_OK = 0;
        private final int STATUS_ERROR = 1;

        @Override
        protected Bundle doInBackground(SharedPreferencesEngine... spe) {
            Bundle bundle = new Bundle();
            bundle.putString("companyName", spe[0].getString("name"));
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
                        bundle.putInt("status", STATUS_ERROR);
                    } break;
                }
            } catch (IOException e) {
                bundle.putInt("status", STATUS_ERROR);
            }
            return bundle;
        }

        @Override
        protected void onPostExecute(Bundle bundle) {
            transaction = fragmentManager.beginTransaction();
            if (bundle.getInt("status") == STATUS_OK) {
                bundle.remove("status");
                Fragment companyFragment = new CompanyFragment();
                companyFragment.setArguments(bundle);
                transaction.replace(R.id.fragment, companyFragment);
            } else {
                bundle.remove("status");
                bundle.putString("error", getString(R.string.global_errors_server_error_text));
                ErrorFragment errorFragment = new ErrorFragment();
                errorFragment.setArguments(bundle);
                transaction.replace(R.id.fragment, errorFragment);
            }
            transaction.commit();
        }
    }
}