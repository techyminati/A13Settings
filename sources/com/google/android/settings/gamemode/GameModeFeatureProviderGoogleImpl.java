package com.google.android.settings.gamemode;

import android.app.GameManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import androidx.window.R;
import com.android.settings.applications.appinfo.ExtraAppInfoFeatureProvider;
import com.android.settings.core.SubSettingLauncher;
/* loaded from: classes2.dex */
public class GameModeFeatureProviderGoogleImpl implements ExtraAppInfoFeatureProvider {
    private String mPackageName;

    @Override // com.android.settings.applications.appinfo.ExtraAppInfoFeatureProvider
    public boolean isSupported(Context context) {
        if (TextUtils.isEmpty(this.mPackageName)) {
            return false;
        }
        PackageManager packageManager = context.getPackageManager();
        if (!packageManager.hasSystemFeature("com.google.android.feature.GAME_OVERLAY") && !Build.IS_DEBUGGABLE) {
            return false;
        }
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(this.mPackageName, 0);
            return applicationInfo != null && applicationInfo.category == 0;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("GameModeFeatureProviderGoogleImpl", "Fail to get ApplicationInfo for package:" + this.mPackageName, e);
            return false;
        }
    }

    @Override // com.android.settings.applications.appinfo.ExtraAppInfoFeatureProvider
    public boolean isEnabled(Context context) {
        for (int i : ((GameManager) context.getSystemService(GameManager.class)).getAvailableGameModes(this.mPackageName)) {
            if (i == 0) {
                return false;
            }
        }
        return true;
    }

    @Override // com.android.settings.applications.appinfo.ExtraAppInfoFeatureProvider
    public void launchExtraAppInfoSettings(Context context) {
        Bundle bundle = new Bundle();
        bundle.putString("package", this.mPackageName);
        new SubSettingLauncher(context).setDestination(GameModeSettings.class.getName()).setArguments(bundle).setSourceMetricsCategory(20).launch();
    }

    @Override // com.android.settings.applications.appinfo.ExtraAppInfoFeatureProvider
    public void setPackageName(String str) {
        this.mPackageName = str;
    }

    @Override // com.android.settings.applications.appinfo.ExtraAppInfoFeatureProvider
    public String getSummary(Context context) {
        if (!isEnabled(context)) {
            return context.getResources().getText(R.string.game_optimization_unavailable).toString();
        }
        int gameMode = ((GameManager) context.getSystemService(GameManager.class)).getGameMode(this.mPackageName);
        if (gameMode == 1) {
            return context.getResources().getText(R.string.game_mode_standard_title).toString();
        }
        if (gameMode == 2) {
            return context.getResources().getText(R.string.game_mode_performance_title).toString();
        }
        return gameMode == 3 ? context.getResources().getText(R.string.game_mode_battery_title).toString() : "";
    }
}
