package com.android.settings.accessibility;

import android.content.Context;
import android.content.IntentFilter;
import androidx.window.R;
/* loaded from: classes.dex */
public class AccessibilityControlTimeoutFooterPreferenceController extends AccessibilityFooterPreferenceController {
    @Override // com.android.settings.accessibility.AccessibilityFooterPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.accessibility.AccessibilityFooterPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.accessibility.AccessibilityFooterPreferenceController
    protected int getHelpResource() {
        return R.string.help_url_timeout;
    }

    @Override // com.android.settings.accessibility.AccessibilityFooterPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.accessibility.AccessibilityFooterPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.accessibility.AccessibilityFooterPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.accessibility.AccessibilityFooterPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.accessibility.AccessibilityFooterPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.accessibility.AccessibilityFooterPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.accessibility.AccessibilityFooterPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AccessibilityControlTimeoutFooterPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.accessibility.AccessibilityFooterPreferenceController
    protected String getLearnMoreContentDescription() {
        return this.mContext.getString(R.string.accessibility_control_timeout_footer_learn_more_content_description);
    }

    @Override // com.android.settings.accessibility.AccessibilityFooterPreferenceController
    protected String getIntroductionTitle() {
        return this.mContext.getString(R.string.accessibility_control_timeout_about_title);
    }
}
