package com.dementev_a.trademate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.api.RequestStatus;
import com.dementev_a.trademate.json.RequestJson;
import com.dementev_a.trademate.preferences.SharedPreferencesEngine;
import com.dementev_a.trademate.requests.RequestEngine;
import com.dementev_a.trademate.widgets.WidgetsEngine;

import java.util.HashMap;
import java.util.Map;

public class AboutMerchandiserActivity extends AppCompatActivity {
    private TextView headerTV, emailTV, passwordTV, errorTV;
    private Button passwordBtn;
    private ListView listView;
    private ProgressBar progressBar;
    private boolean passwordIsShowed;
    private String password, merchandiserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_merchandiser);
        headerTV = findViewById(R.id.about_merchandiser_activity_header_tv);
        emailTV = findViewById(R.id.about_merchandiser_activity_email_tv);
        passwordTV = findViewById(R.id.about_merchandiser_activity_password_tv);
        passwordBtn = findViewById(R.id.about_merchandiser_activity_password_btn);
        listView = findViewById(R.id.about_merchandiser_activity_list_view);
        progressBar = findViewById(R.id.about_merchandiser_activity_progress_bar);
        errorTV = findViewById(R.id.about_merchandiser_activity_error_tv);

        new SetRequests().execute(new SharedPreferencesEngine(this, getString(R.string.shared_preferences_user)));
        merchandiserName = getIntent().getStringExtra("name");
        headerTV.setText(merchandiserName);
        String emailText = getString(R.string.about_merchandiser_activity_email_tv_text);
        emailTV.setText(String.format(emailText, getIntent().getStringExtra("email")));
        password = getIntent().getStringExtra("password");
        passwordIsShowed = false;
        showPassword();
    }

    private void showPassword() {
        String passwordText = getString(R.string.about_merchandiser_activity_password_tv_text);
        if (passwordIsShowed) {
            passwordTV.setText(String.format(passwordText, password));
            passwordBtn.setText(R.string.about_merchandiser_activity_password_btn_hide_text);
        } else {
            StringBuilder dotes = new StringBuilder();
            for (int i = 0; i < password.length(); i++)
                dotes.append("*");
            passwordTV.setText(String.format(passwordText, dotes.toString()));
            passwordBtn.setText(R.string.about_merchandiser_activity_password_btn_show_text);
        }
    }

    public void onPasswordBtnClick(View v) {
        passwordIsShowed = !passwordIsShowed;
        showPassword();
    }

    private class SetRequests extends AsyncTask<SharedPreferencesEngine, Void, Bundle> {

        @Override
        protected Bundle doInBackground(SharedPreferencesEngine... spe) {
            Bundle bundle = new Bundle();

            if (!RequestEngine.isConnectedToInternet(AboutMerchandiserActivity.this)) {
                bundle.putInt("status", RequestStatus.STATUS_INTERNET_ERROR);
                return bundle;
            }

            Map<String, String> headers = new HashMap<>();
            headers.put("access_token", spe[0].getString("accessToken"));

            API.getRequests(bundle, headers, merchandiserName);

            return bundle;
        }

        @Override
        protected void onPostExecute(Bundle bundle) {
            int status = bundle.getInt("status");
            switch (status) {
                case RequestStatus.STATUS_OK: {
                    WidgetsEngine.setRequestsOnListView(bundle.getParcelableArray("requests"), listView, AboutMerchandiserActivity.this, errorTV);
                } break;
                case RequestStatus.STATUS_SERVER_ERROR: {
                    errorTV.setText(R.string.global_errors_server_error_text);
                } break;
                case RequestStatus.STATUS_INTERNET_ERROR: {
                    errorTV.setText(R.string.global_errors_internet_connection_error_text);
                } break;
            }
            progressBar.setVisibility(ProgressBar.INVISIBLE);
        }
    }
}