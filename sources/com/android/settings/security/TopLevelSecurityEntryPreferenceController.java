package com.android.settings.security;

import android.content.Context;
import android.content.IntentFilter;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.safetycenter.SafetyCenterStatusHolder;
/* loaded from: classes.dex */
public class TopLevelSecurityEntryPreferenceController extends BasePreferenceController {
    private final SecuritySettingsFeatureProvider mSecuritySettingsFeatureProvider = FeatureFactory.getFactory(this.mContext).getSecuritySettingsFeatureProvider();

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public TopLevelSecurityEntryPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return !SafetyCenterStatusHolder.get().isEnabled(this.mContext) ? 0 : 2;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        String alternativeSecuritySettingsFragmentClassname;
        if (!TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            return super.handlePreferenceTreeClick(preference);
        }
        if (!this.mSecuritySettingsFeatureProvider.hasAlternativeSecuritySettingsFragment() || (alternativeSecuritySettingsFragmentClassname = this.mSecuritySettingsFeatureProvider.getAlternativeSecuritySettingsFragmentClassname()) == null) {
            return super.handlePreferenceTreeClick(preference);
        }
        new SubSettingLauncher(this.mContext).setDestination(alternativeSecuritySettingsFragmentClassname).setSourceMetricsCategory(getMetricsCategory()).launch();
        return true;
    }
}
