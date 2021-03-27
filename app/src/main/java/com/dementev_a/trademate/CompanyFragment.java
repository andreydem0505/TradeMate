package com.dementev_a.trademate;

import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.bundle.BundleEngine;
import com.dementev_a.trademate.json.JsonEngine;
import com.dementev_a.trademate.json.MerchandiserJson;
import com.dementev_a.trademate.json.RequestJson;
import com.dementev_a.trademate.requests.DataReceiver;
import com.dementev_a.trademate.requests.RequestEngine;
import com.dementev_a.trademate.requests.RequestStatus;
import com.dementev_a.trademate.widgets.ReactOnStatus;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyFragment extends Fragment implements View.OnClickListener {
    private TextView shopsQuantityTV, addShopErrorTV, operatorsQuantityTV, employeesQuantityTV, requestsQuantityTV;
    private Button aboutOperatorsBtn, aboutMerchandisersBtn, aboutRequestsBtn, aboutShopsBtn;
    private FloatingActionButton addOperatorBtn, addMerchandiserBtn, addShopBtn;
    private EditText addShopET;
    private ProgressBar addShopPB;
    private String companyName, accessToken, shopsQuantityText;
    private String[] shops, namesOfOperators, emailsOfOperators;
    private int shopsQuantity;
    private MerchandiserJson[] merchandisers;
    private RequestJson[] requests;

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
        addShopErrorTV = view.findViewById(R.id.company_fragment_add_shop_error_tv);
        addShopPB = view.findViewById(R.id.company_fragment_add_shop_progress_bar);

        Bundle bundle = getArguments();
        companyName = bundle.getString(BundleEngine.COMPANY_NAME_KEY_BUNDLE);
        accessToken = bundle.getString(BundleEngine.ACCESS_TOKEN_KEY_BUNDLE);

        companyNameTV.setText(companyName);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        API api = new API(getContext(), handler);
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
                        } break;
                        case API.GET_MERCHANDISERS_HANDLER_NUMBER: {
                            int merchandisersQuantity = bundle.getInt(BundleEngine.TOTAL_MERCHANDISERS_KEY_BUNDLE);
                            merchandisers = (MerchandiserJson[]) bundle.getParcelableArray(BundleEngine.MERCHANDISERS_KEY_BUNDLE);
                            String employeesQuantityText = getString(R.string.company_fragment_employees_quantity_text);
                            employeesQuantityTV.setText(String.format(employeesQuantityText, merchandisersQuantity));
                            aboutMerchandisersBtn.setOnClickListener(CompanyFragment.this);
                            addMerchandiserBtn.setOnClickListener(CompanyFragment.this);
                        } break;
                        case API.GET_REQUESTS_HANDLER_NUMBER: {
                            int requestsQuantity = bundle.getInt(BundleEngine.TOTAL_REQUESTS_KEY_BUNDLE);
                            requests = (RequestJson[]) bundle.getParcelableArray(BundleEngine.REQUESTS_KEY_BUNDLE);
                            String requestsQuantityText = getString(R.string.company_fragment_requests_quantity_text);
                            requestsQuantityTV.setText(String.format(requestsQuantityText, requestsQuantity));
                            aboutRequestsBtn.setOnClickListener(CompanyFragment.this);
                        } break;
                        case API.GET_SHOPS_HANDLER_NUMBER: {
                            shopsQuantity = bundle.getInt(BundleEngine.TOTAL_SHOPS_KEY_BUNDLE);
                            shops = bundle.getStringArray(BundleEngine.SHOPS_KEY_BUNDLE);
                            shopsQuantityText = getString(R.string.company_fragment_shops_quantity_text);
                            shopsQuantityTV.setText(String.format(shopsQuantityText, shopsQuantity));
                            addShopBtn.setOnClickListener(CompanyFragment.this);
                            aboutShopsBtn.setOnClickListener(CompanyFragment.this);
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
                intent.putExtra("namesOfOperators", namesOfOperators);
                intent.putExtra("emailsOfOperators", emailsOfOperators);
                startActivity(intent);
            } break;
            case R.id.company_fragment_about_requests_btn: {
                Intent intent = new Intent(getContext(), ListActivity.class);
                intent.putExtra("type", "requests");
                intent.putExtra("requests", requests);
                startActivity(intent);
            } break;
            case R.id.company_fragment_add_shop_btn: {
                addShopErrorTV.setText("");
                addShopPB.setVisibility(ProgressBar.VISIBLE);
                new ConcurrentAddShop().execute();
            } break;
            case R.id.company_fragment_about_shops_btn: {
                Intent intent = new Intent(getContext(), ListActivity.class);
                intent.putExtra("type", "shops");
                intent.putExtra("shops", shops);
                startActivity(intent);
            } break;
        }
    }

    private class ConcurrentAddShop extends DataReceiver {

        protected ConcurrentAddShop() {
            super();
        }

        @Override
        public void UIWork() {
            int status = getBundle().getInt("status");
            switch (status) {
                case RequestStatus.STATUS_OK: {
                    shopsQuantityTV.setText(String.format(shopsQuantityText, shopsQuantity));
                } break;
                case RequestStatus.STATUS_ERROR_TEXT: {
                    addShopErrorTV.setText(getBundle().getInt("error_text"));
                } break;
                case RequestStatus.STATUS_INTERNET_ERROR: {
                    addShopErrorTV.setText(R.string.global_errors_internet_connection_error_text);
                } break;
                case RequestStatus.STATUS_SERVER_ERROR: {
                    addShopErrorTV.setText(R.string.global_errors_server_error_text);
                } break;
                case RequestStatus.STATUS_EMPTY_FIELDS: {
                    addShopErrorTV.setText(R.string.global_errors_empty_fields_error_text);
                } break;
            }
            addShopPB.setVisibility(ProgressBar.INVISIBLE);
        }

        @Override
        public void sendRequests() {
            if (TextUtils.isEmpty(addShopET.getText())) {
                getBundle().putInt("status", RequestStatus.STATUS_EMPTY_FIELDS);
                return;
            }

            if (!RequestEngine.isConnectedToInternet(requireContext())) {
                getBundle().putInt("status", RequestStatus.STATUS_INTERNET_ERROR);
                return;
            }

            String name = addShopET.getText().toString();

            String url = API.MAIN_URL + API.ADD_SHOP_URL;
            String json = String.format("{\"name\": \"%s\"}", name);
            try {
                Map<String, String> headers = new HashMap<>();
                headers.put("access_token", accessToken);
                String response = RequestEngine.makePostRequestWithJson(url, json, headers);
                if (response != null) {
                    String message = JsonEngine.getStringFromJson(response, "message");
                    switch (message) {
                        case "Success": {
                            getBundle().putInt("status", RequestStatus.STATUS_OK);
                            shopsQuantity++;
                            List<String> list = new ArrayList<>(Arrays.asList(shops));
                            list.add(name);
                            shops = list.toArray(new String[0]);
                        } break;
                        case "Such shop is already exist": {
                            BundleEngine.putError(getBundle(), R.string.company_fragment_shop_exist_error);
                        } break;
                        default: {
                            getBundle().putInt("status", RequestStatus.STATUS_SERVER_ERROR);
                        }
                    }
                } else
                    getBundle().putInt("status", RequestStatus.STATUS_SERVER_ERROR);
            } catch (IOException e) {
                getBundle().putInt("status", RequestStatus.STATUS_SERVER_ERROR);
            }
        }
    }
}