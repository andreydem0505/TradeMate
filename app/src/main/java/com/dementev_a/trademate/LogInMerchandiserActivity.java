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

public class LogInMerchandiserActivity extends AppCompatActivity {
    private EditText emailET, passwordET;
    private ProgressBar progressBar;
    private TextView errorTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_merchandiser);
        emailET = findViewById(R.id.log_in_merchandiser_activity_email_et);
        passwordET = findViewById(R.id.log_in_merchandiser_activity_password_et);
        progressBar = findViewById(R.id.log_in_merchandiser_activity_progress_bar);
        errorTV = findViewById(R.id.log_in_merchandiser_activity_error_tv);
    }

    public void onLogInClickBtn(View v) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        new ConcurrentLogInMerchandiser().execute();
    }


    private class ConcurrentLogInMerchandiser implements AsyncRequest {
        private final Bundle bundle;

        protected ConcurrentLogInMerchandiser() {
            bundle = new Bundle();
        }

        @Override
        public void execute() {
            Executor executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                sendRequest();
                handler.post(() -> {
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
                });
            });
        }

        @Override
        public void sendRequest() {
            if (TextUtils.isEmpty(emailET.getText()) || TextUtils.isEmpty(passwordET.getText())) {
                bundle.putInt("status", RequestStatus.STATUS_EMPTY_FIELDS);
                return;
            }

            if (!RequestEngine.isConnectedToInternet(LogInMerchandiserActivity.this)) {
                bundle.putInt("status", RequestStatus.STATUS_INTERNET_ERROR);
                return;
            }

            String email = emailET.getText().toString();
            String password = passwordET.getText().toString();

            String url = API.MAIN_URL + API.AUTH_MERCHANDISER_URL;
            String json = String.format("{\"email\": \"%s\"," +
                    "\"password\": \"%s\"" +
                    "}", email, password);
            try {
                String response = RequestEngine.makePostRequestWithJson(url, json);
                if (response != null) {
                    String message = JsonEngine.getStringFromJson(response, "message");
                    switch (message) {
                        case "Success": {
                            String name = JsonEngine.getStringFromJson(response, "name");
                            String accessToken = JsonEngine.getStringFromJson(response, "accessToken");
                            SharedPreferencesEngine spe = new SharedPreferencesEngine(LogInMerchandiserActivity.this, getString(R.string.shared_preferences_user));
                            spe.saveUser(getString(R.string.shared_preferences_type_merchandiser), name, email, accessToken);
                            bundle.putInt("status", RequestStatus.STATUS_OK);
                        } break;
                        case "Merchandiser with this email wasn't found": {
                            BundleEngine.putError(bundle, R.string.log_in_merchandiser_activity_merchandiser_was_not_found_error_text);
                        } break;
                        case "Password is incorrect": {
                            BundleEngine.putError(bundle, R.string.log_in_merchandiser_activity_wrong_password_error_text);
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
        }
    }
}