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

    private class Request extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            if (TextUtils.isEmpty(nameET.getText().toString()) || TextUtils.isEmpty(emailET.getText().toString()) || TextUtils.isEmpty(passwordET.getText().toString())) {
                return R.string.global_errors_empty_fields_error_text;
            }

            if (!RequestEngine.isConnectedToInternet(AddMerchandiserActivity.this)) {
                return R.string.global_errors_internet_connection_error_text;
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
                        case "Success":
                            return RESULT_OK;
                        case "Merchandiser with this name is already exist":
                            return R.string.add_merchandiser_activity_merchandiser_exist_with_name_error_text;
                        case "Merchandiser with this email is already exist":
                            return R.string.add_merchandiser_activity_merchandiser_exist_with_email_error_text;
                        case "Password is unreliable":
                            return R.string.add_merchandiser_activity_password_is_unreliable_error_text;
                        case "Email is incorrect":
                            return R.string.add_merchandiser_activity_incorrect_email_error_text;
                        default:
                            return R.string.global_errors_server_error_text;
                    }
                } else
                    return R.string.global_errors_server_error_text;
            } catch (IOException e) {
                return R.string.global_errors_server_error_text;
            }
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