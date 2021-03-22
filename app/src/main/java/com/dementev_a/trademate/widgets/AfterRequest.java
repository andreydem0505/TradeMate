package com.dementev_a.trademate.widgets;

import android.os.Bundle;
import android.widget.TextView;

import com.dementev_a.trademate.requests.RequestErrors;
import com.dementev_a.trademate.requests.RequestStatus;

public abstract class AfterRequest {
    private final Bundle bundle;
    private final TextView textView;

    public AfterRequest(Bundle bundle, TextView textView) {
        this.bundle = bundle;
        this.textView = textView;
    }

    public void execute() {
        int status = bundle.getInt("status");
        if (status == RequestStatus.STATUS_OK) {
            ok();
        } else if (status == RequestStatus.STATUS_ERROR_TEXT) {
            textView.setText(RequestErrors.errors.get(bundle.getInt("error_text")));
        } else {
            textView.setText(RequestErrors.globalErrors.get(status));
        }
    }

    public abstract void ok();
}
