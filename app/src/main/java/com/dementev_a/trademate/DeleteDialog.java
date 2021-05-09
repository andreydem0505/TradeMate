package com.dementev_a.trademate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.dementev_a.trademate.api.API;

import org.jetbrains.annotations.NotNull;

public class DeleteDialog extends DialogFragment {
    private final Handler handler;

    public DeleteDialog(Handler handler) {
        this.handler = handler;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.delete_dialog_title)
                .setIcon(R.drawable.delete_icon)
                .setPositiveButton(R.string.delete_dialog_yes, (dialog, which) -> {
                    Message message = Message.obtain();
                    message.what = API.DELETE_DIALOG_HANDLER_NUMBER;
                    handler.sendMessage(message);
                    dialog.cancel();
                });
        return builder.create();
    }
}
