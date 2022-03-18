package com.android.settings.applications.appinfo;

import android.content.Context;
/* loaded from: classes.dex */
public class ExtraAppInfoFeatureProviderImpl implements ExtraAppInfoFeatureProvider {
    @Override // com.android.settings.applications.appinfo.ExtraAppInfoFeatureProvider
    public String getSummary(Context context) {
        return "";
    }

    @Override // com.android.settings.applications.appinfo.ExtraAppInfoFeatureProvider
    public boolean isEnabled(Context context) {
        return false;
    }

    @Override // com.android.settings.applications.appinfo.ExtraAppInfoFeatureProvider
    public boolean isSupported(Context context) {
        return false;
    }

    @Override // com.android.settings.applications.appinfo.ExtraAppInfoFeatureProvider
    public void launchExtraAppInfoSettings(Context context) {
    }

    @Override // com.android.settings.applications.appinfo.ExtraAppInfoFeatureProvider
    public void setPackageName(String str) {
    }
}
