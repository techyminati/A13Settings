package com.android.settings.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.UserHandle;
import android.text.TextUtils;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
/* loaded from: classes.dex */
public final class BluetoothPairingRequest extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        int intExtra;
        String action = intent.getAction();
        if (action != null) {
            BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            LocalBluetoothManager localBtManager = Utils.getLocalBtManager(context);
            if (TextUtils.equals(action, "android.bluetooth.device.action.PAIRING_REQUEST")) {
                PowerManager powerManager = (PowerManager) context.getSystemService(PowerManager.class);
                int intExtra2 = intent.getIntExtra("android.bluetooth.device.extra.PAIRING_VARIANT", Integer.MIN_VALUE);
                String str = null;
                String address = bluetoothDevice != null ? bluetoothDevice.getAddress() : null;
                if (bluetoothDevice != null) {
                    str = bluetoothDevice.getName();
                }
                boolean shouldShowDialogInForeground = LocalBluetoothPreferences.shouldShowDialogInForeground(context, address, str);
                if (intExtra2 == 3 && (bluetoothDevice.canBondWithoutDialog() || localBtManager.getCachedDeviceManager().isOngoingPairByCsip(bluetoothDevice))) {
                    bluetoothDevice.setPairingConfirmation(true);
                } else if (!powerManager.isInteractive() || !shouldShowDialogInForeground) {
                    intent.setClass(context, BluetoothPairingService.class);
                    intent.setAction("android.bluetooth.device.action.PAIRING_REQUEST");
                    context.startServiceAsUser(intent, UserHandle.CURRENT);
                } else {
                    context.startActivityAsUser(BluetoothPairingService.getPairingDialogIntent(context, intent, 1), UserHandle.CURRENT);
                }
            } else if (TextUtils.equals(action, "android.bluetooth.action.CSIS_SET_MEMBER_AVAILABLE") && bluetoothDevice != null && (intExtra = intent.getIntExtra("android.bluetooth.extra.CSIS_GROUP_ID", -1)) != -1 && localBtManager.getCachedDeviceManager().shouldPairByCsip(bluetoothDevice, intExtra)) {
                bluetoothDevice.createBond(2);
            }
        }
    }
}
