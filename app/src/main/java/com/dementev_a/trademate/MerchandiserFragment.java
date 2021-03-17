package com.dementev_a.trademate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dementev_a.trademate.json.RequestJson;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MerchandiserFragment extends Fragment implements View.OnClickListener {
    private String merchandiserName;
    private String[] namesOfOperators, emailsOfOperators, shops;
    private RequestJson[] requests;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();

        View view = inflater.inflate(R.layout.fragment_merchandiser, container, false);
        TextView merchandiserNameTV = view.findViewById(R.id.merchandiser_fragment_merchandiser_name);
        FloatingActionButton addRequestBtn = view.findViewById(R.id.merchandiser_fragment_add_request_btn);
        addRequestBtn.setOnClickListener(this);
        Button aboutRequestsBtn = view.findViewById(R.id.merchandiser_fragment_about_requests_btn);
        aboutRequestsBtn.setOnClickListener(this);
        TextView requestsQuantityTV = view.findViewById(R.id.merchandiser_fragment_requests_quantity);

        merchandiserName = bundle.getString("merchandiserName");
        merchandiserNameTV.setText(merchandiserName);
        int requestsQuantity = bundle.getInt("total_requests");
        String requestsQuantityText = getString(R.string.merchandiser_fragment_requests_quantity_text);
        requestsQuantityTV.setText(String.format(requestsQuantityText, requestsQuantity));
        namesOfOperators = bundle.getStringArray("namesOfOperators");
        emailsOfOperators = bundle.getStringArray("emailsOfOperators");
        requests = (RequestJson[]) bundle.getParcelableArray("requests");
        shops = bundle.getStringArray("shops");

        return view;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.merchandiser_fragment_add_request_btn: {
                Intent intent = new Intent(getContext(), MakeRequestActivity.class);
                intent.putExtra("namesOfOperators", namesOfOperators);
                intent.putExtra("emailsOfOperators", emailsOfOperators);
                intent.putExtra("shops", shops);
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