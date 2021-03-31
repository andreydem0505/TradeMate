package com.dementev_a.trademate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.bundle.BundleEngine;
import com.dementev_a.trademate.json.RequestJson;
import com.dementev_a.trademate.widgets.ReactOnStatus;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MerchandiserFragment extends Fragment implements View.OnClickListener {
    private TextView requestsQuantityTV;
    private ProgressBar PB1;
    private Button aboutRequestsBtn;
    private FloatingActionButton addRequestBtn;
    private String merchandiserName, accessToken;
    private RequestJson[] requests;
    private API api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_merchandiser, container, false);
        TextView merchandiserNameTV = view.findViewById(R.id.merchandiser_fragment_merchandiser_name);
        addRequestBtn = view.findViewById(R.id.merchandiser_fragment_add_request_btn);
        aboutRequestsBtn = view.findViewById(R.id.merchandiser_fragment_about_requests_btn);
        requestsQuantityTV = view.findViewById(R.id.merchandiser_fragment_requests_quantity);
        PB1 = view.findViewById(R.id.merchandiser_fragment_panel_1_progress_bar);

        Bundle bundle = getArguments();
        accessToken = bundle.getString(BundleEngine.ACCESS_TOKEN_KEY_BUNDLE);
        merchandiserName = bundle.getString(BundleEngine.MERCHANDISER_NAME_KEY_BUNDLE);
        merchandiserNameTV.setText(merchandiserName);

        api = new API(getContext(), handler);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        PB1.setVisibility(ProgressBar.VISIBLE);
        api.getRequestsToday(accessToken);
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            new ReactOnStatus(bundle, getContext()) {
                @Override
                public void success() {
                    switch (msg.what) {
                        case API.GET_REQUESTS_HANDLER_NUMBER: {
                            int requestsQuantity = bundle.getInt(BundleEngine.TOTAL_REQUESTS_KEY_BUNDLE);
                            requests = (RequestJson[]) bundle.getParcelableArray(BundleEngine.REQUESTS_KEY_BUNDLE);
                            String requestsQuantityText = getString(R.string.merchandiser_fragment_requests_quantity_text);
                            requestsQuantityTV.setText(String.format(requestsQuantityText, requestsQuantity));
                            aboutRequestsBtn.setOnClickListener(MerchandiserFragment.this);
                            addRequestBtn.setOnClickListener(MerchandiserFragment.this);
                            PB1.setVisibility(ProgressBar.INVISIBLE);
                        } break;
                    }
                }
            }.execute();
        }
    };

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.merchandiser_fragment_add_request_btn: {
                Intent intent = new Intent(getContext(), MakeRequestActivity.class);
                intent.putExtra("merchandiserName", merchandiserName);
                startActivity(intent);
            } break;
            case R.id.merchandiser_fragment_about_requests_btn: {
                Intent intent = new Intent(getContext(), ListActivity.class);
                intent.putExtra("type", "requests");
                intent.putExtra("requests", requests);
                startActivity(intent);
            } break;
        }
    }
}