package com.dementev_a.trademate.bundle;

import android.os.Bundle;

import com.dementev_a.trademate.api.RequestStatus;

import org.jetbrains.annotations.NotNull;

public class BundleEngine {
    public static void putError(@NotNull Bundle bundle, int id) {
        bundle.putInt("status", RequestStatus.STATUS_ERROR_TEXT);
        bundle.putInt("error_text", id);
    }
}
