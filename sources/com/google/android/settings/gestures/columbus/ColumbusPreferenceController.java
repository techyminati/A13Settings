package com.google.android.settings.gestures.columbus;

import android.app.ActivityManager;
import android.content.Context;
import android.content.IntentFilter;
import android.provider.Settings;
import androidx.window.R;
import com.android.settings.core.BasePreferenceController;
/* loaded from: classes2.dex */
public class ColumbusPreferenceController extends BasePreferenceController {
    static final String FEATURE_QUICK_TAP = "com.google.android.feature.QUICK_TAP";

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

    public ColumbusPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return isColumbusSupported(this.mContext) ? 0 : 3;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        if (!isColumbusEnabled(this.mContext)) {
            return this.mContext.getText(R.string.gesture_setting_off);
        }
        return this.mContext.getString(R.string.columbus_summary, this.mContext.getText(R.string.gesture_setting_on), ColumbusActionsPreferenceController.getColumbusAction(this.mContext));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isColumbusSupported(Context context) {
        return context.getPackageManager().hasSystemFeature(FEATURE_QUICK_TAP);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isColumbusEnabled(Context context) {
        return Settings.Secure.getIntForUser(context.getContentResolver(), "columbus_enabled", 0, ActivityManager.getCurrentUser()) != 0;
    }
}
