package com.google.android.wifitrackerlib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;
import androidx.core.util.Preconditions;
import androidx.lifecycle.Lifecycle;
import com.android.internal.annotations.VisibleForTesting;
import com.android.wifitrackerlib.WifiEntry;
import com.android.wifitrackerlib.WifiPickerTracker;
import com.google.android.wifitrackerlib.WsuManager;
import java.time.Clock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/* loaded from: classes2.dex */
public class GoogleWifiPickerTracker extends WifiPickerTracker {
    @VisibleForTesting
    public WsuManager mWsuManager;
    private final Map<String, WsuWifiEntry> mWsuWifiEntryCache = new HashMap();
    private final WsuManager.WsuProvidersLoadCallback mWsuProvidersLoadCallback = new WsuManager.WsuProvidersLoadCallback() { // from class: com.google.android.wifitrackerlib.GoogleWifiPickerTracker$$ExternalSyntheticLambda0
        @Override // com.google.android.wifitrackerlib.WsuManager.WsuProvidersLoadCallback
        public final void onLoaded() {
            GoogleWifiPickerTracker.this.lambda$new$0();
        }
    };
    private WsuManager.WsuProvisionStatusUpdateCallback mWsuProvisonStatusCallback = new WsuManager.WsuProvisionStatusUpdateCallback() { // from class: com.google.android.wifitrackerlib.GoogleWifiPickerTracker$$ExternalSyntheticLambda1
        @Override // com.google.android.wifitrackerlib.WsuManager.WsuProvisionStatusUpdateCallback
        public final void onProvisionStatusChanged(WsuProvider wsuProvider, int i) {
            GoogleWifiPickerTracker.this.lambda$new$1(wsuProvider, i);
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        if (this.mWifiManager.getWifiState() != 1) {
            List<ScanResult> scanResults = this.mScanResultUpdater.getScanResults(this.mMaxScanAgeMillis);
            this.mWsuWifiEntryCache.clear();
            updateWsuWifiEntryScans(scanResults);
            updateWifiEntries();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(WsuProvider wsuProvider, int i) {
        Log.d("GoogleWifiPickerTracker", "WSU provision status update: " + i + ", for network: " + wsuProvider.networkGroupIdentity);
        if (i == 2) {
            List<ScanResult> scanResults = this.mScanResultUpdater.getScanResults();
            this.mWsuWifiEntryCache.clear();
            updateWsuWifiEntryScans(scanResults);
            processConfiguredNetworksChanged();
            return;
        }
        WsuWifiEntry wsuWifiEntry = this.mWsuWifiEntryCache.get(WsuWifiEntry.generateWsuWifiEntryKey(wsuProvider));
        if (wsuWifiEntry != null) {
            wsuWifiEntry.notifyProvisionStatusChanged(i);
        }
    }

    public GoogleWifiPickerTracker(Lifecycle lifecycle, Context context, WifiManager wifiManager, ConnectivityManager connectivityManager, Handler handler, Handler handler2, Clock clock, long j, long j2, WifiPickerTracker.WifiPickerTrackerCallback wifiPickerTrackerCallback) {
        super(lifecycle, context, wifiManager, connectivityManager, handler, handler2, clock, j, j2, wifiPickerTrackerCallback);
        this.mWsuManager = new WsuManager(context, wifiManager, handler2);
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    public void onStart() {
        super.onStart();
        this.mWsuManager.addWsuProvidersLoadCallback(this.mWsuProvidersLoadCallback);
        this.mWsuManager.addWsuProvisionStatusUpdateCallback(this.mWsuProvisonStatusCallback);
        this.mWsuManager.bindAllServices();
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    public void onStop() {
        this.mWsuManager.removeWsuProvidersLoadCallback(this.mWsuProvidersLoadCallback);
        this.mWsuManager.removeWsuProvisionStatusUpdateCallback(this.mWsuProvisonStatusCallback);
        this.mWsuManager.unbindAllServices();
        super.onStop();
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker
    protected List<WifiEntry> getContextualWifiEntries() {
        return (List) this.mWsuWifiEntryCache.values().stream().collect(Collectors.toList());
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker
    protected void updateContextualWifiEntryScans(List<ScanResult> list) {
        updateWsuWifiEntryScans(list);
    }

    private void updateWsuWifiEntryScans(List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        Map<WsuProvider, List<ScanResult>> matchingWsuProviders = this.mWsuManager.getMatchingWsuProviders(list);
        for (WsuProvider wsuProvider : matchingWsuProviders.keySet()) {
            String generateWsuWifiEntryKey = WsuWifiEntry.generateWsuWifiEntryKey(wsuProvider);
            if (!this.mWsuWifiEntryCache.containsKey(generateWsuWifiEntryKey)) {
                this.mWsuWifiEntryCache.put(generateWsuWifiEntryKey, new WsuWifiEntry(this.mContext, this.mMainHandler, wsuProvider, this.mWifiManager));
            }
            this.mWsuWifiEntryCache.get(generateWsuWifiEntryKey).updateScanResultInfo(matchingWsuProviders.get(wsuProvider));
            this.mWsuWifiEntryCache.get(generateWsuWifiEntryKey).setSignupAction(this.mWsuManager.createSignupAction(wsuProvider));
        }
        this.mWsuWifiEntryCache.entrySet().removeIf(GoogleWifiPickerTracker$$ExternalSyntheticLambda2.INSTANCE);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateWsuWifiEntryScans$2(Map.Entry entry) {
        return ((WsuWifiEntry) entry.getValue()).getLevel() == -1;
    }
}
