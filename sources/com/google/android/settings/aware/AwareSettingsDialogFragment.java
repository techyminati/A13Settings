package com.google.android.settings.aware;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.window.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
/* loaded from: classes2.dex */
public class AwareSettingsDialogFragment extends InstrumentedDialogFragment {
    private static DialogInterface.OnClickListener mClickListener;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1633;
    }

    public static void show(Fragment fragment, DialogInterface.OnClickListener onClickListener) {
        mClickListener = onClickListener;
        AwareSettingsDialogFragment awareSettingsDialogFragment = new AwareSettingsDialogFragment();
        awareSettingsDialogFragment.setTargetFragment(fragment, 0);
        awareSettingsDialogFragment.show(fragment.getFragmentManager(), "AwareSettingsDialog");
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return new AlertDialog.Builder(getContext()).setTitle(R.string.dialog_aware_settings_title).setMessage(R.string.dialog_aware_settings_message).setPositiveButton(R.string.condition_turn_off, mClickListener).setNegativeButton(R.string.cancel, mClickListener).create();
    }
}
