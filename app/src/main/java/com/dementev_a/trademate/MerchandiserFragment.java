package com.dementev_a.trademate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.json.OperatorJson;
import com.dementev_a.trademate.json.RequestJson;
import com.dementev_a.trademate.preferences.SharedPreferencesEngine;
import com.dementev_a.trademate.requests.AsyncRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MerchandiserFragment extends Fragment implements View.OnClickListener {
    private View view;
    private TextView merchandiserNameTV, requestsQuantityTV;
    private GraphView graphView;
    private FloatingActionButton addRequestBtn;
    private Button aboutRequestsBtn;
    private OperatorJson[] operators;
    private RequestJson[] requests;
    private int requestsQuantity;

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
        aboutRequestsBtn = view.findViewById(R.id.merchandiser_fragment_about_requests_btn);
        aboutRequestsBtn.setOnClickListener(this);
        requestsQuantityTV = view.findViewById(R.id.merchandiser_fragment_requests_quantity);
        graphView = view.findViewById(R.id.merchandiser_fragment_graph);

        merchandiserNameTV.setText(bundle.getString("merchandiserName"));
        requestsQuantity = bundle.getInt("total_requests");
        String requestsQuantityText = getString(R.string.merchandiser_fragment_requests_quantity_text);
        requestsQuantityTV.setText(String.format(requestsQuantityText, requestsQuantity));
        operators = (OperatorJson[]) bundle.getParcelableArray("operators");
        requests = (RequestJson[]) bundle.getParcelableArray("requests");

        SharedPreferencesEngine spe = new SharedPreferencesEngine(getContext(), getString(R.string.shared_preferences_user));
        new ConcurrentSetGraph(spe).execute();

        return view;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.merchandiser_fragment_add_request_btn: {
                Intent intent = new Intent(getContext(), MakeRequestActivity.class);
                intent.putExtra("operators", operators);
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


    private class ConcurrentSetGraph implements AsyncRequest {
        private final Bundle bundle;
        private final SharedPreferencesEngine spe;

        protected ConcurrentSetGraph(SharedPreferencesEngine spe) {
            bundle = new Bundle();
            this.spe = spe;
        }

        @Override
        public void execute() {
            Executor executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                sendRequest();
                handler.post(() -> {
                    graphView.setTitle("Количество заявок");

                    DataPoint[] dataPoints = new DataPoint[3];
                    for (int i = 0; i < 3; i++) {
                        Date date = Date.from(LocalDate.parse(bundle.getString("date_"+(i+1))).atStartOfDay()
                                .atZone(ZoneId.systemDefault())
                                .toInstant());

                        dataPoints[2-i] = new DataPoint(
                                date,
                                bundle.getInt("total_"+(i+1))
                        );
                    }
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
                    graphView.addSeries(series);

                    graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
                    graphView.getGridLabelRenderer().setNumHorizontalLabels(3);
                });
            });
        }

        @Override
        public void sendRequest() {
            Map<String, String> headers = new HashMap<>();
            headers.put("access_token", spe.getString("accessToken"));

            Clock clock = Clock.systemUTC();
            LocalDate dateNow = LocalDate.now(clock);
            for (int i = 1; i <= 3; i++) {
                LocalDate requestDate = dateNow.minusDays(i-1);
                Bundle shortBundle = new Bundle();
                API.getRequestsForDate(shortBundle, headers, requestDate);
                bundle.putString("date_"+i, requestDate.toString());
                bundle.putInt("total_"+i, shortBundle.getInt("total_requests"));
            }
        }
    }
}