package com.android.settings.development.tare;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.window.R;
import com.android.settings.Utils;
/* loaded from: classes.dex */
public class TareFactorDialogFragment extends DialogFragment {
    private int mFactorEditedValue;
    private final String mFactorTitle;
    private final int mFactorValue;
    private EditText mFactorValueView;

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onCreateDialog$1(DialogInterface dialogInterface, int i) {
    }

    private String getFactorValue() {
        return Integer.toString(this.mFactorValue);
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return new AlertDialog.Builder(getActivity()).setTitle(this.mFactorTitle).setView(createDialogView()).setPositiveButton(R.string.tare_dialog_confirm_button_title, new DialogInterface.OnClickListener() { // from class: com.android.settings.development.tare.TareFactorDialogFragment$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                TareFactorDialogFragment.this.lambda$onCreateDialog$0(dialogInterface, i);
            }
        }).setNegativeButton(17039360, TareFactorDialogFragment$$ExternalSyntheticLambda1.INSTANCE).create();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$0(DialogInterface dialogInterface, int i) {
        String obj = this.mFactorValueView.getText().toString();
        this.mFactorEditedValue = this.mFactorValue;
        try {
            this.mFactorEditedValue = Integer.parseInt(obj);
        } catch (NumberFormatException e) {
            Log.e("TareDialogFragment", "Error converting '" + obj + "' to integer. Using " + this.mFactorValue + " instead", e);
        }
        throw null;
    }

    private View createDialogView() {
        View inflate = ((LayoutInflater) getActivity().getSystemService("layout_inflater")).inflate(R.layout.dialog_edittext, (ViewGroup) null);
        EditText editText = (EditText) inflate.findViewById(R.id.edittext);
        this.mFactorValueView = editText;
        editText.setInputType(2);
        this.mFactorValueView.setText(getFactorValue());
        Utils.setEditTextCursorPosition(this.mFactorValueView);
        return inflate;
    }
}
