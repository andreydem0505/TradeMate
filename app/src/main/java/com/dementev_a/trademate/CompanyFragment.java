package com.dementev_a.trademate;

import android.app.Fragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CompanyFragment extends Fragment {
    private View view;
    private TextView companyNameTV;
    private String companyName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        companyNameTV.setText(companyName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        companyName = getArguments().getString("companyName");
        view = inflater.inflate(R.layout.fragment_company, container, false);
        companyNameTV = view.findViewById(R.id.company_fragment_company_name);
        return view;
    }
}