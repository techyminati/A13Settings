package com.android.settings.notification.zen;

import android.app.AutomaticZenRule;
import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.Map;
import java.util.Objects;
/* loaded from: classes.dex */
public class ZenModeAutomaticRulesPreferenceController extends AbstractZenModeAutomaticRulePreferenceController {
    protected PreferenceCategory mPreferenceCategory;

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "zen_mode_automatic_rules";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public ZenModeAutomaticRulesPreferenceController(Context context, Fragment fragment, Lifecycle lifecycle) {
        super(context, "zen_mode_automatic_rules", fragment, lifecycle);
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreferenceCategory = preferenceCategory;
        preferenceCategory.setPersistent(false);
    }

    @Override // com.android.settings.notification.zen.AbstractZenModeAutomaticRulePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        Map.Entry<String, AutomaticZenRule>[] rules = getRules();
        if (this.mPreferenceCategory.getPreferenceCount() == rules.length) {
            for (int i = 0; i < rules.length; i++) {
                ZenRulePreference zenRulePreference = (ZenRulePreference) this.mPreferenceCategory.getPreference(i);
                if (Objects.equals(zenRulePreference.mId, rules[i].getKey())) {
                    zenRulePreference.updatePreference(rules[i].getValue());
                } else {
                    reloadAllRules(rules);
                    return;
                }
            }
            return;
        }
        reloadAllRules(rules);
    }

    void reloadAllRules(Map.Entry<String, AutomaticZenRule>[] entryArr) {
        this.mPreferenceCategory.removeAll();
        for (Map.Entry<String, AutomaticZenRule> entry : entryArr) {
            this.mPreferenceCategory.addPreference(createZenRulePreference(entry));
        }
    }

    ZenRulePreference createZenRulePreference(Map.Entry<String, AutomaticZenRule> entry) {
        return new ZenRulePreference(this.mPreferenceCategory.getContext(), entry, this.mParent, this.mMetricsFeatureProvider);
    }
}
