package com.android.settings.applications.appinfo;

import android.content.Context;
import android.content.IntentFilter;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.overlay.FeatureFactory;
/* loaded from: classes.dex */
public class ExtraAppInfoPreferenceController extends BasePreferenceController {
    private final ExtraAppInfoFeatureProvider mExtraAppInfoFeatureProvider;

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

    public ExtraAppInfoPreferenceController(Context context, String str) {
        super(context, str);
        this.mExtraAppInfoFeatureProvider = FeatureFactory.getFactory(context).getExtraAppInfoFeatureProvider();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mExtraAppInfoFeatureProvider.isSupported(this.mContext) ? 0 : 3;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(getPreferenceKey(), preference.getKey())) {
            return super.handlePreferenceTreeClick(preference);
        }
        this.mExtraAppInfoFeatureProvider.launchExtraAppInfoSettings(this.mContext);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return this.mExtraAppInfoFeatureProvider.getSummary(this.mContext);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (this.mExtraAppInfoFeatureProvider != null) {
            Preference findPreference = preferenceScreen.findPreference(getPreferenceKey());
            findPreference.setEnabled(this.mExtraAppInfoFeatureProvider.isEnabled(findPreference.getContext()));
        }
    }

    public void setPackageName(String str) {
        ExtraAppInfoFeatureProvider extraAppInfoFeatureProvider = this.mExtraAppInfoFeatureProvider;
        if (extraAppInfoFeatureProvider != null) {
            extraAppInfoFeatureProvider.setPackageName(str);
        }
    }
}
