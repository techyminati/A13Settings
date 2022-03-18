package com.google.android.settings.aware;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import androidx.window.R;
/* loaded from: classes2.dex */
public class WakeScreenGestureDialogPreference extends AwareGestureDialogPreference {
    @Override // com.google.android.settings.aware.AwareGestureDialogPreference
    int getDialogDisabledMessage() {
        return R.string.wake_screen_aware_disabled_info_dialog_content;
    }

    @Override // com.google.android.settings.aware.AwareGestureDialogPreference
    public int getGestureDialogMessage() {
        return R.string.wake_screen_aware_off_dialog_content;
    }

    @Override // com.google.android.settings.aware.AwareGestureDialogPreference
    public int getGestureDialogTitle() {
        return R.string.wake_screen_aware_off_dialog_title;
    }

    @Override // com.google.android.settings.aware.AwareGestureDialogPreference, com.android.settingslib.CustomDialogPreferenceCompat
    public /* bridge */ /* synthetic */ void onClick(DialogInterface dialogInterface, int i) {
        super.onClick(dialogInterface, i);
    }

    public WakeScreenGestureDialogPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.google.android.settings.aware.AwareGestureDialogPreference
    public String getDestination() {
        return WakeScreenGestureSettings.class.getName();
    }

    @Override // com.google.android.settings.aware.AwareDialogPreferenceBase, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ((TextView) preferenceViewHolder.findViewById(16908310)).setSingleLine(false);
    }
}
