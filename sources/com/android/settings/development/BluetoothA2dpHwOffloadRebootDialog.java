package com.android.settings.development;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PowerManager;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.window.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
/* loaded from: classes.dex */
public class BluetoothA2dpHwOffloadRebootDialog extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {

    /* loaded from: classes.dex */
    public interface OnA2dpHwDialogConfirmedListener {
        void onA2dpHwDialogConfirmed();
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1441;
    }

    public static void show(DevelopmentSettingsDashboardFragment developmentSettingsDashboardFragment, BluetoothA2dpHwOffloadPreferenceController bluetoothA2dpHwOffloadPreferenceController) {
        FragmentManager supportFragmentManager = developmentSettingsDashboardFragment.getActivity().getSupportFragmentManager();
        if (supportFragmentManager.findFragmentByTag("BluetoothA2dpHwOffloadReboot") == null) {
            BluetoothA2dpHwOffloadRebootDialog bluetoothA2dpHwOffloadRebootDialog = new BluetoothA2dpHwOffloadRebootDialog();
            bluetoothA2dpHwOffloadRebootDialog.setTargetFragment(developmentSettingsDashboardFragment, 0);
            bluetoothA2dpHwOffloadRebootDialog.show(supportFragmentManager, "BluetoothA2dpHwOffloadReboot");
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return new AlertDialog.Builder(getActivity()).setMessage(R.string.bluetooth_disable_a2dp_hw_offload_dialog_message).setTitle(R.string.bluetooth_disable_a2dp_hw_offload_dialog_title).setPositiveButton(R.string.bluetooth_disable_a2dp_hw_offload_dialog_confirm, this).setNegativeButton(R.string.bluetooth_disable_a2dp_hw_offload_dialog_cancel, this).create();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        OnA2dpHwDialogConfirmedListener onA2dpHwDialogConfirmedListener = (OnA2dpHwDialogConfirmedListener) getTargetFragment();
        if (onA2dpHwDialogConfirmedListener != null && i == -1) {
            onA2dpHwDialogConfirmedListener.onA2dpHwDialogConfirmed();
            ((PowerManager) getContext().getSystemService(PowerManager.class)).reboot(null);
        }
    }
}
