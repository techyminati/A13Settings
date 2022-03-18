package com.android.settings.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.net.Uri;
/* loaded from: classes.dex */
public interface BluetoothFeatureProvider {
    Uri getBluetoothDeviceSettingsUri(BluetoothDevice bluetoothDevice);
}
