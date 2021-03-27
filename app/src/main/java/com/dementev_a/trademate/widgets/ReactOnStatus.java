package com.dementev_a.trademate.widgets;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.dementev_a.trademate.bundle.BundleEngine;
import com.dementev_a.trademate.requests.RequestErrors;
import com.dementev_a.trademate.requests.RequestStatus;

public abstract class ReactOnStatus {
    private final int
        TEXT_VIEW_WIDGET = 0,
        TOAST_WIDGET = 1;
    private final Bundle bundle;
    private TextView errorTV;
    private Context context;
    private final int widget;

    public ReactOnStatus(Bundle bundle, TextView textView) {
        this.bundle = bundle;
        errorTV = textView;
        widget = TEXT_VIEW_WIDGET;
    }

    public ReactOnStatus(Bundle bundle, Context context) {
        this.bundle = bundle;
        this.context = context;
        widget = TOAST_WIDGET;
    }

    public void execute() {
        int status = bundle.getInt(BundleEngine.STATUS_KEY_BUNDLE);
        if (status == RequestStatus.STATUS_OK) {
            success();
        } else if (status == RequestStatus.STATUS_ERROR_TEXT) {
            if (widget == TEXT_VIEW_WIDGET) {
                errorTV.setText(bundle.getInt(BundleEngine.ERROR_TEXT_KEY_BUNDLE));
            } else if (widget == TOAST_WIDGET) {
                Toast.makeText(context, bundle.getInt(BundleEngine.ERROR_TEXT_KEY_BUNDLE), Toast.LENGTH_SHORT).show();
            }
        } else {
            if (widget == TEXT_VIEW_WIDGET) {
                errorTV.setText(RequestErrors.globalErrors.get(status));
            } else if (widget == TOAST_WIDGET) {
                Toast.makeText(context, RequestErrors.globalErrors.get(status), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public abstract void success();
}
