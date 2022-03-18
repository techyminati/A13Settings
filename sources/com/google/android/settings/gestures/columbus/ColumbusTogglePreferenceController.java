package com.google.android.settings.gestures.columbus;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.app.SynchronousUserSwitchObserver;
import android.app.UserSwitchObserver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.window.R;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
/* loaded from: classes2.dex */
public abstract class ColumbusTogglePreferenceController extends TogglePreferenceController implements LifecycleObserver, OnStart, OnStop {
    private static final Uri COLUMBUS_ENABLED_URI = Settings.Secure.getUriFor("columbus_enabled");
    private static final int DISABLED = 0;
    private static final int ENABLED = 1;
    private static final String TAG = "ColumbusTogglePreference";
    private final Context mContext;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private SettingObserver mSettingObserver;
    private SwitchPreference mSwitchPreference;
    private final IActivityManager mActivityManager = ActivityManager.getService();
    private final UserSwitchObserver mUserSwitchObserver = new SynchronousUserSwitchObserver() { // from class: com.google.android.settings.gestures.columbus.ColumbusTogglePreferenceController.1
        public void onUserSwitching(int i) throws RemoteException {
            if (ColumbusTogglePreferenceController.this.mSettingObserver != null) {
                ColumbusTogglePreferenceController.this.mSettingObserver.unregister();
                ColumbusTogglePreferenceController.this.mSettingObserver.register();
            }
        }
    };

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
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
        return R.string.menu_key_system;
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

    public ColumbusTogglePreferenceController(Context context, String str, int i) {
        super(context, str);
        this.mContext = context;
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
        setMetricsCategory(i);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        SwitchPreference switchPreference = (SwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mSwitchPreference = switchPreference;
        if (switchPreference != null) {
            this.mSettingObserver = new SettingObserver(this.mSwitchPreference);
        }
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), getPreferenceKey(), 0, ActivityManager.getCurrentUser()) != 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        this.mMetricsFeatureProvider.action(this.mContext, getMetricsCategory(), z);
        return Settings.Secure.putIntForUser(this.mContext.getContentResolver(), getPreferenceKey(), z ? 1 : 0, ActivityManager.getCurrentUser());
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return ColumbusPreferenceController.isColumbusSupported(this.mContext) ? 0 : 3;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        try {
            this.mActivityManager.registerUserSwitchObserver(this.mUserSwitchObserver, TAG);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to register user switch observer", e);
        }
        SettingObserver settingObserver = this.mSettingObserver;
        if (settingObserver != null) {
            settingObserver.register();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        try {
            this.mActivityManager.unregisterUserSwitchObserver(this.mUserSwitchObserver);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed  to unregister user switch observer", e);
        }
        SettingObserver settingObserver = this.mSettingObserver;
        if (settingObserver != null) {
            settingObserver.unregister();
        }
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        SwitchPreference switchPreference = this.mSwitchPreference;
        if (switchPreference != null) {
            switchPreference.setEnabled(ColumbusPreferenceController.isColumbusEnabled(this.mContext));
        }
    }

    /* loaded from: classes2.dex */
    private class SettingObserver extends ContentObserver {
        private final Preference mPreference;

        SettingObserver(Preference preference) {
            super(new Handler(Looper.myLooper()));
            this.mPreference = preference;
        }

        public void register() {
            ColumbusTogglePreferenceController.this.mContext.getContentResolver().registerContentObserver(ColumbusTogglePreferenceController.COLUMBUS_ENABLED_URI, false, this, ActivityManager.getCurrentUser());
        }

        public void unregister() {
            ColumbusTogglePreferenceController.this.mContext.getContentResolver().unregisterContentObserver(this);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            ColumbusTogglePreferenceController.this.updateState(this.mPreference);
        }
    }
}
