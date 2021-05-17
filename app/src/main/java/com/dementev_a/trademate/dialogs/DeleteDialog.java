package com.dementev_a.trademate.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.dementev_a.trademate.R;
import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.bundle.BundleEngine;

import org.jetbrains.annotations.NotNull;

public class DeleteDialog extends DialogFragment {
    private final Handler handler;
    private final String name;

    public DeleteDialog(Handler handler, String name) {
        this.handler = handler;
        this.name = name;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder = builder.setTitle(String.format(getString(R.string.delete_dialog_title_with_word), name));
        builder.setIcon(R.drawable.delete_icon)
                .setPositiveButton(R.string.delete_dialog_yes, (dialog, which) -> {
                    Message message = Message.obtain();
                    message.what = API.DELETE_DIALOG_HANDLER_NUMBER;
                    Bundle bundle = new Bundle();
                    bundle.putString(BundleEngine.NAME_KEY_BUNDLE, name);
                    message.setData(bundle);
                    handler.sendMessage(message);
                    dialog.cancel();
                });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red, getContext().getTheme()));
    }
}
