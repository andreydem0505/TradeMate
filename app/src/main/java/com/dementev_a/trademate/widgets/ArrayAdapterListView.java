package com.dementev_a.trademate.widgets;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class ArrayAdapterListView {
    private ArrayAdapter<String> adapter;
    private List<String> names;
    private Context context;

    public int deleteItem(String name) {
        int index = names.indexOf(name);
        adapter.remove(name);
        adapter.notifyDataSetChanged();
        return index;
    }

    // getters
    public ArrayAdapter<String> getAdapter() {
        return adapter;
    }

    public List<String> getNames() {
        return names;
    }

    public Context getContext() {
        return context;
    }

    // setters
    public void setNames(List<String> names) {
        this.names = names;
    }

    public void setListView(ListView listView) {
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, names);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this::onItemClickListener);
        listView.setOnItemLongClickListener(this::onItemLongClickListener);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void onItemClickListener(AdapterView<?> parent, View view, int position, long id) {}

    public boolean onItemLongClickListener(AdapterView<?> parent, View view, int position, long id) {
        return true;
    }
}
