package com.google.android.settings.network;

import android.content.ContentValues;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.preference.PreferenceScreen;
import com.android.settings.network.telephony.TelephonyTogglePreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
/* loaded from: classes2.dex */
public abstract class ConnectivityHelperBasePreferenceController extends TelephonyTogglePreferenceController implements LifecycleObserver, OnStart {
    private ConnectivityHelperSettingsContract mConnectivityHelperSettingsContract = getConnectivityHelperSettingsContract();
    private final boolean mDefaultValue;
    private final boolean mDeviceSupport;
    private boolean mIsCarrierAllowed;
    private boolean mIsChecked;
    private final String mLogTag;
    private PreferenceScreen mScreen;

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public abstract boolean getDefaultValue();

    public abstract boolean getDeviceSupport();

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public abstract String getKeyName();

    public abstract String getLogTag();

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public ConnectivityHelperBasePreferenceController(Context context, String str) {
        super(context, str);
        String logTag = getLogTag();
        this.mLogTag = logTag;
        boolean defaultValue = getDefaultValue();
        this.mDefaultValue = defaultValue;
        this.mIsChecked = defaultValue;
        boolean deviceSupport = getDeviceSupport();
        this.mDeviceSupport = deviceSupport;
        Log.d(logTag, "isDeviceSupport: " + deviceSupport + "mIsChecked: " + this.mIsChecked);
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.network.telephony.TelephonyAvailabilityCallback
    public int getAvailabilityStatus(int i) {
        return (!this.mDeviceSupport || !this.mIsCarrierAllowed) ? 2 : 0;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        Executors.newFixedThreadPool(1).execute(new Runnable() { // from class: com.google.android.settings.network.ConnectivityHelperBasePreferenceController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ConnectivityHelperBasePreferenceController.this.lambda$onStart$1();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onStart$1() {
        updateStateFromContentProvider();
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.google.android.settings.network.ConnectivityHelperBasePreferenceController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ConnectivityHelperBasePreferenceController.this.lambda$onStart$0();
            }
        });
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mScreen = preferenceScreen;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        String str = this.mLogTag;
        Log.d(str, "setChecked:" + z);
        if (this.mConnectivityHelperSettingsContract.setSettingsValue(z)) {
            return true;
        }
        Log.d(this.mLogTag, "setChecked fails");
        return false;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return this.mIsChecked;
    }

    ConnectivityHelperSettingsContract getConnectivityHelperSettingsContract() {
        return new ConnectivityHelperSettingsContract(this.mContext, getKeyName(), this.mDefaultValue);
    }

    void updateStateFromContentProvider() {
        this.mConnectivityHelperSettingsContract.queryStateFromContentProvider();
        this.mIsCarrierAllowed = this.mConnectivityHelperSettingsContract.isFeatureEnabled();
        this.mIsChecked = this.mConnectivityHelperSettingsContract.getSettingsValue();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: refreshPreference */
    public void lambda$onStart$0() {
        if (!ProcessLifecycleOwner.get().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            String str = this.mLogTag;
            Log.d(str, "stop refresh. CurrentState:" + ProcessLifecycleOwner.get().getLifecycle().getCurrentState().name());
            return;
        }
        PreferenceScreen preferenceScreen = this.mScreen;
        if (preferenceScreen != null) {
            displayPreference(preferenceScreen);
        }
    }

    /* loaded from: classes2.dex */
    public static class ConnectivityHelperSettingsContract {
        private final Context mContext;
        private boolean mIsFeatureEnabled;
        private boolean mIsFeatureState;
        private final String mLog;
        private final String mUiKey;
        private final Uri mSettingsUri = Uri.parse("content://com.google.android.connectivitymonitor.connectivityhelperprovider/settings");
        private ArrayList<String> mSettingsKeys = new ArrayList<>(Arrays.asList("feature_control", "on_device_notifications", "d2d_notifications"));

        ConnectivityHelperSettingsContract(Context context, String str, boolean z) {
            this.mContext = context;
            this.mUiKey = str;
            this.mIsFeatureState = z;
            this.mLog = "ch_" + str;
        }

        public boolean setSettingsValue(boolean z) {
            if (!isValidKey(this.mUiKey)) {
                Log.d(this.mLog, "the column name is wrong");
                return false;
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put(this.mUiKey, z ? "on" : "off");
            try {
            } catch (IllegalArgumentException e) {
                String str = this.mLog;
                Log.d(str, "setSettingsValue get exception: " + e);
            }
            return this.mContext.getContentResolver().update(this.mSettingsUri, contentValues, null, null) > 0;
        }

        public boolean getSettingsValue() {
            if (isValidKey(this.mUiKey)) {
                return this.mIsFeatureState;
            }
            Log.d(this.mLog, "the key is wrong");
            return false;
        }

        public boolean isFeatureEnabled() {
            return this.mIsFeatureEnabled;
        }

        public void queryStateFromContentProvider() {
            Log.d(this.mLog, "query the state from ContentProvider");
            Cursor query = this.mContext.getContentResolver().query(this.mSettingsUri, null, null, null, null);
            if (query != null) {
                while (query.moveToNext()) {
                    if (query.getColumnNames() != null) {
                        String string = query.getString(query.getColumnIndex("KEY"));
                        String string2 = query.getString(query.getColumnIndex("VALUE"));
                        if (!TextUtils.isEmpty(string) && !TextUtils.isEmpty(string2)) {
                            if (string.equals("feature_control")) {
                                this.mIsFeatureEnabled = string2.equals("on");
                                String str = this.mLog;
                                Log.d(str, "isFeatureEnabled: the state:" + this.mIsFeatureEnabled);
                            } else if (string.equals(this.mUiKey)) {
                                this.mIsFeatureState = string2.equals("on");
                                String str2 = this.mLog;
                                Log.d(str2, "getSettingsValue: the state:" + this.mIsFeatureState);
                            }
                        }
                    }
                }
                query.close();
            }
        }

        private boolean isValidKey(String str) {
            return this.mSettingsKeys.contains(str);
        }
    }
}
