package com.dementev_a.trademate.widgets;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayAdapterListView {
    private ArrayAdapter<String> adapter;
    private String[] names;
    private ListView listView;
    private Context context;

    public int deleteItem(String name) {
        List<String> list = new ArrayList<>(Arrays.asList(names));
        int index = list.indexOf(name);
        list.remove(index);
        names = list.toArray(new String[0]);
        adapter.notifyDataSetChanged();
        return index;
    }

    // getters
    public ArrayAdapter<String> getAdapter() {
        return adapter;
    }

    public String[] getNames() {
        return names;
    }

    public ListView getListView() {
        return listView;
    }

    public Context getContext() {
        return context;
    }

    // setters
    public void setAdapter(ArrayAdapter<String> adapter) {
        this.adapter = adapter;
    }

    public void setNames(String[] names) {
        this.names = names;
    }

    public void setListView(ListView listView) {
        this.listView = listView;
        listView.setOnItemClickListener(this::onItemClickListener);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void onItemClickListener(AdapterView<?> parent, View view, int position, long id) {}
}
