package com.android.settings.accessibility;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.accessibility.AccessibilityManager;
import androidx.preference.PreferenceScreen;
import androidx.window.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.widget.IllustrationPreference;
/* loaded from: classes.dex */
public class AccessibilityButtonPreviewPreferenceController extends BasePreferenceController implements LifecycleObserver, OnResume, OnPause {
    private static final float DEFAULT_OPACITY = 0.55f;
    private static final int DEFAULT_SIZE = 0;
    private static final int SMALL_SIZE = 0;
    private AccessibilityLayerDrawable mAccessibilityPreviewDrawable;
    private final ContentResolver mContentResolver;
    IllustrationPreference mIllustrationPreference;
    final ContentObserver mContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.android.settings.accessibility.AccessibilityButtonPreviewPreferenceController.1
        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            AccessibilityButtonPreviewPreferenceController.this.updatePreviewPreference();
        }
    };
    private AccessibilityManager.TouchExplorationStateChangeListener mTouchExplorationStateChangeListener = new AccessibilityManager.TouchExplorationStateChangeListener() { // from class: com.android.settings.accessibility.AccessibilityButtonPreviewPreferenceController$$ExternalSyntheticLambda0
        @Override // android.view.accessibility.AccessibilityManager.TouchExplorationStateChangeListener
        public final void onTouchExplorationStateChanged(boolean z) {
            AccessibilityButtonPreviewPreferenceController.this.lambda$new$0(z);
        }
    };

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

    public AccessibilityButtonPreviewPreferenceController(Context context, String str) {
        super(context, str);
        this.mContentResolver = context.getContentResolver();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(boolean z) {
        updatePreviewPreference();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mIllustrationPreference = (IllustrationPreference) preferenceScreen.findPreference(getPreferenceKey());
        updatePreviewPreference();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        ((AccessibilityManager) this.mContext.getSystemService(AccessibilityManager.class)).addTouchExplorationStateChangeListener(this.mTouchExplorationStateChangeListener);
        this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor("accessibility_button_mode"), false, this.mContentObserver);
        this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor("accessibility_floating_menu_size"), false, this.mContentObserver);
        this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor("accessibility_floating_menu_opacity"), false, this.mContentObserver);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        ((AccessibilityManager) this.mContext.getSystemService(AccessibilityManager.class)).removeTouchExplorationStateChangeListener(this.mTouchExplorationStateChangeListener);
        this.mContentResolver.unregisterContentObserver(this.mContentObserver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePreviewPreference() {
        if (AccessibilityUtil.isFloatingMenuEnabled(this.mContext)) {
            int i = Settings.Secure.getInt(this.mContentResolver, "accessibility_floating_menu_size", 0);
            this.mIllustrationPreference.setImageDrawable(getAccessibilityPreviewDrawable(i == 0 ? R.drawable.accessibility_button_preview_small_floating_menu : R.drawable.accessibility_button_preview_large_floating_menu, (int) (Settings.Secure.getFloat(this.mContentResolver, "accessibility_floating_menu_opacity", DEFAULT_OPACITY) * 100.0f)));
        } else if (AccessibilityUtil.isGestureNavigateEnabled(this.mContext)) {
            IllustrationPreference illustrationPreference = this.mIllustrationPreference;
            Context context = this.mContext;
            illustrationPreference.setImageDrawable(context.getDrawable(AccessibilityUtil.isTouchExploreEnabled(context) ? R.drawable.accessibility_button_preview_three_finger : R.drawable.accessibility_button_preview_two_finger));
        } else {
            this.mIllustrationPreference.setImageDrawable(this.mContext.getDrawable(R.drawable.accessibility_button_navigation));
        }
    }

    private Drawable getAccessibilityPreviewDrawable(int i, int i2) {
        AccessibilityLayerDrawable accessibilityLayerDrawable = this.mAccessibilityPreviewDrawable;
        if (accessibilityLayerDrawable == null) {
            this.mAccessibilityPreviewDrawable = AccessibilityLayerDrawable.createLayerDrawable(this.mContext, i, i2);
        } else {
            accessibilityLayerDrawable.updateLayerDrawable(this.mContext, i, i2);
            this.mAccessibilityPreviewDrawable.invalidateSelf();
        }
        return this.mAccessibilityPreviewDrawable;
    }
}
