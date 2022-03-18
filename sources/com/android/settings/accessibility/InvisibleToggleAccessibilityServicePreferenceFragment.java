package com.android.settings.accessibility;

import android.content.DialogInterface;
import android.view.View;
import androidx.window.R;
import com.android.settingslib.accessibility.AccessibilityUtils;
/* loaded from: classes.dex */
public class InvisibleToggleAccessibilityServicePreferenceFragment extends ToggleAccessibilityServicePreferenceFragment {
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected void onInstallSwitchPreferenceToggleSwitch() {
        super.onInstallSwitchPreferenceToggleSwitch();
        this.mToggleServiceSwitchPreference.setVisible(false);
    }

    @Override // com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment, com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.accessibility.ShortcutPreference.OnClickCallback
    public void onToggleClicked(ShortcutPreference shortcutPreference) {
        super.onToggleClicked(shortcutPreference);
        AccessibilityUtils.setAccessibilityServiceState(getContext(), this.mComponentName, getArguments().getBoolean("checked") && shortcutPreference.isChecked());
    }

    @Override // com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment
    void onDialogButtonFromShortcutToggleClicked(View view) {
        super.onDialogButtonFromShortcutToggleClicked(view);
        if (view.getId() == R.id.permission_enable_allow_button) {
            AccessibilityUtils.setAccessibilityServiceState(getContext(), this.mComponentName, true);
        }
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected void callOnAlertDialogCheckboxClicked(DialogInterface dialogInterface, int i) {
        super.callOnAlertDialogCheckboxClicked(dialogInterface, i);
        AccessibilityUtils.setAccessibilityServiceState(getContext(), this.mComponentName, this.mShortcutPreference.isChecked());
    }
}
