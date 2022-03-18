package com.google.android.settings.security;

import android.content.Context;
import android.util.FeatureFlagUtils;
import androidx.window.R;
import com.android.settings.security.SecuritySettingsFeatureProvider;
import com.android.settingslib.utils.ThreadUtils;
import java.util.concurrent.Callable;
/* loaded from: classes2.dex */
public class SecuritySettingsFeatureProviderGoogleImpl implements SecuritySettingsFeatureProvider {
    private final Context mContext;

    public SecuritySettingsFeatureProviderGoogleImpl(Context context) {
        this.mContext = context;
        ThreadUtils.postOnBackgroundThread(new Callable() { // from class: com.google.android.settings.security.SecuritySettingsFeatureProviderGoogleImpl$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.Callable
            public final Object call() {
                Object lambda$new$0;
                lambda$new$0 = SecuritySettingsFeatureProviderGoogleImpl.this.lambda$new$0();
                return lambda$new$0;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Object lambda$new$0() throws Exception {
        return Boolean.valueOf(SecurityContentManager.getInstance(this.mContext).getSecurityHubIsEnabled());
    }

    @Override // com.android.settings.security.SecuritySettingsFeatureProvider
    public boolean hasAlternativeSecuritySettingsFragment() {
        return SecurityContentManager.getInstance(this.mContext).getSecurityHubIsEnabled() && FeatureFlagUtils.isEnabled(this.mContext, "settings_enable_security_hub");
    }

    @Override // com.android.settings.security.SecuritySettingsFeatureProvider
    public String getAlternativeSecuritySettingsFragmentClassname() {
        return SecurityHubDashboard.class.getName();
    }

    @Override // com.android.settings.security.SecuritySettingsFeatureProvider
    public String getAlternativeAdvancedSettingsCategoryKey() {
        return this.mContext.getString(R.string.config_alternative_advanced_security_category_key);
    }
}
