package com.android.settings.wifi.tether;

import android.content.Context;
import android.net.TetheringManager;
import android.net.wifi.WifiManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes.dex */
public abstract class WifiTetherBasePreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {
    protected final OnTetherConfigUpdateListener mListener;
    protected Preference mPreference;
    protected final TetheringManager mTm;
    protected final WifiManager mWifiManager;
    protected final String[] mWifiRegexs;

    /* loaded from: classes.dex */
    public interface OnTetherConfigUpdateListener {
        void onTetherConfigUpdated(AbstractPreferenceController abstractPreferenceController);
    }

    public abstract void updateDisplay();

    public WifiTetherBasePreferenceController(Context context, OnTetherConfigUpdateListener onTetherConfigUpdateListener) {
        super(context);
        this.mListener = onTetherConfigUpdateListener;
        this.mWifiManager = (WifiManager) context.getSystemService(WifiManager.class);
        TetheringManager tetheringManager = (TetheringManager) context.getSystemService(TetheringManager.class);
        this.mTm = tetheringManager;
        this.mWifiRegexs = tetheringManager.getTetherableWifiRegexs();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        String[] strArr;
        return (this.mWifiManager == null || (strArr = this.mWifiRegexs) == null || strArr.length <= 0) ? false : true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
        updateDisplay();
    }
}
