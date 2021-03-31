package com.dementev_a.trademate;

import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.bundle.BundleEngine;
import com.dementev_a.trademate.intent.IntentConstants;
import com.dementev_a.trademate.json.MerchandiserJson;
import com.dementev_a.trademate.json.RequestJson;
import com.dementev_a.trademate.widgets.ReactOnStatus;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompanyFragment extends Fragment implements View.OnClickListener {
    private TextView shopsQuantityTV, operatorsQuantityTV, employeesQuantityTV, requestsQuantityTV;
    private Button aboutOperatorsBtn, aboutMerchandisersBtn, aboutRequestsBtn, aboutShopsBtn;
    private FloatingActionButton addOperatorBtn, addMerchandiserBtn, addShopBtn;
    private EditText addShopET;
    private ProgressBar PB1, PB2, PB3, PB4;
    private String companyName, accessToken, shopsQuantityText;
    private String[] shops, namesOfOperators, emailsOfOperators;
    private int shopsQuantity;
    private MerchandiserJson[] merchandisers;
    private RequestJson[] requests;
    private API api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_company, container, false);
        TextView companyNameTV = view.findViewById(R.id.company_fragment_company_name);
        employeesQuantityTV = view.findViewById(R.id.company_fragment_merchandisers_quantity);
        operatorsQuantityTV = view.findViewById(R.id.company_fragment_operators_quantity);
        aboutMerchandisersBtn = view.findViewById(R.id.company_fragment_about_merchandisers_btn);
        aboutOperatorsBtn = view.findViewById(R.id.company_fragment_about_operators_btn);
        aboutRequestsBtn = view.findViewById(R.id.company_fragment_about_requests_btn);
        aboutShopsBtn = view.findViewById(R.id.company_fragment_about_shops_btn);
        addMerchandiserBtn = view.findViewById(R.id.company_fragment_add_merchandiser_btn);
        addOperatorBtn = view.findViewById(R.id.company_fragment_add_operator_btn);
        addShopBtn = view.findViewById(R.id.company_fragment_add_shop_btn);
        requestsQuantityTV = view.findViewById(R.id.company_fragment_requests_quantity);
        shopsQuantityTV = view.findViewById(R.id.company_fragment_shops_quantity);
        addShopET = view.findViewById(R.id.company_fragment_add_shop_et);
        PB1 = view.findViewById(R.id.company_fragment_panel_1_progress_bar);
        PB2 = view.findViewById(R.id.company_fragment_panel_2_progress_bar);
        PB3 = view.findViewById(R.id.company_fragment_panel_3_progress_bar);
        PB4 = view.findViewById(R.id.company_fragment_panel_4_progress_bar);

        Bundle bundle = getArguments();
        companyName = bundle.getString(BundleEngine.COMPANY_NAME_KEY_BUNDLE);
        accessToken = bundle.getString(BundleEngine.ACCESS_TOKEN_KEY_BUNDLE);

        companyNameTV.setText(companyName);
        api = new API(getContext(), handler);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        PB1.setVisibility(ProgressBar.VISIBLE);
        PB2.setVisibility(ProgressBar.VISIBLE);
        PB3.setVisibility(ProgressBar.VISIBLE);
        PB4.setVisibility(ProgressBar.VISIBLE);
        api.getMerchandisers(accessToken);
        api.getOperators(accessToken);
        api.getRequestsToday(accessToken);
        api.getShops(accessToken);
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            new ReactOnStatus(bundle, getContext()) {
                @Override
                public void success() {
                    switch (msg.what) {
                        case API.GET_OPERATORS_HANDLER_NUMBER: {
                            int operatorsQuantity = bundle.getInt(BundleEngine.TOTAL_OPERATORS_KEY_BUNDLE);
                            namesOfOperators = bundle.getStringArray(BundleEngine.NAMES_OF_OPERATORS_KEY_BUNDLE);
                            emailsOfOperators = bundle.getStringArray(BundleEngine.EMAILS_OF_OPERATORS_KEY_BUNDLE);
                            String operatorsQuantityText = getString(R.string.company_fragment_operators_quantity_text);
                            operatorsQuantityTV.setText(String.format(operatorsQuantityText, operatorsQuantity));
                            aboutOperatorsBtn.setOnClickListener(CompanyFragment.this);
                            addOperatorBtn.setOnClickListener(CompanyFragment.this);
                            PB2.setVisibility(ProgressBar.INVISIBLE);
                        } break;
                        case API.GET_MERCHANDISERS_HANDLER_NUMBER: {
                            int merchandisersQuantity = bundle.getInt(BundleEngine.TOTAL_MERCHANDISERS_KEY_BUNDLE);
                            merchandisers = (MerchandiserJson[]) bundle.getParcelableArray(BundleEngine.MERCHANDISERS_KEY_BUNDLE);
                            String employeesQuantityText = getString(R.string.company_fragment_employees_quantity_text);
                            employeesQuantityTV.setText(String.format(employeesQuantityText, merchandisersQuantity));
                            aboutMerchandisersBtn.setOnClickListener(CompanyFragment.this);
                            addMerchandiserBtn.setOnClickListener(CompanyFragment.this);
                            PB1.setVisibility(ProgressBar.INVISIBLE);
                        } break;
                        case API.GET_REQUESTS_HANDLER_NUMBER: {
                            int requestsQuantity = bundle.getInt(BundleEngine.TOTAL_REQUESTS_KEY_BUNDLE);
                            requests = (RequestJson[]) bundle.getParcelableArray(BundleEngine.REQUESTS_KEY_BUNDLE);
                            String requestsQuantityText = getString(R.string.company_fragment_requests_quantity_text);
                            requestsQuantityTV.setText(String.format(requestsQuantityText, requestsQuantity));
                            aboutRequestsBtn.setOnClickListener(CompanyFragment.this);
                            PB3.setVisibility(ProgressBar.INVISIBLE);
                        } break;
                        case API.GET_SHOPS_HANDLER_NUMBER: {
                            shopsQuantity = bundle.getInt(BundleEngine.TOTAL_SHOPS_KEY_BUNDLE);
                            shops = bundle.getStringArray(BundleEngine.SHOPS_KEY_BUNDLE);
                            shopsQuantityText = getString(R.string.company_fragment_shops_quantity_text);
                            shopsQuantityTV.setText(String.format(shopsQuantityText, shopsQuantity));
                            addShopBtn.setOnClickListener(CompanyFragment.this);
                            aboutShopsBtn.setOnClickListener(CompanyFragment.this);
                            PB4.setVisibility(ProgressBar.INVISIBLE);
                        } break;
                        case API.ADD_SHOP_HANDLER_NUMBER: {
                            shopsQuantity++;
                            shopsQuantityTV.setText(String.format(shopsQuantityText, shopsQuantity));
                            List<String> list = new ArrayList<>(Arrays.asList(shops));
                            list.add(bundle.getString(BundleEngine.SHOP_NAME_KEY_BUNDLE));
                            shops = list.toArray(new String[0]);
                            PB4.setVisibility(ProgressBar.INVISIBLE);
                            addShopET.setText("");
                        } break;
                    }
                }
            }.execute();
        }
    };

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(@NotNull View v) {
        switch (v.getId()) {
            case R.id.company_fragment_add_merchandiser_btn: {
                Intent intent = new Intent(getContext(), AddMerchandiserActivity.class);
                intent.putExtra(IntentConstants.ACCESS_TOKEN_INTENT_KEY, accessToken);
                startActivity(intent);
            } break;
            case R.id.company_fragment_add_operator_btn: {
                Intent intent = new Intent(getContext(), AddOperatorActivity.class);
                intent.putExtra(IntentConstants.ACCESS_TOKEN_INTENT_KEY, accessToken);
                startActivity(intent);
            } break;
            case R.id.company_fragment_about_merchandisers_btn: {
                Intent intent = new Intent(getContext(), ListActivity.class);
                intent.putExtra(IntentConstants.TYPE_INTENT_KEY, IntentConstants.MERCHANDISERS_DATA_TYPE);
                intent.putExtra("companyName", companyName);
                intent.putExtra("merchandisers", merchandisers);
                startActivity(intent);
            } break;
            case R.id.company_fragment_about_operators_btn: {
                Intent intent = new Intent(getContext(), ListActivity.class);
                intent.putExtra(IntentConstants.TYPE_INTENT_KEY, IntentConstants.OPERATORS_DATA_TYPE);
                intent.putExtra("companyName", companyName);
                intent.putExtra("namesOfOperators", namesOfOperators);
                intent.putExtra("emailsOfOperators", emailsOfOperators);
                startActivity(intent);
            } break;
            case R.id.company_fragment_about_requests_btn: {
                Intent intent = new Intent(getContext(), ListActivity.class);
                intent.putExtra(IntentConstants.TYPE_INTENT_KEY, IntentConstants.REQUESTS_DATA_TYPE);
                intent.putExtra("requests", requests);
                startActivity(intent);
            } break;
            case R.id.company_fragment_add_shop_btn: {
                PB4.setVisibility(ProgressBar.VISIBLE);
                api.addShop(accessToken, addShopET);
            } break;
            case R.id.company_fragment_about_shops_btn: {
                Intent intent = new Intent(getContext(), ListActivity.class);
                intent.putExtra(IntentConstants.TYPE_INTENT_KEY, IntentConstants.SHOPS_DATA_TYPE);
                intent.putExtra("shops", shops);
                startActivity(intent);
            } break;
        }
    }
}