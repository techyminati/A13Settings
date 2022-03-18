package com.google.android.settings.aware;

import android.content.Context;
import android.os.SystemProperties;
import android.provider.Settings;
import androidx.fragment.app.Fragment;
import com.android.settings.aware.AwareFeatureProviderImpl;
/* loaded from: classes2.dex */
public class AwareFeatureProviderGoogleImpl extends AwareFeatureProviderImpl {
    @Override // com.android.settings.aware.AwareFeatureProviderImpl, com.android.settings.aware.AwareFeatureProvider
    public boolean isSupported(Context context) {
        return hasAwareSensor() && isAllowed(context);
    }

    @Override // com.android.settings.aware.AwareFeatureProviderImpl, com.android.settings.aware.AwareFeatureProvider
    public boolean isEnabled(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "aware_enabled", 0) == 1;
    }

    @Override // com.android.settings.aware.AwareFeatureProviderImpl, com.android.settings.aware.AwareFeatureProvider
    public void showRestrictionDialog(Fragment fragment) {
        AwareEnabledDialogFragment.show(fragment, Boolean.FALSE);
    }

    private static boolean isAllowed(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "aware_allowed", 0) == 1;
    }

    private static boolean hasAwareSensor() {
        return SystemProperties.getBoolean("ro.vendor.aware_available", false);
    }
}
