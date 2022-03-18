package com.android.settings.accessibility;

import android.os.Bundle;
import android.view.View;
import androidx.window.R;
/* loaded from: classes.dex */
public class VolumeShortcutToggleAccessibilityServicePreferenceFragment extends ToggleAccessibilityServicePreferenceFragment {
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mShortcutPreference.setSummary(getPrefContext().getText(R.string.accessibility_shortcut_edit_dialog_title_hardware));
        this.mShortcutPreference.setSettingsEditable(false);
        setAllowedPreferredShortcutType(2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment, com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public int getUserShortcutTypes() {
        int userShortcutTypes = super.getUserShortcutTypes();
        boolean z = getArguments().getBoolean("checked");
        int i = getAccessibilityServiceInfo().flags & 256;
        boolean z2 = true;
        if (i == 0) {
            z2 = false;
        }
        return (!z2 || !z) ? userShortcutTypes & (-2) : userShortcutTypes | 1;
    }

    private void setAllowedPreferredShortcutType(int i) {
        PreferredShortcuts.saveUserShortcutType(getPrefContext(), new PreferredShortcut(this.mComponentName.flattenToString(), i));
    }
}
