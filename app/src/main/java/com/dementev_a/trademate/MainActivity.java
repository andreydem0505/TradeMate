package com.dementev_a.trademate;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.fragment.app.Fragment;

import android.graphics.PixelFormat;
import android.os.Bundle;

import com.dementev_a.trademate.bundle.BundleEngine;
import com.dementev_a.trademate.preferences.SharedPreferencesEngine;

public class MainActivity extends AppCompatActivity {
    private final int
            NONE = 0,
            DESCRIPTION_STATE = 1,
            USER_STATE = 2;
    private SharedPreferencesEngine spe;
    private int state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_TradeMate);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spe = new SharedPreferencesEngine(this, getString(R.string.shared_preferences_user));
        state = NONE;
    }

    @Override
    protected void onResume() {
        super.onResume();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (spe.count() == 0) { // user has not a role
            Fragment descriptionFragment = new DescriptionFragment();
            transaction.replace(R.id.fragment, descriptionFragment);
            state = DESCRIPTION_STATE;
        } else if (state != USER_STATE) { // user has a role
            String type = spe.getString(SharedPreferencesEngine.TYPE_KEY);
            Bundle bundle = new Bundle();
            bundle.putString(BundleEngine.ACCESS_TOKEN_KEY_BUNDLE, spe.getString(SharedPreferencesEngine.ACCESS_TOKEN_KEY));
            if (type.equals(getString(R.string.shared_preferences_type_company))) {
                bundle.putString(BundleEngine.COMPANY_NAME_KEY_BUNDLE, spe.getString(SharedPreferencesEngine.NAME_KEY));
                Fragment companyFragment = new CompanyFragment();
                companyFragment.setArguments(bundle);
                transaction.replace(R.id.fragment, companyFragment);
                state = USER_STATE;
            } else if (type.equals(getString(R.string.shared_preferences_type_merchandiser))) {
                bundle.putString(BundleEngine.MERCHANDISER_NAME_KEY_BUNDLE, spe.getString(SharedPreferencesEngine.NAME_KEY));
                Fragment merchandiserFragment = new MerchandiserFragment();
                merchandiserFragment.setArguments(bundle);
                transaction.replace(R.id.fragment, merchandiserFragment);
                state = USER_STATE;
            }
        }
        transaction.commitAllowingStateLoss();
    }
}