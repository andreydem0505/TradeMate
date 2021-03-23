package com.dementev_a.trademate;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.requests.DataReceiver;
import com.dementev_a.trademate.requests.RequestStatus;
import com.dementev_a.trademate.preferences.SharedPreferencesEngine;
import com.dementev_a.trademate.requests.RequestEngine;


public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private SharedPreferencesEngine spe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        spe = new SharedPreferencesEngine(this, getString(R.string.shared_preferences_user));
        update(true);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        update(false);
    }

    private void update(boolean progress) {
        if (spe.count() == 0) { // user has not a role
            if (progress) {
                transaction = fragmentManager.beginTransaction();
                Fragment descriptionFragment = new DescriptionFragment();
                transaction.replace(R.id.fragment, descriptionFragment);
                transaction.commit();
            }
        } else { // user has a role
            if (progress) {
                transaction = fragmentManager.beginTransaction();
                Fragment progressFragment = new ProgressFragment();
                transaction.replace(R.id.fragment, progressFragment);
                transaction.commit();
            }
            String type = spe.getString("type");
            if (type.equals(getString(R.string.shared_preferences_type_company))) {
                new SetCompanyFragment(spe).execute();
            } else if (type.equals(getString(R.string.shared_preferences_type_merchandiser))) {
                new ConcurrentSetMerchandiserFragment(spe).execute();
            }
        }
    }


    private class SetCompanyFragment extends DataReceiver {
        private final SharedPreferencesEngine spe;

        protected SetCompanyFragment(SharedPreferencesEngine spe) {
            super();
            this.spe = spe;
        }

        @Override
        public void UIWork() {
            int status = getBundle().getInt(API.STATUS_KEY_BUNDLE);
            if (status == RequestStatus.STATUS_OK) {
                transaction = fragmentManager.beginTransaction();
                Fragment companyFragment = new CompanyFragment();
                companyFragment.setArguments(getBundle());
                transaction.replace(R.id.fragment, companyFragment);
            } else {
                transaction = fragmentManager.beginTransaction();
                ErrorFragment errorFragment = new ErrorFragment();
                errorFragment.setArguments(getBundle());
                transaction.replace(R.id.fragment, errorFragment);
            }
            transaction.commit();
        }

        @Override
        public void sendRequests() {
            if (!RequestEngine.isConnectedToInternet(MainActivity.this)) {
                getBundle().putInt(API.STATUS_KEY_BUNDLE, RequestStatus.STATUS_INTERNET_ERROR);
                return;
            }

            String accessToken = spe.getString(SharedPreferencesEngine.ACCESS_TOKEN_KEY);

            getBundle().putString("companyName", spe.getString(SharedPreferencesEngine.NAME_KEY));
            getBundle().putString("accessToken", accessToken);

            API api = new API();

            api.getMerchandisers(getBundle(), accessToken);

            if (getBundle().getInt(API.STATUS_KEY_BUNDLE) != RequestStatus.STATUS_OK)
                return;

            api.getOperators(getBundle(), accessToken);

            if (getBundle().getInt(API.STATUS_KEY_BUNDLE) != RequestStatus.STATUS_OK)
                return;

            api.getRequestsToday(getBundle(), accessToken);

            if (getBundle().getInt(API.STATUS_KEY_BUNDLE) != RequestStatus.STATUS_OK)
                return;

            api.getShops(getBundle(), accessToken);
        }
    }


    private class ConcurrentSetMerchandiserFragment extends DataReceiver {
        private final SharedPreferencesEngine spe;

        protected ConcurrentSetMerchandiserFragment(SharedPreferencesEngine spe) {
            super();
            this.spe = spe;
        }

        @Override
        public void UIWork() {
            int status = getBundle().getInt("status");
            if (status == RequestStatus.STATUS_OK) {
                transaction = fragmentManager.beginTransaction();
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
                transaction = fragmentManager.beginTransaction();
                ErrorFragment errorFragment = new ErrorFragment();
                errorFragment.setArguments(getBundle());
                transaction.replace(R.id.fragment, errorFragment);
            }
            getBundle().remove("status");
            transaction.commit();
        }

        @Override
        public void sendRequests() {
            if (!RequestEngine.isConnectedToInternet(MainActivity.this)) {
                getBundle().putInt("status", RequestStatus.STATUS_INTERNET_ERROR);
                return;
            }

            getBundle().putString("merchandiserName", spe.getString("name"));

            String accessToken = spe.getString("accessToken");

            API api = new API();

            api.getOperators(getBundle(), accessToken);

            if (getBundle().getInt("status") != RequestStatus.STATUS_OK)
                return;

            api.getRequestsToday(getBundle(), accessToken);

            if (getBundle().getInt("status") != RequestStatus.STATUS_OK)
                return;

            api.getShops(getBundle(), accessToken);
        }
    }
}