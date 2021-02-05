package com.dementev_a.trademate;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dementev_a.trademate.json.OperatorJson;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MerchandiserFragment extends Fragment implements View.OnClickListener {
    private View view;
    private TextView merchandiserNameTV;
    private FloatingActionButton addRequestBtn;
    private OperatorJson[] operators;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();

        view = inflater.inflate(R.layout.fragment_merchandiser, container, false);
        merchandiserNameTV = view.findViewById(R.id.merchandiser_fragment_merchandiser_name);
        addRequestBtn = view.findViewById(R.id.merchandiser_fragment_add_request_btn);
        addRequestBtn.setOnClickListener(this);

        merchandiserNameTV.setText(bundle.getString("merchandiserName"));
        operators = (OperatorJson[]) bundle.getParcelableArray("operators");

        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.merchandiser_fragment_add_request_btn: {
                Intent intent = new Intent(getContext(), MakeRequestActivity.class);
                intent.putExtra("operators", operators);
                startActivity(intent);
            } break;
        }
    }
}