package com.google.android.settings.fuelgauge.reversecharging;

import android.content.Context;
import android.content.IntentFilter;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.core.SliderPreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.Utils;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.google.android.systemui.reversecharging.ReverseChargingMetrics;
/* loaded from: classes2.dex */
public class ReverseChargingSeekBarController extends SliderPreferenceController implements LifecycleObserver, OnStop {
    public static final int BASE_LEVEL_TIMES = 5;
    public static final int MAX_SEEKBAR_VALUE = 10;
    public static final int MIN_SEEKBAR_VALUE = 2;
    @VisibleForTesting
    boolean mIsPreferenceChanged;
    @VisibleForTesting
    boolean mIsSliderPositionChanged = false;
    @VisibleForTesting
    ReverseChargingSeekBarPreference mPreference;
    @VisibleForTesting
    ReverseChargingManager mReverseChargingManager;

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.SliderPreferenceController
    public int getMax() {
        return 10;
    }

    @Override // com.android.settings.core.SliderPreferenceController
    public int getMin() {
        return 2;
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public ReverseChargingSeekBarController(Context context, String str) {
        super(context, str);
        this.mReverseChargingManager = ReverseChargingManager.getInstance(context);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        ReverseChargingSeekBarPreference reverseChargingSeekBarPreference = (ReverseChargingSeekBarPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = reverseChargingSeekBarPreference;
        reverseChargingSeekBarPreference.setContinuousUpdates(true);
        this.mPreference.setMax(getMax());
        this.mPreference.setMin(getMin());
        this.mPreference.setHapticFeedbackMode(1);
        this.mPreference.overrideSeekBarStateDescription(Utils.formatPercentage(getSliderPosition() * 5));
    }

    @Override // com.android.settings.core.SliderPreferenceController
    public int getSliderPosition() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "advanced_battery_usage_amount", 2);
    }

    @Override // com.android.settings.core.SliderPreferenceController
    public boolean setSliderPosition(int i) {
        this.mIsSliderPositionChanged = true;
        Settings.Global.putInt(this.mContext.getContentResolver(), "advanced_battery_usage_amount", i);
        return true;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return ReverseChargingUtils.getAvailability(this.mContext, this.mReverseChargingManager);
    }

    @Override // com.android.settings.core.SliderPreferenceController, androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        Integer num = (Integer) obj;
        int intValue = num.intValue() * 5;
        this.mPreference.setPercentageValue(Utils.formatPercentage(intValue));
        this.mPreference.overrideSeekBarStateDescription(Utils.formatPercentage(intValue));
        setSliderPosition(num.intValue());
        this.mIsPreferenceChanged = true;
        return true;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        if (this.mIsPreferenceChanged) {
            ReverseChargingMetrics.logLowBatteryThresholdChange(getSliderPosition() * 5);
            this.mIsPreferenceChanged = false;
        }
        if (this.mIsSliderPositionChanged) {
            this.mIsSliderPositionChanged = false;
            FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider().action(this.mContext, 1783, getSliderPosition());
        }
    }
}
