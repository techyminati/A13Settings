package com.google.android.settings.aware;

import android.content.Context;
import android.content.IntentFilter;
import androidx.window.R;
/* loaded from: classes2.dex */
public class SilenceGestureFooterPreferenceController extends AwareFooterPreferenceController {
    @Override // com.google.android.settings.aware.AwareFooterPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.google.android.settings.aware.AwareFooterPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.google.android.settings.aware.AwareFooterPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.google.android.settings.aware.AwareFooterPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.google.android.settings.aware.AwareFooterPreferenceController
    public int getText() {
        return R.string.gesture_aware_footer;
    }

    @Override // com.google.android.settings.aware.AwareFooterPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.google.android.settings.aware.AwareFooterPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.google.android.settings.aware.AwareFooterPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.google.android.settings.aware.AwareFooterPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.google.android.settings.aware.AwareFooterPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    @Override // com.google.android.settings.aware.AwareFooterPreferenceController, com.android.settings.core.BasePreferenceController
    public /* bridge */ /* synthetic */ int getAvailabilityStatus() {
        return super.getAvailabilityStatus();
    }

    @Override // com.google.android.settings.aware.AwareFooterPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public /* bridge */ /* synthetic */ CharSequence getSummary() {
        return super.getSummary();
    }

    public SilenceGestureFooterPreferenceController(Context context, String str) {
        super(context, str);
    }
}
