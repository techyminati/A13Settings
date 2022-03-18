package com.android.settings.accessibility;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.PreferenceScreen;
import androidx.window.R;
/* loaded from: classes.dex */
public class AccessibilityButtonFooterPreferenceController extends AccessibilityFooterPreferenceController {
    @Override // com.android.settings.accessibility.AccessibilityFooterPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.accessibility.AccessibilityFooterPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
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

    public AccessibilityButtonFooterPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.accessibility.AccessibilityFooterPreferenceController
    protected String getLearnMoreContentDescription() {
        return this.mContext.getString(R.string.accessibility_button_gesture_footer_learn_more_content_description);
    }

    @Override // com.android.settings.accessibility.AccessibilityFooterPreferenceController
    protected String getIntroductionTitle() {
        return this.mContext.getString(R.string.accessibility_button_about_title);
    }

    @Override // com.android.settings.accessibility.AccessibilityFooterPreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        ((AccessibilityFooterPreference) preferenceScreen.findPreference(getPreferenceKey())).setTitle(this.mContext.getText(AccessibilityUtil.isGestureNavigateEnabled(this.mContext) ? R.string.accessibility_button_gesture_description : R.string.accessibility_button_description));
        super.displayPreference(preferenceScreen);
    }
}
