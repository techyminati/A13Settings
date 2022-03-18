package com.android.settings.biometrics.combination;

import android.content.Context;
import androidx.window.R;
import com.android.settings.search.BaseSearchIndexProvider;
/* loaded from: classes.dex */
public class CombinedBiometricSettings extends BiometricsSettingsBase {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new CombinedBiometricSearchIndexProvider(R.xml.security_settings_combined_biometric);

    @Override // com.android.settings.biometrics.combination.BiometricsSettingsBase
    public String getFacePreferenceKey() {
        return "biometric_face_settings";
    }

    @Override // com.android.settings.biometrics.combination.BiometricsSettingsBase
    public String getFingerprintPreferenceKey() {
        return "biometric_fingerprint_settings";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "BiometricSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1878;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.security_settings_combined_biometric;
    }

    @Override // com.android.settings.biometrics.combination.BiometricsSettingsBase
    public String getUnlockPhonePreferenceKey() {
        return "biometric_settings_biometric_keyguard";
    }

    @Override // com.android.settings.biometrics.combination.BiometricsSettingsBase
    public String getUseInAppsPreferenceKey() {
        return "biometric_settings_biometric_app";
    }

    @Override // com.android.settings.biometrics.combination.BiometricsSettingsBase, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((BiometricSettingsKeyguardPreferenceController) use(BiometricSettingsKeyguardPreferenceController.class)).setUserId(this.mUserId);
        ((BiometricSettingsAppPreferenceController) use(BiometricSettingsAppPreferenceController.class)).setUserId(this.mUserId);
    }
}
