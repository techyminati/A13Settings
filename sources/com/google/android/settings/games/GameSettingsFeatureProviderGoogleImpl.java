package com.google.android.settings.games;

import android.content.Context;
import android.os.Build;
import com.android.settings.applications.GameSettingsFeatureProvider;
import com.android.settings.core.SubSettingLauncher;
/* loaded from: classes2.dex */
public class GameSettingsFeatureProviderGoogleImpl implements GameSettingsFeatureProvider {
    @Override // com.android.settings.applications.GameSettingsFeatureProvider
    public boolean isSupported(Context context) {
        return context.getPackageManager().hasSystemFeature("com.google.android.feature.GAME_OVERLAY") || Build.IS_DEBUGGABLE;
    }

    @Override // com.android.settings.applications.GameSettingsFeatureProvider
    public void launchGameSettings(Context context) {
        new SubSettingLauncher(context).setDestination(GameSettings.class.getName()).setSourceMetricsCategory(1886).launch();
    }
}
