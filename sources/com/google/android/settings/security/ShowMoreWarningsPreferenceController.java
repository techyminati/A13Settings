package com.google.android.settings.security;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.Preference;
import androidx.window.R;
import com.android.settings.core.BasePreferenceController;
/* loaded from: classes2.dex */
public class ShowMoreWarningsPreferenceController extends BasePreferenceController {
    static final String KEY_SECURITY_SHOW_MORE_WARNINGS = "security_show_more_warnings";
    private SecurityContentManager mSecurityContentManager;

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

    public ShowMoreWarningsPreferenceController(Context context) {
        super(context, KEY_SECURITY_SHOW_MORE_WARNINGS);
        this.mSecurityContentManager = SecurityContentManager.getInstance(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ShowMoreWarningsPreference showMoreWarningsPreference = (ShowMoreWarningsPreference) preference;
        int securityWarningCount = this.mSecurityContentManager.getSecurityWarningCount();
        if (securityWarningCount > 1) {
            showMoreWarningsPreference.setTitle(this.mContext.getResources().getQuantityString(R.plurals.security_settings_hub_show_warnings_preference, securityWarningCount, Integer.valueOf(securityWarningCount)));
            showMoreWarningsPreference.setCardBackgroundColor(this.mContext.getColor(this.mSecurityContentManager.getPrimarySecurityWarning().getSecurityLevel().getAttentionLevel().getBackgroundColorResId()));
            showMoreWarningsPreference.setVisible(true);
            return;
        }
        showMoreWarningsPreference.setVisible(false);
    }
}
