package com.dementev_a.trademate.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.dementev_a.trademate.R;

import org.jetbrains.annotations.NotNull;

public class OperatorDialog extends DialogFragment {
    private final String name;
    private final String email;

    public OperatorDialog(String name, String email) {
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
                .setPositiveButton(R.string.ok, (dialog, which) -> dialog.cancel());
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.green, getContext().getTheme()));
    }
}
