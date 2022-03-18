package com.google.android.settings.aware;

import android.content.Context;
import android.content.IntentFilter;
/* loaded from: classes2.dex */
public class AwareSettingsPreferenceController extends AwareGesturePreferenceController {
    @Override // com.google.android.settings.aware.AwareGesturePreferenceController, com.google.android.settings.aware.AwareBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.google.android.settings.aware.AwareGesturePreferenceController, com.google.android.settings.aware.AwareBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.google.android.settings.aware.AwareGesturePreferenceController
    protected CharSequence getGestureSummary() {
        return null;
    }

    @Override // com.google.android.settings.aware.AwareGesturePreferenceController, com.google.android.settings.aware.AwareBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.google.android.settings.aware.AwareGesturePreferenceController, com.google.android.settings.aware.AwareBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.google.android.settings.aware.AwareGesturePreferenceController, com.google.android.settings.aware.AwareBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.google.android.settings.aware.AwareGesturePreferenceController, com.google.android.settings.aware.AwareBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.google.android.settings.aware.AwareGesturePreferenceController, com.google.android.settings.aware.AwareBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.google.android.settings.aware.AwareGesturePreferenceController, com.google.android.settings.aware.AwareBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.google.android.settings.aware.AwareGesturePreferenceController, com.google.android.settings.aware.AwareBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AwareSettingsPreferenceController(Context context, String str) {
        super(context, str);
    }
}
