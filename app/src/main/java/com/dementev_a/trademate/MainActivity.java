package com.dementev_a.trademate;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.requests.AsyncRequest;
import com.dementev_a.trademate.requests.RequestStatus;
import com.dementev_a.trademate.preferences.SharedPreferencesEngine;
import com.dementev_a.trademate.requests.RequestEngine;

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
                new ConcurrentSetCompanyFragment(spe).execute();
            } else if (type.equals(getString(R.string.shared_preferences_type_merchandiser))) {
                new ConcurrentSetMerchandiserFragment(spe).execute();
            }
        }
    }


    private class ConcurrentSetCompanyFragment extends AsyncRequest {
        private final SharedPreferencesEngine spe;

        protected ConcurrentSetCompanyFragment(SharedPreferencesEngine spe) {
            super();
            this.spe = spe;
        }

        @Override
        public void UIWork() {
            transaction = fragmentManager.beginTransaction();
            int status = getBundle().getInt("status");
            if (status == RequestStatus.STATUS_OK) {
                Fragment companyFragment = new CompanyFragment();
                companyFragment.setArguments(getBundle());
                transaction.replace(R.id.fragment, companyFragment);
            } else {
                switch (status) {
                    case RequestStatus.STATUS_SERVER_ERROR: {
                        getBundle().putString("error", getString(R.string.global_errors_server_error_text));
                    } break;
                    case RequestStatus.STATUS_INTERNET_ERROR: {
                        getBundle().putString("error", getString(R.string.global_errors_internet_connection_error_text));
                    } break;
                }
                ErrorFragment errorFragment = new ErrorFragment();
                errorFragment.setArguments(getBundle());
                transaction.replace(R.id.fragment, errorFragment);
            }
            getBundle().remove("status");
            transaction.commit();
        }

        @Override
        public void sendRequest() {
            if (!RequestEngine.isConnectedToInternet(MainActivity.this)) {
                getBundle().putInt("status", RequestStatus.STATUS_INTERNET_ERROR);
                return;
            }

            getBundle().putString("companyName", spe.getString("name"));
            getBundle().putString("accessToken", spe.getString("accessToken"));

            Map<String, String> headers = new HashMap<>();
            headers.put("access_token", spe.getString("accessToken"));

            API.getMerchandisers(getBundle(), headers);

            if (getBundle().getInt("status") != RequestStatus.STATUS_OK)
                return;

            API.getOperators(getBundle(), headers);

            if (getBundle().getInt("status") != RequestStatus.STATUS_OK)
                return;

            API.getRequestsToday(getBundle(), headers);

            if (getBundle().getInt("status") != RequestStatus.STATUS_OK)
                return;

            API.getShops(getBundle(), headers);
        }
    }


    private class ConcurrentSetMerchandiserFragment extends AsyncRequest {
        private final SharedPreferencesEngine spe;

        protected ConcurrentSetMerchandiserFragment(SharedPreferencesEngine spe) {
            super();
            this.spe = spe;
        }

        @Override
        public void UIWork() {
            transaction = fragmentManager.beginTransaction();
            int status = getBundle().getInt("status");
            if (status == RequestStatus.STATUS_OK) {
                Fragment merchandiserFragment = new MerchandiserFragment();
                merchandiserFragment.setArguments(getBundle());
                transaction.replace(R.id.fragment, merchandiserFragment);
            } else {
                switch (status) {
                    case RequestStatus.STATUS_SERVER_ERROR: {
                        getBundle().putString("error", getString(R.string.global_errors_server_error_text));
                    } break;
                    case RequestStatus.STATUS_INTERNET_ERROR: {
                        getBundle().putString("error", getString(R.string.global_errors_internet_connection_error_text));
                    } break;
                }
                ErrorFragment errorFragment = new ErrorFragment();
                errorFragment.setArguments(getBundle());
                transaction.replace(R.id.fragment, errorFragment);
            }
            getBundle().remove("status");
            transaction.commit();
        }

        @Override
        public void sendRequest() {
            if (!RequestEngine.isConnectedToInternet(MainActivity.this)) {
                getBundle().putInt("status", RequestStatus.STATUS_INTERNET_ERROR);
                return;
            }

            getBundle().putString("merchandiserName", spe.getString("name"));

            Map<String, String> headers = new HashMap<>();
            headers.put("access_token", spe.getString("accessToken"));

            API.getOperators(getBundle(), headers);

            if (getBundle().getInt("status") != RequestStatus.STATUS_OK)
                return;

            API.getRequestsToday(getBundle(), headers);
        }
    }
}