package com.google.android.settings.aware;

import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.aware.AwareFeatureProvider;
import com.android.settings.gestures.GesturePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.google.android.settings.aware.AwareHelper;
/* loaded from: classes2.dex */
public class TapGesturePreferenceController extends GesturePreferenceController implements AwareHelper.Callback {
    private static final int OFF = 0;
    private static final int ON = 1;
    private static final String PREF_KEY_VIDEO = "gesture_tap_video";
    private final AwareFeatureProvider mFeatureProvider;
    private AwareHelper mHelper;
    private Preference mPreference;

    @Override // com.android.settings.gestures.GesturePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.gestures.GesturePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.gestures.GesturePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.gestures.GesturePreferenceController
    protected String getVideoPrefKey() {
        return PREF_KEY_VIDEO;
    }

    @Override // com.android.settings.gestures.GesturePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.gestures.GesturePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public boolean isPublicSlice() {
        return true;
    }

    @Override // com.android.settings.gestures.GesturePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public TapGesturePreferenceController(Context context, String str) {
        super(context, str);
        this.mFeatureProvider = FeatureFactory.getFactory(context).getAwareFeatureProvider();
        this.mHelper = new AwareHelper(context);
    }

    @Override // com.android.settings.gestures.GesturePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (!this.mFeatureProvider.isSupported(this.mContext)) {
            return 3;
        }
        return !this.mHelper.isGestureConfigurable() ? 5 : 0;
    }

    @Override // com.android.settings.gestures.GesturePreferenceController
    protected boolean canHandleClicks() {
        return this.mHelper.isGestureConfigurable();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return this.mFeatureProvider.isEnabled(this.mContext) && Settings.Secure.getInt(this.mContext.getContentResolver(), "tap_gesture", 0) == 1;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        this.mHelper.writeFeatureEnabled("tap_gesture", z);
        return Settings.Secure.putInt(this.mContext.getContentResolver(), "tap_gesture", z ? 1 : 0);
    }

    @Override // com.android.settings.gestures.GesturePreferenceController, com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mHelper.register(this);
    }

    @Override // com.android.settings.gestures.GesturePreferenceController, com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mHelper.unregister();
    }

    @Override // com.google.android.settings.aware.AwareHelper.Callback
    public void onChange(Uri uri) {
        updateState(this.mPreference);
    }
}
