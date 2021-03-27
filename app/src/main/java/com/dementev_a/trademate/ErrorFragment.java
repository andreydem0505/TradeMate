package com.dementev_a.trademate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.bundle.BundleEngine;

public class ErrorFragment extends Fragment {
    private View view;
    private TextView errorTV;
    private String errorText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        errorTV.setText(errorText);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        errorText = getArguments().getString(BundleEngine.ERROR_TEXT_KEY_BUNDLE);
        view = inflater.inflate(R.layout.fragment_error, container, false);
        errorTV = view.findViewById(R.id.error_fragment_error_tv);
        return view;
    }
}