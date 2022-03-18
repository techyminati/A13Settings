package com.android.settings.applications;

import android.content.Context;
/* loaded from: classes.dex */
public interface GameSettingsFeatureProvider {
    boolean isSupported(Context context);

    void launchGameSettings(Context context);
}
