package com.dementev_a.trademate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.requests.AsyncRequest;
import com.dementev_a.trademate.requests.RequestStatus;
import com.dementev_a.trademate.bundle.BundleEngine;
import com.dementev_a.trademate.json.JsonEngine;
import com.dementev_a.trademate.preferences.SharedPreferencesEngine;
import com.dementev_a.trademate.requests.RequestEngine;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SignUpCompanyActivity extends AppCompatActivity {
    private EditText nameET, emailET, passwordET;
    private ProgressBar progressBar;
    private TextView errorTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_company);
        nameET = findViewById(R.id.sign_up_company_activity_name_et);
        emailET = findViewById(R.id.sign_up_company_activity_email_et);
        passwordET = findViewById(R.id.sign_up_company_activity_password_et);
        progressBar = findViewById(R.id.sign_up_company_activity_progress_bar);
        errorTV = findViewById(R.id.sign_up_company_activity_error_tv);
    }

    public void onSignUpClickBtn(View v) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        new ConcurrentSignUpCompany().execute();
    }


    private class ConcurrentSignUpCompany extends AsyncRequest {

        protected ConcurrentSignUpCompany() {
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
        public void sendRequest() {
            if (TextUtils.isEmpty(nameET.getText()) || TextUtils.isEmpty(emailET.getText()) || TextUtils.isEmpty(passwordET.getText())) {
                getBundle().putInt("status", RequestStatus.STATUS_EMPTY_FIELDS);
                return;
            }

            if (!RequestEngine.isConnectedToInternet(SignUpCompanyActivity.this)) {
                getBundle().putInt("status", RequestStatus.STATUS_INTERNET_ERROR);
                return;
            }

            String name = nameET.getText().toString();
            String email = emailET.getText().toString();
            String password = passwordET.getText().toString();

            String url = API.MAIN_URL + API.SIGN_UP_COMPANY_URL;
            String json = String.format("{\"name\": \"%s\"," +
                    "\"password\": \"%s\"," +
                    "\"email\": \"%s\"" +
                    "}", name, password, email);
            try {
                String response = RequestEngine.makePostRequestWithJson(url, json);
                if (response != null) {
                    String message = JsonEngine.getStringFromJson(response, "message");
                    switch (message) {
                        case "Success": {
                            String accessToken = JsonEngine.getStringFromJson(response, "accessToken");
                            SharedPreferencesEngine spe = new SharedPreferencesEngine(SignUpCompanyActivity.this, getString(R.string.shared_preferences_user));
                            spe.saveUser(getString(R.string.shared_preferences_type_company), name, email, accessToken);
                            getBundle().putInt("status", RequestStatus.STATUS_OK);
                        } break;
                        case "Such company is already exist": {
                            BundleEngine.putError(getBundle(), R.string.sign_up_company_activity_company_with_this_name_exist_error_text);
                        } break;
                        case "Company with this email is already exist": {
                            BundleEngine.putError(getBundle(), R.string.sign_up_company_activity_company_with_this_email_exist_error_text);
                        } break;
                        case "Password is unreliable": {
                            BundleEngine.putError(getBundle(), R.string.sign_up_company_activity_password_is_unreliable_error_text);
                        } break;
                        case "Email is incorrect": {
                            BundleEngine.putError(getBundle(), R.string.sign_up_company_activity_incorrect_email_error_text);
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