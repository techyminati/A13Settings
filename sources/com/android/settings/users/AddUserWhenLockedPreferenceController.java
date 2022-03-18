package com.android.settings.users;

import android.content.Context;
import android.content.IntentFilter;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.window.R;
import com.android.settings.core.TogglePreferenceController;
import com.android.settingslib.RestrictedSwitchPreference;
/* loaded from: classes.dex */
public class AddUserWhenLockedPreferenceController extends TogglePreferenceController {
    private final UserCapabilities mUserCaps;

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
        return R.string.menu_key_system;
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

    public AddUserWhenLockedPreferenceController(Context context, String str) {
        super(context, str);
        this.mUserCaps = UserCapabilities.create(context);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        this.mUserCaps.updateAddUserCapabilities(this.mContext);
        RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) preference;
        if (!isAvailable()) {
            restrictedSwitchPreference.setVisible(false);
            return;
        }
        restrictedSwitchPreference.setDisabledByAdmin(this.mUserCaps.disallowAddUser() ? this.mUserCaps.getEnforcedAdmin() : null);
        restrictedSwitchPreference.setVisible(this.mUserCaps.mUserSwitcherEnabled);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (this.mUserCaps.isAdmin() && !this.mUserCaps.disallowAddUser() && !this.mUserCaps.disallowAddUserSetByAdmin()) {
            return this.mUserCaps.mUserSwitcherEnabled ? 0 : 2;
        }
        return 4;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "add_users_when_locked", 0) == 1;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        return Settings.Global.putInt(this.mContext.getContentResolver(), "add_users_when_locked", z ? 1 : 0);
    }
}
