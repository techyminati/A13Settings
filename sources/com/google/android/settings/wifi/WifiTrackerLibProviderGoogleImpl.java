package com.google.android.settings.wifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import androidx.lifecycle.Lifecycle;
import com.android.settings.wifi.WifiTrackerLibProvider;
import com.android.wifitrackerlib.NetworkDetailsTracker;
import com.android.wifitrackerlib.WifiPickerTracker;
import com.google.android.wifitrackerlib.GooglePasspointNetworkDetailsTracker;
import com.google.android.wifitrackerlib.GoogleStandardNetworkDetailsTracker;
import com.google.android.wifitrackerlib.GoogleWifiPickerTracker;
import java.time.Clock;
/* loaded from: classes2.dex */
public class WifiTrackerLibProviderGoogleImpl implements WifiTrackerLibProvider {
    @Override // com.android.settings.wifi.WifiTrackerLibProvider
    public WifiPickerTracker createWifiPickerTracker(Lifecycle lifecycle, Context context, Handler handler, Handler handler2, Clock clock, long j, long j2, WifiPickerTracker.WifiPickerTrackerCallback wifiPickerTrackerCallback) {
        return new GoogleWifiPickerTracker(lifecycle, context, (WifiManager) context.getSystemService(WifiManager.class), (ConnectivityManager) context.getSystemService(ConnectivityManager.class), handler, handler2, clock, j, j2, wifiPickerTrackerCallback);
    }

    @Override // com.android.settings.wifi.WifiTrackerLibProvider
    public NetworkDetailsTracker createNetworkDetailsTracker(Lifecycle lifecycle, Context context, Handler handler, Handler handler2, Clock clock, long j, long j2, String str) {
        if (str.startsWith("StandardWifiEntry:") || str.startsWith("NetworkRequestEntry:")) {
            return new GoogleStandardNetworkDetailsTracker(lifecycle, context, (WifiManager) context.getSystemService(WifiManager.class), (ConnectivityManager) context.getSystemService(ConnectivityManager.class), handler, handler2, clock, j, j2, str);
        }
        if (str.startsWith("PasspointWifiEntry:")) {
            return new GooglePasspointNetworkDetailsTracker(lifecycle, context, (WifiManager) context.getSystemService(WifiManager.class), (ConnectivityManager) context.getSystemService(ConnectivityManager.class), handler, handler2, clock, j, j2, str);
        }
        throw new IllegalArgumentException("Key does not contain valid key prefix!");
    }
}
