package com.google.android.wifitrackerlib;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import com.android.wifitrackerlib.NetworkDetailsTracker;
import com.android.wifitrackerlib.WifiEntry;
import com.google.android.wifitrackerlib.WsuManager;
/* loaded from: classes2.dex */
public final class WsuNetworkDetailsController {
    private final NetworkDetailsTracker mNetworkDetailsTracker;
    private final Handler mWorkerHandler;
    private final WsuManager mWsuManager;
    private final WsuManager.WsuProvidersLoadCallback mWsuProvidersLoadCallback = new WsuManager.WsuProvidersLoadCallback() { // from class: com.google.android.wifitrackerlib.WsuNetworkDetailsController$$ExternalSyntheticLambda0
        @Override // com.google.android.wifitrackerlib.WsuManager.WsuProvidersLoadCallback
        public final void onLoaded() {
            WsuNetworkDetailsController.this.lambda$new$0();
        }
    };

    public WsuNetworkDetailsController(Context context, WifiManager wifiManager, Handler handler, NetworkDetailsTracker networkDetailsTracker) {
        this.mWorkerHandler = handler;
        this.mNetworkDetailsTracker = networkDetailsTracker;
        this.mWsuManager = new WsuManager(context, wifiManager, handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        WifiEntry.ManageSubscriptionAction tryGetManageSubscriptionAction = this.mWsuManager.tryGetManageSubscriptionAction(this.mNetworkDetailsTracker.getWifiEntry());
        if (tryGetManageSubscriptionAction != null) {
            this.mNetworkDetailsTracker.getWifiEntry().setManageSubscriptionAction(tryGetManageSubscriptionAction);
        }
    }

    public void onUiStart() {
        this.mWsuManager.bindAllServices();
        this.mWsuManager.addWsuProvidersLoadCallback(this.mWsuProvidersLoadCallback);
    }

    public void onUiStop() {
        this.mWsuManager.unbindAllServices();
        this.mWsuManager.removeWsuProvidersLoadCallback(this.mWsuProvidersLoadCallback);
    }
}
