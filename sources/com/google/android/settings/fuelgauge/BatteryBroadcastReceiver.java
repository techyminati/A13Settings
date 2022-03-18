package com.google.android.settings.fuelgauge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import com.android.settings.fuelgauge.BatteryDiffEntry;
import com.android.settings.fuelgauge.BatteryEntry;
/* loaded from: classes2.dex */
public final class BatteryBroadcastReceiver extends BroadcastReceiver {
    static boolean isDebugMode = Build.TYPE.equals("userdebug");
    boolean mFetchBatteryUsageData = false;

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null) {
            Log.d("BatteryBroadcastReceiver", "onReceive:" + intent.getAction());
            String action = intent.getAction();
            action.hashCode();
            char c = 65535;
            switch (action.hashCode()) {
                case -1634889582:
                    if (action.equals("settings.intelligence.battery.action.FETCH_BATTERY_USAGE_DATA")) {
                        c = 0;
                        break;
                    }
                    break;
                case -133993435:
                    if (action.equals("settings.intelligence.battery.action.FETCH_BLUETOOTH_BATTERY_DATA")) {
                        c = 1;
                        break;
                    }
                    break;
                case 769486046:
                    if (action.equals("settings.intelligence.battery.action.CLEAR_BATTERY_CACHE_DATA")) {
                        c = 2;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    this.mFetchBatteryUsageData = true;
                    BatteryUsageLoaderService.enqueueWork(context);
                    return;
                case 1:
                    try {
                        BluetoothBatteryMetadataFetcher.returnBluetoothDevices(context, intent);
                        return;
                    } catch (Exception e) {
                        Log.e("BatteryBroadcastReceiver", "returnBluetoothDevices() error", e);
                        return;
                    }
                case 2:
                    if (isDebugMode) {
                        BatteryDiffEntry.clearCache();
                        BatteryEntry.clearUidCache();
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }
}
