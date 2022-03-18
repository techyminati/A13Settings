package com.google.android.wifitrackerlib;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.android.wifitrackerlib.PasspointWifiEntry;
import com.android.wifitrackerlib.Utils;
import com.android.wifitrackerlib.WifiEntry;
import com.android.wsuinterface.IGetNetworkGroupSubscriptionsCallback;
import com.android.wsuinterface.ISubscriptionProvisionStatusListener;
import com.android.wsuinterface.IWsuService;
import com.android.wsuinterface.NetworkGroupSubscription;
import com.google.android.wifitrackerlib.WsuManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
/* loaded from: classes2.dex */
public class WsuServiceClient {
    private final Context mContext;
    private final Handler mHandler;
    private boolean mIsBound;
    private final String mPackageName;
    private final WifiManager mWifiManager;
    private final WsuManager mWsuManager;
    private final ISubscriptionProvisionStatusListener mProvisionStatusListener = new ISubscriptionProvisionStatusListener.Stub() { // from class: com.google.android.wifitrackerlib.WsuServiceClient.1
        @Override // com.android.wsuinterface.ISubscriptionProvisionStatusListener
        public void onStatusChanged(String str, int i) {
            WsuServiceClient.this.mHandler.sendMessage(WsuServiceClient.this.mHandler.obtainMessage(2, i, -1, str));
        }
    };
    private Map<String, NetworkGroupSubscription> mNetworkGroups = new HashMap();
    private ServiceConnection mServiceConnection = null;
    private IWsuService mWsuService = null;
    private Map<String, WsuProvider> mWsuProviderCache = new HashMap();

