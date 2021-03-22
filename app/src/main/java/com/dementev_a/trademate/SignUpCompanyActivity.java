package com.dementev_a.trademate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.requests.AsyncRequest;
import com.dementev_a.trademate.widgets.AfterRequest;


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
            new AfterRequest(getBundle(), errorTV) {
                @Override
                public void ok() {
                    finish();
                }
            }.execute();
        }

        @Override
        public void sendRequest() {
            API api = new API();
            api.signUpCompany(getBaseContext(), getBundle(), nameET, emailET, passwordET);
        }
    }
}