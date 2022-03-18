package com.google.android.wifitrackerlib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import androidx.lifecycle.Lifecycle;
import com.android.wifitrackerlib.StandardNetworkDetailsTracker;
import java.time.Clock;
/* loaded from: classes2.dex */
public class GoogleStandardNetworkDetailsTracker extends StandardNetworkDetailsTracker {
    private final WsuNetworkDetailsController mWsuNetworkDetailsController;

    public GoogleStandardNetworkDetailsTracker(Lifecycle lifecycle, Context context, WifiManager wifiManager, ConnectivityManager connectivityManager, Handler handler, Handler handler2, Clock clock, long j, long j2, String str) {
        super(lifecycle, context, wifiManager, connectivityManager, handler, handler2, clock, j, j2, str);
        this.mWsuNetworkDetailsController = new WsuNetworkDetailsController(context, wifiManager, handler2, this);
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    public void onStart() {
        super.onStart();
        this.mWsuNetworkDetailsController.onUiStart();
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    public void onStop() {
        this.mWsuNetworkDetailsController.onUiStop();
        super.onStop();
    }
}
