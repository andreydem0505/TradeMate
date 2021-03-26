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
import android.widget.Toast;

import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.bundle.BundleEngine;
import com.dementev_a.trademate.json.JsonEngine;
import com.dementev_a.trademate.json.MerchandiserJson;
import com.dementev_a.trademate.json.RequestJson;
import com.dementev_a.trademate.requests.DataReceiver;
import com.dementev_a.trademate.requests.RequestEngine;
import com.dementev_a.trademate.requests.RequestErrors;
import com.dementev_a.trademate.requests.RequestStatus;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyFragment extends Fragment implements View.OnClickListener {
    private TextView shopsQuantityTV, addShopErrorTV, operatorsQuantityTV, employeesQuantityTV;
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
        Button aboutMerchandisersBtn = view.findViewById(R.id.company_fragment_about_merchandisers_btn);
        Button aboutOperatorsBtn = view.findViewById(R.id.company_fragment_about_operators_btn);
        Button aboutRequestsBtn = view.findViewById(R.id.company_fragment_about_requests_btn);
        Button aboutShopsBtn = view.findViewById(R.id.company_fragment_about_shops_btn);
        FloatingActionButton addMerchandiserBtn = view.findViewById(R.id.company_fragment_add_merchandiser_btn);
        FloatingActionButton addOperatorBtn = view.findViewById(R.id.company_fragment_add_operator_btn);
        FloatingActionButton addShopBtn = view.findViewById(R.id.company_fragment_add_shop_btn);
        TextView requestsQuantityTV = view.findViewById(R.id.company_fragment_requests_quantity);
        shopsQuantityTV = view.findViewById(R.id.company_fragment_shops_quantity);
        addShopET = view.findViewById(R.id.company_fragment_add_shop_et);
        addShopErrorTV = view.findViewById(R.id.company_fragment_add_shop_error_tv);
        addShopPB = view.findViewById(R.id.company_fragment_add_shop_progress_bar);

        aboutMerchandisersBtn.setOnClickListener(this);
        aboutOperatorsBtn.setOnClickListener(this);
        aboutRequestsBtn.setOnClickListener(this);
        addMerchandiserBtn.setOnClickListener(this);
        addOperatorBtn.setOnClickListener(this);
        addShopBtn.setOnClickListener(this);
        aboutShopsBtn.setOnClickListener(this);

        Bundle bundle = getArguments();
        companyName = bundle.getString("companyName");
        accessToken = bundle.getString("accessToken");
//        int merchandisersQuantity = bundle.getInt("total_merchandisers");
//        int operatorsQuantity = bundle.getInt("total_operators");
//        int requestsQuantity = bundle.getInt("total_requests");
//        shopsQuantity = bundle.getInt("total_shops");
//        merchandisers = (MerchandiserJson[]) bundle.getParcelableArray("merchandisers");
//        namesOfOperators = bundle.getStringArray("namesOfOperators");
//        emailsOfOperators = bundle.getStringArray("emailsOfOperators");
//        requests = (RequestJson[]) bundle.getParcelableArray("requests");
//        shops = bundle.getStringArray("shops");

        companyNameTV.setText(companyName);
//        String employeesQuantityText = getString(R.string.company_fragment_employees_quantity_text);
//        employeesQuantityTV.setText(String.format(employeesQuantityText, merchandisersQuantity));
//        String operatorsQuantityText = getString(R.string.company_fragment_operators_quantity_text);
//        operatorsQuantityTV.setText(String.format(operatorsQuantityText, operatorsQuantity));
//        String requestsQuantityText = getString(R.string.company_fragment_requests_quantity_text);
//        requestsQuantityTV.setText(String.format(requestsQuantityText, requestsQuantity));
//        shopsQuantityText = getString(R.string.company_fragment_shops_quantity_text);
//        shopsQuantityTV.setText(String.format(shopsQuantityText, shopsQuantity));
        API api = new API(getContext());
//        api.getMerchandisers(handler, accessToken);
        api.getOperators(handler, accessToken);
        return view;
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            int status = bundle.getInt(API.STATUS_KEY_BUNDLE);
            if (status == RequestStatus.STATUS_OK) {
                switch (msg.what) {
                    case API.GET_OPERATORS_HANDLER_NUMBER: {
                        int operatorsQuantity = bundle.getInt("total_operators");
                        namesOfOperators = bundle.getStringArray("namesOfOperators");
                        emailsOfOperators = bundle.getStringArray("emailsOfOperators");
                        String operatorsQuantityText = getString(R.string.company_fragment_operators_quantity_text);
                        operatorsQuantityTV.setText(String.format(operatorsQuantityText, operatorsQuantity));
                    } break;
                    case API.GET_MERCHANDISERS_HANDLER_NUMBER: {
                        int merchandisersQuantity = bundle.getInt("total_merchandisers");
                        merchandisers = (MerchandiserJson[]) bundle.getParcelableArray("merchandisers");
                        String employeesQuantityText = getString(R.string.company_fragment_employees_quantity_text);
                        employeesQuantityTV.setText(String.format(employeesQuantityText, merchandisersQuantity));
                    } break;
                }
            } else {
                Toast.makeText(getContext(), RequestErrors.globalErrors.get(status), Toast.LENGTH_LONG).show();
            }
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