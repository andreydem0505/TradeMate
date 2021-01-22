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
import com.dementev_a.trademate.json.JsonEngine;
import com.dementev_a.trademate.preferences.SharedPreferencesEngine;
import com.dementev_a.trademate.requests.RequestEngine;

import java.io.IOException;

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
        new Request().execute();
    }

    private class Request extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            if (TextUtils.isEmpty(emailET.getText().toString()) || TextUtils.isEmpty(passwordET.getText().toString())) {
                return R.string.global_errors_empty_fields_error_text;
            }

            if (!RequestEngine.isConnectedToInternet(LogInMerchandiserActivity.this)) {
                return R.string.global_errors_internet_connection_error_text;
            }

            String email = emailET.getText().toString();
            String password = passwordET.getText().toString();

            String url = API.API_URL + "/auth/merchandiser";
            String json = String.format("{\"email\": \"%s\"," +
                    "\"password\": \"%s\"" +
                    "}", email, password);
            try {
                String response = RequestEngine.makePostRequestWithJson(url, json);
                if (response != null) {
                    JsonEngine jsonEngine = new JsonEngine();
                    String message = jsonEngine.getStringFromJson(response, "message");
                    switch (message) {
                        case "Success": {
                            String name = jsonEngine.getStringFromJson(response, "name");
                            String accessToken = jsonEngine.getStringFromJson(response, "accessToken");
                            SharedPreferencesEngine spe = new SharedPreferencesEngine(LogInMerchandiserActivity.this, getString(R.string.shared_preferences_user));
                            spe.saveUser(getString(R.string.shared_preferences_type_merchandiser), name, email, accessToken);
                            return RESULT_OK;
                        }
                        case "Merchandiser with this email wasn't found":
                            return R.string.log_in_merchandiser_activity_merchandiser_was_not_found_error_text;
                        case "Password is incorrect":
                            return R.string.log_in_merchandiser_activity_wrong_password_error_text;
                    }
                } else
                    return R.string.global_errors_server_error_text;
            } catch (IOException e) {
                return R.string.global_errors_server_error_text;
            }
            return R.string.global_errors_server_error_text;
        }

        @Override
        protected void onPostExecute(Integer code) {
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            if (code != RESULT_OK)
                errorTV.setText(code);
            else
                finish();
        }
    }
}