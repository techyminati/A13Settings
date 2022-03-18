package com.android.settings.security;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.CrossProfileApps;
import androidx.window.R;
import com.android.settings.core.BasePreferenceController;
/* loaded from: classes.dex */
public class SecurityAdvancedSettingsController extends BasePreferenceController {
    private final CrossProfileApps mCrossProfileApps;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
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

    public SecurityAdvancedSettingsController(Context context, String str) {
        super(context, str);
        this.mCrossProfileApps = (CrossProfileApps) context.getSystemService(CrossProfileApps.class);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        if (isWorkProfilePresent()) {
            return this.mContext.getResources().getString(R.string.security_advanced_settings_work_profile_settings_summary);
        }
        return this.mContext.getResources().getString(R.string.security_advanced_settings_no_work_profile_settings_summary);
    }

    private boolean isWorkProfilePresent() {
        return !this.mCrossProfileApps.getTargetUserProfiles().isEmpty();
    }
}
