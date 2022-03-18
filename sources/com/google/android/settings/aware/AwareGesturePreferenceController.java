package com.google.android.settings.aware;

import android.content.Context;
import android.content.IntentFilter;
import android.os.SystemProperties;
import androidx.window.R;
/* loaded from: classes2.dex */
public abstract class AwareGesturePreferenceController extends AwareBasePreferenceController {
    @Override // com.google.android.settings.aware.AwareBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.google.android.settings.aware.AwareBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    protected abstract CharSequence getGestureSummary();

    @Override // com.google.android.settings.aware.AwareBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.google.android.settings.aware.AwareBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.google.android.settings.aware.AwareBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.google.android.settings.aware.AwareBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.google.android.settings.aware.AwareBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.google.android.settings.aware.AwareBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.google.android.settings.aware.AwareBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AwareGesturePreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.google.android.settings.aware.AwareBasePreferenceController, com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (!SystemProperties.getBoolean("ro.vendor.aware_available", false)) {
            return 3;
        }
        return (this.mHelper.isAirplaneModeOn() || this.mHelper.isBatterySaverModeOn()) ? 5 : 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        boolean isBatterySaverModeOn = this.mHelper.isBatterySaverModeOn();
        boolean isAirplaneModeOn = this.mHelper.isAirplaneModeOn();
        if (!isBatterySaverModeOn && !isAirplaneModeOn) {
            return getGestureSummary();
        }
        return this.mContext.getText((!isBatterySaverModeOn || !isAirplaneModeOn) ? isBatterySaverModeOn ? R.string.aware_summary_when_batterysaver_on : isAirplaneModeOn ? R.string.aware_summary_when_airplane_on : 0 : R.string.aware_summary_when_airplane_batterysaver_on);
    }
}
