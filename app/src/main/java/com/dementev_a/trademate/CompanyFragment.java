package com.dementev_a.trademate;

import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class CompanyFragment extends Fragment implements View.OnClickListener {
    private View view;
    private TextView companyNameTV, employeesQuantityTV;
    private Button addMerchandiserBtn;
    private String companyName, accessToken;
    private int employeesQuantity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        companyNameTV.setText(companyName);
        String employeesQuantityText = getString(R.string.main_activity_employees_quantity_text);
        employeesQuantityTV.setText(String.format(employeesQuantityText, employeesQuantity));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        companyName = getArguments().getString("companyName");
        accessToken = getArguments().getString("accessToken");
        employeesQuantity = getArguments().getInt("total");
        view = inflater.inflate(R.layout.fragment_company, container, false);
        companyNameTV = view.findViewById(R.id.company_fragment_company_name);
        employeesQuantityTV = view.findViewById(R.id.company_fragment_employees_quantity);
        addMerchandiserBtn = view.findViewById(R.id.add_merchandiser_btn);
        addMerchandiserBtn.setOnClickListener(this);
        return view;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_merchandiser_btn: {
                Intent intent = new Intent(getContext(), AddMerchandiserActivity.class);
                intent.putExtra("accessToken", accessToken);
                startActivity(intent);
            } break;
        }
    }
}