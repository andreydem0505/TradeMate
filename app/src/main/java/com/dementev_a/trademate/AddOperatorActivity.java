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

public class AddOperatorActivity extends AppCompatActivity {
    private EditText nameET, emailET;
    private ProgressBar progressBar;
    private TextView errorTV;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_operator);
        nameET = findViewById(R.id.add_operator_activity_name_et);
        emailET = findViewById(R.id.add_operator_activity_email_et);
        progressBar = findViewById(R.id.add_operator_activity_progress_bar);
        errorTV = findViewById(R.id.add_operator_activity_error_tv);
        accessToken = getIntent().getStringExtra("accessToken");
    }

    public void onAddClickBtn(View v) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        new Request().execute();
    }


    private class Request extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            if (TextUtils.isEmpty(nameET.getText().toString()) || TextUtils.isEmpty(emailET.getText().toString())) {
                return R.string.global_errors_empty_fields_error_text;
            }

            if (!RequestEngine.isConnectedToInternet(AddOperatorActivity.this)) {
                return R.string.global_errors_internet_connection_error_text;
            }

            String name = nameET.getText().toString();
            String email = emailET.getText().toString();

            String url = API.API_URL + "/register/operator";
            String json = String.format("{\"name\": \"%s\"," +
                    "\"email\": \"%s\"" +
                    "}", name, email);
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
                        case "Operator with this name is already exist":
                            return R.string.add_operator_activity_operator_with_name_exist_error_text;
                        case "Operator with this email is already exist":
                            return R.string.add_operator_activity_operator_with_email_exist_error_text;
                        case "Email is incorrect":
                            return R.string.add_operator_activity_incorrect_email_error_text;
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