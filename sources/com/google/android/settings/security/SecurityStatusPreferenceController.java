package com.google.android.settings.security;

import android.content.Context;
import android.content.IntentFilter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.window.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.widget.LayoutPreference;
import com.google.android.settings.security.SecurityContentManager;
/* loaded from: classes2.dex */
public class SecurityStatusPreferenceController extends BasePreferenceController {
    private SecurityContentManager mSecurityContentManager;

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

    public SecurityStatusPreferenceController(Context context, String str) {
        super(context, str);
        this.mSecurityContentManager = SecurityContentManager.getInstance(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        SecurityContentManager.OverallStatus overallStatus = this.mSecurityContentManager.getOverallStatus();
        if (overallStatus != null) {
            LayoutPreference layoutPreference = (LayoutPreference) preference;
            ((ImageView) layoutPreference.findViewById(R.id.status_image)).setImageResource(overallStatus.getStatusSecurityLevel().getImageResId());
            ((TextView) layoutPreference.findViewById(R.id.status_title)).setText(overallStatus.getTitle());
            ((TextView) layoutPreference.findViewById(R.id.status_summary)).setText(overallStatus.getSummary());
        }
    }
}
