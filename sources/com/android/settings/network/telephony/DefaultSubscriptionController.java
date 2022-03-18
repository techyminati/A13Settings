package com.android.settings.network.telephony;

import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.window.R;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.network.SubscriptionsChangeListener;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public abstract class DefaultSubscriptionController extends TelephonyBasePreferenceController implements LifecycleObserver, Preference.OnPreferenceChangeListener, SubscriptionsChangeListener.SubscriptionsChangeListenerClient {
    private static final String EMERGENCY_ACCOUNT_HANDLE_ID = "E";
    private static final ComponentName PSTN_CONNECTION_SERVICE_COMPONENT = new ComponentName("com.android.phone", "com.android.services.telephony.TelephonyConnectionService");
    private static final String TAG = "DefaultSubController";
    protected SubscriptionsChangeListener mChangeListener;
    private boolean mIsRtlMode;
    protected SubscriptionManager mManager;
    protected ListPreference mPreference;
    protected TelecomManager mTelecomManager;

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.network.telephony.TelephonyAvailabilityCallback
    public int getAvailabilityStatus(int i) {
        return 0;
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    protected abstract int getDefaultSubscriptionId();

    protected abstract SubscriptionInfo getDefaultSubscriptionInfo();

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

    protected boolean isAskEverytimeSupported() {
        return true;
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

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onAirplaneModeChanged(boolean z) {
    }

    protected abstract void setDefaultSubscription(int i);

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public DefaultSubscriptionController(Context context, String str) {
        super(context, str);
        this.mManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        this.mChangeListener = new SubscriptionsChangeListener(context, this);
        this.mIsRtlMode = context.getResources().getConfiguration().getLayoutDirection() != 1 ? false : true;
    }

    public void init(Lifecycle lifecycle) {
        lifecycle.addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        this.mChangeListener.start();
        updateEntries();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        this.mChangeListener.stop();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (ListPreference) preferenceScreen.findPreference(getPreferenceKey());
        updateEntries();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        PhoneAccountHandle defaultCallingAccountHandle = getDefaultCallingAccountHandle();
        if (defaultCallingAccountHandle != null && !isCallingAccountBindToSubscription(defaultCallingAccountHandle)) {
            return getLabelFromCallingAccount(defaultCallingAccountHandle);
        }
        SubscriptionInfo defaultSubscriptionInfo = getDefaultSubscriptionInfo();
        if (defaultSubscriptionInfo != null) {
            return SubscriptionUtil.getUniqueSubscriptionDisplayName(defaultSubscriptionInfo, this.mContext);
        }
        return isAskEverytimeSupported() ? this.mContext.getString(R.string.calls_and_sms_ask_every_time) : "";
    }

    private void updateEntries() {
        if (this.mPreference != null) {
            if (!isAvailable()) {
                this.mPreference.setVisible(false);
                return;
            }
            this.mPreference.setVisible(true);
            this.mPreference.setOnPreferenceChangeListener(this);
            List<SubscriptionInfo> activeSubscriptions = SubscriptionUtil.getActiveSubscriptions(this.mManager);
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            if (activeSubscriptions.size() == 1) {
                this.mPreference.setEnabled(false);
                this.mPreference.setSummary(SubscriptionUtil.getUniqueSubscriptionDisplayName(activeSubscriptions.get(0), this.mContext));
                return;
            }
            int defaultSubscriptionId = getDefaultSubscriptionId();
            boolean z = false;
            for (SubscriptionInfo subscriptionInfo : activeSubscriptions) {
                if (!subscriptionInfo.isOpportunistic()) {
                    arrayList.add(SubscriptionUtil.getUniqueSubscriptionDisplayName(subscriptionInfo, this.mContext));
                    int subscriptionId = subscriptionInfo.getSubscriptionId();
                    arrayList2.add(Integer.toString(subscriptionId));
                    if (subscriptionId == defaultSubscriptionId) {
                        z = true;
                    }
                }
            }
            if (isAskEverytimeSupported()) {
                arrayList.add(this.mContext.getString(R.string.calls_and_sms_ask_every_time));
                arrayList2.add(Integer.toString(-1));
            }
            this.mPreference.setEnabled(true);
            this.mPreference.setEntries((CharSequence[]) arrayList.toArray(new CharSequence[0]));
            this.mPreference.setEntryValues((CharSequence[]) arrayList2.toArray(new CharSequence[0]));
            if (z) {
                this.mPreference.setValue(Integer.toString(defaultSubscriptionId));
            } else {
                this.mPreference.setValue(Integer.toString(-1));
            }
        }
    }

    public PhoneAccountHandle getDefaultCallingAccountHandle() {
        PhoneAccountHandle userSelectedOutgoingPhoneAccount = getTelecomManager().getUserSelectedOutgoingPhoneAccount();
        if (userSelectedOutgoingPhoneAccount == null) {
            return null;
        }
        List<PhoneAccountHandle> callCapablePhoneAccounts = getTelecomManager().getCallCapablePhoneAccounts(false);
        if (userSelectedOutgoingPhoneAccount.equals(new PhoneAccountHandle(PSTN_CONNECTION_SERVICE_COMPONENT, EMERGENCY_ACCOUNT_HANDLE_ID))) {
            return null;
        }
        for (PhoneAccountHandle phoneAccountHandle : callCapablePhoneAccounts) {
            if (userSelectedOutgoingPhoneAccount.equals(phoneAccountHandle)) {
                return userSelectedOutgoingPhoneAccount;
            }
        }
        return null;
    }

    @VisibleForTesting
    TelecomManager getTelecomManager() {
        if (this.mTelecomManager == null) {
            this.mTelecomManager = (TelecomManager) this.mContext.getSystemService(TelecomManager.class);
        }
        return this.mTelecomManager;
    }

    @VisibleForTesting
    PhoneAccount getPhoneAccount(PhoneAccountHandle phoneAccountHandle) {
        return getTelecomManager().getPhoneAccount(phoneAccountHandle);
    }

    public boolean isCallingAccountBindToSubscription(PhoneAccountHandle phoneAccountHandle) {
        PhoneAccount phoneAccount = getPhoneAccount(phoneAccountHandle);
        if (phoneAccount == null) {
            return false;
        }
        return phoneAccount.hasCapabilities(4);
    }

    public CharSequence getLabelFromCallingAccount(PhoneAccountHandle phoneAccountHandle) {
        PhoneAccount phoneAccount = getPhoneAccount(phoneAccountHandle);
        CharSequence label = phoneAccount != null ? phoneAccount.getLabel() : null;
        if (label != null) {
            label = this.mContext.getPackageManager().getUserBadgedLabel(label, phoneAccountHandle.getUserHandle());
        }
        return label != null ? label : "";
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        setDefaultSubscription(Integer.parseInt((String) obj));
        refreshSummary(this.mPreference);
        return true;
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onSubscriptionsChanged() {
        if (this.mPreference != null) {
            updateEntries();
            refreshSummary(this.mPreference);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isRtlMode() {
        return this.mIsRtlMode;
    }
}
