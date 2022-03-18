package com.google.android.settings.fuelgauge.reversecharging;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.window.R;
import com.android.settingslib.Utils;
/* loaded from: classes2.dex */
public class ReverseChargingPreferenceController extends ReverseChargingBasePreferenceController {
    Preference mPreference;

    @Override // com.google.android.settings.fuelgauge.reversecharging.ReverseChargingBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.google.android.settings.fuelgauge.reversecharging.ReverseChargingBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.google.android.settings.fuelgauge.reversecharging.ReverseChargingBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.google.android.settings.fuelgauge.reversecharging.ReverseChargingBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.google.android.settings.fuelgauge.reversecharging.ReverseChargingBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.google.android.settings.fuelgauge.reversecharging.ReverseChargingBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public ReverseChargingPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.google.android.settings.fuelgauge.reversecharging.ReverseChargingBasePreferenceController
    public void updateState() {
        updateState(this.mPreference);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        int thresholdLevel = getThresholdLevel();
        boolean isReverseChargingOn = this.mReverseChargingManager.isReverseChargingOn();
        if (isOverheat()) {
            preference.setSummary(((ReverseChargingBasePreferenceController) this).mContext.getString(R.string.reverse_charging_overheat_summary));
        } else if (isPowerSaveMode()) {
            preference.setSummary(((ReverseChargingBasePreferenceController) this).mContext.getString(R.string.reverse_charging_power_save_mode_is_on_message));
        } else if (this.mReverseChargingManager.isOnWirelessCharge()) {
            preference.setSummary(((ReverseChargingBasePreferenceController) this).mContext.getString(R.string.reverse_charging_charging_wirelessly_message));
        } else if (this.mIsUsbPlugIn) {
            preference.setSummary(((ReverseChargingBasePreferenceController) this).mContext.getString(R.string.reverse_charging_unplug_usb_cable_message));
        } else {
            int i = this.mLevel;
            if (i < 10) {
                preference.setSummary(((ReverseChargingBasePreferenceController) this).mContext.getString(R.string.reverse_charging_warning_summary, Utils.formatPercentage(10)));
            } else if (thresholdLevel >= i) {
                preference.setSummary(((ReverseChargingBasePreferenceController) this).mContext.getString(R.string.reverse_charging_sharing_level_message));
            } else {
                preference.setSummary(((ReverseChargingBasePreferenceController) this).mContext.getString(isReverseChargingOn ? R.string.reverse_charging_is_on_summary : R.string.reverse_charging_is_off_summary));
            }
        }
    }
}
