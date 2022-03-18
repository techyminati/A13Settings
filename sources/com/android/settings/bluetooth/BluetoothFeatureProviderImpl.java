package com.android.settings.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.Uri;
/* loaded from: classes.dex */
public class BluetoothFeatureProviderImpl implements BluetoothFeatureProvider {
    private Context mContext;

    public BluetoothFeatureProviderImpl(Context context) {
        this.mContext = context;
    }

    @Override // com.android.settings.bluetooth.BluetoothFeatureProvider
    public Uri getBluetoothDeviceSettingsUri(BluetoothDevice bluetoothDevice) {
        byte[] metadata = bluetoothDevice.getMetadata(16);
        if (metadata == null) {
            return null;
        }
        return Uri.parse(new String(metadata));
    }
}
