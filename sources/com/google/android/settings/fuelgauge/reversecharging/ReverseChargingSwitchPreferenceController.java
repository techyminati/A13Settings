package com.google.android.settings.fuelgauge.reversecharging;

import android.content.Context;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Switch;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.window.R;
import com.android.settingslib.Utils;
import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
import com.android.settingslib.widget.TopIntroPreference;
import com.google.android.systemui.reversecharging.ReverseChargingMetrics;
/* loaded from: classes2.dex */
public class ReverseChargingSwitchPreferenceController extends ReverseChargingBasePreferenceController implements OnMainSwitchChangeListener {
    private static final String KEY_INTRO_PREFERENCE = "reverse_charging_summary";
    static final int NO_ERROR = -1;
    MainSwitchPreference mPreference;
    TopIntroPreference mTopIntroPreference;
    private static final String TAG = "RCSwitchPrefController";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);

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

    public ReverseChargingSwitchPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.google.android.settings.fuelgauge.reversecharging.ReverseChargingBasePreferenceController
    public void updateState() {
        updateState(this.mPreference);
    }

    @Override // com.google.android.settings.fuelgauge.reversecharging.ReverseChargingBasePreferenceController, com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return this.mReverseChargingManager.isReverseChargingOn();
    }

    int checkLaunchRequirements() {
        if (this.mReverseChargingManager.isOnWirelessCharge()) {
            return 101;
        }
        if (isLowBattery()) {
            return 100;
        }
        if (isPowerSaveMode()) {
            return 104;
        }
        if (this.mIsUsbPlugIn) {
            return 107;
        }
        if (isOverheat()) {
            return 109;
        }
        return NO_ERROR;
    }

    private void logLaunchFailEvent(int i) {
        Log.d(TAG, "checkLaunchRequirements() = " + i);
        int intProperty = ((BatteryManager) ((ReverseChargingBasePreferenceController) this).mContext.getSystemService(BatteryManager.class)).getIntProperty(4);
        if (i != 100) {
            ReverseChargingMetrics.logStopEvent(i, intProperty, 0L);
        } else if (this.mLevelChanged) {
            ReverseChargingMetrics.logStopEvent(i, intProperty, 0L);
        }
    }

    @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
    public void onSwitchChanged(Switch r2, boolean z) {
        this.mPreference.setChecked(z);
        if (z != this.mReverseChargingManager.isReverseChargingOn()) {
            if (DEBUG) {
                Log.d(TAG, "isChecked : " + z);
            }
            this.mReverseChargingManager.setReverseChargingState(z);
        }
    }

    boolean isLowBattery() {
        int thresholdLevel = getThresholdLevel();
        int i = this.mLevel;
        return thresholdLevel >= i || i < 10;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        MainSwitchPreference mainSwitchPreference = (MainSwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = mainSwitchPreference;
        mainSwitchPreference.addOnSwitchChangeListener(this);
        this.mTopIntroPreference = (TopIntroPreference) preferenceScreen.findPreference(KEY_INTRO_PREFERENCE);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        int thresholdLevel = getThresholdLevel();
        String string = ((ReverseChargingBasePreferenceController) this).mContext.getString(R.string.reverse_charging_switch_title);
        boolean z = true;
        if (isOverheat()) {
            string = ((ReverseChargingBasePreferenceController) this).mContext.getString(R.string.reverse_charging_detail_charging_overheat_message);
        } else if (isPowerSaveMode()) {
            string = ((ReverseChargingBasePreferenceController) this).mContext.getString(R.string.reverse_charging_detail_power_save_mode_is_on_message);
        } else if (this.mReverseChargingManager.isOnWirelessCharge()) {
            string = ((ReverseChargingBasePreferenceController) this).mContext.getString(R.string.reverse_charging_detail_charging_wirelessly_message);
        } else if (this.mIsUsbPlugIn) {
            string = ((ReverseChargingBasePreferenceController) this).mContext.getString(R.string.reverse_charging_detail_unplug_usb_cable_message);
        } else {
            int i = this.mLevel;
            if (i != NO_ERROR && i < 10) {
                string = ((ReverseChargingBasePreferenceController) this).mContext.getString(R.string.reverse_charging_warning_title, Utils.formatPercentage(10));
                Log.d(TAG, "updateState() phone is low battery ! level : " + this.mLevel);
            } else if (i != NO_ERROR && thresholdLevel >= i) {
                string = ((ReverseChargingBasePreferenceController) this).mContext.getString(R.string.reverse_charging_warning_title, Utils.formatPercentage(thresholdLevel));
                Log.d(TAG, "updateState() phone is low battery ! level : " + this.mLevel + ", thresholdLevel : " + thresholdLevel);
            }
        }
        if (!TextUtils.isEmpty(string)) {
            this.mTopIntroPreference.setTitle(string);
        }
        int checkLaunchRequirements = checkLaunchRequirements();
        if (checkLaunchRequirements != NO_ERROR) {
            logLaunchFailEvent(checkLaunchRequirements);
        }
        if (checkLaunchRequirements != NO_ERROR) {
            z = false;
        }
        preference.setEnabled(z);
    }
}
