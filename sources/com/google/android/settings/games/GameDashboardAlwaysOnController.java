package com.google.android.settings.games;

import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.window.R;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.core.TogglePreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes2.dex */
public class GameDashboardAlwaysOnController extends TogglePreferenceController implements LifecycleObserver {
    @VisibleForTesting
    static final int OFF = 0;
    @VisibleForTesting
    static final int ON = 1;
    private static final String TAG = "GDAlwaysOnController";
    private final ContentObserver mAlwaysOnObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.google.android.settings.games.GameDashboardAlwaysOnController.1
        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            super.onChange(z);
            GameDashboardAlwaysOnController gameDashboardAlwaysOnController = GameDashboardAlwaysOnController.this;
            boolean z2 = false;
            if (Settings.Secure.getIntForUser(((AbstractPreferenceController) gameDashboardAlwaysOnController).mContext.getContentResolver(), "game_dashboard_always_on", 0, ((AbstractPreferenceController) GameDashboardAlwaysOnController.this).mContext.getUserId()) == 1) {
                z2 = true;
            }
            gameDashboardAlwaysOnController.setChecked(z2);
        }
    };

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_apps;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public GameDashboardAlwaysOnController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "game_dashboard_always_on", 0, this.mContext.getUserId()) == 1;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "game_dashboard_always_on", z ? 1 : 0, this.mContext.getUserId());
        return true;
    }

    public void init(Lifecycle lifecycle) {
        lifecycle.addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onLifeCycleStartEvent() {
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("game_dashboard_always_on"), false, this.mAlwaysOnObserver, this.mContext.getUserId());
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onLifeCycleStopEvent() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mAlwaysOnObserver);
    }
}
