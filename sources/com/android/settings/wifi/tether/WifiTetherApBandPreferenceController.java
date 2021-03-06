package com.android.settings.wifi.tether;

import android.content.Context;
import android.content.res.Resources;
import android.net.wifi.SoftApConfiguration;
import android.util.FeatureFlagUtils;
import android.util.Log;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.window.R;
import com.android.settings.wifi.tether.WifiTetherBasePreferenceController;
/* loaded from: classes.dex */
public class WifiTetherApBandPreferenceController extends WifiTetherBasePreferenceController {
    private String[] mBandEntries;
    private int mBandIndex;
    private String[] mBandSummaries;

    public WifiTetherApBandPreferenceController(Context context, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener) {
        super(context, onTetherConfigUpdateListener);
        updatePreferenceEntries();
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController
    public void updateDisplay() {
        SoftApConfiguration softApConfiguration = this.mWifiManager.getSoftApConfiguration();
        if (softApConfiguration == null) {
            this.mBandIndex = 1;
            Log.d("WifiTetherApBandPref", "Updating band index to BAND_2GHZ because no config");
        } else if (is5GhzBandSupported()) {
            this.mBandIndex = validateSelection(softApConfiguration.getBand());
            Log.d("WifiTetherApBandPref", "Updating band index to " + this.mBandIndex);
        } else {
            this.mWifiManager.setSoftApConfiguration(new SoftApConfiguration.Builder(softApConfiguration).setBand(1).build());
            this.mBandIndex = 1;
            Log.d("WifiTetherApBandPref", "5Ghz not supported, updating band index to 2GHz");
        }
        ListPreference listPreference = (ListPreference) this.mPreference;
        listPreference.setEntries(this.mBandSummaries);
        listPreference.setEntryValues(this.mBandEntries);
        if (!is5GhzBandSupported()) {
            listPreference.setEnabled(false);
            listPreference.setSummary(R.string.wifi_ap_choose_2G);
            return;
        }
        listPreference.setValue(Integer.toString(softApConfiguration.getBand()));
        listPreference.setSummary(getConfigSummary());
    }

    String getConfigSummary() {
        int i = this.mBandIndex;
        if (i == 1) {
            return this.mBandSummaries[0];
        }
        if (i != 2) {
            return this.mContext.getString(R.string.wifi_ap_prefer_5G);
        }
        return this.mBandSummaries[1];
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return FeatureFlagUtils.isEnabled(this.mContext, "settings_tether_all_in_one") ? "wifi_tether_network_ap_band_2" : "wifi_tether_network_ap_band";
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        this.mBandIndex = validateSelection(Integer.parseInt((String) obj));
        Log.d("WifiTetherApBandPref", "Band preference changed, updating band index to " + this.mBandIndex);
        preference.setSummary(getConfigSummary());
        this.mListener.onTetherConfigUpdated(this);
        return true;
    }

    private int validateSelection(int i) {
        return 2 == i ? !is5GhzBandSupported() ? 1 : 3 : i;
    }

    void updatePreferenceEntries() {
        Resources resources = this.mContext.getResources();
        this.mBandEntries = resources.getStringArray(R.array.wifi_ap_band);
        this.mBandSummaries = resources.getStringArray(R.array.wifi_ap_band_summary);
    }

    private boolean is5GhzBandSupported() {
        return this.mWifiManager.is5GHzBandSupported() && this.mWifiManager.getCountryCode() != null;
    }

    public int getBandIndex() {
        return this.mBandIndex;
    }
}