    /* JADX INFO: Access modifiers changed from: package-private */
    public WsuServiceClient(Context context, Handler handler, WifiManager wifiManager, WsuManager wsuManager, String str) {
        this.mContext = context;
        this.mWifiManager = wifiManager;
        this.mPackageName = str;
        this.mHandler = new WsuHandler(handler.getLooper());
        this.mWsuManager = wsuManager;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Map<WsuProvider, List<ScanResult>> getMatchingWsuProviders(List<ScanResult> list) {
        HashMap hashMap = new HashMap();
        for (NetworkGroupSubscription networkGroupSubscription : this.mNetworkGroups.values()) {
            if (networkGroupSubscription.getProvisionStatus() != 2) {
                Map matchingScanResults = this.mWifiManager.getMatchingScanResults(networkGroupSubscription.matchingSuggestions, list);
                ArrayList arrayList = new ArrayList();
                for (List list2 : matchingScanResults.values()) {
                    arrayList.addAll(list2);
                }
                if (arrayList.size() != 0) {
                    hashMap.put(getWsuProvider(networkGroupSubscription), arrayList);
                }
            }
        }
        return hashMap;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WifiEntry.ManageSubscriptionAction tryGetManageSubscriptionAction(WifiEntry wifiEntry) {
        for (NetworkGroupSubscription networkGroupSubscription : this.mNetworkGroups.values()) {
            if (isManagedWifiEntry(wifiEntry, networkGroupSubscription)) {
                logd("WifiEntry - " + wifiEntry.getKey() + ", managed by: " + networkGroupSubscription.uniqueIdentifier);
                return createManageSubscriptionAction(networkGroupSubscription);
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String tryGetOverrideConnectedSummary(WifiEntry wifiEntry) {
        for (NetworkGroupSubscription networkGroupSubscription : this.mNetworkGroups.values()) {
            if (isManagedWifiEntry(wifiEntry, networkGroupSubscription)) {
                logd("Summary for WifiEntry - " + wifiEntry.getKey() + ", managed by: " + networkGroupSubscription.uniqueIdentifier);
                return this.mContext.getString(R$string.wifitrackerlib_connected_with_wsu_authenticator, networkGroupSubscription.subscriptionAuthenticator);
            }
        }
        return null;
    }

    private boolean isManagedWifiEntry(WifiEntry wifiEntry, NetworkGroupSubscription networkGroupSubscription) {
        if (networkGroupSubscription.getApiVersion() >= 2 && networkGroupSubscription.getManageSubscriptionAction() == null) {
            return false;
        }
        if (wifiEntry instanceof PasspointWifiEntry) {
            PasspointConfiguration passpointConfig = ((PasspointWifiEntry) wifiEntry).getPasspointConfig();
            for (PasspointConfiguration passpointConfiguration : networkGroupSubscription.getProvisionedPasspoints()) {
                if (passpointConfig == null) {
                    WifiConfiguration wifiConfiguration = wifiEntry.getWifiConfiguration();
                    if (wifiConfiguration != null && TextUtils.equals(passpointConfiguration.getUniqueId(), wifiConfiguration.getKey())) {
                        return true;
                    }
                } else if (TextUtils.equals(passpointConfiguration.getUniqueId(), passpointConfig.getUniqueId())) {
                    return true;
                }
            }
        } else if (wifiEntry.getWifiConfiguration() != null && (!wifiEntry.getWifiConfiguration().isEphemeral() || wifiEntry.getWifiConfiguration().fromWifiNetworkSuggestion)) {
            for (WifiConfiguration wifiConfiguration2 : networkGroupSubscription.getProvisionedWifiConfigurations()) {
                if (TextUtils.equals(wifiConfiguration2.getKey(), wifiEntry.getWifiConfiguration().getKey())) {
                    return true;
                }
            }
        }
        return false;
    }

    private WifiEntry.ManageSubscriptionAction createManageSubscriptionAction(final NetworkGroupSubscription networkGroupSubscription) {
        logd("create ManageSubscriptionAction for: " + networkGroupSubscription.uniqueIdentifier);
        if (networkGroupSubscription.getApiVersion() >= 2) {
            return new WifiEntry.ManageSubscriptionAction() { // from class: com.google.android.wifitrackerlib.WsuServiceClient$$ExternalSyntheticLambda0
                @Override // com.android.wifitrackerlib.WifiEntry.ManageSubscriptionAction
                public final void onExecute() {
                    WsuServiceClient.this.lambda$createManageSubscriptionAction$0(networkGroupSubscription);
                }
            };
        }
        return new WifiEntry.ManageSubscriptionAction() { // from class: com.google.android.wifitrackerlib.WsuServiceClient$$ExternalSyntheticLambda1
            @Override // com.android.wifitrackerlib.WifiEntry.ManageSubscriptionAction
            public final void onExecute() {
                WsuServiceClient.this.lambda$createManageSubscriptionAction$1(networkGroupSubscription);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createManageSubscriptionAction$0(NetworkGroupSubscription networkGroupSubscription) {
        try {
            networkGroupSubscription.getManageSubscriptionAction().send();
        } catch (PendingIntent.CanceledException e) {
            loge("The sign up action was canceled. " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createManageSubscriptionAction$1(NetworkGroupSubscription networkGroupSubscription) {
        Intent intent = new Intent("android.net.wifi.wsu.action.SHOW_WSU_MANAGE_DIALOG");
        intent.addFlags(8388608);
        intent.setPackage(this.mPackageName);
        intent.putExtra("android.net.wifi.wsu.extra.NGS_UNIQUE_IDENTIFIER", networkGroupSubscription.uniqueIdentifier);
        if (this.mContext.getPackageManager().resolveActivity(intent, 0) == null) {
            logd("the specified wsu settings activity is not installed.");
        } else {
            this.mContext.startActivity(intent);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WsuManager.WsuSignupAction createSignupAction(final String str) {
        logd("create WsuSignupAction for: " + str);
        final NetworkGroupSubscription networkGroupSubscription = this.mNetworkGroups.get(str);
        if (networkGroupSubscription == null) {
            logd("no NetworkGroupSubscription for " + str);
        }
        if (networkGroupSubscription.getApiVersion() >= 2) {
            return new WsuManager.WsuSignupAction() { // from class: com.google.android.wifitrackerlib.WsuServiceClient$$ExternalSyntheticLambda2
                @Override // com.google.android.wifitrackerlib.WsuManager.WsuSignupAction
                public final void onExecute() {
                    WsuServiceClient.this.lambda$createSignupAction$2(networkGroupSubscription);
                }
            };
        }
        return new WsuManager.WsuSignupAction() { // from class: com.google.android.wifitrackerlib.WsuServiceClient$$ExternalSyntheticLambda3
            @Override // com.google.android.wifitrackerlib.WsuManager.WsuSignupAction
            public final void onExecute() {
                WsuServiceClient.this.lambda$createSignupAction$3(str);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createSignupAction$2(NetworkGroupSubscription networkGroupSubscription) {
        try {
            networkGroupSubscription.getSignUpAction().send();
        } catch (PendingIntent.CanceledException e) {
            loge("The manage subscription acton was canceled." + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createSignupAction$3(String str) {
        Intent intent = new Intent("android.net.wifi.wsu.action.SHOW_WSU_PROVISION_DIALOG");
        intent.setPackage(this.mPackageName);
        intent.addFlags(8388608);
        intent.putExtra("android.net.wifi.wsu.extra.NGS_UNIQUE_IDENTIFIER", str);
        if (this.mContext.getPackageManager().resolveActivity(intent, 0) == null) {
            logd("the specified sign up activity is not installed.");
        } else {
            this.mContext.startActivity(intent);
        }
    }

    /* loaded from: classes2.dex */
    private class WsuHandler extends Handler {
        WsuHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                WsuServiceClient.this.loadNetworkGroups();
            } else if (i == 1) {
                NetworkGroupsLoadResult networkGroupsLoadResult = (NetworkGroupsLoadResult) message.obj;
                WsuServiceClient.this.mNetworkGroups = new HashMap();
                int i2 = 0;
                for (NetworkGroupSubscription networkGroupSubscription : networkGroupsLoadResult.loadedNetworkGroups) {
                    WsuServiceClient.this.mNetworkGroups.put(networkGroupSubscription.uniqueIdentifier, networkGroupSubscription);
                    i2 = networkGroupSubscription.getApiVersion();
                }
                if (networkGroupsLoadResult.firstProvisionedNetworkGroupId != null && i2 < 2) {
                    WsuServiceClient.this.logd("connect to the provisioned profile for api version 1.");
                    WsuServiceClient wsuServiceClient = WsuServiceClient.this;
                    wsuServiceClient.tryToConnectBestMatchingProvisionedNetwork((NetworkGroupSubscription) wsuServiceClient.mNetworkGroups.get(networkGroupsLoadResult.firstProvisionedNetworkGroupId));
                }
                WsuServiceClient.this.mWsuManager.nofityWsuProvidersLoaded();
            } else if (i != 2) {
                WsuServiceClient wsuServiceClient2 = WsuServiceClient.this;
                wsuServiceClient2.logd("Unknown message : " + message.what);
            } else {
                NetworkGroupSubscription networkGroupSubscription2 = (NetworkGroupSubscription) WsuServiceClient.this.mNetworkGroups.get((String) message.obj);
                if (networkGroupSubscription2 == null) {
                    WsuServiceClient wsuServiceClient3 = WsuServiceClient.this;
                    wsuServiceClient3.logd("The status of NetworkGroupSubscription of " + networkGroupSubscription2 + " is not loaded!");
                    return;
                }
                int provisionStatus = networkGroupSubscription2.getProvisionStatus();
                int i3 = message.arg1;
                if (provisionStatus != 2 && i3 == 2) {
                    WsuServiceClient.this.loadNetworkGroups(networkGroupSubscription2.uniqueIdentifier);
                }
                networkGroupSubscription2.setProvisionStatus(i3);
                WsuServiceClient.this.mWsuManager.notifyWsuProvisionStatusUpdated(WsuServiceClient.this.getWsuProvider(networkGroupSubscription2), i3);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void tryToConnectBestMatchingProvisionedNetwork(NetworkGroupSubscription networkGroupSubscription) {
        WifiConfiguration wifiConfiguration;
        if (networkGroupSubscription == null) {
            logd("no just provisioned network, this shouldn't be called.");
            return;
        }
        this.mWifiManager.getScanResults();
        Pair pair = null;
        Pair pair2 = null;
        for (PasspointConfiguration passpointConfiguration : networkGroupSubscription.getProvisionedPasspoints()) {
            String uniqueId = passpointConfiguration.getUniqueId();
            WifiManager wifiManager = this.mWifiManager;
            for (Pair pair3 : wifiManager.getAllMatchingWifiConfigs(wifiManager.getScanResults())) {
                WifiConfiguration wifiConfiguration2 = (WifiConfiguration) pair3.first;
                if (TextUtils.equals(wifiConfiguration2.getKey(), uniqueId)) {
                    List list = (List) ((Map) pair3.second).get(0);
                    List list2 = (List) ((Map) pair3.second).get(1);
                    if (list != null && !list.isEmpty()) {
                        ScanResult bestScanResultByLevel = Utils.getBestScanResultByLevel(list);
                        if (pair == null || ((ScanResult) pair.second).level < bestScanResultByLevel.level) {
                            pair = new Pair(wifiConfiguration2, bestScanResultByLevel);
                        }
                    }
                    if (list2 != null && !list2.isEmpty()) {
                        ScanResult bestScanResultByLevel2 = Utils.getBestScanResultByLevel(list2);
                        if (pair2 == null || ((ScanResult) pair2.second).level < bestScanResultByLevel2.level) {
                            pair2 = new Pair(wifiConfiguration2, bestScanResultByLevel2);
                        }
                    }
                }
            }
        }
        if (pair != null) {
            wifiConfiguration = (WifiConfiguration) pair.first;
            wifiConfiguration.SSID = "\"" + ((ScanResult) pair.second).SSID + "\"";
        } else if (pair2 != null) {
            WifiConfiguration wifiConfiguration3 = (WifiConfiguration) pair2.first;
            wifiConfiguration3.SSID = "\"" + ((ScanResult) pair2.second).SSID + "\"";
            wifiConfiguration = wifiConfiguration3;
        } else {
            wifiConfiguration = null;
        }
        if (wifiConfiguration != null) {
            logd("connect to network: " + wifiConfiguration.getKey() + ", provided by " + networkGroupSubscription.uniqueIdentifier);
            this.mWifiManager.connect(wifiConfiguration, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public WsuProvider getWsuProvider(final NetworkGroupSubscription networkGroupSubscription) {
        return this.mWsuProviderCache.computeIfAbsent(networkGroupSubscription.uniqueIdentifier, new Function() { // from class: com.google.android.wifitrackerlib.WsuServiceClient$$ExternalSyntheticLambda4
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                WsuProvider lambda$getWsuProvider$4;
                lambda$getWsuProvider$4 = WsuServiceClient.this.lambda$getWsuProvider$4(networkGroupSubscription, (String) obj);
                return lambda$getWsuProvider$4;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ WsuProvider lambda$getWsuProvider$4(NetworkGroupSubscription networkGroupSubscription, String str) {
        return new WsuProvider(this.mPackageName, networkGroupSubscription.uniqueIdentifier, networkGroupSubscription.subscriptionProviderName, networkGroupSubscription.helpUriString);
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadNetworkGroups() {
        loadNetworkGroups(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadNetworkGroups(final String str) {
        IWsuService iWsuService = this.mWsuService;
        if (iWsuService == null) {
            logd("service is not available to load networks.");
            return;
        }
        try {
            iWsuService.getNetworkGroupSubscriptions(new IGetNetworkGroupSubscriptionsCallback.Stub() { // from class: com.google.android.wifitrackerlib.WsuServiceClient.2
                @Override // com.android.wsuinterface.IGetNetworkGroupSubscriptionsCallback
                public void onAvailable(List<NetworkGroupSubscription> list) {
                    WsuServiceClient.this.mHandler.sendMessage(WsuServiceClient.this.mHandler.obtainMessage(1, new NetworkGroupsLoadResult(list, str)));
                }
            });
        } catch (RemoteException e) {
            logd("load network groups failed, caused by: " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void bindWsuService() {
        logd("Binding to " + this.mPackageName);
        Intent intent = new Intent("android.net.wifi.wsu.WsuService");
        intent.setPackage(this.mPackageName);
        if (this.mContext.getPackageManager().resolveService(intent, 0) == null) {
            logd("the specified wsu service is not installed.");
            return;
        }
        WsuServiceConnection wsuServiceConnection = new WsuServiceConnection();
        this.mServiceConnection = wsuServiceConnection;
        try {
            this.mIsBound = this.mContext.bindService(intent, wsuServiceConnection, 1);
        } catch (SecurityException e) {
            loge("failed to bind the WsuService: " + this.mPackageName + ", caused by: " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void unbindWsuService() {
        ServiceConnection serviceConnection;
        IWsuService iWsuService = this.mWsuService;
        if (iWsuService != null) {
            try {
                iWsuService.unregisterSubscriptionProvisionStatusListener(this.mProvisionStatusListener);
            } catch (RemoteException e) {
                loge("unregister provision status listener failed, caused by: " + e);
            }
            this.mWsuService = null;
        }
        if (this.mIsBound && (serviceConnection = this.mServiceConnection) != null) {
            this.mIsBound = false;
            try {
                this.mContext.unbindService(serviceConnection);
            } catch (IllegalArgumentException e2) {
                loge("already unbound? caused by: " + e2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class WsuServiceConnection implements ServiceConnection {
        WsuServiceConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            WsuServiceClient.this.logd("WsuService connected.");
            WsuServiceClient.this.mWsuService = IWsuService.Stub.asInterface(iBinder);
            try {
                WsuServiceClient.this.mWsuService.registerSubscriptionProvisionStatusListener(WsuServiceClient.this.mProvisionStatusListener);
            } catch (RemoteException e) {
                WsuServiceClient wsuServiceClient = WsuServiceClient.this;
                wsuServiceClient.logd("register provision status listener failed, caused by: " + e);
            }
            WsuServiceClient.this.mHandler.sendMessage(WsuServiceClient.this.mHandler.obtainMessage(0, iBinder));
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            WsuServiceClient wsuServiceClient = WsuServiceClient.this;
            wsuServiceClient.logd("Disconnected from WsuService app: " + componentName.flattenToShortString());
            WsuServiceClient.this.mWsuService = null;
        }

        @Override // android.content.ServiceConnection
        public void onBindingDied(ComponentName componentName) {
            WsuServiceClient wsuServiceClient = WsuServiceClient.this;
            wsuServiceClient.logd("Binding died from WsuService app: " + componentName.flattenToShortString());
            WsuServiceClient.this.mWsuService = null;
        }

        @Override // android.content.ServiceConnection
        public void onNullBinding(ComponentName componentName) {
            WsuServiceClient wsuServiceClient = WsuServiceClient.this;
            wsuServiceClient.logd("Null binding from WsuService app: " + componentName.flattenToShortString());
            WsuServiceClient.this.mWsuService = null;
        }
    }

    /* loaded from: classes2.dex */
    private static final class NetworkGroupsLoadResult {
        public final String firstProvisionedNetworkGroupId;
        public final List<NetworkGroupSubscription> loadedNetworkGroups;

        private NetworkGroupsLoadResult() {
            this.loadedNetworkGroups = null;
            this.firstProvisionedNetworkGroupId = null;
        }

        NetworkGroupsLoadResult(List<NetworkGroupSubscription> list, String str) {
            this.loadedNetworkGroups = list;
            this.firstProvisionedNetworkGroupId = str;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logd(String str) {
        Log.d("WsuServiceClient", str);
    }

    private void loge(String str) {
        Log.e("WsuServiceClient", str);
    }
}
