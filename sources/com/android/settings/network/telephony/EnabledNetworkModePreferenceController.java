package com.android.settings.network.telephony;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.window.R;
import com.android.settings.network.AllowedNetworkTypesListener;
import com.android.settings.network.CarrierConfigCache;
import com.android.settings.network.SubscriptionsChangeListener;
import com.android.settings.network.telephony.EnabledNetworkModePreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
/* loaded from: classes.dex */
public class EnabledNetworkModePreferenceController extends TelephonyBasePreferenceController implements Preference.OnPreferenceChangeListener, LifecycleObserver, SubscriptionsChangeListener.SubscriptionsChangeListenerClient {
    private static final String LOG_TAG = "EnabledNetworkMode";
    private AllowedNetworkTypesListener mAllowedNetworkTypesListener;
    private PreferenceEntriesBuilder mBuilder;
    private int mCallState = 0;
    private CarrierConfigCache mCarrierConfigCache;
    private Preference mPreference;
    private PreferenceScreen mPreferenceScreen;
    private SubscriptionsChangeListener mSubscriptionsListener;
    private PhoneCallStateTelephonyCallback mTelephonyCallback;
    private TelephonyManager mTelephonyManager;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum EnabledNetworks {
        ENABLED_NETWORKS_UNKNOWN,
        ENABLED_NETWORKS_CDMA_CHOICES,
        ENABLED_NETWORKS_CDMA_NO_LTE_CHOICES,
        ENABLED_NETWORKS_CDMA_ONLY_LTE_CHOICES,
        ENABLED_NETWORKS_TDSCDMA_CHOICES,
        ENABLED_NETWORKS_EXCEPT_GSM_LTE_CHOICES,
        ENABLED_NETWORKS_EXCEPT_GSM_4G_CHOICES,
        ENABLED_NETWORKS_EXCEPT_GSM_CHOICES,
        ENABLED_NETWORKS_EXCEPT_LTE_CHOICES,
        ENABLED_NETWORKS_4G_CHOICES,
        ENABLED_NETWORKS_CHOICES,
        PREFERRED_NETWORK_MODE_CHOICES_WORLD_MODE
    }

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

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onAirplaneModeChanged(boolean z) {
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public EnabledNetworkModePreferenceController(Context context, String str) {
        super(context, str);
        this.mSubscriptionsListener = new SubscriptionsChangeListener(context, this);
        this.mCarrierConfigCache = CarrierConfigCache.getInstance(context);
        if (this.mTelephonyCallback == null) {
            this.mTelephonyCallback = new PhoneCallStateTelephonyCallback();
        }
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.network.telephony.TelephonyAvailabilityCallback
    public int getAvailabilityStatus(int i) {
        boolean z = true;
        if (!isCallStateIdle()) {
            return 1;
        }
        PersistableBundle configForSubId = this.mCarrierConfigCache.getConfigForSubId(i);
        if (i == -1 || configForSubId == null || configForSubId.getBoolean("hide_carrier_network_settings_bool") || configForSubId.getBoolean("hide_preferred_network_type_bool") || configForSubId.getBoolean("world_phone_bool")) {
            z = false;
        }
        return z ? 0 : 2;
    }

    protected boolean isCallStateIdle() {
        return this.mCallState == 0;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        this.mSubscriptionsListener.start();
        AllowedNetworkTypesListener allowedNetworkTypesListener = this.mAllowedNetworkTypesListener;
        if (allowedNetworkTypesListener != null && this.mTelephonyCallback != null) {
            allowedNetworkTypesListener.register(this.mContext, this.mSubId);
            this.mTelephonyCallback.register(this.mTelephonyManager, this.mSubId);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        this.mSubscriptionsListener.stop();
        AllowedNetworkTypesListener allowedNetworkTypesListener = this.mAllowedNetworkTypesListener;
        if (allowedNetworkTypesListener != null && this.mTelephonyCallback != null) {
            allowedNetworkTypesListener.unregister(this.mContext, this.mSubId);
            this.mTelephonyCallback.unregister();
        }
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceScreen = preferenceScreen;
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        ListPreference listPreference = (ListPreference) preference;
        this.mBuilder.setPreferenceEntries();
        this.mBuilder.setPreferenceValueAndSummary();
        listPreference.setEntries(this.mBuilder.getEntries());
        listPreference.setEntryValues(this.mBuilder.getEntryValues());
        listPreference.setValue(Integer.toString(this.mBuilder.getSelectedEntryValue()));
        listPreference.setSummary(this.mBuilder.getSummary());
        listPreference.setEnabled(isCallStateIdle());
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        int parseInt = Integer.parseInt((String) obj);
        ListPreference listPreference = (ListPreference) preference;
        if (!this.mTelephonyManager.setPreferredNetworkTypeBitmask(MobileNetworkUtils.getRafFromNetworkType(parseInt))) {
            return false;
        }
        this.mBuilder.setPreferenceValueAndSummary(parseInt);
        listPreference.setValue(Integer.toString(this.mBuilder.getSelectedEntryValue()));
        listPreference.setSummary(this.mBuilder.getSummary());
        return true;
    }

    public void init(Lifecycle lifecycle, int i) {
        this.mSubId = i;
        this.mTelephonyManager = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mSubId);
        this.mBuilder = new PreferenceEntriesBuilder(this.mContext, this.mSubId);
        if (this.mAllowedNetworkTypesListener == null) {
            AllowedNetworkTypesListener allowedNetworkTypesListener = new AllowedNetworkTypesListener(this.mContext.getMainExecutor());
            this.mAllowedNetworkTypesListener = allowedNetworkTypesListener;
            allowedNetworkTypesListener.setAllowedNetworkTypesListener(new AllowedNetworkTypesListener.OnAllowedNetworkTypesListener() { // from class: com.android.settings.network.telephony.EnabledNetworkModePreferenceController$$ExternalSyntheticLambda0
                @Override // com.android.settings.network.AllowedNetworkTypesListener.OnAllowedNetworkTypesListener
                public final void onAllowedNetworkTypesChanged() {
                    EnabledNetworkModePreferenceController.this.lambda$init$0();
                }
            });
        }
        lifecycle.addObserver(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$init$0() {
        this.mBuilder.updateConfig();
        updatePreference();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePreference() {
        PreferenceScreen preferenceScreen = this.mPreferenceScreen;
        if (preferenceScreen != null) {
            displayPreference(preferenceScreen);
        }
        Preference preference = this.mPreference;
        if (preference != null) {
            updateState(preference);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class PreferenceEntriesBuilder {
        private boolean mAllowed5gNetworkType;
        private CarrierConfigCache mCarrierConfigCache;
        private Context mContext;
        private List<String> mEntries = new ArrayList();
        private List<Integer> mEntriesValue = new ArrayList();
        private boolean mIs5gEntryDisplayed;
        private boolean mIsGlobalCdma;
        private int mSelectedEntry;
        private boolean mShow4gForLTE;
        private int mSubId;
        private String mSummary;
        private boolean mSupported5gRadioAccessFamily;
        private TelephonyManager mTelephonyManager;

        private int addNrToLteNetworkType(int i) {
            switch (i) {
                case 8:
                    return 25;
                case 9:
                    return 26;
                case 10:
                    return 27;
                case 11:
                    return 24;
                case 12:
                    return 28;
                case 13:
                case 14:
                case 16:
                case 18:
                case 21:
                default:
                    return i;
                case 15:
                    return 29;
                case 17:
                    return 30;
                case 19:
                    return 31;
                case 20:
                    return 32;
                case 22:
                    return 33;
            }
        }

        private boolean checkSupportedRadioBitmask(long j, long j2) {
            return (j2 & j) > 0;
        }

        private int reduceNrToLteNetworkType(int i) {
            switch (i) {
                case 24:
                    return 11;
                case 25:
                    return 8;
                case 26:
                    return 9;
                case 27:
                    return 10;
                case 28:
                    return 12;
                case 29:
                    return 15;
                case 30:
                    return 17;
                case 31:
                    return 19;
                case 32:
                    return 20;
                case 33:
                    return 22;
                default:
                    return i;
            }
        }

        PreferenceEntriesBuilder(Context context, int i) {
            this.mContext = context;
            this.mSubId = i;
            this.mCarrierConfigCache = CarrierConfigCache.getInstance(context);
            this.mTelephonyManager = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mSubId);
            updateConfig();
        }

        public void updateConfig() {
            this.mTelephonyManager = this.mTelephonyManager.createForSubscriptionId(this.mSubId);
            PersistableBundle configForSubId = this.mCarrierConfigCache.getConfigForSubId(this.mSubId);
            this.mAllowed5gNetworkType = checkSupportedRadioBitmask(this.mTelephonyManager.getAllowedNetworkTypesForReason(2), 524288L);
            this.mSupported5gRadioAccessFamily = checkSupportedRadioBitmask(this.mTelephonyManager.getSupportedRadioAccessFamily(), 524288L);
            boolean z = true;
            this.mIsGlobalCdma = this.mTelephonyManager.isLteCdmaEvdoGsmWcdmaEnabled() && configForSubId != null && configForSubId.getBoolean("show_cdma_choices_bool");
            if (configForSubId == null || !configForSubId.getBoolean("show_4g_for_lte_data_icon_bool")) {
                z = false;
            }
            this.mShow4gForLTE = z;
            Log.d(EnabledNetworkModePreferenceController.LOG_TAG, "PreferenceEntriesBuilder: subId" + this.mSubId + ",Supported5gRadioAccessFamily :" + this.mSupported5gRadioAccessFamily + ",mAllowed5gNetworkType :" + this.mAllowed5gNetworkType + ",IsGlobalCdma :" + this.mIsGlobalCdma + ",Show4gForLTE :" + this.mShow4gForLTE);
        }

        void setPreferenceEntries() {
            clearAllEntries();
            switch (AnonymousClass1.$SwitchMap$com$android$settings$network$telephony$EnabledNetworkModePreferenceController$EnabledNetworks[getEnabledNetworkType().ordinal()]) {
                case 1:
                    int[] array = Stream.of((Object[]) EnabledNetworkModePreferenceController.this.getResourcesForSubId().getStringArray(R.array.enabled_networks_cdma_values)).mapToInt(EnabledNetworkModePreferenceController$PreferenceEntriesBuilder$$ExternalSyntheticLambda3.INSTANCE).toArray();
                    if (array.length >= 4) {
                        add5gEntry(addNrToLteNetworkType(array[0]));
                        addLteEntry(array[0]);
                        add3gEntry(array[1]);
                        add1xEntry(array[2]);
                        addGlobalEntry(array[3]);
                        return;
                    }
                    throw new IllegalArgumentException("ENABLED_NETWORKS_CDMA_CHOICES index error.");
                case 2:
                    int[] array2 = Stream.of((Object[]) EnabledNetworkModePreferenceController.this.getResourcesForSubId().getStringArray(R.array.enabled_networks_cdma_no_lte_values)).mapToInt(EnabledNetworkModePreferenceController$PreferenceEntriesBuilder$$ExternalSyntheticLambda3.INSTANCE).toArray();
                    if (array2.length >= 2) {
                        add3gEntry(array2[0]);
                        add1xEntry(array2[1]);
                        return;
                    }
                    throw new IllegalArgumentException("ENABLED_NETWORKS_CDMA_NO_LTE_CHOICES index error.");
                case 3:
                    int[] array3 = Stream.of((Object[]) EnabledNetworkModePreferenceController.this.getResourcesForSubId().getStringArray(R.array.enabled_networks_cdma_only_lte_values)).mapToInt(EnabledNetworkModePreferenceController$PreferenceEntriesBuilder$$ExternalSyntheticLambda3.INSTANCE).toArray();
                    if (array3.length >= 2) {
                        addLteEntry(array3[0]);
                        addGlobalEntry(array3[1]);
                        return;
                    }
                    throw new IllegalArgumentException("ENABLED_NETWORKS_CDMA_ONLY_LTE_CHOICES index error.");
                case 4:
                    int[] array4 = Stream.of((Object[]) EnabledNetworkModePreferenceController.this.getResourcesForSubId().getStringArray(R.array.enabled_networks_tdscdma_values)).mapToInt(EnabledNetworkModePreferenceController$PreferenceEntriesBuilder$$ExternalSyntheticLambda3.INSTANCE).toArray();
                    if (array4.length >= 3) {
                        add5gEntry(addNrToLteNetworkType(array4[0]));
                        addLteEntry(array4[0]);
                        add3gEntry(array4[1]);
                        add2gEntry(array4[2]);
                        return;
                    }
                    throw new IllegalArgumentException("ENABLED_NETWORKS_TDSCDMA_CHOICES index error.");
                case 5:
                    int[] array5 = Stream.of((Object[]) EnabledNetworkModePreferenceController.this.getResourcesForSubId().getStringArray(R.array.enabled_networks_except_gsm_lte_values)).mapToInt(EnabledNetworkModePreferenceController$PreferenceEntriesBuilder$$ExternalSyntheticLambda3.INSTANCE).toArray();
                    if (array5.length >= 1) {
                        add3gEntry(array5[0]);
                        return;
                    }
                    throw new IllegalArgumentException("ENABLED_NETWORKS_EXCEPT_GSM_LTE_CHOICES index error.");
                case 6:
                    int[] array6 = Stream.of((Object[]) EnabledNetworkModePreferenceController.this.getResourcesForSubId().getStringArray(R.array.enabled_networks_except_gsm_values)).mapToInt(EnabledNetworkModePreferenceController$PreferenceEntriesBuilder$$ExternalSyntheticLambda3.INSTANCE).toArray();
                    if (array6.length >= 2) {
                        add5gEntry(addNrToLteNetworkType(array6[0]));
                        add4gEntry(array6[0]);
                        add3gEntry(array6[1]);
                        return;
                    }
                    throw new IllegalArgumentException("ENABLED_NETWORKS_EXCEPT_GSM_4G_CHOICES index error.");
                case 7:
                    int[] array7 = Stream.of((Object[]) EnabledNetworkModePreferenceController.this.getResourcesForSubId().getStringArray(R.array.enabled_networks_except_gsm_values)).mapToInt(EnabledNetworkModePreferenceController$PreferenceEntriesBuilder$$ExternalSyntheticLambda3.INSTANCE).toArray();
                    if (array7.length >= 2) {
                        add5gEntry(addNrToLteNetworkType(array7[0]));
                        addLteEntry(array7[0]);
                        add3gEntry(array7[1]);
                        return;
                    }
                    throw new IllegalArgumentException("ENABLED_NETWORKS_EXCEPT_GSM_CHOICES index error.");
                case 8:
                    int[] array8 = Stream.of((Object[]) EnabledNetworkModePreferenceController.this.getResourcesForSubId().getStringArray(R.array.enabled_networks_except_lte_values)).mapToInt(EnabledNetworkModePreferenceController$PreferenceEntriesBuilder$$ExternalSyntheticLambda3.INSTANCE).toArray();
                    if (array8.length >= 2) {
                        add3gEntry(array8[0]);
                        add2gEntry(array8[1]);
                        return;
                    }
                    throw new IllegalArgumentException("ENABLED_NETWORKS_EXCEPT_LTE_CHOICES index error.");
                case 9:
                    int[] array9 = Stream.of((Object[]) EnabledNetworkModePreferenceController.this.getResourcesForSubId().getStringArray(R.array.enabled_networks_values)).mapToInt(EnabledNetworkModePreferenceController$PreferenceEntriesBuilder$$ExternalSyntheticLambda3.INSTANCE).toArray();
                    if (array9.length >= 3) {
                        add5gEntry(addNrToLteNetworkType(array9[0]));
                        add4gEntry(array9[0]);
                        add3gEntry(array9[1]);
                        add2gEntry(array9[2]);
                        return;
                    }
                    throw new IllegalArgumentException("ENABLED_NETWORKS_4G_CHOICES index error.");
                case 10:
                    int[] array10 = Stream.of((Object[]) EnabledNetworkModePreferenceController.this.getResourcesForSubId().getStringArray(R.array.enabled_networks_values)).mapToInt(EnabledNetworkModePreferenceController$PreferenceEntriesBuilder$$ExternalSyntheticLambda3.INSTANCE).toArray();
                    if (array10.length >= 3) {
                        add5gEntry(addNrToLteNetworkType(array10[0]));
                        addLteEntry(array10[0]);
                        add3gEntry(array10[1]);
                        add2gEntry(array10[2]);
                        return;
                    }
                    throw new IllegalArgumentException("ENABLED_NETWORKS_CHOICES index error.");
                case 11:
                    int[] array11 = Stream.of((Object[]) EnabledNetworkModePreferenceController.this.getResourcesForSubId().getStringArray(R.array.preferred_network_mode_values_world_mode)).mapToInt(EnabledNetworkModePreferenceController$PreferenceEntriesBuilder$$ExternalSyntheticLambda3.INSTANCE).toArray();
                    if (array11.length >= 3) {
                        addGlobalEntry(array11[0]);
                        addCustomEntry(EnabledNetworkModePreferenceController.this.getResourcesForSubId().getString(R.string.network_world_mode_cdma_lte), array11[1]);
                        addCustomEntry(EnabledNetworkModePreferenceController.this.getResourcesForSubId().getString(R.string.network_world_mode_gsm_lte), array11[2]);
                        return;
                    }
                    throw new IllegalArgumentException("PREFERRED_NETWORK_MODE_CHOICES_WORLD_MODE index error.");
                default:
                    throw new IllegalArgumentException("Not supported enabled network types.");
            }
        }

        private int getPreferredNetworkMode() {
            int networkTypeFromRaf = MobileNetworkUtils.getNetworkTypeFromRaf((int) this.mTelephonyManager.getAllowedNetworkTypesForReason(0));
            if (!showNrList()) {
                Log.d(EnabledNetworkModePreferenceController.LOG_TAG, "Network mode :" + networkTypeFromRaf + " reduce NR");
                networkTypeFromRaf = reduceNrToLteNetworkType(networkTypeFromRaf);
            }
            Log.d(EnabledNetworkModePreferenceController.LOG_TAG, "getPreferredNetworkMode: " + networkTypeFromRaf);
            return networkTypeFromRaf;
        }

        private EnabledNetworks getEnabledNetworkType() {
            EnabledNetworks enabledNetworks = EnabledNetworks.ENABLED_NETWORKS_UNKNOWN;
            int phoneType = this.mTelephonyManager.getPhoneType();
            PersistableBundle configForSubId = this.mCarrierConfigCache.getConfigForSubId(this.mSubId);
            if (phoneType == 2) {
                ContentResolver contentResolver = this.mContext.getContentResolver();
                int i = Settings.Global.getInt(contentResolver, "lte_service_forced" + this.mSubId, 0);
                int preferredNetworkMode = getPreferredNetworkMode();
                if (this.mTelephonyManager.isLteCdmaEvdoGsmWcdmaEnabled()) {
                    if (i != 0) {
                        switch (preferredNetworkMode) {
                            case 4:
                            case 5:
                            case 6:
                                enabledNetworks = EnabledNetworks.ENABLED_NETWORKS_CDMA_NO_LTE_CHOICES;
                                break;
                            case 7:
                            case 8:
                            case 10:
                            case 11:
                                enabledNetworks = EnabledNetworks.ENABLED_NETWORKS_CDMA_ONLY_LTE_CHOICES;
                                break;
                            case 9:
                            default:
                                enabledNetworks = EnabledNetworks.ENABLED_NETWORKS_CDMA_CHOICES;
                                break;
                        }
                    } else {
                        enabledNetworks = EnabledNetworks.ENABLED_NETWORKS_CDMA_CHOICES;
                    }
                }
            } else if (phoneType == 1) {
                enabledNetworks = MobileNetworkUtils.isTdscdmaSupported(this.mContext, this.mSubId) ? EnabledNetworks.ENABLED_NETWORKS_TDSCDMA_CHOICES : (configForSubId == null || configForSubId.getBoolean("prefer_2g_bool") || configForSubId.getBoolean("lte_enabled_bool")) ? (configForSubId == null || configForSubId.getBoolean("prefer_2g_bool")) ? (configForSubId == null || configForSubId.getBoolean("lte_enabled_bool")) ? this.mIsGlobalCdma ? EnabledNetworks.ENABLED_NETWORKS_CDMA_CHOICES : this.mShow4gForLTE ? EnabledNetworks.ENABLED_NETWORKS_4G_CHOICES : EnabledNetworks.ENABLED_NETWORKS_CHOICES : EnabledNetworks.ENABLED_NETWORKS_EXCEPT_LTE_CHOICES : this.mShow4gForLTE ? EnabledNetworks.ENABLED_NETWORKS_EXCEPT_GSM_4G_CHOICES : EnabledNetworks.ENABLED_NETWORKS_EXCEPT_GSM_CHOICES : EnabledNetworks.ENABLED_NETWORKS_EXCEPT_GSM_LTE_CHOICES;
            }
            if (MobileNetworkUtils.isWorldMode(this.mContext, this.mSubId)) {
                enabledNetworks = EnabledNetworks.PREFERRED_NETWORK_MODE_CHOICES_WORLD_MODE;
            }
            Log.d(EnabledNetworkModePreferenceController.LOG_TAG, "enabledNetworkType: " + enabledNetworks);
            return enabledNetworks;
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        void setPreferenceValueAndSummary(int i) {
            setSelectedEntry(i);
            int i2 = R.string.network_4G_pure;
            int i3 = R.string.network_4G;
            int i4 = R.string.network_lte_pure;
            switch (i) {
                case 0:
                case 2:
                case 3:
                    if (!this.mIsGlobalCdma) {
                        setSelectedEntry(0);
                        setSummary(R.string.network_3G);
                        return;
                    }
                    setSelectedEntry(10);
                    setSummary(R.string.network_global);
                    return;
                case 1:
                    if (!this.mIsGlobalCdma) {
                        setSelectedEntry(1);
                        setSummary(R.string.network_2G);
                        return;
                    }
                    setSelectedEntry(10);
                    setSummary(R.string.network_global);
                    return;
                case 4:
                case 6:
                case 7:
                    setSelectedEntry(4);
                    setSummary(R.string.network_3G);
                    return;
                case 5:
                    setSelectedEntry(5);
                    setSummary(R.string.network_1x);
                    return;
                case 8:
                    if (MobileNetworkUtils.isWorldMode(this.mContext, this.mSubId)) {
                        setSummary(R.string.preferred_network_mode_lte_cdma_summary);
                        return;
                    }
                    setSelectedEntry(8);
                    if (!is5gEntryDisplayed()) {
                        i4 = R.string.network_lte;
                    }
                    setSummary(i4);
                    return;
                case 9:
                    if (MobileNetworkUtils.isWorldMode(this.mContext, this.mSubId)) {
                        setSummary(R.string.preferred_network_mode_lte_gsm_umts_summary);
                        return;
                    }
                    break;
                case 10:
                case 15:
                case 17:
                case 19:
                case 20:
                case 22:
                    if (MobileNetworkUtils.isTdscdmaSupported(this.mContext, this.mSubId)) {
                        setSelectedEntry(22);
                        if (!is5gEntryDisplayed()) {
                            i4 = R.string.network_lte;
                        }
                        setSummary(i4);
                        return;
                    }
                    setSelectedEntry(10);
                    if (this.mTelephonyManager.getPhoneType() == 2 || this.mIsGlobalCdma || MobileNetworkUtils.isWorldMode(this.mContext, this.mSubId)) {
                        setSummary(R.string.network_global);
                        return;
                    } else if (is5gEntryDisplayed()) {
                        if (!this.mShow4gForLTE) {
                            i2 = R.string.network_lte_pure;
                        }
                        setSummary(i2);
                        return;
                    } else {
                        if (!this.mShow4gForLTE) {
                            i3 = R.string.network_lte;
                        }
                        setSummary(i3);
                        return;
                    }
                case 11:
                case 12:
                    break;
                case 13:
                    setSelectedEntry(13);
                    setSummary(R.string.network_3G);
                    return;
                case 14:
                case 16:
                case 18:
                    setSelectedEntry(18);
                    setSummary(R.string.network_3G);
                    return;
                case 21:
                    setSelectedEntry(21);
                    setSummary(R.string.network_3G);
                    return;
                case 23:
                case 24:
                case 26:
                case 28:
                    setSelectedEntry(26);
                    setSummary(EnabledNetworkModePreferenceController.this.getResourcesForSubId().getString(R.string.network_5G_recommended));
                    return;
                case 25:
                    setSelectedEntry(25);
                    setSummary(EnabledNetworkModePreferenceController.this.getResourcesForSubId().getString(R.string.network_5G_recommended));
                    return;
                case 27:
                    setSelectedEntry(27);
                    if (this.mTelephonyManager.getPhoneType() == 2 || this.mIsGlobalCdma || MobileNetworkUtils.isWorldMode(this.mContext, this.mSubId)) {
                        setSummary(R.string.network_global);
                        return;
                    } else {
                        setSummary(EnabledNetworkModePreferenceController.this.getResourcesForSubId().getString(R.string.network_5G_recommended));
                        return;
                    }
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                    setSelectedEntry(33);
                    setSummary(EnabledNetworkModePreferenceController.this.getResourcesForSubId().getString(R.string.network_5G_recommended));
                    return;
                default:
                    setSummary(EnabledNetworkModePreferenceController.this.getResourcesForSubId().getString(R.string.mobile_network_mode_error, Integer.valueOf(i)));
                    return;
            }
            if (!this.mIsGlobalCdma) {
                setSelectedEntry(9);
                if (is5gEntryDisplayed()) {
                    if (!this.mShow4gForLTE) {
                        i2 = R.string.network_lte_pure;
                    }
                    setSummary(i2);
                    return;
                }
                if (!this.mShow4gForLTE) {
                    i3 = R.string.network_lte;
                }
                setSummary(i3);
                return;
            }
            setSelectedEntry(10);
            setSummary(R.string.network_global);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setPreferenceValueAndSummary() {
            setPreferenceValueAndSummary(getPreferredNetworkMode());
        }

        private void add5gEntry(int i) {
            boolean z = i >= 23;
            if (!showNrList() || !z) {
                this.mIs5gEntryDisplayed = false;
                Log.d(EnabledNetworkModePreferenceController.LOG_TAG, "Hide 5G option.  supported5GRadioAccessFamily: " + this.mSupported5gRadioAccessFamily + " allowed5GNetworkType: " + this.mAllowed5gNetworkType + " isNRValue: " + z);
                return;
            }
            this.mEntries.add(EnabledNetworkModePreferenceController.this.getResourcesForSubId().getString(R.string.network_5G_recommended));
            this.mEntriesValue.add(Integer.valueOf(i));
            this.mIs5gEntryDisplayed = true;
        }

        private void addGlobalEntry(int i) {
            Log.d(EnabledNetworkModePreferenceController.LOG_TAG, "addGlobalEntry.  supported5GRadioAccessFamily: " + this.mSupported5gRadioAccessFamily + " allowed5GNetworkType: " + this.mAllowed5gNetworkType);
            this.mEntries.add(EnabledNetworkModePreferenceController.this.getResourcesForSubId().getString(R.string.network_global));
            if (showNrList()) {
                i = addNrToLteNetworkType(i);
            }
            this.mEntriesValue.add(Integer.valueOf(i));
        }

        private boolean showNrList() {
            return this.mSupported5gRadioAccessFamily && this.mAllowed5gNetworkType;
        }

        private void addLteEntry(int i) {
            if (showNrList()) {
                this.mEntries.add(EnabledNetworkModePreferenceController.this.getResourcesForSubId().getString(R.string.network_lte_pure));
            } else {
                this.mEntries.add(EnabledNetworkModePreferenceController.this.getResourcesForSubId().getString(R.string.network_lte));
            }
            this.mEntriesValue.add(Integer.valueOf(i));
        }

        private void add4gEntry(int i) {
            if (showNrList()) {
                this.mEntries.add(EnabledNetworkModePreferenceController.this.getResourcesForSubId().getString(R.string.network_4G_pure));
            } else {
                this.mEntries.add(EnabledNetworkModePreferenceController.this.getResourcesForSubId().getString(R.string.network_4G));
            }
            this.mEntriesValue.add(Integer.valueOf(i));
        }

        private void add3gEntry(int i) {
            this.mEntries.add(EnabledNetworkModePreferenceController.this.getResourcesForSubId().getString(R.string.network_3G));
            this.mEntriesValue.add(Integer.valueOf(i));
        }

        private void add2gEntry(int i) {
            this.mEntries.add(EnabledNetworkModePreferenceController.this.getResourcesForSubId().getString(R.string.network_2G));
            this.mEntriesValue.add(Integer.valueOf(i));
        }

        private void add1xEntry(int i) {
            this.mEntries.add(EnabledNetworkModePreferenceController.this.getResourcesForSubId().getString(R.string.network_1x));
            this.mEntriesValue.add(Integer.valueOf(i));
        }

        private void addCustomEntry(String str, int i) {
            this.mEntries.add(str);
            this.mEntriesValue.add(Integer.valueOf(i));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String[] getEntries() {
            return (String[]) this.mEntries.toArray(new String[0]);
        }

        private void clearAllEntries() {
            this.mEntries.clear();
            this.mEntriesValue.clear();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String[] getEntryValues() {
            return (String[]) Arrays.stream((Integer[]) this.mEntriesValue.toArray(new Integer[0])).map(EnabledNetworkModePreferenceController$PreferenceEntriesBuilder$$ExternalSyntheticLambda0.INSTANCE).toArray(EnabledNetworkModePreferenceController$PreferenceEntriesBuilder$$ExternalSyntheticLambda1.INSTANCE);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ String[] lambda$getEntryValues$0(int i) {
            return new String[i];
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getSelectedEntryValue() {
            return this.mSelectedEntry;
        }

        private void setSelectedEntry(final int i) {
            if (this.mEntriesValue.stream().anyMatch(new Predicate() { // from class: com.android.settings.network.telephony.EnabledNetworkModePreferenceController$PreferenceEntriesBuilder$$ExternalSyntheticLambda2
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$setSelectedEntry$1;
                    lambda$setSelectedEntry$1 = EnabledNetworkModePreferenceController.PreferenceEntriesBuilder.lambda$setSelectedEntry$1(i, (Integer) obj);
                    return lambda$setSelectedEntry$1;
                }
            })) {
                this.mSelectedEntry = i;
            } else if (this.mEntriesValue.size() > 0) {
                this.mSelectedEntry = this.mEntriesValue.get(0).intValue();
            } else {
                Log.e(EnabledNetworkModePreferenceController.LOG_TAG, "entriesValue is empty");
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ boolean lambda$setSelectedEntry$1(int i, Integer num) {
            return num.intValue() == i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String getSummary() {
            return this.mSummary;
        }

        private void setSummary(int i) {
            setSummary(EnabledNetworkModePreferenceController.this.getResourcesForSubId().getString(i));
        }

        private void setSummary(String str) {
            this.mSummary = str;
        }

        private boolean is5gEntryDisplayed() {
            return this.mIs5gEntryDisplayed;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.network.telephony.EnabledNetworkModePreferenceController$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$settings$network$telephony$EnabledNetworkModePreferenceController$EnabledNetworks;

        static {
            int[] iArr = new int[EnabledNetworks.values().length];
            $SwitchMap$com$android$settings$network$telephony$EnabledNetworkModePreferenceController$EnabledNetworks = iArr;
            try {
                iArr[EnabledNetworks.ENABLED_NETWORKS_CDMA_CHOICES.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$settings$network$telephony$EnabledNetworkModePreferenceController$EnabledNetworks[EnabledNetworks.ENABLED_NETWORKS_CDMA_NO_LTE_CHOICES.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$settings$network$telephony$EnabledNetworkModePreferenceController$EnabledNetworks[EnabledNetworks.ENABLED_NETWORKS_CDMA_ONLY_LTE_CHOICES.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$settings$network$telephony$EnabledNetworkModePreferenceController$EnabledNetworks[EnabledNetworks.ENABLED_NETWORKS_TDSCDMA_CHOICES.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$settings$network$telephony$EnabledNetworkModePreferenceController$EnabledNetworks[EnabledNetworks.ENABLED_NETWORKS_EXCEPT_GSM_LTE_CHOICES.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$settings$network$telephony$EnabledNetworkModePreferenceController$EnabledNetworks[EnabledNetworks.ENABLED_NETWORKS_EXCEPT_GSM_4G_CHOICES.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$android$settings$network$telephony$EnabledNetworkModePreferenceController$EnabledNetworks[EnabledNetworks.ENABLED_NETWORKS_EXCEPT_GSM_CHOICES.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$android$settings$network$telephony$EnabledNetworkModePreferenceController$EnabledNetworks[EnabledNetworks.ENABLED_NETWORKS_EXCEPT_LTE_CHOICES.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$android$settings$network$telephony$EnabledNetworkModePreferenceController$EnabledNetworks[EnabledNetworks.ENABLED_NETWORKS_4G_CHOICES.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$com$android$settings$network$telephony$EnabledNetworkModePreferenceController$EnabledNetworks[EnabledNetworks.ENABLED_NETWORKS_CHOICES.ordinal()] = 10;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$com$android$settings$network$telephony$EnabledNetworkModePreferenceController$EnabledNetworks[EnabledNetworks.PREFERRED_NETWORK_MODE_CHOICES_WORLD_MODE.ordinal()] = 11;
            } catch (NoSuchFieldError unused11) {
            }
        }
    }

    /* loaded from: classes.dex */
    class PhoneCallStateTelephonyCallback extends TelephonyCallback implements TelephonyCallback.CallStateListener {
        private TelephonyManager mTelephonyManager;

        PhoneCallStateTelephonyCallback() {
        }

        public void onCallStateChanged(int i) {
            Log.d(EnabledNetworkModePreferenceController.LOG_TAG, "onCallStateChanged:" + i);
            EnabledNetworkModePreferenceController.this.mCallState = i;
            EnabledNetworkModePreferenceController.this.mBuilder.updateConfig();
            EnabledNetworkModePreferenceController.this.updatePreference();
        }

        public void register(TelephonyManager telephonyManager, int i) {
            this.mTelephonyManager = telephonyManager;
            EnabledNetworkModePreferenceController.this.mCallState = telephonyManager.getCallState(i);
            this.mTelephonyManager.registerTelephonyCallback(((AbstractPreferenceController) EnabledNetworkModePreferenceController.this).mContext.getMainExecutor(), EnabledNetworkModePreferenceController.this.mTelephonyCallback);
        }

        public void unregister() {
            EnabledNetworkModePreferenceController.this.mCallState = 0;
            TelephonyManager telephonyManager = this.mTelephonyManager;
            if (telephonyManager != null) {
                telephonyManager.unregisterTelephonyCallback(this);
            }
        }
    }

    PhoneCallStateTelephonyCallback getTelephonyCallback() {
        return this.mTelephonyCallback;
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onSubscriptionsChanged() {
        this.mBuilder.updateConfig();
    }
}
