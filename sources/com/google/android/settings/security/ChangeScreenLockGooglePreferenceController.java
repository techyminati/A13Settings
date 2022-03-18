package com.google.android.settings.security;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.security.ChangeScreenLockPreferenceController;
/* loaded from: classes2.dex */
public class ChangeScreenLockGooglePreferenceController extends ChangeScreenLockPreferenceController {
    private final SecurityContentManager mSecurityContentManager;

    @Override // com.android.settings.security.ChangeScreenLockPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "securityhub_unlock_set_or_change";
    }

    public ChangeScreenLockGooglePreferenceController(Context context, SettingsPreferenceFragment settingsPreferenceFragment) {
        super(context, settingsPreferenceFragment);
        this.mSecurityContentManager = SecurityContentManager.getInstance(context);
    }

    @Override // com.android.settings.security.ChangeScreenLockPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        preference.setIcon(this.mSecurityContentManager.getScreenLockSecurityLevel(this.mLockPatternUtils.isSecure(this.mUserId)).getEntryIconResId());
        preference.setOrder(this.mSecurityContentManager.getScreenLockOrder());
    }
}
