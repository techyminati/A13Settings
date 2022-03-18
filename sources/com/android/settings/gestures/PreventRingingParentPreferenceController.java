package com.android.settings.gestures;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.window.R;
import com.android.settings.core.TogglePreferenceController;
import com.android.settingslib.PrimarySwitchPreference;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
/* loaded from: classes.dex */
public class PreventRingingParentPreferenceController extends TogglePreferenceController implements LifecycleObserver, OnStart, OnStop {
    static final int KEY_CHORD_POWER_VOLUME_UP_MUTE_TOGGLE = 1;
    final String SECURE_KEY = "volume_hush_gesture";
    private PrimarySwitchPreference mPreference;
    private SettingObserver mSettingObserver;

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_sound;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public PreventRingingParentPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (PrimarySwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mSettingObserver = new SettingObserver(this.mPreference);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return isVolumePowerKeyChordSetToHush() && Settings.Secure.getInt(this.mContext.getContentResolver(), "volume_hush_gesture", 1) != 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        int i = 1;
        int i2 = Settings.Secure.getInt(this.mContext.getContentResolver(), "volume_hush_gesture", 1);
        if (i2 != 0) {
            i = i2;
        }
        ContentResolver contentResolver = this.mContext.getContentResolver();
        if (!z) {
            i = 0;
        }
        return Settings.Secure.putInt(contentResolver, "volume_hush_gesture", i);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        CharSequence charSequence;
        super.updateState(preference);
        int i = Settings.Secure.getInt(this.mContext.getContentResolver(), "volume_hush_gesture", 1);
        if (isVolumePowerKeyChordSetToHush()) {
            if (i == 1) {
                charSequence = this.mContext.getText(R.string.prevent_ringing_option_vibrate_summary);
            } else if (i != 2) {
                charSequence = this.mContext.getText(R.string.switch_off_text);
            } else {
                charSequence = this.mContext.getText(R.string.prevent_ringing_option_mute_summary);
            }
            preference.setEnabled(true);
            this.mPreference.setSwitchEnabled(true);
        } else {
            charSequence = this.mContext.getText(R.string.prevent_ringing_option_unavailable_lpp_summary);
            preference.setEnabled(false);
            this.mPreference.setSwitchEnabled(false);
        }
        preference.setSummary(charSequence);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (!this.mContext.getResources().getBoolean(17891814)) {
            return 3;
        }
        if (isVolumePowerKeyChordSetToHush()) {
            return 0;
        }
        return this.mContext.getResources().getBoolean(17891690) ? 5 : 3;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        SettingObserver settingObserver = this.mSettingObserver;
        if (settingObserver != null) {
            settingObserver.register(this.mContext.getContentResolver());
            this.mSettingObserver.onChange(false, null);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        SettingObserver settingObserver = this.mSettingObserver;
        if (settingObserver != null) {
            settingObserver.unregister(this.mContext.getContentResolver());
        }
    }

    private boolean isVolumePowerKeyChordSetToHush() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "key_chord_power_volume_up", this.mContext.getResources().getInteger(17694840)) == 1;
    }

    /* loaded from: classes.dex */
    private class SettingObserver extends ContentObserver {
        private final Preference mPreference;
        private final Uri mVolumeHushGestureUri = Settings.Secure.getUriFor("volume_hush_gesture");
        private final Uri mKeyChordVolumePowerUpUri = Settings.Global.getUriFor("key_chord_power_volume_up");

        SettingObserver(Preference preference) {
            super(new Handler());
            this.mPreference = preference;
        }

        public void register(ContentResolver contentResolver) {
            contentResolver.registerContentObserver(this.mKeyChordVolumePowerUpUri, false, this);
            contentResolver.registerContentObserver(this.mVolumeHushGestureUri, false, this);
        }

        public void unregister(ContentResolver contentResolver) {
            contentResolver.unregisterContentObserver(this);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            if (uri == null || this.mVolumeHushGestureUri.equals(uri) || this.mKeyChordVolumePowerUpUri.equals(uri)) {
                PreventRingingParentPreferenceController.this.updateState(this.mPreference);
            }
        }
    }
}
