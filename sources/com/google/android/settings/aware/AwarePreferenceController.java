package com.google.android.settings.aware;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.provider.Settings;
import androidx.fragment.app.Fragment;
import androidx.window.R;
/* loaded from: classes2.dex */
public class AwarePreferenceController extends AwareTogglePreferenceController implements DialogInterface.OnClickListener {
    private Fragment mParent;

    @Override // com.google.android.settings.aware.AwareTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.google.android.settings.aware.AwareTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.google.android.settings.aware.AwareTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_system;
    }

    @Override // com.google.android.settings.aware.AwareTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.google.android.settings.aware.AwareTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public boolean isSliceable() {
        return false;
    }

    @Override // com.google.android.settings.aware.AwareTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AwarePreferenceController(Context context, String str) {
        super(context, str);
    }

    public void init(Fragment fragment) {
        this.mParent = fragment;
    }

    @Override // com.google.android.settings.aware.AwareTogglePreferenceController, com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mHelper.isAvailable() ? 0 : 5;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), "aware_enabled", 0) == 1;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        if (this.mPreference.isChecked()) {
            AwareSettingsDialogFragment.show(this.mParent, this);
            return false;
        }
        Settings.Secure.putInt(this.mContext.getContentResolver(), "aware_enabled", 1);
        enableAllFeatures();
        return true;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            Settings.Secure.putInt(this.mContext.getContentResolver(), "aware_enabled", 0);
            this.mPreference.setChecked(false);
        } else if (i == -2) {
            this.mPreference.setChecked(true);
        }
    }

    private void enableAllFeatures() {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        if (this.mHelper.readFeatureEnabled("silence_gesture")) {
            Settings.Secure.putInt(contentResolver, "silence_gesture", 1);
        }
        if (this.mHelper.readFeatureEnabled("skip_gesture")) {
            Settings.Secure.putInt(contentResolver, "skip_gesture", 1);
        }
        if (this.mHelper.readFeatureEnabled("doze_wake_display_gesture")) {
            Settings.Secure.putInt(contentResolver, "doze_wake_display_gesture", 1);
        }
        if (this.mHelper.readFeatureEnabled("doze_always_on")) {
            Settings.Secure.putInt(contentResolver, "doze_always_on", 1);
        }
        if (this.mHelper.readFeatureEnabled("doze_wake_screen_gesture")) {
            Settings.Secure.putInt(contentResolver, "doze_wake_screen_gesture", 1);
        }
        if (this.mHelper.readFeatureEnabled("aware_lock_enabled")) {
            Settings.Secure.putInt(contentResolver, "aware_lock_enabled", 1);
        }
        if (this.mHelper.readFeatureEnabled("tap_gesture")) {
            Settings.Secure.putInt(contentResolver, "tap_gesture", 1);
        }
    }
}
