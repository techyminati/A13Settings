package com.google.android.settings.security;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.Preference;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.core.BasePreferenceController;
/* loaded from: classes2.dex */
public class PrimarySecurityWarningPreferenceController extends BasePreferenceController {
    private SettingsPreferenceFragment mHost;
    private final SecurityContentManager mSecurityContentManager;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 1;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getMetricsCategory() {
        return 1884;
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

    public PrimarySecurityWarningPreferenceController(Context context, String str) {
        super(context, str);
        this.mSecurityContentManager = SecurityContentManager.getInstance(context);
    }

    public void init(SettingsPreferenceFragment settingsPreferenceFragment) {
        this.mHost = settingsPreferenceFragment;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        SecurityWarningPreference securityWarningPreference = (SecurityWarningPreference) preference;
        SecurityWarning primarySecurityWarning = this.mSecurityContentManager.getPrimarySecurityWarning();
        if (primarySecurityWarning != null) {
            securityWarningPreference.setSecurityWarning(primarySecurityWarning, this.mHost);
            securityWarningPreference.setVisible(true);
            return;
        }
        securityWarningPreference.setVisible(false);
    }
}
