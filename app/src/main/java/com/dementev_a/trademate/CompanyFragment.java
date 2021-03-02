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
import com.dementev_a.trademate.json.RequestJson;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

public class CompanyFragment extends Fragment implements View.OnClickListener {
    private View view;
    private TextView companyNameTV, employeesQuantityTV, operatorsQuantityTV, requestsQuantityTV;
    private Button aboutMerchandisersBtn, aboutOperatorsBtn, aboutRequestsBtn;
    private FloatingActionButton addMerchandiserBtn, addOperatorBtn;
    private String companyName, accessToken;
    private int merchandisersQuantity, operatorsQuantity, requestsQuantity;
    private MerchandiserJson[] merchandisers;
    private OperatorJson[] operators;
    private RequestJson[] requests;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_company, container, false);
        companyNameTV = view.findViewById(R.id.company_fragment_company_name);
        employeesQuantityTV = view.findViewById(R.id.company_fragment_merchandisers_quantity);
        operatorsQuantityTV = view.findViewById(R.id.company_fragment_operators_quantity);
        aboutMerchandisersBtn = view.findViewById(R.id.company_fragment_about_merchandisers_btn);
        aboutOperatorsBtn = view.findViewById(R.id.company_fragment_about_operators_btn);
        aboutRequestsBtn = view.findViewById(R.id.company_fragment_about_requests_btn);
        addMerchandiserBtn = view.findViewById(R.id.company_fragment_add_merchandiser_btn);
        addOperatorBtn = view.findViewById(R.id.company_fragment_add_operator_btn);
        requestsQuantityTV = view.findViewById(R.id.company_fragment_requests_quantity);

        aboutMerchandisersBtn.setOnClickListener(this);
        aboutOperatorsBtn.setOnClickListener(this);
        aboutRequestsBtn.setOnClickListener(this);
        addMerchandiserBtn.setOnClickListener(this);
        addOperatorBtn.setOnClickListener(this);

        Bundle bundle = getArguments();
        companyName = bundle.getString("companyName");
        accessToken = bundle.getString("accessToken");
        merchandisersQuantity = bundle.getInt("total_merchandisers");
        operatorsQuantity = bundle.getInt("total_operators");
        requestsQuantity = bundle.getInt("total_requests");
        merchandisers = (MerchandiserJson[]) bundle.getParcelableArray("merchandisers");
        operators = (OperatorJson[]) bundle.getParcelableArray("operators");
        requests = (RequestJson[]) bundle.getParcelableArray("requests");

        companyNameTV.setText(companyName);
        String employeesQuantityText = getString(R.string.company_fragment_employees_quantity_text);
        employeesQuantityTV.setText(String.format(employeesQuantityText, merchandisersQuantity));
        String operatorsQuantityText = getString(R.string.company_fragment_operators_quantity_text);
        operatorsQuantityTV.setText(String.format(operatorsQuantityText, operatorsQuantity));
        String requestsQuantityText = getString(R.string.company_fragment_requests_quantity_text);
        requestsQuantityTV.setText(String.format(requestsQuantityText, requestsQuantity));

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
                Intent intent = new Intent(getContext(), ListActivity.class);
                intent.putExtra("type", "merchandisers");
                intent.putExtra("companyName", companyName);
                intent.putExtra("merchandisers", merchandisers);
                startActivity(intent);
            } break;
            case R.id.company_fragment_about_operators_btn: {
                Intent intent = new Intent(getContext(), ListActivity.class);
                intent.putExtra("type", "operators");
                intent.putExtra("companyName", companyName);
                intent.putExtra("operators", operators);
                startActivity(intent);
            } break;
            case R.id.company_fragment_about_requests_btn: {
                Intent intent = new Intent(getContext(), ListActivity.class);
                intent.putExtra("type", "requests");
                intent.putExtra("requests", requests);
                startActivity(intent);
            } break;
        }
    }
}