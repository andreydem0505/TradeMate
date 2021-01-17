package com.dementev_a.trademate;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.dementev_a.trademate.preferences.SharedPreferencesEngine;


public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getFragmentManager();
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
            Bundle bundle = new Bundle();
            bundle.putString("companyName", spe.getString("name"));
            Fragment companyFragment = new CompanyFragment();
            companyFragment.setArguments(bundle);
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment, companyFragment);
            transaction.commit();
        }
    }
}