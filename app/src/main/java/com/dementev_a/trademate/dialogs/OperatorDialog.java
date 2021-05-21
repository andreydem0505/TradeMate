package com.dementev_a.trademate.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.dementev_a.trademate.R;
import com.dementev_a.trademate.widgets.WidgetsEngine;

import org.jetbrains.annotations.NotNull;

public class OperatorDialog extends DialogFragment {
    private final String name;
    private final String email;
    private final FragmentManager manager;
    private final Handler handler;

    public OperatorDialog(Handler handler, FragmentManager manager, String name, String email) {
        this.handler = handler;
        this.manager = manager;
        this.name = name;
        this.email = email;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(name)
                .setMessage(String.format(getString(R.string.operator_dialog_message), email))
                .setPositiveButton(R.string.ok, (dialog, which) -> dialog.cancel())
                .setNegativeButton(R.string.operator_dialog_delete, (dialog, which) -> {
                    dialog.cancel();
                    WidgetsEngine.showDeleteDialog(manager, handler, name);
                });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.green, getContext().getTheme()));
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red, getContext().getTheme()));
    }
}
