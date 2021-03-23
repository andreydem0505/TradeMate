package com.dementev_a.trademate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.requests.DataReceiver;
import com.dementev_a.trademate.requests.RequestStatus;
import com.dementev_a.trademate.bundle.BundleEngine;
import com.dementev_a.trademate.json.JsonEngine;
import com.dementev_a.trademate.requests.RequestEngine;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddMerchandiserActivity extends AppCompatActivity {
    private EditText nameET, emailET, passwordET;
    private ProgressBar progressBar;
    private TextView errorTV;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_merchandiser);
        nameET = findViewById(R.id.add_merchandiser_activity_name_et);
        emailET = findViewById(R.id.add_merchandiser_activity_email_et);
        passwordET = findViewById(R.id.add_merchandiser_activity_password_et);
        progressBar = findViewById(R.id.add_merchandiser_activity_progress_bar);
        errorTV = findViewById(R.id.add_merchandiser_activity_error_tv);
        accessToken = getIntent().getStringExtra("accessToken");
    }

    public void onAddMerchandiserClickBtn(View v) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        new ConcurrentAddMerchandiser().execute();
    }


    private class ConcurrentAddMerchandiser extends DataReceiver {

        protected ConcurrentAddMerchandiser() {
            super();
        }

        @Override
        public void UIWork() {
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            int status = getBundle().getInt("status");
            switch (status) {
                case RequestStatus.STATUS_OK: {
                    finish();
                } break;
                case RequestStatus.STATUS_ERROR_TEXT: {
                    errorTV.setText(getBundle().getInt("error_text"));
                } break;
                case RequestStatus.STATUS_INTERNET_ERROR: {
                    errorTV.setText(R.string.global_errors_internet_connection_error_text);
                } break;
                case RequestStatus.STATUS_SERVER_ERROR: {
                    errorTV.setText(R.string.global_errors_server_error_text);
                } break;
                case RequestStatus.STATUS_EMPTY_FIELDS: {
                    errorTV.setText(R.string.global_errors_empty_fields_error_text);
                } break;
            }
        }

        @Override
        public void sendRequests() {
            if (TextUtils.isEmpty(nameET.getText()) || TextUtils.isEmpty(emailET.getText()) || TextUtils.isEmpty(passwordET.getText())) {
                getBundle().putInt("status", RequestStatus.STATUS_EMPTY_FIELDS);
                return;
            }

            if (!RequestEngine.isConnectedToInternet(AddMerchandiserActivity.this)) {
                getBundle().putInt("status", RequestStatus.STATUS_INTERNET_ERROR);
                return;
            }

            String name = nameET.getText().toString();
            String email = emailET.getText().toString();
            String password = passwordET.getText().toString();

            String url = API.MAIN_URL + API.ADD_MERCHANDISER_URL;
            String json = String.format("{\"name\": \"%s\"," +
                    "\"password\": \"%s\"," +
                    "\"email\": \"%s\"" +
                    "}", name, password, email);
            try {
                Map<String, String> headers = new HashMap<>();
                headers.put("access_token", accessToken);
                String response = RequestEngine.makePostRequestWithJson(url, json, headers);
                if (response != null) {
                    String message = JsonEngine.getStringFromJson(response, "message");
                    switch (message) {
                        case "Success": {
                            getBundle().putInt("status", RequestStatus.STATUS_OK);
                        } break;
                        case "Merchandiser with this name is already exist": {
                            BundleEngine.putError(getBundle(), R.string.add_merchandiser_activity_merchandiser_exist_with_name_error_text);
                        } break;
                        case "Merchandiser with this email is already exist": {
                            BundleEngine.putError(getBundle(), R.string.add_merchandiser_activity_merchandiser_exist_with_email_error_text);
                        } break;
                        case "Password is unreliable": {
                            BundleEngine.putError(getBundle(), R.string.add_merchandiser_activity_password_is_unreliable_error_text);
                        } break;
                        case "Email is incorrect": {
                            BundleEngine.putError(getBundle(), R.string.add_merchandiser_activity_incorrect_email_error_text);
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