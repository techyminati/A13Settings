package com.android.settings.wifi.tether;

import android.content.Context;
import android.net.TetheringManager;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiClient;
import android.net.wifi.WifiManager;
import android.text.BidiFormatter;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.window.R;
import com.android.settings.Utils;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.wifi.tether.WifiTetherSoftApManager;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.wifi.WifiEnterpriseRestrictionUtils;
import com.android.settingslib.wifi.WifiUtils;
import java.util.List;
/* loaded from: classes.dex */
public class WifiTetherPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, LifecycleObserver, OnStart, OnStop {
    boolean mIsWifiTetheringAllow;
    private final Lifecycle mLifecycle;
    Preference mPreference;
    private int mSoftApState;
    private final TetheringManager mTetheringManager;
    private final WifiManager mWifiManager;
    private final String[] mWifiRegexs;
    WifiTetherSoftApManager mWifiTetherSoftApManager;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "wifi_tether";
    }

    public WifiTetherPreferenceController(Context context, Lifecycle lifecycle) {
        this(context, lifecycle, true);
    }

    WifiTetherPreferenceController(Context context, Lifecycle lifecycle, boolean z) {
        super(context);
        TetheringManager tetheringManager = (TetheringManager) context.getSystemService(TetheringManager.class);
        this.mTetheringManager = tetheringManager;
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
        this.mWifiRegexs = tetheringManager.getTetherableWifiRegexs();
        this.mIsWifiTetheringAllow = WifiEnterpriseRestrictionUtils.isWifiTetheringAllowed(context);
        this.mLifecycle = lifecycle;
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
        if (z) {
            initWifiTetherSoftApManager();
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        String[] strArr = this.mWifiRegexs;
        return (strArr == null || strArr.length == 0 || Utils.isMonkeyRunning()) ? false : true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference("wifi_tether");
        this.mPreference = findPreference;
        if (findPreference != null) {
            findPreference.setEnabled(this.mIsWifiTetheringAllow);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        WifiTetherSoftApManager wifiTetherSoftApManager;
        if (this.mPreference != null && (wifiTetherSoftApManager = this.mWifiTetherSoftApManager) != null) {
            wifiTetherSoftApManager.registerSoftApCallback();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        WifiTetherSoftApManager wifiTetherSoftApManager;
        if (this.mPreference != null && (wifiTetherSoftApManager = this.mWifiTetherSoftApManager) != null) {
            wifiTetherSoftApManager.unRegisterSoftApCallback();
        }
    }

    void initWifiTetherSoftApManager() {
        this.mWifiTetherSoftApManager = new WifiTetherSoftApManager(this.mWifiManager, new WifiTetherSoftApManager.WifiTetherSoftApCallback() { // from class: com.android.settings.wifi.tether.WifiTetherPreferenceController.1
            @Override // com.android.settings.wifi.tether.WifiTetherSoftApManager.WifiTetherSoftApCallback
            public void onStateChanged(int i, int i2) {
                WifiTetherPreferenceController.this.mSoftApState = i;
                WifiTetherPreferenceController.this.handleWifiApStateChanged(i, i2);
            }

            @Override // com.android.settings.wifi.tether.WifiTetherSoftApManager.WifiTetherSoftApCallback
            public void onConnectedClientsChanged(List<WifiClient> list) {
                WifiTetherPreferenceController wifiTetherPreferenceController = WifiTetherPreferenceController.this;
                if (wifiTetherPreferenceController.mPreference != null && wifiTetherPreferenceController.mSoftApState == 13) {
                    WifiTetherPreferenceController wifiTetherPreferenceController2 = WifiTetherPreferenceController.this;
                    wifiTetherPreferenceController2.mPreference.setSummary(WifiUtils.getWifiTetherSummaryForConnectedDevices(((AbstractPreferenceController) wifiTetherPreferenceController2).mContext, list.size()));
                }
            }
        });
    }

    void handleWifiApStateChanged(int i, int i2) {
        switch (i) {
            case 10:
                this.mPreference.setSummary(R.string.wifi_tether_stopping);
                return;
            case 11:
                this.mPreference.setSummary(R.string.wifi_hotspot_off_subtext);
                return;
            case 12:
                this.mPreference.setSummary(R.string.wifi_tether_starting);
                return;
            case 13:
                updateConfigSummary(this.mWifiManager.getSoftApConfiguration());
                return;
            default:
                if (i2 == 1) {
                    this.mPreference.setSummary(R.string.wifi_sap_no_channel_error);
                    return;
                } else {
                    this.mPreference.setSummary(R.string.wifi_error);
                    return;
                }
        }
    }

    private void updateConfigSummary(SoftApConfiguration softApConfiguration) {
        if (softApConfiguration != null) {
            this.mPreference.setSummary(this.mContext.getString(R.string.wifi_tether_enabled_subtext, BidiFormatter.getInstance().unicodeWrap(softApConfiguration.getSsid())));
        }
    }
}
