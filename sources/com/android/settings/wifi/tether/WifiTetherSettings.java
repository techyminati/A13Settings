package com.android.settings.wifi.tether;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.FeatureFlagUtils;
import android.util.Log;
import androidx.window.R;
import com.android.settings.SettingsActivity;
import com.android.settings.dashboard.RestrictedDashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.SettingsMainSwitchBar;
import com.android.settings.wifi.tether.WifiTetherBasePreferenceController;
import com.android.settingslib.TetherUtil;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class WifiTetherSettings extends RestrictedDashboardFragment implements WifiTetherBasePreferenceController.OnTetherConfigUpdateListener {
    static final String KEY_WIFI_TETHER_AUTO_OFF = "wifi_tether_auto_turn_off";
    static final String KEY_WIFI_TETHER_MAXIMIZE_COMPATIBILITY = "wifi_tether_maximize_compatibility";
    static final String KEY_WIFI_TETHER_NETWORK_NAME = "wifi_tether_network_name";
    static final String KEY_WIFI_TETHER_NETWORK_PASSWORD = "wifi_tether_network_password";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.wifi_tether_settings) { // from class: com.android.settings.wifi.tether.WifiTetherSettings.1
        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            if (!TetherUtil.isTetherAvailable(context)) {
                nonIndexableKeys.add(WifiTetherSettings.KEY_WIFI_TETHER_NETWORK_NAME);
                nonIndexableKeys.add(WifiTetherSettings.KEY_WIFI_TETHER_NETWORK_PASSWORD);
                nonIndexableKeys.add(WifiTetherSettings.KEY_WIFI_TETHER_AUTO_OFF);
                nonIndexableKeys.add(WifiTetherSettings.KEY_WIFI_TETHER_MAXIMIZE_COMPATIBILITY);
            }
            nonIndexableKeys.add("wifi_tether_settings_screen");
            return nonIndexableKeys;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return !FeatureFlagUtils.isEnabled(context, "settings_tether_all_in_one");
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return WifiTetherSettings.buildPreferenceControllers(context, null);
        }
    };
    private static final IntentFilter TETHER_STATE_CHANGE_FILTER;
    private WifiTetherMaximizeCompatibilityPreferenceController mMaxCompatibilityPrefController;
    private WifiTetherPasswordPreferenceController mPasswordPreferenceController;
    private boolean mRestartWifiApAfterConfigChange;
    private WifiTetherSSIDPreferenceController mSSIDPreferenceController;
    private WifiTetherSecurityPreferenceController mSecurityPreferenceController;
    private WifiTetherSwitchBarController mSwitchBarController;
    TetherChangeReceiver mTetherChangeReceiver;
    private boolean mUnavailable;
    private WifiManager mWifiManager;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "WifiTetherSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1014;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.wifi_tether_settings;
    }

    static {
        IntentFilter intentFilter = new IntentFilter("android.net.conn.TETHER_STATE_CHANGED");
        TETHER_STATE_CHANGE_FILTER = intentFilter;
        intentFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
    }

    public WifiTetherSettings() {
        super("no_config_tethering");
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setIfOnlyAvailableForAdmins(true);
        if (isUiRestricted()) {
            this.mUnavailable = true;
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
        this.mTetherChangeReceiver = new TetherChangeReceiver();
        this.mSSIDPreferenceController = (WifiTetherSSIDPreferenceController) use(WifiTetherSSIDPreferenceController.class);
        this.mSecurityPreferenceController = (WifiTetherSecurityPreferenceController) use(WifiTetherSecurityPreferenceController.class);
        this.mPasswordPreferenceController = (WifiTetherPasswordPreferenceController) use(WifiTetherPasswordPreferenceController.class);
        this.mMaxCompatibilityPrefController = (WifiTetherMaximizeCompatibilityPreferenceController) use(WifiTetherMaximizeCompatibilityPreferenceController.class);
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (!this.mUnavailable) {
            SettingsActivity settingsActivity = (SettingsActivity) getActivity();
            SettingsMainSwitchBar switchBar = settingsActivity.getSwitchBar();
            switchBar.setTitle(getContext().getString(R.string.use_wifi_hotsopt_main_switch_title));
            this.mSwitchBarController = new WifiTetherSwitchBarController(settingsActivity, switchBar);
            getSettingsLifecycle().addObserver(this.mSwitchBarController);
            switchBar.show();
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        if (this.mUnavailable) {
            if (!isUiRestrictedByOnlyAdmin()) {
                getEmptyTextView().setText(R.string.tethering_settings_not_available);
            }
            getPreferenceScreen().removeAll();
            return;
        }
        Context context = getContext();
        if (context != null) {
            context.registerReceiver(this.mTetherChangeReceiver, TETHER_STATE_CHANGE_FILTER, 2);
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        Context context;
        super.onStop();
        if (!this.mUnavailable && (context = getContext()) != null) {
            context.unregisterReceiver(this.mTetherChangeReceiver);
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, new WifiTetherBasePreferenceController.OnTetherConfigUpdateListener() { // from class: com.android.settings.wifi.tether.WifiTetherSettings$$ExternalSyntheticLambda0
            @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController.OnTetherConfigUpdateListener
            public final void onTetherConfigUpdated(AbstractPreferenceController abstractPreferenceController) {
                WifiTetherSettings.this.onTetherConfigUpdated(abstractPreferenceController);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new WifiTetherSSIDPreferenceController(context, onTetherConfigUpdateListener));
        arrayList.add(new WifiTetherSecurityPreferenceController(context, onTetherConfigUpdateListener));
        arrayList.add(new WifiTetherPasswordPreferenceController(context, onTetherConfigUpdateListener));
        arrayList.add(new WifiTetherAutoOffPreferenceController(context, KEY_WIFI_TETHER_AUTO_OFF));
        arrayList.add(new WifiTetherMaximizeCompatibilityPreferenceController(context, onTetherConfigUpdateListener));
        return arrayList;
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController.OnTetherConfigUpdateListener
    public void onTetherConfigUpdated(AbstractPreferenceController abstractPreferenceController) {
        SoftApConfiguration buildNewConfig = buildNewConfig();
        this.mPasswordPreferenceController.setSecurityType(buildNewConfig.getSecurityType());
        if (this.mWifiManager.getWifiApState() == 13) {
            Log.d("TetheringSettings", "Wifi AP config changed while enabled, stop and restart");
            this.mRestartWifiApAfterConfigChange = true;
            this.mSwitchBarController.stopTether();
        }
        this.mWifiManager.setSoftApConfiguration(buildNewConfig);
    }

    private SoftApConfiguration buildNewConfig() {
        SoftApConfiguration.Builder builder = new SoftApConfiguration.Builder();
        int securityType = this.mSecurityPreferenceController.getSecurityType();
        builder.setSsid(this.mSSIDPreferenceController.getSSID());
        if (securityType != 0) {
            builder.setPassphrase(this.mPasswordPreferenceController.getPasswordValidated(securityType), securityType);
        }
        this.mMaxCompatibilityPrefController.setupMaximizeCompatibility(builder);
        return builder.build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startTether() {
        this.mRestartWifiApAfterConfigChange = false;
        this.mSwitchBarController.startTether();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDisplayWithNewConfig() {
        ((WifiTetherSSIDPreferenceController) use(WifiTetherSSIDPreferenceController.class)).updateDisplay();
        ((WifiTetherSecurityPreferenceController) use(WifiTetherSecurityPreferenceController.class)).updateDisplay();
        ((WifiTetherPasswordPreferenceController) use(WifiTetherPasswordPreferenceController.class)).updateDisplay();
        ((WifiTetherMaximizeCompatibilityPreferenceController) use(WifiTetherMaximizeCompatibilityPreferenceController.class)).updateDisplay();
    }

    /* loaded from: classes.dex */
    class TetherChangeReceiver extends BroadcastReceiver {
        TetherChangeReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("WifiTetherSettings", "updating display config due to receiving broadcast action " + action);
            WifiTetherSettings.this.updateDisplayWithNewConfig();
            if (action.equals("android.net.conn.TETHER_STATE_CHANGED")) {
                if (WifiTetherSettings.this.mWifiManager.getWifiApState() == 11 && WifiTetherSettings.this.mRestartWifiApAfterConfigChange) {
                    WifiTetherSettings.this.startTether();
                }
            } else if (action.equals("android.net.wifi.WIFI_AP_STATE_CHANGED") && intent.getIntExtra("wifi_state", 0) == 11 && WifiTetherSettings.this.mRestartWifiApAfterConfigChange) {
                WifiTetherSettings.this.startTether();
            }
        }
    }
}
