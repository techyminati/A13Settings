package com.google.android.settings.network;

import android.content.Context;
import android.content.IntentFilter;
import androidx.window.R;
/* loaded from: classes2.dex */
public class ConnectivityHelperDeviceToDevicePreferenceController extends ConnectivityHelperBasePreferenceController {
    private static final String LOG_TAG = "ch_d2d";

    @Override // com.google.android.settings.network.ConnectivityHelperBasePreferenceController, com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.google.android.settings.network.ConnectivityHelperBasePreferenceController, com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.google.android.settings.network.ConnectivityHelperBasePreferenceController, com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.google.android.settings.network.ConnectivityHelperBasePreferenceController
    public String getKeyName() {
        return "d2d_notifications";
    }

    @Override // com.google.android.settings.network.ConnectivityHelperBasePreferenceController
    public String getLogTag() {
        return LOG_TAG;
    }

    @Override // com.google.android.settings.network.ConnectivityHelperBasePreferenceController, com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.google.android.settings.network.ConnectivityHelperBasePreferenceController, com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.google.android.settings.network.ConnectivityHelperBasePreferenceController, com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public ConnectivityHelperDeviceToDevicePreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.google.android.settings.network.ConnectivityHelperBasePreferenceController
    public boolean getDefaultValue() {
        return this.mContext.getResources().getBoolean(R.bool.config_connectivity_helper_d2d_default_value);
    }

    @Override // com.google.android.settings.network.ConnectivityHelperBasePreferenceController
    public boolean getDeviceSupport() {
        return this.mContext.getResources().getBoolean(R.bool.config_show_connectivity_helper_d2d);
    }
}
