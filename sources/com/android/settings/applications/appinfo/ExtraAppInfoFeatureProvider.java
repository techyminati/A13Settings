package com.android.settings.applications.appinfo;

import android.content.Context;
/* loaded from: classes.dex */
public interface ExtraAppInfoFeatureProvider {
    String getSummary(Context context);

    boolean isEnabled(Context context);

    boolean isSupported(Context context);

    void launchExtraAppInfoSettings(Context context);

    void setPackageName(String str);
}
