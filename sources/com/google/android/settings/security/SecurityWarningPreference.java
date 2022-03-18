package com.google.android.settings.security;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import androidx.window.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.widget.BannerMessagePreference;
/* loaded from: classes2.dex */
public class SecurityWarningPreference extends BannerMessagePreference {
    private SecurityContentManager mSecurityContentManager;

    public SecurityWarningPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mSecurityContentManager = SecurityContentManager.getInstance(context);
    }

    public void setSecurityWarning(SecurityWarning securityWarning, final SettingsPreferenceFragment settingsPreferenceFragment) {
        setTitle(securityWarning.getTitle());
        setSubtitle(securityWarning.getSubtitle());
        setSummary(securityWarning.getSummary());
        final Bundle primaryButtonClickBundle = securityWarning.getPrimaryButtonClickBundle();
        if (primaryButtonClickBundle != null) {
            setPositiveButtonOnClickListener(new View.OnClickListener() { // from class: com.google.android.settings.security.SecurityWarningPreference$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    SecurityWarningPreference.this.lambda$setSecurityWarning$0(primaryButtonClickBundle, settingsPreferenceFragment, view);
                }
            });
            setPositiveButtonText(securityWarning.getPrimaryButtonText());
        }
        final Bundle secondaryButtonClickBundle = securityWarning.getSecondaryButtonClickBundle();
        if (secondaryButtonClickBundle != null) {
            setNegativeButtonOnClickListener(new View.OnClickListener() { // from class: com.google.android.settings.security.SecurityWarningPreference$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    SecurityWarningPreference.this.lambda$setSecurityWarning$1(secondaryButtonClickBundle, settingsPreferenceFragment, view);
                }
            });
            setNegativeButtonText(securityWarning.getSecondaryButtonText());
        }
        final Bundle dismissButtonClickBundle = securityWarning.getDismissButtonClickBundle();
        if (dismissButtonClickBundle != null) {
            if (securityWarning.showConfirmationDialogOnDismiss()) {
                setDismissButtonOnClickListener(new View.OnClickListener() { // from class: com.google.android.settings.security.SecurityWarningPreference$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        SecurityWarningPreference.lambda$setSecurityWarning$2(dismissButtonClickBundle, settingsPreferenceFragment, view);
                    }
                });
            } else {
                setDismissButtonOnClickListener(new View.OnClickListener() { // from class: com.google.android.settings.security.SecurityWarningPreference$$ExternalSyntheticLambda1
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        SecurityWarningPreference.this.lambda$setSecurityWarning$3(dismissButtonClickBundle, settingsPreferenceFragment, view);
                    }
                });
            }
        }
        SecurityLevel securityLevel = securityWarning.getSecurityLevel();
        if (securityLevel != null) {
            setIcon(getContext().getResources().getDrawable(securityLevel.getWarningCardIconResId(), getContext().getTheme()));
            setAttentionLevel(securityLevel.getAttentionLevel());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setSecurityWarning$0(Bundle bundle, SettingsPreferenceFragment settingsPreferenceFragment, View view) {
        this.mSecurityContentManager.handleClick(bundle, settingsPreferenceFragment.getActivity());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setSecurityWarning$1(Bundle bundle, SettingsPreferenceFragment settingsPreferenceFragment, View view) {
        this.mSecurityContentManager.handleClick(bundle, settingsPreferenceFragment.getActivity());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$setSecurityWarning$2(Bundle bundle, SettingsPreferenceFragment settingsPreferenceFragment, View view) {
        SecurityConfirmationDialogFragment.newInstance(R.string.security_dismiss_dialog_title, 0, R.string.security_dismiss_dialog_dismiss_button, 17039360, bundle).show(settingsPreferenceFragment.getParentFragmentManager(), "SecurityConfirmationDialogFragment");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setSecurityWarning$3(Bundle bundle, SettingsPreferenceFragment settingsPreferenceFragment, View view) {
        this.mSecurityContentManager.handleClick(bundle, settingsPreferenceFragment.getActivity());
    }
}
