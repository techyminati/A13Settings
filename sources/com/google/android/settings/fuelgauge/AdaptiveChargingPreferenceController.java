package com.google.android.settings.fuelgauge;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.Preference;
import androidx.window.R;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.google.android.systemui.adaptivecharging.AdaptiveChargingManager;
/* loaded from: classes2.dex */
public class AdaptiveChargingPreferenceController extends TogglePreferenceController {
    @VisibleForTesting
    AdaptiveChargingManager mAdaptiveChargingManager;
    private boolean mChecked;

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_battery;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AdaptiveChargingPreferenceController(Context context, String str) {
        super(context, str);
        this.mAdaptiveChargingManager = new AdaptiveChargingManager(context);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mAdaptiveChargingManager.isAvailable() ? 0 : 3;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return this.mAdaptiveChargingManager.isEnabled();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        this.mChecked = isChecked();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        this.mAdaptiveChargingManager.setEnabled(z);
        if (!z) {
            this.mAdaptiveChargingManager.setAdaptiveChargingDeadline(-1);
        }
        if (this.mChecked == z) {
            return true;
        }
        this.mChecked = z;
        FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider().action(this.mContext, 1781, z);
        return true;
    }
}
