package com.android.settings.notification.app;

import android.content.Context;
import androidx.preference.Preference;
import androidx.window.R;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.RestrictedSwitchPreference;
/* loaded from: classes.dex */
public class InvalidConversationPreferenceController extends NotificationPreferenceController implements Preference.OnPreferenceChangeListener {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "invalid_conversation_switch";
    }

    public InvalidConversationPreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        NotificationBackend.AppRow appRow = this.mAppRow;
        if (appRow == null || appRow.banned) {
            return false;
        }
        if (this.mPreferenceFilter != null && !isIncludedInFilter()) {
            return false;
        }
        NotificationBackend notificationBackend = this.mBackend;
        NotificationBackend.AppRow appRow2 = this.mAppRow;
        return notificationBackend.isInInvalidMsgState(appRow2.pkg, appRow2.uid);
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController
    boolean isIncludedInFilter() {
        return this.mPreferenceFilter.contains("conversation");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (this.mAppRow != null) {
            RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) preference;
            restrictedSwitchPreference.setDisabledByAdmin(this.mAdmin);
            restrictedSwitchPreference.setEnabled(!restrictedSwitchPreference.isDisabledByAdmin());
            NotificationBackend notificationBackend = this.mBackend;
            NotificationBackend.AppRow appRow = this.mAppRow;
            restrictedSwitchPreference.setChecked(!notificationBackend.hasUserDemotedInvalidMsgApp(appRow.pkg, appRow.uid));
            preference.setSummary(((NotificationPreferenceController) this).mContext.getString(R.string.conversation_section_switch_summary));
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        NotificationBackend.AppRow appRow = this.mAppRow;
        if (appRow == null) {
            return false;
        }
        this.mBackend.setInvalidMsgAppDemoted(appRow.pkg, appRow.uid, !((Boolean) obj).booleanValue());
        return true;
    }
}
