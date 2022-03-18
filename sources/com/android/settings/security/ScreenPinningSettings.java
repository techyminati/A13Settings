package com.android.settings.security;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.widget.Switch;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.window.R;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.SettingsMainSwitchBar;
import com.android.settingslib.widget.FooterPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes.dex */
public class ScreenPinningSettings extends SettingsPreferenceFragment implements OnMainSwitchChangeListener, DialogInterface.OnClickListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() { // from class: com.android.settings.security.ScreenPinningSettings.2
        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = R.xml.screen_pinning_settings;
            return Arrays.asList(searchIndexableResource);
        }
    };
    private FooterPreference mFooterPreference;
    private LockPatternUtils mLockPatternUtils;
    private SettingsMainSwitchBar mSwitchBar;
    private SwitchPreference mUseScreenLock;
    private UserManager mUserManager;

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_url_screen_pinning;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 86;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
        settingsActivity.setTitle(R.string.screen_pinning_title);
        this.mLockPatternUtils = new LockPatternUtils(settingsActivity);
        this.mUserManager = (UserManager) settingsActivity.getSystemService(UserManager.class);
        addPreferencesFromResource(R.xml.screen_pinning_settings);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        this.mUseScreenLock = (SwitchPreference) preferenceScreen.findPreference("use_screen_lock");
        this.mFooterPreference = (FooterPreference) preferenceScreen.findPreference("screen_pinning_settings_screen_footer");
        SettingsMainSwitchBar switchBar = settingsActivity.getSwitchBar();
        this.mSwitchBar = switchBar;
        switchBar.setTitle(getContext().getString(R.string.app_pinning_main_switch_title));
        this.mSwitchBar.show();
        this.mSwitchBar.setChecked(isLockToAppEnabled(getActivity()));
        this.mSwitchBar.addOnSwitchChangeListener(this);
        updateDisplay();
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mSwitchBar.removeOnSwitchChangeListener(this);
        this.mSwitchBar.hide();
    }

    private static boolean isLockToAppEnabled(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "lock_to_app_enabled", 0) != 0;
    }

    private void setLockToAppEnabled(boolean z) {
        Settings.System.putInt(getContentResolver(), "lock_to_app_enabled", z ? 1 : 0);
        if (z) {
            setScreenLockUsedSetting(isScreenLockUsed());
        }
    }

    private boolean isScreenLockUsed() {
        return Settings.Secure.getInt(getContentResolver(), "lock_to_app_exit_locked", this.mLockPatternUtils.isSecure(UserHandle.myUserId()) ? 1 : 0) != 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean setScreenLockUsed(boolean z) {
        if (!z || new LockPatternUtils(getActivity()).getKeyguardStoredPasswordQuality(UserHandle.myUserId()) != 0) {
            setScreenLockUsedSetting(z);
            return true;
        }
        Intent intent = new Intent("android.app.action.SET_NEW_PASSWORD");
        intent.putExtra("hide_insecure_options", true);
        startActivityForResult(intent, 43);
        return false;
    }

    private void setScreenLockUsedSetting(boolean z) {
        Settings.Secure.putInt(getContentResolver(), "lock_to_app_exit_locked", z ? 1 : 0);
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 43) {
            boolean z = new LockPatternUtils(getActivity()).getKeyguardStoredPasswordQuality(UserHandle.myUserId()) != 0;
            setScreenLockUsed(z);
            this.mUseScreenLock.setChecked(z);
        }
    }

    private int getCurrentSecurityTitle() {
        int keyguardStoredPasswordQuality = this.mLockPatternUtils.getKeyguardStoredPasswordQuality(UserHandle.myUserId());
        return keyguardStoredPasswordQuality != 65536 ? (keyguardStoredPasswordQuality == 131072 || keyguardStoredPasswordQuality == 196608) ? R.string.screen_pinning_unlock_pin : (keyguardStoredPasswordQuality == 262144 || keyguardStoredPasswordQuality == 327680 || keyguardStoredPasswordQuality == 393216 || keyguardStoredPasswordQuality == 524288) ? R.string.screen_pinning_unlock_password : R.string.screen_pinning_unlock_none : this.mLockPatternUtils.isLockPatternEnabled(UserHandle.myUserId()) ? R.string.screen_pinning_unlock_pattern : R.string.screen_pinning_unlock_none;
    }

    @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
    public void onSwitchChanged(Switch r2, boolean z) {
        if (z) {
            new AlertDialog.Builder(getContext()).setMessage(R.string.screen_pinning_dialog_message).setPositiveButton(R.string.dlg_ok, this).setNegativeButton(R.string.dlg_cancel, this).setCancelable(false).show();
            return;
        }
        setLockToAppEnabled(false);
        updateDisplay();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            setLockToAppEnabled(true);
        } else {
            this.mSwitchBar.setChecked(false);
        }
        updateDisplay();
    }

    private void updateDisplay() {
        if (isLockToAppEnabled(getActivity())) {
            this.mUseScreenLock.setEnabled(true);
            this.mUseScreenLock.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.security.ScreenPinningSettings.1
                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    return ScreenPinningSettings.this.setScreenLockUsed(((Boolean) obj).booleanValue());
                }
            });
            this.mUseScreenLock.setChecked(isScreenLockUsed());
            this.mUseScreenLock.setTitle(getCurrentSecurityTitle());
            return;
        }
        this.mFooterPreference.setSummary(getAppPinningContent());
        this.mUseScreenLock.setEnabled(false);
    }

    private boolean isGuestModeSupported() {
        return UserManager.supportsMultipleUsers() && !this.mUserManager.hasUserRestriction("no_user_switch");
    }

    private CharSequence getAppPinningContent() {
        if (isGuestModeSupported()) {
            return getActivity().getText(R.string.screen_pinning_guest_user_description);
        }
        return getActivity().getText(R.string.screen_pinning_description);
    }
}
