package com.android.settings.notification.zen;

import android.app.AutomaticZenRule;
import android.content.Context;
import android.service.notification.ZenPolicy;
import android.util.Pair;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.SelectorWithWidgetPreference;
/* loaded from: classes.dex */
public class ZenRuleVisEffectsNonePreferenceController extends AbstractZenCustomRulePreferenceController {
    private SelectorWithWidgetPreference mPreference;

    @Override // com.android.settings.notification.zen.AbstractZenCustomRulePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public /* bridge */ /* synthetic */ boolean isAvailable() {
        return super.isAvailable();
    }

    @Override // com.android.settings.notification.zen.AbstractZenCustomRulePreferenceController
    public /* bridge */ /* synthetic */ void onResume(AutomaticZenRule automaticZenRule, String str) {
        super.onResume(automaticZenRule, str);
    }

    public ZenRuleVisEffectsNonePreferenceController(Context context, Lifecycle lifecycle, String str) {
        super(context, str, lifecycle);
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        SelectorWithWidgetPreference selectorWithWidgetPreference = (SelectorWithWidgetPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = selectorWithWidgetPreference;
        selectorWithWidgetPreference.setOnClickListener(new SelectorWithWidgetPreference.OnClickListener() { // from class: com.android.settings.notification.zen.ZenRuleVisEffectsNonePreferenceController$$ExternalSyntheticLambda0
            @Override // com.android.settingslib.widget.SelectorWithWidgetPreference.OnClickListener
            public final void onRadioButtonClicked(SelectorWithWidgetPreference selectorWithWidgetPreference2) {
                ZenRuleVisEffectsNonePreferenceController.this.lambda$displayPreference$0(selectorWithWidgetPreference2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$displayPreference$0(SelectorWithWidgetPreference selectorWithWidgetPreference) {
        this.mMetricsFeatureProvider.action(this.mContext, 1397, Pair.create(1603, this.mId));
        this.mRule.setZenPolicy(new ZenPolicy.Builder(this.mRule.getZenPolicy()).hideAllVisualEffects().build());
        this.mBackend.updateZenRule(this.mId, this.mRule);
    }

    @Override // com.android.settings.notification.zen.AbstractZenCustomRulePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        AutomaticZenRule automaticZenRule;
        super.updateState(preference);
        if (this.mId != null && (automaticZenRule = this.mRule) != null && automaticZenRule.getZenPolicy() != null) {
            this.mPreference.setChecked(this.mRule.getZenPolicy().shouldHideAllVisualEffects());
        }
    }
}
