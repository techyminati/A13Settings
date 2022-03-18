package com.android.settings.wifi.tether;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.SoftApConfiguration;
import android.text.TextUtils;
import android.util.FeatureFlagUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.widget.ValidatedEditTextPreference;
import com.android.settings.wifi.dpp.WifiDppUtils;
import com.android.settings.wifi.tether.WifiTetherBasePreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
/* loaded from: classes.dex */
public class WifiTetherSSIDPreferenceController extends WifiTetherBasePreferenceController implements ValidatedEditTextPreference.Validator {
    static final String DEFAULT_SSID = "AndroidAP";
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private String mSSID;
    private WifiDeviceNameTextValidator mWifiDeviceNameTextValidator = new WifiDeviceNameTextValidator();

    WifiTetherSSIDPreferenceController(Context context, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener, MetricsFeatureProvider metricsFeatureProvider) {
        super(context, onTetherConfigUpdateListener);
        this.mMetricsFeatureProvider = metricsFeatureProvider;
    }

    public WifiTetherSSIDPreferenceController(Context context, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener) {
        super(context, onTetherConfigUpdateListener);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return FeatureFlagUtils.isEnabled(this.mContext, "settings_tether_all_in_one") ? "wifi_tether_network_name_2" : "wifi_tether_network_name";
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController
    public void updateDisplay() {
        SoftApConfiguration softApConfiguration = this.mWifiManager.getSoftApConfiguration();
        if (softApConfiguration != null) {
            this.mSSID = softApConfiguration.getSsid();
        } else {
            this.mSSID = DEFAULT_SSID;
        }
        ((ValidatedEditTextPreference) this.mPreference).setValidator(this);
        if (!this.mWifiManager.isWifiApEnabled() || softApConfiguration == null) {
            ((WifiTetherSsidPreference) this.mPreference).setButtonVisible(false);
        } else {
            final Intent hotspotConfiguratorIntentOrNull = WifiDppUtils.getHotspotConfiguratorIntentOrNull(this.mContext, this.mWifiManager, softApConfiguration);
            if (hotspotConfiguratorIntentOrNull == null) {
                Log.e("WifiTetherSsidPref", "Invalid security to share hotspot");
                ((WifiTetherSsidPreference) this.mPreference).setButtonVisible(false);
            } else {
                ((WifiTetherSsidPreference) this.mPreference).setButtonOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wifi.tether.WifiTetherSSIDPreferenceController$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        WifiTetherSSIDPreferenceController.this.lambda$updateDisplay$0(hotspotConfiguratorIntentOrNull, view);
                    }
                });
                ((WifiTetherSsidPreference) this.mPreference).setButtonVisible(true);
            }
        }
        updateSsidDisplay((EditTextPreference) this.mPreference);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateDisplay$0(Intent intent, View view) {
        shareHotspotNetwork(intent);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String str = (String) obj;
        if (!TextUtils.equals(this.mSSID, str)) {
            this.mMetricsFeatureProvider.action(this.mContext, 1736, new Pair[0]);
        }
        this.mSSID = str;
        updateSsidDisplay((EditTextPreference) preference);
        this.mListener.onTetherConfigUpdated(this);
        return true;
    }

    @Override // com.android.settings.widget.ValidatedEditTextPreference.Validator
    public boolean isTextValid(String str) {
        return this.mWifiDeviceNameTextValidator.isTextValid(str);
    }

    public String getSSID() {
        return this.mSSID;
    }

    private void updateSsidDisplay(EditTextPreference editTextPreference) {
        editTextPreference.setText(this.mSSID);
        editTextPreference.setSummary(this.mSSID);
    }

    private void shareHotspotNetwork(final Intent intent) {
        WifiDppUtils.showLockScreen(this.mContext, new Runnable() { // from class: com.android.settings.wifi.tether.WifiTetherSSIDPreferenceController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                WifiTetherSSIDPreferenceController.this.lambda$shareHotspotNetwork$1(intent);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$shareHotspotNetwork$1(Intent intent) {
        this.mMetricsFeatureProvider.action(0, 1712, 1595, null, Integer.MIN_VALUE);
        this.mContext.startActivity(intent);
    }

    boolean isQrCodeButtonAvailable() {
        return ((WifiTetherSsidPreference) this.mPreference).isQrCodeButtonAvailable();
    }
}
