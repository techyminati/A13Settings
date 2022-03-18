package com.android.settings.network.telephony;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.provider.Telephony;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.network.CarrierConfigCache;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
/* loaded from: classes.dex */
public class ApnPreferenceController extends TelephonyBasePreferenceController implements LifecycleObserver, OnStart, OnStop {
    CarrierConfigCache mCarrierConfigCache;
    private DpcApnEnforcedObserver mDpcApnEnforcedObserver = new DpcApnEnforcedObserver(new Handler(Looper.getMainLooper()));
    private Preference mPreference;

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public ApnPreferenceController(Context context, String str) {
        super(context, str);
        this.mCarrierConfigCache = CarrierConfigCache.getInstance(context);
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.network.telephony.TelephonyAvailabilityCallback
    public int getAvailabilityStatus(int i) {
        PersistableBundle configForSubId = this.mCarrierConfigCache.getConfigForSubId(i);
        boolean z = true;
        boolean z2 = MobileNetworkUtils.isCdmaOptions(this.mContext, i) && configForSubId != null && configForSubId.getBoolean("show_apn_setting_cdma_bool");
        boolean z3 = MobileNetworkUtils.isGsmOptions(this.mContext, i) && configForSubId != null && configForSubId.getBoolean("apn_expand_bool");
        if (configForSubId != null && !configForSubId.getBoolean("hide_carrier_network_settings_bool")) {
            z = false;
        }
        return (z || (!z2 && !z3)) ? 2 : 0;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mDpcApnEnforcedObserver.register(this.mContext);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mDpcApnEnforcedObserver.unRegister(this.mContext);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        Preference preference2 = this.mPreference;
        if (preference2 != null) {
            ((RestrictedPreference) preference2).setDisabledByAdmin(MobileNetworkUtils.isDpcApnEnforced(this.mContext) ? RestrictedLockUtilsInternal.getDeviceOwner(this.mContext) : null);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!getPreferenceKey().equals(preference.getKey())) {
            return false;
        }
        Intent intent = new Intent("android.settings.APN_SETTINGS");
        intent.putExtra(":settings:show_fragment_as_subsetting", true);
        intent.putExtra("sub_id", this.mSubId);
        this.mContext.startActivity(intent);
        return true;
    }

    public void init(int i) {
        this.mSubId = i;
    }

    void setPreference(Preference preference) {
        this.mPreference = preference;
    }

    /* loaded from: classes.dex */
    private class DpcApnEnforcedObserver extends ContentObserver {
        DpcApnEnforcedObserver(Handler handler) {
            super(handler);
        }

        public void register(Context context) {
            context.getContentResolver().registerContentObserver(Telephony.Carriers.ENFORCE_MANAGED_URI, false, this);
        }

        public void unRegister(Context context) {
            context.getContentResolver().unregisterContentObserver(this);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            ApnPreferenceController apnPreferenceController = ApnPreferenceController.this;
            apnPreferenceController.updateState(apnPreferenceController.mPreference);
        }
    }
}
