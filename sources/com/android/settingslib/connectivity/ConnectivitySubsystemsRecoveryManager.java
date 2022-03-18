package com.android.settingslib.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.provider.Settings;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.util.Log;
/* loaded from: classes.dex */
public class ConnectivitySubsystemsRecoveryManager {
    private final Context mContext;
    private final Handler mHandler;
    private TelephonyManager mTelephonyManager;
    private WifiManager mWifiManager;
    private RecoveryAvailableListener mRecoveryAvailableListener = null;
    private final BroadcastReceiver mApmMonitor = new BroadcastReceiver() { // from class: com.android.settingslib.connectivity.ConnectivitySubsystemsRecoveryManager.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            RecoveryAvailableListener recoveryAvailableListener = ConnectivitySubsystemsRecoveryManager.this.mRecoveryAvailableListener;
            if (recoveryAvailableListener != null) {
                recoveryAvailableListener.onRecoveryAvailableChangeListener(ConnectivitySubsystemsRecoveryManager.this.isRecoveryAvailable());
            }
        }
    };
    private boolean mApmMonitorRegistered = false;
    private boolean mWifiRestartInProgress = false;
    private boolean mTelephonyRestartInProgress = false;
    private RecoveryStatusCallback mCurrentRecoveryCallback = null;
    private final WifiManager.SubsystemRestartTrackingCallback mWifiSubsystemRestartTrackingCallback = new WifiManager.SubsystemRestartTrackingCallback() { // from class: com.android.settingslib.connectivity.ConnectivitySubsystemsRecoveryManager.2
        public void onSubsystemRestarting() {
        }

        public void onSubsystemRestarted() {
            ConnectivitySubsystemsRecoveryManager.this.mWifiRestartInProgress = false;
            ConnectivitySubsystemsRecoveryManager.this.stopTrackingWifiRestart();
            ConnectivitySubsystemsRecoveryManager.this.checkIfAllSubsystemsRestartsAreDone();
        }
    };
    private final MobileTelephonyCallback mTelephonyCallback = new MobileTelephonyCallback();

    /* loaded from: classes.dex */
    public interface RecoveryAvailableListener {
        void onRecoveryAvailableChangeListener(boolean z);
    }

    /* loaded from: classes.dex */
    public interface RecoveryStatusCallback {
        void onSubsystemRestartOperationBegin();

        void onSubsystemRestartOperationEnd();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MobileTelephonyCallback extends TelephonyCallback implements TelephonyCallback.RadioPowerStateListener {
        private MobileTelephonyCallback() {
        }

        public void onRadioPowerStateChanged(int i) {
            if (!ConnectivitySubsystemsRecoveryManager.this.mTelephonyRestartInProgress || ConnectivitySubsystemsRecoveryManager.this.mCurrentRecoveryCallback == null) {
                ConnectivitySubsystemsRecoveryManager.this.stopTrackingTelephonyRestart();
            }
            if (i == 1) {
                ConnectivitySubsystemsRecoveryManager.this.mTelephonyRestartInProgress = false;
                ConnectivitySubsystemsRecoveryManager.this.stopTrackingTelephonyRestart();
                ConnectivitySubsystemsRecoveryManager.this.checkIfAllSubsystemsRestartsAreDone();
            }
        }
    }

    public ConnectivitySubsystemsRecoveryManager(Context context, Handler handler) {
        this.mWifiManager = null;
        this.mTelephonyManager = null;
        this.mContext = context;
        this.mHandler = new Handler(handler.getLooper());
        if (context.getPackageManager().hasSystemFeature("android.hardware.wifi")) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(WifiManager.class);
            this.mWifiManager = wifiManager;
            if (wifiManager == null) {
                Log.e("ConnectivitySubsystemsRecoveryManager", "WifiManager not available!?");
            }
        }
        if (context.getPackageManager().hasSystemFeature("android.hardware.telephony")) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
            this.mTelephonyManager = telephonyManager;
            if (telephonyManager == null) {
                Log.e("ConnectivitySubsystemsRecoveryManager", "TelephonyManager not available!?");
            }
        }
    }

    private boolean isApmEnabled() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) == 1;
    }

    private boolean isWifiEnabled() {
        WifiManager wifiManager = this.mWifiManager;
        return wifiManager != null && (wifiManager.isWifiEnabled() || this.mWifiManager.isWifiApEnabled());
    }

    public boolean isRecoveryAvailable() {
        if (!isApmEnabled()) {
            return true;
        }
        return isWifiEnabled();
    }

    private void startTrackingWifiRestart() {
        this.mWifiManager.registerSubsystemRestartTrackingCallback(new HandlerExecutor(this.mHandler), this.mWifiSubsystemRestartTrackingCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopTrackingWifiRestart() {
        this.mWifiManager.unregisterSubsystemRestartTrackingCallback(this.mWifiSubsystemRestartTrackingCallback);
    }

    private void startTrackingTelephonyRestart() {
        this.mTelephonyManager.registerTelephonyCallback(new HandlerExecutor(this.mHandler), this.mTelephonyCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopTrackingTelephonyRestart() {
        this.mTelephonyManager.unregisterTelephonyCallback(this.mTelephonyCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkIfAllSubsystemsRestartsAreDone() {
        RecoveryStatusCallback recoveryStatusCallback;
        if (!this.mWifiRestartInProgress && !this.mTelephonyRestartInProgress && (recoveryStatusCallback = this.mCurrentRecoveryCallback) != null) {
            recoveryStatusCallback.onSubsystemRestartOperationEnd();
            this.mCurrentRecoveryCallback = null;
        }
    }

    public void triggerSubsystemRestart(String str, final RecoveryStatusCallback recoveryStatusCallback) {
        this.mHandler.post(new Runnable() { // from class: com.android.settingslib.connectivity.ConnectivitySubsystemsRecoveryManager$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ConnectivitySubsystemsRecoveryManager.this.lambda$triggerSubsystemRestart$3(recoveryStatusCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$triggerSubsystemRestart$3(RecoveryStatusCallback recoveryStatusCallback) {
        if (this.mWifiRestartInProgress) {
            Log.e("ConnectivitySubsystemsRecoveryManager", "Wifi restart still in progress");
        } else if (this.mTelephonyRestartInProgress) {
            Log.e("ConnectivitySubsystemsRecoveryManager", "Telephony restart still in progress");
        } else {
            boolean z = true;
            if (isWifiEnabled()) {
                this.mWifiManager.restartWifiSubsystem();
                this.mWifiRestartInProgress = true;
                startTrackingWifiRestart();
                z = true;
            } else {
                z = false;
            }
            if (this.mTelephonyManager != null && !isApmEnabled() && this.mTelephonyManager.rebootRadio()) {
                this.mTelephonyRestartInProgress = true;
                startTrackingTelephonyRestart();
            }
            if (z) {
                this.mCurrentRecoveryCallback = recoveryStatusCallback;
                recoveryStatusCallback.onSubsystemRestartOperationBegin();
                this.mHandler.postDelayed(new Runnable() { // from class: com.android.settingslib.connectivity.ConnectivitySubsystemsRecoveryManager$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ConnectivitySubsystemsRecoveryManager.this.lambda$triggerSubsystemRestart$2();
                    }
                }, 15000L);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$triggerSubsystemRestart$2() {
        stopTrackingWifiRestart();
        stopTrackingTelephonyRestart();
        this.mWifiRestartInProgress = false;
        this.mTelephonyRestartInProgress = false;
        RecoveryStatusCallback recoveryStatusCallback = this.mCurrentRecoveryCallback;
        if (recoveryStatusCallback != null) {
            recoveryStatusCallback.onSubsystemRestartOperationEnd();
            this.mCurrentRecoveryCallback = null;
        }
    }
}
