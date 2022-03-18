package com.google.android.wifitrackerlib;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.util.Preconditions;
import com.android.wifitrackerlib.Utils;
import com.android.wifitrackerlib.WifiEntry;
import com.google.android.wifitrackerlib.WsuManager;
import java.util.ArrayList;
import java.util.List;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class WsuWifiEntry extends WifiEntry {
    private final Context mContext;
    private final String mKey;
    private WsuManager.WsuSignupAction mSignupAction;
    private final WsuProvider mWsuProvider;
    private final Object mLock = new Object();
    private final List<ScanResult> mCurrentScanResults = new ArrayList();
    private int mProvisionStatus = 0;

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean canDisconnect() {
        return false;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean canEasyConnect() {
        return false;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean canForget() {
        return false;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean canSetAutoJoinEnabled() {
        return false;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean canSetMeteredChoice() {
        return false;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean canSetPrivacy() {
        return false;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean canShare() {
        return false;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean canSignIn() {
        return false;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    protected boolean connectionInfoMatches(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        return false;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public String getMacAddress() {
        return null;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public int getMeteredChoice() {
        return 0;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public int getPrivacy() {
        return 2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.wifitrackerlib.WifiEntry
    public String getScanResultDescription() {
        return "";
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public int getSecurity() {
        return 0;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public String getSecurityString(boolean z) {
        return "";
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public String getSsid() {
        return "";
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public WifiConfiguration getWifiConfiguration() {
        return null;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean isAutoJoinEnabled() {
        return false;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean isMetered() {
        return false;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean isSaved() {
        return false;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean isSubscription() {
        return false;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean isSuggestion() {
        return false;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public void setAutoJoinEnabled(boolean z) {
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public void setMeteredChoice(int i) {
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public void setPrivacy(int i) {
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public void signIn(WifiEntry.SignInCallback signInCallback) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WsuWifiEntry(Context context, Handler handler, WsuProvider wsuProvider, WifiManager wifiManager) throws IllegalArgumentException {
        super(handler, wifiManager, false);
        this.mContext = context;
        this.mWsuProvider = wsuProvider;
        this.mKey = generateWsuWifiEntryKey(wsuProvider);
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public String getKey() {
        return this.mKey;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public String getTitle() {
        return this.mWsuProvider.wsuProviderName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyProvisionStatusChanged(int i) {
        if (this.mProvisionStatus != i) {
            this.mProvisionStatus = i;
            notifyOnUpdated();
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public String getSummary() {
        return getSummary(true);
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public String getSummary(boolean z) {
        int i = this.mProvisionStatus;
        if (i != 0) {
            if (i == 1) {
                return this.mContext.getResources().getString(R$string.wifitrackerlib_wsu_entry_summary_provisioning);
            }
            if (i == 3) {
                return this.mContext.getResources().getString(R$string.wifitrackerlib_wsu_entry_summary_provision_error);
            }
            if (i != 4) {
                String string = this.mContext.getResources().getString(R$string.wifitrackerlib_wsu_entry_summary_not_provisioned);
                Log.e("WsuWifiEntry", "unhandled provision status: " + this.mProvisionStatus);
                return string;
            }
        }
        return this.mContext.getResources().getString(R$string.wifitrackerlib_wsu_entry_summary_not_provisioned);
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public String getHelpUriString() {
        if (TextUtils.isEmpty(this.mWsuProvider.helpUriString)) {
            return null;
        }
        return this.mWsuProvider.helpUriString;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean canConnect() {
        return this.mLevel != -1 && getConnectedState() == 0;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public void connect(WifiEntry.ConnectCallback connectCallback) {
        WsuManager.WsuSignupAction wsuSignupAction = this.mSignupAction;
        if (wsuSignupAction != null) {
            wsuSignupAction.onExecute();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSignupAction(WsuManager.WsuSignupAction wsuSignupAction) {
        this.mSignupAction = wsuSignupAction;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public void disconnect(WifiEntry.DisconnectCallback disconnectCallback) {
        throw new IllegalStateException("This shouldn't be called.");
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public void forget(WifiEntry.ForgetCallback forgetCallback) {
        throw new IllegalStateException("This shouldn't be called.");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateScanResultInfo(List<ScanResult> list) throws IllegalArgumentException {
        if (list == null) {
            list = new ArrayList<>();
        }
        synchronized (this.mLock) {
            this.mCurrentScanResults.clear();
            this.mCurrentScanResults.addAll(list);
        }
        ScanResult bestScanResultByLevel = Utils.getBestScanResultByLevel(list);
        if (getConnectedState() == 0) {
            this.mLevel = bestScanResultByLevel != null ? this.mWifiManager.calculateSignalLevel(bestScanResultByLevel.level) : -1;
        }
        notifyOnUpdated();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String generateWsuWifiEntryKey(WsuProvider wsuProvider) {
        Preconditions.checkNotNull(wsuProvider, "Cannot create key with null WsuProvider!");
        return "WsuWifiEntry:" + wsuProvider.getWsuIdentity();
    }
}
