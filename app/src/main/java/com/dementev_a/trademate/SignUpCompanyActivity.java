package com.dementev_a.trademate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.requests.RequestErrors;
import com.dementev_a.trademate.requests.RequestStatus;


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
        API api = new API(this);
        api.signUpCompany(handler, nameET, emailET, passwordET);
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            Bundle bundle = msg.getData();
            int status = bundle.getInt(API.STATUS_KEY_BUNDLE);
            if (status == RequestStatus.STATUS_OK) {
                finish();
            } else if (status == RequestStatus.STATUS_ERROR_TEXT) {
                try {
                    errorTV.setText(bundle.getInt(API.ERROR_TEXT_KEY_BUNDLE));
                } catch (NullPointerException e) {
                    errorTV.setText(R.string.global_errors_server_error_text);
                }
            } else {
                errorTV.setText(RequestErrors.globalErrors.get(status));
            }
        }
    };
}