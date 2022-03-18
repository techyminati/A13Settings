package com.google.android.settings.fuelgauge;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class BluetoothBatteryMetadataFetcher {
    @VisibleForTesting
    static LocalBluetoothManager sLocalBluetoothManager;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void returnBluetoothDevices(final Context context, Intent intent) {
        final ResultReceiver resultReceiver = (ResultReceiver) intent.getParcelableExtra("android.intent.extra.RESULT_RECEIVER");
        if (resultReceiver == null) {
            Log.w("BluetoothBatteryMetadataFetcher", "No result receiver found from intent");
            return;
        }
        final LocalBluetoothManager localBluetoothManager = sLocalBluetoothManager;
        if (localBluetoothManager == null) {
            localBluetoothManager = LocalBluetoothManager.getInstance(context, null);
        }
        BluetoothAdapter adapter = ((BluetoothManager) context.getSystemService(BluetoothManager.class)).getAdapter();
        if (adapter == null || !adapter.isEnabled() || localBluetoothManager == null) {
            Log.w("BluetoothBatteryMetadataFetcher", "BluetoothAdapter not present or not enabled");
            resultReceiver.send(1, null);
            return;
        }
        final boolean booleanExtra = intent.getBooleanExtra("extra_fetch_icon", false);
        AsyncTask.execute(new Runnable() { // from class: com.google.android.settings.fuelgauge.BluetoothBatteryMetadataFetcher$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                BluetoothBatteryMetadataFetcher.sendAndFilterBluetoothData(context, resultReceiver, localBluetoothManager, booleanExtra);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void sendAndFilterBluetoothData(Context context, ResultReceiver resultReceiver, LocalBluetoothManager localBluetoothManager, boolean z) {
        long currentTimeMillis = System.currentTimeMillis();
        Collection<CachedBluetoothDevice> cachedDevicesCopy = localBluetoothManager.getCachedDeviceManager().getCachedDevicesCopy();
        Log.d("BluetoothBatteryMetadataFetcher", "cachedDevices:" + cachedDevicesCopy);
        if (cachedDevicesCopy == null || cachedDevicesCopy.isEmpty()) {
            resultReceiver.send(0, Bundle.EMPTY);
            return;
        }
        List<CachedBluetoothDevice> list = (List) cachedDevicesCopy.stream().filter(BluetoothBatteryMetadataFetcher$$ExternalSyntheticLambda1.INSTANCE).collect(Collectors.toList());
        Log.d("BluetoothBatteryMetadataFetcher", "connectedDevices:" + list);
        if (list.isEmpty()) {
            resultReceiver.send(0, Bundle.EMPTY);
            return;
        }
        ArrayList<? extends Parcelable> arrayList = new ArrayList<>();
        ArrayList<? extends Parcelable> arrayList2 = new ArrayList<>();
        for (CachedBluetoothDevice cachedBluetoothDevice : list) {
            BluetoothDevice device = cachedBluetoothDevice.getDevice();
            arrayList2.add(device);
            try {
                arrayList.add(BluetoothUtils.wrapBluetoothData(context, cachedBluetoothDevice, z));
            } catch (Exception e) {
                Log.e("BluetoothBatteryMetadataFetcher", "wrapBluetoothData() failed: " + device, e);
            }
        }
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("bluetoothParcelableListKey", arrayList2);
        if (!arrayList.isEmpty()) {
            bundle.putParcelableArrayList("bluetoothWrapDataListKey", arrayList);
        }
        resultReceiver.send(0, bundle);
        Log.d("BluetoothBatteryMetadataFetcher", String.format("sendAndFilterBluetoothData() size=%d in %d/ms", Integer.valueOf(arrayList.size()), Long.valueOf(System.currentTimeMillis() - currentTimeMillis)));
    }
}
