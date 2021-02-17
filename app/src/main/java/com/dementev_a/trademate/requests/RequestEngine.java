package com.dementev_a.trademate.requests;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class RequestEngine {
    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @SafeVarargs
    public static String makePostRequestWithJson(String address, String json, Map<String, String>... headers) throws IOException {
        URL url = new URL(address);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
        urlConnection.setRequestProperty("Accept", "application/json");
        if (headers.length == 1) {
            for (String key : headers[0].keySet()) {
                urlConnection.setRequestProperty(key, headers[0].get(key));
            }
        }
        urlConnection.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
        writer.write(json.replaceAll("\r\n", "/r/n"));
        writer.flush();
        if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            return null;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String responseLine;
        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }
        return response.toString();
    }

    @SafeVarargs
    public static String makeGetRequest(String address, Map<String, String>... headers) throws IOException {
        URL url = new URL(address);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Accept", "application/json");
        if (headers.length == 1) {
            for (String key : headers[0].keySet()) {
                urlConnection.setRequestProperty(key, headers[0].get(key));
            }
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String responseLine;
        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim().replaceAll("/r/n", "\r\n"));
        }
        return response.toString();
    }
}
