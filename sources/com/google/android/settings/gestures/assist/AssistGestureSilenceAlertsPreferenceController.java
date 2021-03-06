package com.google.android.settings.gestures.assist;

import android.content.Context;
import android.content.IntentFilter;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.window.R;
import com.android.settings.gestures.AssistGestureFeatureProvider;
import com.android.settings.gestures.GesturePreferenceController;
import com.android.settings.overlay.FeatureFactory;
/* loaded from: classes2.dex */
public class AssistGestureSilenceAlertsPreferenceController extends GesturePreferenceController {
    private static final String ASSIST_GESTURE_SILENCE_ALERTS_PREF_KEY = "gesture_assist_silence";
    private static final String PREF_KEY_VIDEO = "gesture_assist_video";
    private final AssistGestureFeatureProvider mFeatureProvider;

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

    public AssistGestureSilenceAlertsPreferenceController(Context context, String str) {
        super(context, str);
        this.mFeatureProvider = FeatureFactory.getFactory(context).getAssistGestureFeatureProvider();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mFeatureProvider.isSensorAvailable(this.mContext) ? 0 : 3;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public boolean isSliceable() {
        return TextUtils.equals(getPreferenceKey(), ASSIST_GESTURE_SILENCE_ALERTS_PREF_KEY);
    }

    @Override // com.android.settings.gestures.GesturePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        Preference findPreference;
        if (((AssistGestureFeatureProviderGoogleImpl) this.mFeatureProvider).isDeskClockSupported(this.mContext) && (findPreference = preferenceScreen.findPreference(ASSIST_GESTURE_SILENCE_ALERTS_PREF_KEY)) != null) {
            findPreference.setSummary(R.string.assist_gesture_setting_enable_ring_alarm_silence_text);
        }
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        return Settings.Secure.putInt(this.mContext.getContentResolver(), "assist_gesture_silence_alerts_enabled", z ? 1 : 0);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), "assist_gesture_silence_alerts_enabled", 1) != 0;
    }
}
