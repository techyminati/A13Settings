package com.google.android.wifitrackerlib;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.ArraySet;
import com.android.internal.annotations.VisibleForTesting;
import com.android.wifitrackerlib.WifiEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
@VisibleForTesting
/* loaded from: classes2.dex */
public class WsuManager {
    private final Context mContext;
    private final WifiManager mWifiManager;
    private final Handler mWorkerHandler;
    private final List<WsuServiceClient> mWsuServiceClients = new ArrayList();
    private final Set<WsuProvidersLoadCallback> mWsuProvidersLoadCallbacks = new ArraySet();
    private final Set<WsuProvisionStatusUpdateCallback> mWsuProvisionStatusUpdateCallbacks = new ArraySet();

    /* loaded from: classes2.dex */
    public interface WsuProvidersLoadCallback {
        void onLoaded();
    }

    /* loaded from: classes2.dex */
    public interface WsuProvisionStatusUpdateCallback {
        void onProvisionStatusChanged(WsuProvider wsuProvider, int i);
    }

    /* loaded from: classes2.dex */
    public interface WsuSignupAction {
        void onExecute();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WsuManager(Context context, WifiManager wifiManager, Handler handler) {
        this.mContext = context;
        this.mWifiManager = wifiManager;
        this.mWorkerHandler = handler;
        for (String str : loadWsuServicePkgs()) {
            this.mWsuServiceClients.add(new WsuServiceClient(this.mContext, this.mWorkerHandler, this.mWifiManager, this, str));
        }
    }

    private List<String> loadWsuServicePkgs() {
        return Arrays.asList(this.mContext.getResources().getStringArray(R$array.wifitrackerlib_wsu_service_provider_packages));
    }

    @VisibleForTesting
    public void bindAllServices() {
        for (WsuServiceClient wsuServiceClient : this.mWsuServiceClients) {
            wsuServiceClient.bindWsuService();
        }
    }

    @VisibleForTesting
    public void unbindAllServices() {
        for (WsuServiceClient wsuServiceClient : this.mWsuServiceClients) {
            wsuServiceClient.unbindWsuService();
        }
    }

    @VisibleForTesting
    public Map<WsuProvider, List<ScanResult>> getMatchingWsuProviders(List<ScanResult> list) {
        HashMap hashMap = new HashMap();
        for (WsuServiceClient wsuServiceClient : this.mWsuServiceClients) {
            hashMap.putAll(wsuServiceClient.getMatchingWsuProviders(list));
        }
        return hashMap;
    }

    @VisibleForTesting
    public void addWsuProvidersLoadCallback(WsuProvidersLoadCallback wsuProvidersLoadCallback) {
        this.mWsuProvidersLoadCallbacks.add(wsuProvidersLoadCallback);
    }

    @VisibleForTesting
    public void removeWsuProvidersLoadCallback(WsuProvidersLoadCallback wsuProvidersLoadCallback) {
        this.mWsuProvidersLoadCallbacks.remove(wsuProvidersLoadCallback);
    }

    @VisibleForTesting
    public void nofityWsuProvidersLoaded() {
        for (WsuProvidersLoadCallback wsuProvidersLoadCallback : this.mWsuProvidersLoadCallbacks) {
            wsuProvidersLoadCallback.onLoaded();
        }
    }

    @VisibleForTesting
    public void addWsuProvisionStatusUpdateCallback(WsuProvisionStatusUpdateCallback wsuProvisionStatusUpdateCallback) {
        this.mWsuProvisionStatusUpdateCallbacks.add(wsuProvisionStatusUpdateCallback);
    }

    @VisibleForTesting
    public void removeWsuProvisionStatusUpdateCallback(WsuProvisionStatusUpdateCallback wsuProvisionStatusUpdateCallback) {
        this.mWsuProvisionStatusUpdateCallbacks.remove(wsuProvisionStatusUpdateCallback);
    }

    @VisibleForTesting
    public void notifyWsuProvisionStatusUpdated(WsuProvider wsuProvider, int i) {
        for (WsuProvisionStatusUpdateCallback wsuProvisionStatusUpdateCallback : this.mWsuProvisionStatusUpdateCallbacks) {
            wsuProvisionStatusUpdateCallback.onProvisionStatusChanged(wsuProvider, i);
        }
    }

    @VisibleForTesting
    public WsuSignupAction createSignupAction(WsuProvider wsuProvider) {
        for (WsuServiceClient wsuServiceClient : this.mWsuServiceClients) {
            if (wsuServiceClient.getPackageName().equals(wsuProvider.servicePackageName)) {
                return wsuServiceClient.createSignupAction(wsuProvider.networkGroupIdentity);
            }
        }
        return null;
    }

    @VisibleForTesting
    public WifiEntry.ManageSubscriptionAction tryGetManageSubscriptionAction(WifiEntry wifiEntry) {
        WifiEntry.ManageSubscriptionAction manageSubscriptionAction = null;
        for (WsuServiceClient wsuServiceClient : this.mWsuServiceClients) {
            manageSubscriptionAction = wsuServiceClient.tryGetManageSubscriptionAction(wifiEntry);
        }
        return manageSubscriptionAction;
    }

    @VisibleForTesting
    public String tryGetOverrideConnectedSummary(WifiEntry wifiEntry) {
        for (WsuServiceClient wsuServiceClient : this.mWsuServiceClients) {
            String tryGetOverrideConnectedSummary = wsuServiceClient.tryGetOverrideConnectedSummary(wifiEntry);
            if (!TextUtils.isEmpty(tryGetOverrideConnectedSummary)) {
                return tryGetOverrideConnectedSummary;
            }
        }
        return null;
    }
}
