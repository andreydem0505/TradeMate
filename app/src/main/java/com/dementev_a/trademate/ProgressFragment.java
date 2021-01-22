package com.dementev_a.trademate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hamza.slidingsquaresloaderview.SlidingSquareLoaderView;

public class ProgressFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);
        final SlidingSquareLoaderView slidingview = (SlidingSquareLoaderView) view.findViewById(R.id.sliding_view);
        slidingview.start();
        slidingview.setDuration(200);
        slidingview.setDelay(15);
        return view;
    }
}