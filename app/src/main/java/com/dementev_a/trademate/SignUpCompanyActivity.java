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
        new Request().execute();
    }


    private class Request extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            if (TextUtils.isEmpty(nameET.getText().toString()) || TextUtils.isEmpty(emailET.getText().toString()) || TextUtils.isEmpty(passwordET.getText().toString())) {
                return R.string.global_errors_empty_fields_error_text;
            }

            if (!RequestEngine.isConnectedToInternet(SignUpCompanyActivity.this)) {
                return R.string.global_errors_internet_connection_error_text;
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
                    JsonEngine jsonEngine = new JsonEngine();
                    String message = jsonEngine.getStringFromJson(response, "message");
                    switch (message) {
                        case "Success": {
                            String accessToken = jsonEngine.getStringFromJson(response, "accessToken");
                            SharedPreferencesEngine spe = new SharedPreferencesEngine(SignUpCompanyActivity.this, getString(R.string.shared_preferences_user));
                            spe.saveUser(getString(R.string.shared_preferences_type_company), name, email, accessToken);
                            return RESULT_OK;
                        }
                        case "Such company is already exist":
                            return R.string.sign_up_company_activity_company_exist_error_text;
                        case "Password is unreliable":
                            return R.string.sign_up_company_activity_password_is_unreliable_error_text;
                        case "Email is incorrect":
                            return R.string.sign_up_company_activity_incorrect_email_error_text;
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