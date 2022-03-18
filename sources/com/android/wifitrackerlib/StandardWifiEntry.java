package com.android.wifitrackerlib;

import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.app.admin.WifiSsidPolicy;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiSsid;
import android.os.Handler;
import android.os.SystemClock;
import android.os.UserManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import androidx.core.os.BuildCompat;
import com.android.wifitrackerlib.WifiEntry;
import com.google.android.settings.security.SecurityContentManager;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class StandardWifiEntry extends WifiEntry {
    private final Context mContext;
    private final DevicePolicyManager mDevicePolicyManager;
    private boolean mHasAddConfigUserRestriction;
    private final WifiTrackerInjector mInjector;
    private boolean mIsAdminRestricted;
    private final boolean mIsEnhancedOpenSupported;
    private boolean mIsUserShareable;
    private final boolean mIsWpa3SaeSupported;
    private final boolean mIsWpa3SuiteBSupported;
    private final StandardWifiEntryKey mKey;
    private final Map<Integer, List<ScanResult>> mMatchingScanResults;
    private final Map<Integer, WifiConfiguration> mMatchingWifiConfigs;
    private boolean mShouldAutoOpenCaptivePortal;
    private final List<ScanResult> mTargetScanResults;
    private List<Integer> mTargetSecurityTypes;
    private WifiConfiguration mTargetWifiConfig;
    private final UserManager mUserManager;

    /* JADX INFO: Access modifiers changed from: package-private */
    public StandardWifiEntry(WifiTrackerInjector wifiTrackerInjector, Context context, Handler handler, StandardWifiEntryKey standardWifiEntryKey, WifiManager wifiManager, boolean z) {
        super(handler, wifiManager, z);
        this.mMatchingScanResults = new HashMap();
        this.mMatchingWifiConfigs = new HashMap();
        this.mTargetScanResults = new ArrayList();
        this.mTargetSecurityTypes = new ArrayList();
        this.mIsUserShareable = false;
        this.mShouldAutoOpenCaptivePortal = false;
        this.mIsAdminRestricted = false;
        this.mHasAddConfigUserRestriction = false;
        this.mInjector = wifiTrackerInjector;
        this.mContext = context;
        this.mKey = standardWifiEntryKey;
        this.mIsWpa3SaeSupported = wifiManager.isWpa3SaeSupported();
        this.mIsWpa3SuiteBSupported = wifiManager.isWpa3SuiteBSupported();
        this.mIsEnhancedOpenSupported = wifiManager.isEnhancedOpenSupported();
        this.mUserManager = wifiTrackerInjector.getUserManager();
        this.mDevicePolicyManager = wifiTrackerInjector.getDevicePolicyManager();
        updateSecurityTypes();
        if (BuildCompat.isAtLeastT()) {
            updateAdminRestrictions();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public StandardWifiEntry(WifiTrackerInjector wifiTrackerInjector, Context context, Handler handler, StandardWifiEntryKey standardWifiEntryKey, List<WifiConfiguration> list, List<ScanResult> list2, WifiManager wifiManager, boolean z) throws IllegalArgumentException {
        this(wifiTrackerInjector, context, handler, standardWifiEntryKey, wifiManager, z);
        if (list != null && !list.isEmpty()) {
            updateConfig(list);
        }
        if (list2 != null && !list2.isEmpty()) {
            updateScanResultInfo(list2);
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public String getKey() {
        return this.mKey.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public StandardWifiEntryKey getStandardWifiEntryKey() {
        return this.mKey;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public String getTitle() {
        return this.mKey.getScanResultKey().getSsid();
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized String getSummary(boolean z) {
        String str;
        if (hasAdminRestrictions()) {
            return this.mContext.getString(R$string.wifitrackerlib_admin_restricted_network);
        }
        StringJoiner stringJoiner = new StringJoiner(this.mContext.getString(R$string.wifitrackerlib_summary_separator));
        int connectedState = getConnectedState();
        if (connectedState == 0) {
            str = Utils.getDisconnectedDescription(this.mInjector, this.mContext, this.mTargetWifiConfig, this.mForSavedNetworksPage, z);
        } else if (connectedState == 1) {
            str = Utils.getConnectingDescription(this.mContext, this.mNetworkInfo);
        } else if (connectedState != 2) {
            Log.e("StandardWifiEntry", "getConnectedState() returned unknown state: " + connectedState);
            str = null;
        } else {
            str = Utils.getConnectedDescription(this.mContext, this.mTargetWifiConfig, this.mNetworkCapabilities, this.mIsDefaultNetwork, this.mIsLowQuality);
        }
        if (!TextUtils.isEmpty(str)) {
            stringJoiner.add(str);
        }
        String autoConnectDescription = Utils.getAutoConnectDescription(this.mContext, this);
        if (!TextUtils.isEmpty(autoConnectDescription)) {
            stringJoiner.add(autoConnectDescription);
        }
        String meteredDescription = Utils.getMeteredDescription(this.mContext, this);
        if (!TextUtils.isEmpty(meteredDescription)) {
            stringJoiner.add(meteredDescription);
        }
        if (!z) {
            String verboseLoggingDescription = Utils.getVerboseLoggingDescription(this);
            if (!TextUtils.isEmpty(verboseLoggingDescription)) {
                stringJoiner.add(verboseLoggingDescription);
            }
        }
        return stringJoiner.toString();
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public CharSequence getSecondSummary() {
        return getConnectedState() == 2 ? Utils.getImsiProtectionDescription(this.mContext, getWifiConfiguration()) : "";
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public String getSsid() {
        return this.mKey.getScanResultKey().getSsid();
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized List<Integer> getSecurityTypes() {
        return new ArrayList(this.mTargetSecurityTypes);
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized String getMacAddress() {
        WifiInfo wifiInfo = this.mWifiInfo;
        if (wifiInfo != null) {
            String macAddress = wifiInfo.getMacAddress();
            if (!TextUtils.isEmpty(macAddress) && !TextUtils.equals(macAddress, "02:00:00:00:00:00")) {
                return macAddress;
            }
        }
        if (this.mTargetWifiConfig != null && getPrivacy() == 1) {
            return this.mTargetWifiConfig.getRandomizedMacAddress().toString();
        }
        String[] factoryMacAddresses = this.mWifiManager.getFactoryMacAddresses();
        if (factoryMacAddresses.length <= 0) {
            return null;
        }
        return factoryMacAddresses[0];
    }

    /* JADX WARN: Code restructure failed: missing block: B:8:0x000e, code lost:
        if (r0.meteredHint != false) goto L_0x0012;
     */
    @Override // com.android.wifitrackerlib.WifiEntry
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized boolean isMetered() {
        /*
            r2 = this;
            monitor-enter(r2)
            int r0 = r2.getMeteredChoice()     // Catch: all -> 0x0014
            r1 = 1
            if (r0 == r1) goto L_0x0012
            android.net.wifi.WifiConfiguration r0 = r2.mTargetWifiConfig     // Catch: all -> 0x0014
            if (r0 == 0) goto L_0x0011
            boolean r0 = r0.meteredHint     // Catch: all -> 0x0014
            if (r0 == 0) goto L_0x0011
            goto L_0x0012
        L_0x0011:
            r1 = 0
        L_0x0012:
            monitor-exit(r2)
            return r1
        L_0x0014:
            r0 = move-exception
            monitor-exit(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.wifitrackerlib.StandardWifiEntry.isMetered():boolean");
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized boolean isSaved() {
        boolean z;
        WifiConfiguration wifiConfiguration = this.mTargetWifiConfig;
        if (wifiConfiguration != null && !wifiConfiguration.fromWifiNetworkSuggestion) {
            if (!wifiConfiguration.isEphemeral()) {
                z = true;
            }
        }
        z = false;
        return z;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized boolean isSuggestion() {
        boolean z;
        WifiConfiguration wifiConfiguration = this.mTargetWifiConfig;
        if (wifiConfiguration != null) {
            if (wifiConfiguration.fromWifiNetworkSuggestion) {
                z = true;
            }
        }
        z = false;
        return z;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized WifiConfiguration getWifiConfiguration() {
        if (!isSaved()) {
            return null;
        }
        return this.mTargetWifiConfig;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized boolean canConnect() {
        WifiConfiguration wifiConfiguration;
        WifiEnterpriseConfig wifiEnterpriseConfig;
        if (this.mLevel != -1 && getConnectedState() == 0) {
            if (hasAdminRestrictions()) {
                return false;
            }
            if (!this.mTargetSecurityTypes.contains(3) || (wifiConfiguration = this.mTargetWifiConfig) == null || (wifiEnterpriseConfig = wifiConfiguration.enterpriseConfig) == null) {
                return true;
            }
            if (!wifiEnterpriseConfig.isAuthenticationSimBased()) {
                return true;
            }
            List<SubscriptionInfo> activeSubscriptionInfoList = ((SubscriptionManager) this.mContext.getSystemService("telephony_subscription_service")).getActiveSubscriptionInfoList();
            if (!(activeSubscriptionInfoList == null || activeSubscriptionInfoList.size() == 0)) {
                if (this.mTargetWifiConfig.carrierId == -1) {
                    return true;
                }
                for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
                    if (subscriptionInfo.getCarrierId() == this.mTargetWifiConfig.carrierId) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        }
        return false;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized void connect(final WifiEntry.ConnectCallback connectCallback) {
        this.mConnectCallback = connectCallback;
        this.mShouldAutoOpenCaptivePortal = true;
        this.mWifiManager.stopRestrictingAutoJoinToSubscriptionId();
        if (!isSaved() && !isSuggestion()) {
            if (this.mTargetSecurityTypes.contains(6)) {
                WifiConfiguration wifiConfiguration = new WifiConfiguration();
                wifiConfiguration.SSID = "\"" + this.mKey.getScanResultKey().getSsid() + "\"";
                wifiConfiguration.setSecurityParams(6);
                this.mWifiManager.connect(wifiConfiguration, new WifiEntry.ConnectActionListener());
                if (this.mTargetSecurityTypes.contains(0)) {
                    WifiConfiguration wifiConfiguration2 = new WifiConfiguration();
                    wifiConfiguration2.SSID = "\"" + this.mKey.getScanResultKey().getSsid() + "\"";
                    wifiConfiguration2.setSecurityParams(0);
                    this.mWifiManager.save(wifiConfiguration2, null);
                }
            } else if (this.mTargetSecurityTypes.contains(0)) {
                WifiConfiguration wifiConfiguration3 = new WifiConfiguration();
                wifiConfiguration3.SSID = "\"" + this.mKey.getScanResultKey().getSsid() + "\"";
                wifiConfiguration3.setSecurityParams(0);
                this.mWifiManager.connect(wifiConfiguration3, new WifiEntry.ConnectActionListener());
            } else if (connectCallback != null) {
                this.mCallbackHandler.post(new Runnable() { // from class: com.android.wifitrackerlib.StandardWifiEntry$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        WifiEntry.ConnectCallback.this.onConnectResult(1);
                    }
                });
            }
            return;
        }
        if (!Utils.isSimCredential(this.mTargetWifiConfig) || Utils.isSimPresent(this.mContext, this.mTargetWifiConfig.carrierId)) {
            this.mWifiManager.connect(this.mTargetWifiConfig.networkId, new WifiEntry.ConnectActionListener());
            return;
        }
        if (connectCallback != null) {
            this.mCallbackHandler.post(new Runnable() { // from class: com.android.wifitrackerlib.StandardWifiEntry$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    WifiEntry.ConnectCallback.this.onConnectResult(3);
                }
            });
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean canDisconnect() {
        return getConnectedState() == 2;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized void disconnect(final WifiEntry.DisconnectCallback disconnectCallback) {
        if (canDisconnect()) {
            this.mCalledDisconnect = true;
            this.mDisconnectCallback = disconnectCallback;
            this.mCallbackHandler.postDelayed(new Runnable() { // from class: com.android.wifitrackerlib.StandardWifiEntry$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    StandardWifiEntry.this.lambda$disconnect$2(disconnectCallback);
                }
            }, 10000L);
            WifiManager wifiManager = this.mWifiManager;
            wifiManager.disableEphemeralNetwork("\"" + this.mKey.getScanResultKey().getSsid() + "\"");
            this.mWifiManager.disconnect();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$disconnect$2(WifiEntry.DisconnectCallback disconnectCallback) {
        if (disconnectCallback != null && this.mCalledDisconnect) {
            disconnectCallback.onDisconnectResult(1);
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean canForget() {
        return getWifiConfiguration() != null;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized void forget(WifiEntry.ForgetCallback forgetCallback) {
        if (canForget()) {
            this.mForgetCallback = forgetCallback;
            this.mWifiManager.forget(this.mTargetWifiConfig.networkId, new WifiEntry.ForgetActionListener());
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized boolean canSignIn() {
        boolean z;
        NetworkCapabilities networkCapabilities = this.mNetworkCapabilities;
        if (networkCapabilities != null) {
            if (networkCapabilities.hasCapability(17)) {
                z = true;
            }
        }
        z = false;
        return z;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public void signIn(WifiEntry.SignInCallback signInCallback) {
        if (canSignIn()) {
            HiddenApiWrapper.startCaptivePortalApp((ConnectivityManager) this.mContext.getSystemService(ConnectivityManager.class), this.mWifiManager.getCurrentNetwork());
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x0044 A[Catch: all -> 0x0061, TRY_LEAVE, TryCatch #0 {, blocks: (B:3:0x0001, B:8:0x000c, B:12:0x0014, B:14:0x001a, B:16:0x002a, B:20:0x0038, B:21:0x003e, B:23:0x0044), top: B:39:0x0001 }] */
    @Override // com.android.wifitrackerlib.WifiEntry
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized boolean canShare() {
        /*
            r5 = this;
            monitor-enter(r5)
            com.android.wifitrackerlib.WifiTrackerInjector r0 = r5.mInjector     // Catch: all -> 0x0061
            boolean r0 = r0.isDemoMode()     // Catch: all -> 0x0061
            r1 = 0
            if (r0 == 0) goto L_0x000c
            monitor-exit(r5)
            return r1
        L_0x000c:
            android.net.wifi.WifiConfiguration r0 = r5.getWifiConfiguration()     // Catch: all -> 0x0061
            if (r0 != 0) goto L_0x0014
            monitor-exit(r5)
            return r1
        L_0x0014:
            boolean r2 = androidx.core.os.BuildCompat.isAtLeastT()     // Catch: all -> 0x0061
            if (r2 == 0) goto L_0x0038
            android.os.UserManager r2 = r5.mUserManager     // Catch: all -> 0x0061
            java.lang.String r3 = "no_sharing_admin_configured_wifi"
            int r4 = r0.creatorUid     // Catch: all -> 0x0061
            android.os.UserHandle r4 = android.os.UserHandle.getUserHandleForUid(r4)     // Catch: all -> 0x0061
            boolean r2 = r2.hasUserRestrictionForUser(r3, r4)     // Catch: all -> 0x0061
            if (r2 == 0) goto L_0x0038
            int r2 = r0.creatorUid     // Catch: all -> 0x0061
            java.lang.String r0 = r0.creatorName     // Catch: all -> 0x0061
            android.content.Context r3 = r5.mContext     // Catch: all -> 0x0061
            boolean r0 = com.android.wifitrackerlib.Utils.isDeviceOrProfileOwner(r2, r0, r3)     // Catch: all -> 0x0061
            if (r0 == 0) goto L_0x0038
            monitor-exit(r5)
            return r1
        L_0x0038:
            java.util.List<java.lang.Integer> r0 = r5.mTargetSecurityTypes     // Catch: all -> 0x0061
            java.util.Iterator r0 = r0.iterator()     // Catch: all -> 0x0061
        L_0x003e:
            boolean r2 = r0.hasNext()     // Catch: all -> 0x0061
            if (r2 == 0) goto L_0x005f
            java.lang.Object r2 = r0.next()     // Catch: all -> 0x0061
            java.lang.Integer r2 = (java.lang.Integer) r2     // Catch: all -> 0x0061
            int r2 = r2.intValue()     // Catch: all -> 0x0061
            r3 = 1
            if (r2 == 0) goto L_0x005d
            if (r2 == r3) goto L_0x005d
            r4 = 2
            if (r2 == r4) goto L_0x005d
            r4 = 4
            if (r2 == r4) goto L_0x005d
            r4 = 6
            if (r2 == r4) goto L_0x005d
            goto L_0x003e
        L_0x005d:
            monitor-exit(r5)
            return r3
        L_0x005f:
            monitor-exit(r5)
            return r1
        L_0x0061:
            r0 = move-exception
            monitor-exit(r5)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.wifitrackerlib.StandardWifiEntry.canShare():boolean");
    }

    /* JADX WARN: Code restructure failed: missing block: B:27:0x005a, code lost:
        if (r5.mTargetSecurityTypes.contains(4) != false) goto L_0x005c;
     */
    @Override // com.android.wifitrackerlib.WifiEntry
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized boolean canEasyConnect() {
        /*
            r5 = this;
            monitor-enter(r5)
            com.android.wifitrackerlib.WifiTrackerInjector r0 = r5.mInjector     // Catch: all -> 0x005f
            boolean r0 = r0.isDemoMode()     // Catch: all -> 0x005f
            r1 = 0
            if (r0 == 0) goto L_0x000c
            monitor-exit(r5)
            return r1
        L_0x000c:
            android.net.wifi.WifiConfiguration r0 = r5.getWifiConfiguration()     // Catch: all -> 0x005f
            if (r0 != 0) goto L_0x0014
            monitor-exit(r5)
            return r1
        L_0x0014:
            android.net.wifi.WifiManager r2 = r5.mWifiManager     // Catch: all -> 0x005f
            boolean r2 = r2.isEasyConnectSupported()     // Catch: all -> 0x005f
            if (r2 != 0) goto L_0x001e
            monitor-exit(r5)
            return r1
        L_0x001e:
            boolean r2 = androidx.core.os.BuildCompat.isAtLeastT()     // Catch: all -> 0x005f
            if (r2 == 0) goto L_0x0042
            android.os.UserManager r2 = r5.mUserManager     // Catch: all -> 0x005f
            java.lang.String r3 = "no_sharing_admin_configured_wifi"
            int r4 = r0.creatorUid     // Catch: all -> 0x005f
            android.os.UserHandle r4 = android.os.UserHandle.getUserHandleForUid(r4)     // Catch: all -> 0x005f
            boolean r2 = r2.hasUserRestrictionForUser(r3, r4)     // Catch: all -> 0x005f
            if (r2 == 0) goto L_0x0042
            int r2 = r0.creatorUid     // Catch: all -> 0x005f
            java.lang.String r0 = r0.creatorName     // Catch: all -> 0x005f
            android.content.Context r3 = r5.mContext     // Catch: all -> 0x005f
            boolean r0 = com.android.wifitrackerlib.Utils.isDeviceOrProfileOwner(r2, r0, r3)     // Catch: all -> 0x005f
            if (r0 == 0) goto L_0x0042
            monitor-exit(r5)
            return r1
        L_0x0042:
            java.util.List<java.lang.Integer> r0 = r5.mTargetSecurityTypes     // Catch: all -> 0x005f
            r2 = 2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch: all -> 0x005f
            boolean r0 = r0.contains(r2)     // Catch: all -> 0x005f
            if (r0 != 0) goto L_0x005c
            java.util.List<java.lang.Integer> r0 = r5.mTargetSecurityTypes     // Catch: all -> 0x005f
            r2 = 4
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch: all -> 0x005f
            boolean r0 = r0.contains(r2)     // Catch: all -> 0x005f
            if (r0 == 0) goto L_0x005d
        L_0x005c:
            r1 = 1
        L_0x005d:
            monitor-exit(r5)
            return r1
        L_0x005f:
            r0 = move-exception
            monitor-exit(r5)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.wifitrackerlib.StandardWifiEntry.canEasyConnect():boolean");
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized int getMeteredChoice() {
        WifiConfiguration wifiConfiguration;
        if (!isSuggestion() && (wifiConfiguration = this.mTargetWifiConfig) != null) {
            int i = wifiConfiguration.meteredOverride;
            if (i == 1) {
                return 1;
            }
            if (i == 2) {
                return 2;
            }
        }
        return 0;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean canSetMeteredChoice() {
        return getWifiConfiguration() != null;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized void setMeteredChoice(int i) {
        if (canSetMeteredChoice()) {
            if (i == 0) {
                this.mTargetWifiConfig.meteredOverride = 0;
            } else if (i == 1) {
                this.mTargetWifiConfig.meteredOverride = 1;
            } else if (i == 2) {
                this.mTargetWifiConfig.meteredOverride = 2;
            }
            this.mWifiManager.save(this.mTargetWifiConfig, null);
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean canSetPrivacy() {
        return isSaved();
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized int getPrivacy() {
        WifiConfiguration wifiConfiguration = this.mTargetWifiConfig;
        if (wifiConfiguration != null) {
            if (wifiConfiguration.macRandomizationSetting == 0) {
                return 0;
            }
        }
        return 1;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized void setPrivacy(int i) {
        if (canSetPrivacy()) {
            WifiConfiguration wifiConfiguration = this.mTargetWifiConfig;
            wifiConfiguration.macRandomizationSetting = i == 1 ? 3 : 0;
            this.mWifiManager.save(wifiConfiguration, null);
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized boolean isAutoJoinEnabled() {
        WifiConfiguration wifiConfiguration = this.mTargetWifiConfig;
        if (wifiConfiguration == null) {
            return false;
        }
        return wifiConfiguration.allowAutojoin;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean canSetAutoJoinEnabled() {
        return isSaved() || isSuggestion();
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized void setAutoJoinEnabled(boolean z) {
        if (this.mTargetWifiConfig != null && canSetAutoJoinEnabled()) {
            this.mWifiManager.allowAutojoin(this.mTargetWifiConfig.networkId, z);
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized String getSecurityString(boolean z) {
        String str;
        String str2;
        String str3;
        String str4;
        String str5;
        String str6;
        String str7;
        String str8;
        String str9;
        if (this.mTargetSecurityTypes.size() == 0) {
            return z ? "" : this.mContext.getString(R$string.wifitrackerlib_wifi_security_none);
        }
        if (this.mTargetSecurityTypes.size() == 1) {
            int intValue = this.mTargetSecurityTypes.get(0).intValue();
            if (intValue != 9) {
                switch (intValue) {
                    case 0:
                        return z ? "" : this.mContext.getString(R$string.wifitrackerlib_wifi_security_none);
                    case 1:
                        return this.mContext.getString(R$string.wifitrackerlib_wifi_security_wep);
                    case 2:
                        if (z) {
                            str5 = this.mContext.getString(R$string.wifitrackerlib_wifi_security_short_wpa_wpa2);
                        } else {
                            str5 = this.mContext.getString(R$string.wifitrackerlib_wifi_security_wpa_wpa2);
                        }
                        return str5;
                    case 3:
                        if (z) {
                            str6 = this.mContext.getString(R$string.wifitrackerlib_wifi_security_short_eap_wpa_wpa2);
                        } else {
                            str6 = this.mContext.getString(R$string.wifitrackerlib_wifi_security_eap_wpa_wpa2);
                        }
                        return str6;
                    case 4:
                        if (z) {
                            str7 = this.mContext.getString(R$string.wifitrackerlib_wifi_security_short_sae);
                        } else {
                            str7 = this.mContext.getString(R$string.wifitrackerlib_wifi_security_sae);
                        }
                        return str7;
                    case 5:
                        if (z) {
                            str8 = this.mContext.getString(R$string.wifitrackerlib_wifi_security_short_eap_suiteb);
                        } else {
                            str8 = this.mContext.getString(R$string.wifitrackerlib_wifi_security_eap_suiteb);
                        }
                        return str8;
                    case 6:
                        if (z) {
                            str9 = this.mContext.getString(R$string.wifitrackerlib_wifi_security_short_owe);
                        } else {
                            str9 = this.mContext.getString(R$string.wifitrackerlib_wifi_security_owe);
                        }
                        return str9;
                }
            } else {
                if (z) {
                    str4 = this.mContext.getString(R$string.wifitrackerlib_wifi_security_short_eap_wpa3);
                } else {
                    str4 = this.mContext.getString(R$string.wifitrackerlib_wifi_security_eap_wpa3);
                }
                return str4;
            }
        }
        if (this.mTargetSecurityTypes.size() == 2) {
            if (this.mTargetSecurityTypes.contains(0) && this.mTargetSecurityTypes.contains(6)) {
                StringJoiner stringJoiner = new StringJoiner("/");
                stringJoiner.add(this.mContext.getString(R$string.wifitrackerlib_wifi_security_none));
                if (z) {
                    str3 = this.mContext.getString(R$string.wifitrackerlib_wifi_security_short_owe);
                } else {
                    str3 = this.mContext.getString(R$string.wifitrackerlib_wifi_security_owe);
                }
                stringJoiner.add(str3);
                return stringJoiner.toString();
            } else if (this.mTargetSecurityTypes.contains(2) && this.mTargetSecurityTypes.contains(4)) {
                if (z) {
                    str2 = this.mContext.getString(R$string.wifitrackerlib_wifi_security_short_wpa_wpa2_wpa3);
                } else {
                    str2 = this.mContext.getString(R$string.wifitrackerlib_wifi_security_wpa_wpa2_wpa3);
                }
                return str2;
            } else if (this.mTargetSecurityTypes.contains(3) && this.mTargetSecurityTypes.contains(9)) {
                if (z) {
                    str = this.mContext.getString(R$string.wifitrackerlib_wifi_security_short_eap_wpa_wpa2_wpa3);
                } else {
                    str = this.mContext.getString(R$string.wifitrackerlib_wifi_security_eap_wpa_wpa2_wpa3);
                }
                return str;
            }
        }
        Log.e("StandardWifiEntry", "Couldn't get string for security types: " + this.mTargetSecurityTypes);
        return z ? "" : this.mContext.getString(R$string.wifitrackerlib_wifi_security_none);
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x0028, code lost:
        if (r0.getDisableReasonCounter(5) > 0) goto L_0x002a;
     */
    @Override // com.android.wifitrackerlib.WifiEntry
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized boolean shouldEditBeforeConnect() {
        /*
            r3 = this;
            monitor-enter(r3)
            android.net.wifi.WifiConfiguration r0 = r3.getWifiConfiguration()     // Catch: all -> 0x002f
            r1 = 0
            if (r0 != 0) goto L_0x000a
            monitor-exit(r3)
            return r1
        L_0x000a:
            android.net.wifi.WifiConfiguration$NetworkSelectionStatus r0 = r0.getNetworkSelectionStatus()     // Catch: all -> 0x002f
            int r2 = r0.getNetworkSelectionStatus()     // Catch: all -> 0x002f
            if (r2 == 0) goto L_0x002d
            r2 = 2
            int r2 = r0.getDisableReasonCounter(r2)     // Catch: all -> 0x002f
            if (r2 > 0) goto L_0x002a
            r2 = 8
            int r2 = r0.getDisableReasonCounter(r2)     // Catch: all -> 0x002f
            if (r2 > 0) goto L_0x002a
            r2 = 5
            int r0 = r0.getDisableReasonCounter(r2)     // Catch: all -> 0x002f
            if (r0 <= 0) goto L_0x002d
        L_0x002a:
            r0 = 1
            monitor-exit(r3)
            return r0
        L_0x002d:
            monitor-exit(r3)
            return r1
        L_0x002f:
            r0 = move-exception
            monitor-exit(r3)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.wifitrackerlib.StandardWifiEntry.shouldEditBeforeConnect():boolean");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void updateScanResultInfo(List<ScanResult> list) throws IllegalArgumentException {
        if (list == null) {
            list = new ArrayList<>();
        }
        String ssid = this.mKey.getScanResultKey().getSsid();
        for (ScanResult scanResult : list) {
            if (!TextUtils.equals(scanResult.SSID, ssid)) {
                throw new IllegalArgumentException("Attempted to update with wrong SSID! Expected: " + ssid + ", Actual: " + scanResult.SSID + ", ScanResult: " + scanResult);
            }
        }
        this.mMatchingScanResults.clear();
        Set<Integer> securityTypes = this.mKey.getScanResultKey().getSecurityTypes();
        for (ScanResult scanResult2 : list) {
            for (Integer num : Utils.getSecurityTypesFromScanResult(scanResult2)) {
                int intValue = num.intValue();
                if (securityTypes.contains(Integer.valueOf(intValue)) && isSecurityTypeSupported(intValue)) {
                    if (!this.mMatchingScanResults.containsKey(Integer.valueOf(intValue))) {
                        this.mMatchingScanResults.put(Integer.valueOf(intValue), new ArrayList());
                    }
                    this.mMatchingScanResults.get(Integer.valueOf(intValue)).add(scanResult2);
                }
            }
        }
        updateSecurityTypes();
        updateTargetScanResultInfo();
        notifyOnUpdated();
    }

    private synchronized void updateTargetScanResultInfo() {
        ScanResult bestScanResultByLevel = Utils.getBestScanResultByLevel(this.mTargetScanResults);
        if (getConnectedState() == 0) {
            this.mLevel = bestScanResultByLevel != null ? this.mWifiManager.calculateSignalLevel(bestScanResultByLevel.level) : -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized void updateNetworkCapabilities(NetworkCapabilities networkCapabilities) {
        super.updateNetworkCapabilities(networkCapabilities);
        if (canSignIn() && this.mShouldAutoOpenCaptivePortal) {
            this.mShouldAutoOpenCaptivePortal = false;
            signIn(null);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void updateConfig(List<WifiConfiguration> list) throws IllegalArgumentException {
        if (list == null) {
            list = Collections.emptyList();
        }
        ScanResultKey scanResultKey = this.mKey.getScanResultKey();
        String ssid = scanResultKey.getSsid();
        Set<Integer> securityTypes = scanResultKey.getSecurityTypes();
        this.mMatchingWifiConfigs.clear();
        for (WifiConfiguration wifiConfiguration : list) {
            if (TextUtils.equals(ssid, WifiInfo.sanitizeSsid(wifiConfiguration.SSID))) {
                for (Integer num : Utils.getSecurityTypesFromWifiConfiguration(wifiConfiguration)) {
                    int intValue = num.intValue();
                    if (!securityTypes.contains(Integer.valueOf(intValue))) {
                        throw new IllegalArgumentException("Attempted to update with wrong security! Expected one of: " + securityTypes + ", Actual: " + intValue + ", Config: " + wifiConfiguration);
                    } else if (isSecurityTypeSupported(intValue)) {
                        this.mMatchingWifiConfigs.put(Integer.valueOf(intValue), wifiConfiguration);
                    }
                }
            } else {
                throw new IllegalArgumentException("Attempted to update with wrong SSID! Expected: " + ssid + ", Actual: " + WifiInfo.sanitizeSsid(wifiConfiguration.SSID) + ", Config: " + wifiConfiguration);
            }
        }
        updateSecurityTypes();
        updateTargetScanResultInfo();
        notifyOnUpdated();
    }

    private boolean isSecurityTypeSupported(int i) {
        if (i == 4) {
            return this.mIsWpa3SaeSupported;
        }
        if (i == 5) {
            return this.mIsWpa3SuiteBSupported;
        }
        if (i != 6) {
            return true;
        }
        return this.mIsEnhancedOpenSupported;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    protected synchronized void updateSecurityTypes() {
        this.mTargetSecurityTypes.clear();
        WifiInfo wifiInfo = this.mWifiInfo;
        if (!(wifiInfo == null || wifiInfo.getCurrentSecurityType() == -1)) {
            this.mTargetSecurityTypes.add(Integer.valueOf(this.mWifiInfo.getCurrentSecurityType()));
        }
        Set<Integer> keySet = this.mMatchingWifiConfigs.keySet();
        if (this.mTargetSecurityTypes.isEmpty() && this.mKey.isTargetingNewNetworks()) {
            boolean z = false;
            Set<Integer> keySet2 = this.mMatchingScanResults.keySet();
            Iterator<Integer> it = keySet.iterator();
            while (true) {
                if (it.hasNext()) {
                    if (keySet2.contains(Integer.valueOf(it.next().intValue()))) {
                        z = true;
                        break;
                    }
                } else {
                    break;
                }
            }
            if (!z) {
                this.mTargetSecurityTypes.addAll(keySet2);
            }
        }
        if (this.mTargetSecurityTypes.isEmpty()) {
            this.mTargetSecurityTypes.addAll(keySet);
        }
        if (this.mTargetSecurityTypes.isEmpty()) {
            this.mTargetSecurityTypes.addAll(this.mKey.getScanResultKey().getSecurityTypes());
        }
        this.mTargetWifiConfig = this.mMatchingWifiConfigs.get(Integer.valueOf(Utils.getSingleSecurityTypeFromMultipleSecurityTypes(this.mTargetSecurityTypes)));
        ArraySet arraySet = new ArraySet();
        for (Integer num : this.mTargetSecurityTypes) {
            int intValue = num.intValue();
            if (this.mMatchingScanResults.containsKey(Integer.valueOf(intValue))) {
                arraySet.addAll(this.mMatchingScanResults.get(Integer.valueOf(intValue)));
            }
        }
        this.mTargetScanResults.clear();
        this.mTargetScanResults.addAll(arraySet);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void setUserShareable(boolean z) {
        this.mIsUserShareable = z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized boolean isUserShareable() {
        return this.mIsUserShareable;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    protected synchronized boolean connectionInfoMatches(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        if (!wifiInfo.isPasspointAp() && !wifiInfo.isOsuAp()) {
            for (WifiConfiguration wifiConfiguration : this.mMatchingWifiConfigs.values()) {
                if (wifiConfiguration.networkId == wifiInfo.getNetworkId()) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized String getScanResultDescription() {
        if (this.mTargetScanResults.size() == 0) {
            return "";
        }
        return "[" + getScanResultDescription(2400, 2500) + ";" + getScanResultDescription(4900, 5900) + ";" + getScanResultDescription(5925, 7125) + ";" + getScanResultDescription(58320, 70200) + "]";
    }

    private synchronized String getScanResultDescription(final int i, final int i2) {
        List list = (List) this.mTargetScanResults.stream().filter(new Predicate() { // from class: com.android.wifitrackerlib.StandardWifiEntry$$ExternalSyntheticLambda4
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getScanResultDescription$3;
                lambda$getScanResultDescription$3 = StandardWifiEntry.lambda$getScanResultDescription$3(i, i2, (ScanResult) obj);
                return lambda$getScanResultDescription$3;
            }
        }).sorted(Comparator.comparingInt(StandardWifiEntry$$ExternalSyntheticLambda6.INSTANCE)).collect(Collectors.toList());
        int size = list.size();
        if (size == 0) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(size);
        sb.append(")");
        if (size > 4) {
            int asInt = list.stream().mapToInt(StandardWifiEntry$$ExternalSyntheticLambda5.INSTANCE).max().getAsInt();
            sb.append("max=");
            sb.append(asInt);
            sb.append(",");
        }
        final long elapsedRealtime = SystemClock.elapsedRealtime();
        list.forEach(new Consumer() { // from class: com.android.wifitrackerlib.StandardWifiEntry$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                StandardWifiEntry.this.lambda$getScanResultDescription$6(sb, elapsedRealtime, (ScanResult) obj);
            }
        });
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getScanResultDescription$3(int i, int i2, ScanResult scanResult) {
        int i3 = scanResult.frequency;
        return i3 >= i && i3 <= i2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$getScanResultDescription$4(ScanResult scanResult) {
        return scanResult.level * (-1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getScanResultDescription$6(StringBuilder sb, long j, ScanResult scanResult) {
        sb.append(getScanResultDescription(scanResult, j));
    }

    private synchronized String getScanResultDescription(ScanResult scanResult, long j) {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(" \n{");
        sb.append(scanResult.BSSID);
        WifiInfo wifiInfo = this.mWifiInfo;
        if (wifiInfo != null && scanResult.BSSID.equals(wifiInfo.getBSSID())) {
            sb.append("*");
        }
        sb.append("=");
        sb.append(scanResult.frequency);
        sb.append(",");
        sb.append(scanResult.level);
        int i = ((int) (j - (scanResult.timestamp / 1000))) / SecurityContentManager.DEFAULT_ORDER;
        sb.append(",");
        sb.append(i);
        sb.append("s");
        sb.append("}");
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.wifitrackerlib.WifiEntry
    public String getNetworkSelectionDescription() {
        return Utils.getNetworkSelectionDescription(getWifiConfiguration());
    }

    @SuppressLint({"NewApi"})
    private void updateAdminRestrictions() {
        UserManager userManager = this.mUserManager;
        if (userManager != null) {
            this.mHasAddConfigUserRestriction = userManager.hasUserRestriction("no_add_wifi_config");
        }
        DevicePolicyManager devicePolicyManager = this.mDevicePolicyManager;
        if (devicePolicyManager != null) {
            int minimumRequiredWifiSecurityLevel = devicePolicyManager.getMinimumRequiredWifiSecurityLevel();
            if (minimumRequiredWifiSecurityLevel != 0) {
                boolean z = false;
                Iterator<Integer> it = getSecurityTypes().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    int convertSecurityTypeToDpmWifiSecurity = Utils.convertSecurityTypeToDpmWifiSecurity(it.next().intValue());
                    if (convertSecurityTypeToDpmWifiSecurity != -1 && minimumRequiredWifiSecurityLevel <= convertSecurityTypeToDpmWifiSecurity) {
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    this.mIsAdminRestricted = true;
                    return;
                }
            }
            WifiSsidPolicy wifiSsidPolicy = this.mDevicePolicyManager.getWifiSsidPolicy();
            if (wifiSsidPolicy != null) {
                int policyType = wifiSsidPolicy.getPolicyType();
                Set ssids = wifiSsidPolicy.getSsids();
                if (policyType == 0 && !ssids.contains(WifiSsid.fromBytes(getSsid().getBytes(StandardCharsets.UTF_8)))) {
                    this.mIsAdminRestricted = true;
                } else if (policyType == 1 && ssids.contains(WifiSsid.fromBytes(getSsid().getBytes(StandardCharsets.UTF_8)))) {
                    this.mIsAdminRestricted = true;
                }
            }
        }
    }

    private boolean hasAdminRestrictions() {
        return (this.mHasAddConfigUserRestriction && !isSaved() && !isSuggestion()) || this.mIsAdminRestricted;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class StandardWifiEntryKey {
        private boolean mIsNetworkRequest;
        private boolean mIsTargetingNewNetworks;
        private ScanResultKey mScanResultKey;
        private String mSuggestionProfileKey;

        /* JADX INFO: Access modifiers changed from: package-private */
        public StandardWifiEntryKey(ScanResultKey scanResultKey, boolean z) {
            this.mScanResultKey = scanResultKey;
            this.mIsTargetingNewNetworks = z;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public StandardWifiEntryKey(WifiConfiguration wifiConfiguration) {
            this(wifiConfiguration, false);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public StandardWifiEntryKey(WifiConfiguration wifiConfiguration, boolean z) {
            this.mIsTargetingNewNetworks = false;
            this.mScanResultKey = new ScanResultKey(wifiConfiguration);
            if (wifiConfiguration.fromWifiNetworkSuggestion) {
                this.mSuggestionProfileKey = new StringJoiner(",").add(wifiConfiguration.creatorName).add(String.valueOf(wifiConfiguration.carrierId)).add(String.valueOf(wifiConfiguration.subscriptionId)).toString();
            } else if (wifiConfiguration.fromWifiNetworkSpecifier) {
                this.mIsNetworkRequest = true;
            }
            this.mIsTargetingNewNetworks = z;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public StandardWifiEntryKey(String str) {
            this.mIsTargetingNewNetworks = false;
            this.mScanResultKey = new ScanResultKey();
            if (!str.startsWith("StandardWifiEntry:")) {
                Log.e("StandardWifiEntry", "String key does not start with key prefix!");
                return;
            }
            try {
                JSONObject jSONObject = new JSONObject(str.substring(18));
                if (jSONObject.has("SCAN_RESULT_KEY")) {
                    this.mScanResultKey = new ScanResultKey(jSONObject.getString("SCAN_RESULT_KEY"));
                }
                if (jSONObject.has("SUGGESTION_PROFILE_KEY")) {
                    this.mSuggestionProfileKey = jSONObject.getString("SUGGESTION_PROFILE_KEY");
                }
                if (jSONObject.has("IS_NETWORK_REQUEST")) {
                    this.mIsNetworkRequest = jSONObject.getBoolean("IS_NETWORK_REQUEST");
                }
                if (jSONObject.has("IS_TARGETING_NEW_NETWORKS")) {
                    this.mIsTargetingNewNetworks = jSONObject.getBoolean("IS_TARGETING_NEW_NETWORKS");
                }
            } catch (JSONException e) {
                Log.e("StandardWifiEntry", "JSONException while converting StandardWifiEntryKey to string: " + e);
            }
        }

        public String toString() {
            JSONObject jSONObject = new JSONObject();
            try {
                ScanResultKey scanResultKey = this.mScanResultKey;
                if (scanResultKey != null) {
                    jSONObject.put("SCAN_RESULT_KEY", scanResultKey.toString());
                }
                String str = this.mSuggestionProfileKey;
                if (str != null) {
                    jSONObject.put("SUGGESTION_PROFILE_KEY", str);
                }
                boolean z = this.mIsNetworkRequest;
                if (z) {
                    jSONObject.put("IS_NETWORK_REQUEST", z);
                }
                boolean z2 = this.mIsTargetingNewNetworks;
                if (z2) {
                    jSONObject.put("IS_TARGETING_NEW_NETWORKS", z2);
                }
            } catch (JSONException e) {
                Log.wtf("StandardWifiEntry", "JSONException while converting StandardWifiEntryKey to string: " + e);
            }
            return "StandardWifiEntry:" + jSONObject.toString();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public ScanResultKey getScanResultKey() {
            return this.mScanResultKey;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean isNetworkRequest() {
            return this.mIsNetworkRequest;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean isTargetingNewNetworks() {
            return this.mIsTargetingNewNetworks;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            StandardWifiEntryKey standardWifiEntryKey = (StandardWifiEntryKey) obj;
            return Objects.equals(this.mScanResultKey, standardWifiEntryKey.mScanResultKey) && TextUtils.equals(this.mSuggestionProfileKey, standardWifiEntryKey.mSuggestionProfileKey) && this.mIsNetworkRequest == standardWifiEntryKey.mIsNetworkRequest;
        }

        public int hashCode() {
            return Objects.hash(this.mScanResultKey, this.mSuggestionProfileKey, Boolean.valueOf(this.mIsNetworkRequest));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class ScanResultKey {
        private Set<Integer> mSecurityTypes;
        private String mSsid;

        ScanResultKey() {
            this.mSecurityTypes = new ArraySet();
        }

        ScanResultKey(String str, List<Integer> list) {
            this.mSecurityTypes = new ArraySet();
            this.mSsid = str;
            for (Integer num : list) {
                int intValue = num.intValue();
                this.mSecurityTypes.add(Integer.valueOf(intValue));
                if (intValue == 0) {
                    this.mSecurityTypes.add(6);
                } else if (intValue == 6) {
                    this.mSecurityTypes.add(0);
                } else if (intValue == 9) {
                    this.mSecurityTypes.add(3);
                } else if (intValue == 2) {
                    this.mSecurityTypes.add(4);
                } else if (intValue == 3) {
                    this.mSecurityTypes.add(9);
                } else if (intValue == 4) {
                    this.mSecurityTypes.add(2);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public ScanResultKey(ScanResult scanResult) {
            this(scanResult.SSID, Utils.getSecurityTypesFromScanResult(scanResult));
        }

        ScanResultKey(WifiConfiguration wifiConfiguration) {
            this(WifiInfo.sanitizeSsid(wifiConfiguration.SSID), Utils.getSecurityTypesFromWifiConfiguration(wifiConfiguration));
        }

        ScanResultKey(String str) {
            this.mSecurityTypes = new ArraySet();
            try {
                JSONObject jSONObject = new JSONObject(str);
                this.mSsid = jSONObject.getString("SSID");
                JSONArray jSONArray = jSONObject.getJSONArray("SECURITY_TYPES");
                for (int i = 0; i < jSONArray.length(); i++) {
                    this.mSecurityTypes.add(Integer.valueOf(jSONArray.getInt(i)));
                }
            } catch (JSONException e) {
                Log.wtf("StandardWifiEntry", "JSONException while constructing ScanResultKey from string: " + e);
            }
        }

        public String toString() {
            JSONObject jSONObject = new JSONObject();
            try {
                String str = this.mSsid;
                if (str != null) {
                    jSONObject.put("SSID", str);
                }
                if (!this.mSecurityTypes.isEmpty()) {
                    JSONArray jSONArray = new JSONArray();
                    for (Integer num : this.mSecurityTypes) {
                        jSONArray.put(num.intValue());
                    }
                    jSONObject.put("SECURITY_TYPES", jSONArray);
                }
            } catch (JSONException e) {
                Log.e("StandardWifiEntry", "JSONException while converting ScanResultKey to string: " + e);
            }
            return jSONObject.toString();
        }

        String getSsid() {
            return this.mSsid;
        }

        Set<Integer> getSecurityTypes() {
            return this.mSecurityTypes;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ScanResultKey scanResultKey = (ScanResultKey) obj;
            return TextUtils.equals(this.mSsid, scanResultKey.mSsid) && this.mSecurityTypes.equals(scanResultKey.mSecurityTypes);
        }

        public int hashCode() {
            return Objects.hash(this.mSsid, this.mSecurityTypes);
        }
    }
}
