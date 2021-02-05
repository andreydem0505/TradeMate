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
        new Request().execute();
    }

    private class Request extends AsyncTask<Void, Void, Bundle> {
        @Override
        protected Bundle doInBackground(Void... voids) {
            Bundle bundle = new Bundle();

            if (TextUtils.isEmpty(nameET.getText().toString()) || TextUtils.isEmpty(emailET.getText().toString()) || TextUtils.isEmpty(passwordET.getText().toString())) {
                bundle.putInt("status", RequestStatus.STATUS_EMPTY_FIELDS);
                return bundle;
            }

            if (!RequestEngine.isConnectedToInternet(AddMerchandiserActivity.this)) {
                bundle.putInt("status", RequestStatus.STATUS_INTERNET_ERROR);
                return bundle;
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
                    JsonEngine jsonEngine = new JsonEngine();
                    String message = jsonEngine.getStringFromJson(response, "message");
                    switch (message) {
                        case "Success": {
                            bundle.putInt("status", RequestStatus.STATUS_OK);
                        } break;
                        case "Merchandiser with this name is already exist": {
                            BundleEngine.putError(bundle, R.string.add_merchandiser_activity_merchandiser_exist_with_name_error_text);
                        } break;
                        case "Merchandiser with this email is already exist": {
                            BundleEngine.putError(bundle, R.string.add_merchandiser_activity_merchandiser_exist_with_email_error_text);
                        } break;
                        case "Password is unreliable": {
                            BundleEngine.putError(bundle, R.string.add_merchandiser_activity_password_is_unreliable_error_text);
                        } break;
                        case "Email is incorrect": {
                            BundleEngine.putError(bundle, R.string.add_merchandiser_activity_incorrect_email_error_text);
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