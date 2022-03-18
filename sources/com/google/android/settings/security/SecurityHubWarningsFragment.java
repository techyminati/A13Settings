package com.google.android.settings.security;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Keep;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceCategory;
import androidx.window.R;
import com.android.settings.SettingsPreferenceFragment;
import com.google.android.settings.security.SecurityContentManager;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
@Keep
/* loaded from: classes2.dex */
public class SecurityHubWarningsFragment extends SettingsPreferenceFragment implements SecurityContentManager.UiDataSubscriber {
    public static final String SECURITY_WARNINGS_CATEGORY_KEY = "security_hub_warnings_category";
    private static final String TAG = "SecurityHubWarnings";
    private SecurityContentManager mSecurityContentManager;
    List<SecurityWarning> mSecurityWarnings = new ArrayList();

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public /* bridge */ /* synthetic */ int getHelpResource() {
        return super.getHelpResource();
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1887;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.security_hub_warnings;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mSecurityContentManager = SecurityContentManager.getInstance(getContext()).subscribe(this);
    }

    @Override // com.google.android.settings.security.SecurityContentManager.UiDataSubscriber
    public void onSecurityHubUiDataChange() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() { // from class: com.google.android.settings.security.SecurityHubWarningsFragment$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SecurityHubWarningsFragment.this.lambda$onSecurityHubUiDataChange$0();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: updateWarningList */
    public void lambda$onSecurityHubUiDataChange$0() {
        List<SecurityWarning> securityWarnings = this.mSecurityContentManager.getSecurityWarnings();
        if (!this.mSecurityWarnings.equals(securityWarnings)) {
            final Context context = getContext();
            int size = securityWarnings.size();
            final PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference(SECURITY_WARNINGS_CATEGORY_KEY);
            preferenceCategory.removeAll();
            preferenceCategory.setTitle(context.getResources().getQuantityString(R.plurals.security_settings_hub_warnings_title, size, Integer.valueOf(size)));
            securityWarnings.forEach(new Consumer() { // from class: com.google.android.settings.security.SecurityHubWarningsFragment$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    SecurityHubWarningsFragment.this.lambda$updateWarningList$1(context, preferenceCategory, (SecurityWarning) obj);
                }
            });
            this.mSecurityWarnings = securityWarnings;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateWarningList$1(Context context, PreferenceCategory preferenceCategory, SecurityWarning securityWarning) {
        SecurityWarningPreference securityWarningPreference = new SecurityWarningPreference(context, null);
        securityWarningPreference.setSecurityWarning(securityWarning, this);
        preferenceCategory.addPreference(securityWarningPreference);
    }
}
