package com.dementev_a.trademate;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.requests.AsyncRequest;
import com.dementev_a.trademate.requests.RequestStatus;
import com.dementev_a.trademate.preferences.SharedPreferencesEngine;
import com.dementev_a.trademate.requests.RequestEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


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


    private class ConcurrentSetCompanyFragment implements AsyncRequest {
        private final Bundle bundle;
        private final SharedPreferencesEngine spe;

        protected ConcurrentSetCompanyFragment(SharedPreferencesEngine spe) {
            bundle = new Bundle();
            this.spe = spe;
        }

        @Override
        public void execute() {
            Executor executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                sendRequest();
                handler.post(() -> {
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
                });
            });
        }

        @Override
        public void sendRequest() {

            if (!RequestEngine.isConnectedToInternet(MainActivity.this)) {
                bundle.putInt("status", RequestStatus.STATUS_INTERNET_ERROR);
                return;
            }

            bundle.putString("companyName", spe.getString("name"));
            bundle.putString("accessToken", spe.getString("accessToken"));

            Map<String, String> headers = new HashMap<>();
            headers.put("access_token", spe.getString("accessToken"));

            API.getMerchandisers(bundle, headers);

            if (bundle.getInt("status") != RequestStatus.STATUS_OK)
                return;

            API.getOperators(bundle, headers);

            if (bundle.getInt("status") != RequestStatus.STATUS_OK)
                return;

            API.getRequests(bundle, headers);
        }
    }


    private class ConcurrentSetMerchandiserFragment implements AsyncRequest {
        private final Bundle bundle;
        private final SharedPreferencesEngine spe;

        protected ConcurrentSetMerchandiserFragment(SharedPreferencesEngine spe) {
            bundle = new Bundle();
            this.spe = spe;
        }

        @Override
        public void execute() {
            Executor executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                sendRequest();
                handler.post(() -> {
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
                });
            });
        }

        @Override
        public void sendRequest() {

            if (!RequestEngine.isConnectedToInternet(MainActivity.this)) {
                bundle.putInt("status", RequestStatus.STATUS_INTERNET_ERROR);
                return;
            }

            bundle.putString("merchandiserName", spe.getString("name"));

            Map<String, String> headers = new HashMap<>();
            headers.put("access_token", spe.getString("accessToken"));

            API.getOperators(bundle, headers);

            if (bundle.getInt("status") != RequestStatus.STATUS_OK)
                return;

            API.getRequests(bundle, headers);
        }
    }
}