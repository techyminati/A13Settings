package com.google.android.settings.aware;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.aware.AwareFeatureProvider;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.widget.PreferenceCategoryController;
/* loaded from: classes2.dex */
public class AwarePreferenceCategoryController extends PreferenceCategoryController {
    private final AwareFeatureProvider mFeatureProvider;

    @Override // com.android.settings.widget.PreferenceCategoryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.widget.PreferenceCategoryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.widget.PreferenceCategoryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.widget.PreferenceCategoryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.widget.PreferenceCategoryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.widget.PreferenceCategoryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.widget.PreferenceCategoryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.widget.PreferenceCategoryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.widget.PreferenceCategoryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AwarePreferenceCategoryController(Context context, String str) {
        super(context, str);
        this.mFeatureProvider = FeatureFactory.getFactory(context).getAwareFeatureProvider();
    }

    @Override // com.android.settings.widget.PreferenceCategoryController, com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mFeatureProvider.isSupported(this.mContext) ? 0 : 3;
    }
}
