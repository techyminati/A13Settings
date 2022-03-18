package com.android.settings.notification.zen;

import android.app.AutomaticZenRule;
import android.content.Context;
import androidx.preference.Preference;
import androidx.window.R;
import com.android.settingslib.core.lifecycle.Lifecycle;
/* loaded from: classes.dex */
public class ZenRuleNotifFooterPreferenceController extends AbstractZenCustomRulePreferenceController {
    @Override // com.android.settings.notification.zen.AbstractZenCustomRulePreferenceController
    public /* bridge */ /* synthetic */ void onResume(AutomaticZenRule automaticZenRule, String str) {
        super.onResume(automaticZenRule, str);
    }

    public ZenRuleNotifFooterPreferenceController(Context context, Lifecycle lifecycle, String str) {
        super(context, str, lifecycle);
    }

    @Override // com.android.settings.notification.zen.AbstractZenCustomRulePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        if (!super.isAvailable() || this.mRule.getZenPolicy() == null) {
            return false;
        }
        return this.mRule.getZenPolicy().shouldHideAllVisualEffects() || this.mRule.getZenPolicy().shouldShowAllVisualEffects();
    }

    @Override // com.android.settings.notification.zen.AbstractZenCustomRulePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        AutomaticZenRule automaticZenRule = this.mRule;
        if (automaticZenRule != null && automaticZenRule.getZenPolicy() != null) {
            if (this.mRule.getZenPolicy().shouldShowAllVisualEffects()) {
                preference.setTitle(R.string.zen_mode_restrict_notifications_mute_footer);
            } else if (this.mRule.getZenPolicy().shouldHideAllVisualEffects()) {
                preference.setTitle(R.string.zen_mode_restrict_notifications_hide_footer);
            } else {
                preference.setTitle((CharSequence) null);
            }
        }
    }
}
