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

import com.dementev_a.trademate.json.MerchandiserJson;
import com.dementev_a.trademate.json.OperatorJson;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

public class CompanyFragment extends Fragment implements View.OnClickListener {
    private View view;
    private TextView companyNameTV, employeesQuantityTV, operatorsQuantityTV;
    private Button aboutMerchandisersBtn, aboutOperatorsBtn;
    private FloatingActionButton addMerchandiserBtn, addOperatorBtn;
    private String companyName, accessToken;
    private int merchandisersQuantity, operatorsQuantity;
    private MerchandiserJson[] merchandisers;
    private OperatorJson[] operators;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        companyName = bundle.getString("companyName");
        accessToken = bundle.getString("accessToken");
        merchandisersQuantity = bundle.getInt("total_merchandisers");
        operatorsQuantity = bundle.getInt("total_operators");
        merchandisers = (MerchandiserJson[]) bundle.getParcelableArray("merchandisers");
        operators = (OperatorJson[]) bundle.getParcelableArray("operators");

        view = inflater.inflate(R.layout.fragment_company, container, false);
        companyNameTV = view.findViewById(R.id.company_fragment_company_name);
        employeesQuantityTV = view.findViewById(R.id.company_fragment_merchandisers_quantity);
        operatorsQuantityTV = view.findViewById(R.id.company_fragment_operators_quantity);
        aboutMerchandisersBtn = view.findViewById(R.id.company_fragment_about_merchandisers_btn);
        aboutMerchandisersBtn.setOnClickListener(this);
        aboutOperatorsBtn = view.findViewById(R.id.company_fragment_about_operators_btn);
        aboutOperatorsBtn.setOnClickListener(this);
        addMerchandiserBtn = view.findViewById(R.id.company_fragment_add_merchandiser_btn);
        addMerchandiserBtn.setOnClickListener(this);
        addOperatorBtn = view.findViewById(R.id.company_fragment_add_operator_btn);
        addOperatorBtn.setOnClickListener(this);

        companyNameTV.setText(companyName);
        String employeesQuantityText = getString(R.string.company_fragment_employees_quantity_text);
        employeesQuantityTV.setText(String.format(employeesQuantityText, merchandisersQuantity));
        String operatorsQuantityText = getString(R.string.company_fragment_operators_quantity_text);
        operatorsQuantityTV.setText(String.format(operatorsQuantityText, operatorsQuantity));

        return view;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(@NotNull View v) {
        switch (v.getId()) {
            case R.id.company_fragment_add_merchandiser_btn: {
                Intent intent = new Intent(getContext(), AddMerchandiserActivity.class);
                intent.putExtra("accessToken", accessToken);
                startActivity(intent);
            } break;
            case R.id.company_fragment_add_operator_btn: {
                Intent intent = new Intent(getContext(), AddOperatorActivity.class);
                intent.putExtra("accessToken", accessToken);
                startActivity(intent);
            } break;
            case R.id.company_fragment_about_merchandisers_btn: {
                Intent intent = new Intent(getContext(), AboutEmployeesActivity.class);
                intent.putExtra("type", "merchandisers");
                intent.putExtra("merchandisers", merchandisers);
                startActivity(intent);
            } break;
            case R.id.company_fragment_about_operators_btn: {
                Intent intent = new Intent(getContext(), AboutEmployeesActivity.class);
                intent.putExtra("type", "operators");
                intent.putExtra("operators", operators);
                startActivity(intent);
            } break;
        }
    }
}