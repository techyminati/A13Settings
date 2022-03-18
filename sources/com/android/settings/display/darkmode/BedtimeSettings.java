package com.android.settings.display.darkmode;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.FeatureFlagUtils;
/* loaded from: classes.dex */
public final class BedtimeSettings {
    private final Context mContext;
    private final PackageManager mPackageManager;
    private final String mWellbeingPackage;

    public BedtimeSettings(Context context) {
        this.mContext = context;
        this.mPackageManager = context.getPackageManager();
        this.mWellbeingPackage = context.getResources().getString(17039936);
    }

    public Intent getBedtimeSettingsIntent() {
        Intent intent;
        ResolveInfo resolveActivity;
        if (FeatureFlagUtils.isEnabled(this.mContext, "settings_app_allow_dark_theme_activation_at_bedtime") && (resolveActivity = this.mPackageManager.resolveActivity((intent = new Intent("android.settings.BEDTIME_SETTINGS").setPackage(this.mWellbeingPackage)), 65536)) != null && resolveActivity.activityInfo.isEnabled()) {
            return intent;
        }
        return null;
    }
}
