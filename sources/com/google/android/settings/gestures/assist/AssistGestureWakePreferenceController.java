package com.google.android.settings.gestures.assist;

import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.gestures.AssistGestureFeatureProvider;
import com.android.settings.gestures.GesturePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
/* loaded from: classes2.dex */
public class AssistGestureWakePreferenceController extends GesturePreferenceController implements OnPause, OnResume {
    private static final String PREF_KEY_VIDEO = "gesture_assist_video";
    private final AssistGestureFeatureProvider mFeatureProvider;
    private SwitchPreference mPreference;
    private PreferenceScreen mScreen;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private final SettingObserver mSettingObserver = new SettingObserver();

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

    public AssistGestureWakePreferenceController(Context context, String str) {
        super(context, str);
        this.mFeatureProvider = FeatureFactory.getFactory(context).getAssistGestureFeatureProvider();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mFeatureProvider.isSensorAvailable(this.mContext) ? 0 : 3;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public boolean isSliceable() {
        return TextUtils.equals(getPreferenceKey(), "gesture_assist_wake");
    }

    @Override // com.android.settings.gestures.GesturePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mScreen = preferenceScreen;
        this.mPreference = (SwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
        if (!this.mFeatureProvider.isSupported(this.mContext)) {
            this.mScreen.removePreference(this.mPreference);
        } else {
            super.displayPreference(preferenceScreen);
        }
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        return Settings.Secure.putInt(this.mContext.getContentResolver(), "assist_gesture_wake_enabled", z ? 1 : 0);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), "assist_gesture_wake_enabled", 1) != 0;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        this.mSettingObserver.unregister();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        this.mSettingObserver.register();
        updatePreference();
    }

    @Override // com.android.settings.gestures.GesturePreferenceController
    protected boolean canHandleClicks() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), "assist_gesture_enabled", 1) != 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePreference() {
        if (this.mPreference != null) {
            if (this.mFeatureProvider.isSupported(this.mContext)) {
                if (this.mScreen.findPreference(getPreferenceKey()) == null) {
                    this.mScreen.addPreference(this.mPreference);
                }
                this.mPreference.setEnabled(canHandleClicks());
                return;
            }
            this.mScreen.removePreference(this.mPreference);
        }
    }

    /* loaded from: classes2.dex */
    class SettingObserver extends ContentObserver {
        private final Uri ASSIST_GESTURE_ENABLED_URI = Settings.Secure.getUriFor("assist_gesture_enabled");

        public SettingObserver() {
            super(AssistGestureWakePreferenceController.this.mHandler);
        }

        public void register() {
            ((AbstractPreferenceController) AssistGestureWakePreferenceController.this).mContext.getContentResolver().registerContentObserver(this.ASSIST_GESTURE_ENABLED_URI, false, this);
        }

        public void unregister() {
            ((AbstractPreferenceController) AssistGestureWakePreferenceController.this).mContext.getContentResolver().unregisterContentObserver(this);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            AssistGestureWakePreferenceController.this.updatePreference();
        }
    }
}
