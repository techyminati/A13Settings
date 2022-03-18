package com.android.settings.wifi.tether;

import android.content.Context;
import android.net.wifi.SoftApCapability;
import android.net.wifi.WifiManager;
import android.util.FeatureFlagUtils;
import android.util.Log;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.window.R;
import com.android.settings.wifi.tether.WifiTetherBasePreferenceController;
import java.util.LinkedHashMap;
import java.util.Map;
/* loaded from: classes.dex */
public class WifiTetherSecurityPreferenceController extends WifiTetherBasePreferenceController implements WifiManager.SoftApCallback {
    private int mSecurityValue;
    private Map<Integer, String> mSecurityMap = new LinkedHashMap();
    boolean mIsWpa3Supported = true;

    public WifiTetherSecurityPreferenceController(Context context, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener) {
        super(context, onTetherConfigUpdateListener);
        String[] stringArray = this.mContext.getResources().getStringArray(R.array.wifi_tether_security);
        String[] stringArray2 = this.mContext.getResources().getStringArray(R.array.wifi_tether_security_values);
        for (int i = 0; i < stringArray.length; i++) {
            this.mSecurityMap.put(Integer.valueOf(Integer.parseInt(stringArray2[i])), stringArray[i]);
        }
        this.mWifiManager.registerSoftApCallback(context.getMainExecutor(), this);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return FeatureFlagUtils.isEnabled(this.mContext, "settings_tether_all_in_one") ? "wifi_tether_security_2" : "wifi_tether_security";
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController
    public void updateDisplay() {
        Preference preference = this.mPreference;
        if (preference != null) {
            ListPreference listPreference = (ListPreference) preference;
            if (!this.mIsWpa3Supported && this.mSecurityMap.keySet().removeIf(WifiTetherSecurityPreferenceController$$ExternalSyntheticLambda3.INSTANCE)) {
                listPreference.setEntries((CharSequence[]) this.mSecurityMap.values().stream().toArray(WifiTetherSecurityPreferenceController$$ExternalSyntheticLambda1.INSTANCE));
                listPreference.setEntryValues((CharSequence[]) this.mSecurityMap.keySet().stream().map(WifiTetherSecurityPreferenceController$$ExternalSyntheticLambda0.INSTANCE).toArray(WifiTetherSecurityPreferenceController$$ExternalSyntheticLambda2.INSTANCE));
            }
            int securityType = this.mWifiManager.getSoftApConfiguration().getSecurityType();
            if (this.mSecurityMap.get(Integer.valueOf(securityType)) == null) {
                securityType = 1;
            }
            this.mSecurityValue = securityType;
            listPreference.setSummary(this.mSecurityMap.get(Integer.valueOf(securityType)));
            listPreference.setValue(String.valueOf(this.mSecurityValue));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateDisplay$0(Integer num) {
        return num.intValue() > 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ CharSequence[] lambda$updateDisplay$1(int i) {
        return new CharSequence[i];
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String lambda$updateDisplay$2(Integer num) {
        return Integer.toString(num.intValue());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ CharSequence[] lambda$updateDisplay$3(int i) {
        return new CharSequence[i];
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        int parseInt = Integer.parseInt((String) obj);
        this.mSecurityValue = parseInt;
        preference.setSummary(this.mSecurityMap.get(Integer.valueOf(parseInt)));
        WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener = this.mListener;
        if (onTetherConfigUpdateListener == null) {
            return true;
        }
        onTetherConfigUpdateListener.onTetherConfigUpdated(this);
        return true;
    }

    public void onCapabilityChanged(SoftApCapability softApCapability) {
        boolean areFeaturesSupported = softApCapability.areFeaturesSupported(4L);
        if (!areFeaturesSupported) {
            Log.i("wifi_tether_security", "WPA3 SAE is not supported on this device");
        }
        if (this.mIsWpa3Supported != areFeaturesSupported) {
            this.mIsWpa3Supported = areFeaturesSupported;
            updateDisplay();
        }
        this.mWifiManager.unregisterSoftApCallback(this);
    }

    public int getSecurityType() {
        return this.mSecurityValue;
    }
}
