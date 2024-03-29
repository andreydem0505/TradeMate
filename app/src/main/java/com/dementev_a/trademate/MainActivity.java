package com.dementev_a.trademate;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.dementev_a.trademate.bundle.BundleEngine;
import com.dementev_a.trademate.preferences.SharedPreferencesEngine;

public class MainActivity extends AppCompatActivity {
    private SharedPreferencesEngine spe;
    private String userType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_TradeMate);
        setContentView(R.layout.activity_main);
        spe = new SharedPreferencesEngine(this, getString(R.string.shared_preferences_user));
    }

    @Override
    protected void onResume() {
        super.onResume();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (spe.count() == 0 && userType == null) { // user has not a role
            Fragment descriptionFragment = new DescriptionFragment();
            transaction.replace(R.id.fragment, descriptionFragment);
            transaction.commitNowAllowingStateLoss();
        } else { // user has a role
            String type = spe.getString(SharedPreferencesEngine.TYPE_KEY);
            if (userType == null || !userType.equals(type)) {
                userType = type;
                Bundle bundle = new Bundle();
                bundle.putString(BundleEngine.ACCESS_TOKEN_KEY_BUNDLE, spe.getString(SharedPreferencesEngine.ACCESS_TOKEN_KEY));
                if (type.equals(getString(R.string.shared_preferences_type_company))) {
                    bundle.putString(BundleEngine.COMPANY_NAME_KEY_BUNDLE, spe.getString(SharedPreferencesEngine.NAME_KEY));
                    Fragment companyFragment = new CompanyFragment();
                    companyFragment.setArguments(bundle);
                    transaction.replace(R.id.fragment, companyFragment);
                } else if (type.equals(getString(R.string.shared_preferences_type_merchandiser))) {
                    bundle.putString(BundleEngine.MERCHANDISER_NAME_KEY_BUNDLE, spe.getString(SharedPreferencesEngine.NAME_KEY));
                    Fragment merchandiserFragment = new MerchandiserFragment();
                    merchandiserFragment.setArguments(bundle);
                    transaction.replace(R.id.fragment, merchandiserFragment);
                }
                transaction.commitNowAllowingStateLoss();
            }
        }
    }
}