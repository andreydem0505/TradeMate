package com.dementev_a.trademate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MerchandiserFragment extends Fragment {
    private View view;
    private TextView merchandiserNameTV;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_merchandiser, container, false);
        merchandiserNameTV = view.findViewById(R.id.merchandiser_fragment_merchandiser_name);
        merchandiserNameTV.setText(getArguments().getString("name"));

        return view;
    }
}