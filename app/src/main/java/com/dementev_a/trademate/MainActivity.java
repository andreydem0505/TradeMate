package com.dementev_a.trademate;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.fragment.app.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;

import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.api.RequestStatus;
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
            if (restart) {
                transaction = fragmentManager.beginTransaction();
                Fragment progressFragment = new ProgressFragment();
                transaction.replace(R.id.fragment, progressFragment);
                transaction.commit();
            }
            String type = spe.getString("type");
            if (type.equals(getString(R.string.shared_preferences_type_company))) {
                new SetCompanyFragment().execute(spe);
            } else if (type.equals(getString(R.string.shared_preferences_type_merchandiser))) {
                new SetMerchandiserFragment().execute(spe);
            }
        }
    }


    private class SetCompanyFragment extends AsyncTask<SharedPreferencesEngine, Void, Bundle> {

        @Override
        protected Bundle doInBackground(SharedPreferencesEngine... spe) {
            Bundle bundle = new Bundle();

            if (!RequestEngine.isConnectedToInternet(MainActivity.this)) {
                bundle.putInt("status", RequestStatus.STATUS_INTERNET_ERROR);
                return bundle;
            }

            bundle.putString("companyName", spe[0].getString("name"));
            bundle.putString("accessToken", spe[0].getString("accessToken"));

            Map<String, String> headers = new HashMap<>();
            headers.put("access_token", spe[0].getString("accessToken"));

            API.getMerchandisers(bundle, headers);

            if (bundle.getString("status") != null)
                return bundle;

            API.getOperators(bundle, headers);

            return bundle;
        }

        @Override
        protected void onPostExecute(Bundle bundle) {
            transaction = fragmentManager.beginTransaction();
            int status = bundle.getInt("status");
            if (status == RequestStatus.STATUS_OK) {
                Fragment companyFragment = new CompanyFragment();
                companyFragment.setArguments(bundle);
                transaction.replace(R.id.fragment, companyFragment);
            } else {
                switch (status) {
                    case RequestStatus.STATUS_SERVER_ERROR:
                        bundle.putString("error", getString(R.string.global_errors_server_error_text));
                        break;
                    case RequestStatus.STATUS_INTERNET_ERROR:
                        bundle.putString("error", getString(R.string.global_errors_internet_connection_error_text));
                        break;
                }
                ErrorFragment errorFragment = new ErrorFragment();
                errorFragment.setArguments(bundle);
                transaction.replace(R.id.fragment, errorFragment);
            }
            bundle.remove("status");
            transaction.commit();
        }
    }


    private class SetMerchandiserFragment extends AsyncTask<SharedPreferencesEngine, Void, Bundle> {

        @Override
        protected Bundle doInBackground(SharedPreferencesEngine... spe) {
            Bundle bundle = new Bundle();

            if (!RequestEngine.isConnectedToInternet(MainActivity.this)) {
                bundle.putInt("status", RequestStatus.STATUS_INTERNET_ERROR);
                return bundle;
            }

            bundle.putString("merchandiserName", spe[0].getString("name"));

            Map<String, String> headers = new HashMap<>();
            headers.put("access_token", spe[0].getString("accessToken"));

            API.getOperators(bundle, headers);

            if (bundle.getString("status") != null)
                return bundle;

            API.getRequests(bundle, headers);

            return bundle;
        }

        @Override
        protected void onPostExecute(Bundle bundle) {
            transaction = fragmentManager.beginTransaction();
            int status = bundle.getInt("status");
            if (status == RequestStatus.STATUS_OK) {
                Fragment merchandiserFragment = new MerchandiserFragment();
                merchandiserFragment.setArguments(bundle);
                transaction.replace(R.id.fragment, merchandiserFragment);
            } else {
                switch (status) {
                    case RequestStatus.STATUS_SERVER_ERROR:
                        bundle.putString("error", getString(R.string.global_errors_server_error_text));
                        break;
                    case RequestStatus.STATUS_INTERNET_ERROR:
                        bundle.putString("error", getString(R.string.global_errors_internet_connection_error_text));
                        break;
                }
                ErrorFragment errorFragment = new ErrorFragment();
                errorFragment.setArguments(bundle);
                transaction.replace(R.id.fragment, errorFragment);
            }
            bundle.remove("status");
            transaction.commit();
        }
    }
}