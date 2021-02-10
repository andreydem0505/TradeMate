package com.dementev_a.trademate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.api.RequestStatus;
import com.dementev_a.trademate.bundle.BundleEngine;
import com.dementev_a.trademate.json.JsonEngine;
import com.dementev_a.trademate.preferences.SharedPreferencesEngine;
import com.dementev_a.trademate.requests.RequestEngine;

import java.io.IOException;

public class LogInCompanyActivity extends AppCompatActivity {
    private EditText emailET, passwordET;
    private ProgressBar progressBar;
    private TextView errorTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_company);
        emailET = findViewById(R.id.log_in_company_activity_email_et);
        passwordET = findViewById(R.id.log_in_company_activity_password_et);
        progressBar = findViewById(R.id.log_in_company_activity_progress_bar);
        errorTV = findViewById(R.id.log_in_company_activity_error_tv);
    }

    public void onLogInClickBtn(View v) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        new Request().execute();
    }

    private class Request extends AsyncTask<Void, Void, Bundle> {
        @Override
        protected Bundle doInBackground(Void... voids) {
            Bundle bundle = new Bundle();

            if (TextUtils.isEmpty(emailET.getText()) || TextUtils.isEmpty(passwordET.getText())) {
                bundle.putInt("status", RequestStatus.STATUS_EMPTY_FIELDS);
                return bundle;
            }

            if (!RequestEngine.isConnectedToInternet(LogInCompanyActivity.this)) {
                bundle.putInt("status", RequestStatus.STATUS_INTERNET_ERROR);
                return bundle;
            }

            String email = emailET.getText().toString();
            String password = passwordET.getText().toString();

            String url = API.MAIN_URL + API.AUTH_COMPANY_URL;
            String json = String.format("{\"email\": \"%s\"," +
                    "\"password\": \"%s\"}",
                    email, password);
            try {
                String response = RequestEngine.makePostRequestWithJson(url, json);
                if (response != null) {
                    JsonEngine jsonEngine = new JsonEngine();
                    String message = jsonEngine.getStringFromJson(response, "message");
                    switch (message) {
                        case "Success": {
                            String name = jsonEngine.getStringFromJson(response, "name");
                            String accessToken = jsonEngine.getStringFromJson(response, "accessToken");
                            SharedPreferencesEngine spe = new SharedPreferencesEngine(LogInCompanyActivity.this, getString(R.string.shared_preferences_user));
                            spe.saveUser(getString(R.string.shared_preferences_type_company), name, email, accessToken);
                            bundle.putInt("status", RequestStatus.STATUS_OK);
                        } break;
                        case "Company with this email wasn't found": {
                            BundleEngine.putError(bundle, R.string.log_in_company_activity_company_was_not_found_error_text);
                        } break;
                        case "Password is incorrect": {
                            BundleEngine.putError(bundle, R.string.log_in_company_activity_wrong_password_error_text);
                        } break;
                        default: {
                            bundle.putInt("status", RequestStatus.STATUS_SERVER_ERROR);
                        }
                    }
                } else
                    bundle.putInt("status", RequestStatus.STATUS_SERVER_ERROR);
            } catch (IOException e) {
                bundle.putInt("status", RequestStatus.STATUS_SERVER_ERROR);
            }

            return bundle;
        }

        @Override
        protected void onPostExecute(Bundle bundle) {
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            int status = bundle.getInt("status");
            switch (status) {
                case RequestStatus.STATUS_OK: {
                    finish();
                } break;
                case RequestStatus.STATUS_ERROR_TEXT: {
                    errorTV.setText(bundle.getInt("error_text"));
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
    }
}