package com.android.settings;

import android.app.Application;
import com.android.settings.activityembedding.ActivityEmbeddingRulesController;
import com.android.settings.homepage.SettingsHomepageActivity;
import com.android.settingslib.applications.AppIconCacheManager;
import java.lang.ref.WeakReference;
/* loaded from: classes.dex */
public class SettingsApplication extends Application {
    private WeakReference<SettingsHomepageActivity> mHomeActivity = new WeakReference<>(null);

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        new ActivityEmbeddingRulesController(this).initRules();
    }

    public void setHomeActivity(SettingsHomepageActivity settingsHomepageActivity) {
        this.mHomeActivity = new WeakReference<>(settingsHomepageActivity);
    }

    public SettingsHomepageActivity getHomeActivity() {
        return this.mHomeActivity.get();
    }

    @Override // android.app.Application, android.content.ComponentCallbacks
    public void onLowMemory() {
        super.onLowMemory();
        AppIconCacheManager.getInstance();
        AppIconCacheManager.release();
    }
}
