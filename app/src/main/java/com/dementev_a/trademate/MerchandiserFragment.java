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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.bundle.BundleEngine;
import com.dementev_a.trademate.intent.IntentConstants;
import com.dementev_a.trademate.json.RequestJson;
import com.dementev_a.trademate.widgets.ReactOnStatus;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MerchandiserFragment extends Fragment implements View.OnClickListener {
    private TextView requestsQuantityTV, photoReportsQuantityTV;
    private EditText addPhotoReportET;
    private ProgressBar PB1, PB2;
    private Button aboutRequestsBtn, aboutPhotoReportsBtn;
    private FloatingActionButton addRequestBtn, addPhotoReportBtn;
    private String merchandiserName, accessToken, photoReportsQuantityText;
    private String[] reports;
    private int photoReportsQuantity;
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
        aboutPhotoReportsBtn = view.findViewById(R.id.merchandiser_fragment_about_photo_reports_btn);
        addPhotoReportBtn = view.findViewById(R.id.merchandiser_fragment_add_photo_report_btn);
        requestsQuantityTV = view.findViewById(R.id.merchandiser_fragment_requests_quantity);
        photoReportsQuantityTV = view.findViewById(R.id.merchandiser_fragment_photo_reports_quantity);
        addPhotoReportET = view.findViewById(R.id.merchandiser_fragment_add_photo_report_et);
        PB1 = view.findViewById(R.id.merchandiser_fragment_panel_1_progress_bar);
        PB2 = view.findViewById(R.id.merchandiser_fragment_panel_2_progress_bar);

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
        PB2.setVisibility(ProgressBar.VISIBLE);
        api.getRequestsToday(accessToken);
        api.getPhotoReports(accessToken);
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
                        case API.GET_PHOTO_REPORTS_HANDLER_NUMBER: {
                            photoReportsQuantity = bundle.getInt(BundleEngine.TOTAL_PHOTO_REPORTS_KEY_BUNDLE);
                            reports = bundle.getStringArray(BundleEngine.PHOTO_REPORTS_KEY_BUNDLE);
                            photoReportsQuantityText = getString(R.string.merchandiser_fragment_photo_reports_quantity_text);
                            photoReportsQuantityTV.setText(String.format(photoReportsQuantityText, photoReportsQuantity));
                            aboutPhotoReportsBtn.setOnClickListener(MerchandiserFragment.this);
                            addPhotoReportBtn.setOnClickListener(MerchandiserFragment.this);
                            PB2.setVisibility(ProgressBar.INVISIBLE);
                        } break;
                        case API.ADD_PHOTO_REPORT_HANDLER_NUMBER: {
                            photoReportsQuantity++;
                            photoReportsQuantityTV.setText(String.format(photoReportsQuantityText, photoReportsQuantity));
                            List<String> list = new ArrayList<>(Arrays.asList(reports));
                            list.add(bundle.getString(BundleEngine.PHOTO_REPORT_NAME_KEY_BUNDLE));
                            reports = list.toArray(new String[0]);
                            PB2.setVisibility(ProgressBar.INVISIBLE);
                            addPhotoReportET.setText("");
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
                intent.putExtra(IntentConstants.NAME_OF_MERCHANDISER_INTENT_KEY, merchandiserName);
                intent.putExtra(IntentConstants.ACCESS_TOKEN_INTENT_KEY, accessToken);
                startActivity(intent);
            } break;
            case R.id.merchandiser_fragment_about_requests_btn: {
                Intent intent = new Intent(getContext(), ListActivity.class);
                intent.putExtra(IntentConstants.TYPE_INTENT_KEY, IntentConstants.REQUESTS_DATA_TYPE);
                intent.putExtra(IntentConstants.REQUESTS_INTENT_KEY, requests);
                startActivity(intent);
            } break;
            case R.id.merchandiser_fragment_about_photo_reports_btn: {
                Intent intent = new Intent(getContext(), ListActivity.class);
                intent.putExtra(IntentConstants.TYPE_INTENT_KEY, IntentConstants.PHOTO_REPORTS_DATA_TYPE);
                intent.putExtra(IntentConstants.PHOTO_REPORTS_INTENT_KEY, reports);
                startActivity(intent);
            } break;
            case R.id.merchandiser_fragment_add_photo_report_btn: {
                PB2.setVisibility(ProgressBar.VISIBLE);
                api.addPhotoReport(accessToken, addPhotoReportET);
            } break;
        }
    }
}