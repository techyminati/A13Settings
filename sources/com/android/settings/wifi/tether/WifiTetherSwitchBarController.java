package com.android.settings.wifi.tether;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Switch;
import com.android.settings.datausage.DataSaverBackend;
import com.android.settings.widget.SettingsMainSwitchBar;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
/* loaded from: classes.dex */
public class WifiTetherSwitchBarController implements LifecycleObserver, OnStart, OnStop, DataSaverBackend.Listener, View.OnClickListener {
    private static final IntentFilter WIFI_INTENT_FILTER = new IntentFilter("android.net.wifi.WIFI_AP_STATE_CHANGED");
    private final ConnectivityManager mConnectivityManager;
    private final Context mContext;
    final DataSaverBackend mDataSaverBackend;
    final ConnectivityManager.OnStartTetheringCallback mOnStartTetheringCallback = new ConnectivityManager.OnStartTetheringCallback() { // from class: com.android.settings.wifi.tether.WifiTetherSwitchBarController.1
        public void onTetheringFailed() {
            WifiTetherSwitchBarController.super.onTetheringFailed();
            WifiTetherSwitchBarController.this.mSwitchBar.setChecked(false);
            WifiTetherSwitchBarController.this.updateWifiSwitch();
        }
    };
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.settings.wifi.tether.WifiTetherSwitchBarController.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(intent.getAction())) {
                WifiTetherSwitchBarController.this.handleWifiApStateChanged(intent.getIntExtra("wifi_state", 14));
            }
        }
    };
    private final Switch mSwitch;
    private final SettingsMainSwitchBar mSwitchBar;
    private final WifiManager mWifiManager;

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onAllowlistStatusChanged(int i, boolean z) {
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDenylistStatusChanged(int i, boolean z) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WifiTetherSwitchBarController(Context context, SettingsMainSwitchBar settingsMainSwitchBar) {
        this.mContext = context;
        this.mSwitchBar = settingsMainSwitchBar;
        this.mSwitch = settingsMainSwitchBar.getSwitch();
        this.mDataSaverBackend = new DataSaverBackend(context);
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
        this.mWifiManager = wifiManager;
        settingsMainSwitchBar.setChecked(wifiManager.getWifiApState() == 13);
        updateWifiSwitch();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mDataSaverBackend.addListener(this);
        this.mSwitch.setOnClickListener(this);
        this.mContext.registerReceiver(this.mReceiver, WIFI_INTENT_FILTER, 2);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mDataSaverBackend.remListener(this);
        this.mContext.unregisterReceiver(this.mReceiver);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (((Switch) view).isChecked()) {
            startTether();
        } else {
            stopTether();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void stopTether() {
        this.mSwitchBar.setEnabled(false);
        this.mConnectivityManager.stopTethering(0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void startTether() {
        this.mSwitchBar.setEnabled(false);
        this.mConnectivityManager.startTethering(0, true, this.mOnStartTetheringCallback, new Handler(Looper.getMainLooper()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleWifiApStateChanged(int i) {
        switch (i) {
            case 10:
                if (this.mSwitch.isChecked()) {
                    this.mSwitch.setChecked(false);
                }
                this.mSwitchBar.setEnabled(false);
                return;
            case 11:
                this.mSwitch.setChecked(false);
                updateWifiSwitch();
                return;
            case 12:
                this.mSwitchBar.setEnabled(false);
                return;
            case 13:
                if (!this.mSwitch.isChecked()) {
                    this.mSwitch.setChecked(true);
                }
                updateWifiSwitch();
                return;
            default:
                this.mSwitch.setChecked(false);
                updateWifiSwitch();
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateWifiSwitch() {
        this.mSwitchBar.setEnabled(!this.mDataSaverBackend.isDataSaverEnabled());
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDataSaverChanged(boolean z) {
        updateWifiSwitch();
    }
}
