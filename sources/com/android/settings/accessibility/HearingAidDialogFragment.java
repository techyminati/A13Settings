package com.android.settings.accessibility;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.window.R;
import com.android.settings.bluetooth.BluetoothPairingDetail;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
/* loaded from: classes.dex */
public class HearingAidDialogFragment extends InstrumentedDialogFragment {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1512;
    }

    public static HearingAidDialogFragment newInstance() {
        return new HearingAidDialogFragment();
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return new AlertDialog.Builder(getActivity()).setMessage(R.string.accessibility_hearingaid_pair_instructions_message).setPositiveButton(R.string.accessibility_hearingaid_instruction_continue_button, new DialogInterface.OnClickListener() { // from class: com.android.settings.accessibility.HearingAidDialogFragment.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                HearingAidDialogFragment.this.launchBluetoothAddDeviceSetting();
            }
        }).setNegativeButton(17039360, new DialogInterface.OnClickListener() { // from class: com.android.settings.accessibility.HearingAidDialogFragment.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).create();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void launchBluetoothAddDeviceSetting() {
        new SubSettingLauncher(getActivity()).setDestination(BluetoothPairingDetail.class.getName()).setSourceMetricsCategory(2).launch();
    }
}
