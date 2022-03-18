package com.android.settings.wifi.calling;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.telephony.ims.ImsMmTelManager;
import android.telephony.ims.ProvisioningManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.window.R;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.network.ims.WifiCallingQueryImsState;
import com.android.settings.widget.SettingsMainSwitchBar;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
import java.util.List;
/* loaded from: classes.dex */
public class WifiCallingSettingsForSub extends SettingsPreferenceFragment implements OnMainSwitchChangeListener, Preference.OnPreferenceChangeListener {
    @VisibleForTesting
    static final int REQUEST_CHECK_WFC_DISCLAIMER = 2;
    @VisibleForTesting
    static final int REQUEST_CHECK_WFC_EMERGENCY_ADDRESS = 1;
    private ListWithEntrySummaryPreference mButtonWfcMode;
    private ListWithEntrySummaryPreference mButtonWfcRoamingMode;
    private ImsMmTelManager mImsMmTelManager;
    private IntentFilter mIntentFilter;
    private ProvisioningManager mProvisioningManager;
    private SettingsMainSwitchBar mSwitchBar;
    private TelephonyManager mTelephonyManager;
    private Preference mUpdateAddress;
    private boolean mValidListener = false;
    private boolean mEditableWfcMode = true;
    private boolean mEditableWfcRoamingMode = true;
    private boolean mUseWfcHomeModeForRoaming = false;
    private int mSubId = -1;
    private final PhoneTelephonyCallback mTelephonyCallback = new PhoneTelephonyCallback();
    private final Preference.OnPreferenceClickListener mUpdateAddressListener = new Preference.OnPreferenceClickListener() { // from class: com.android.settings.wifi.calling.WifiCallingSettingsForSub$$ExternalSyntheticLambda0
        @Override // androidx.preference.Preference.OnPreferenceClickListener
        public final boolean onPreferenceClick(Preference preference) {
            boolean lambda$new$0;
            lambda$new$0 = WifiCallingSettingsForSub.this.lambda$new$0(preference);
            return lambda$new$0;
        }
    };
    private final ProvisioningManager.Callback mProvisioningCallback = new ProvisioningManager.Callback() { // from class: com.android.settings.wifi.calling.WifiCallingSettingsForSub.1
        public void onProvisioningIntChanged(int i, int i2) {
            if (i == 28 || i == 10) {
                WifiCallingSettingsForSub.this.updateBody();
            }
        }
    };
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() { // from class: com.android.settings.wifi.calling.WifiCallingSettingsForSub.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.telephony.ims.action.WFC_IMS_REGISTRATION_ERROR")) {
                setResultCode(0);
                WifiCallingSettingsForSub.this.showAlert(intent);
            }
        }
    };

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return 0;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1230;
    }

    /* loaded from: classes.dex */
    private class PhoneTelephonyCallback extends TelephonyCallback implements TelephonyCallback.CallStateListener {
        private PhoneTelephonyCallback() {
        }

        public void onCallStateChanged(int i) {
            boolean z;
            boolean z2;
            PersistableBundle configForSubId;
            SettingsActivity settingsActivity = (SettingsActivity) WifiCallingSettingsForSub.this.getActivity();
            WifiCallingSettingsForSub wifiCallingSettingsForSub = WifiCallingSettingsForSub.this;
            boolean isAllowUserControl = wifiCallingSettingsForSub.queryImsState(wifiCallingSettingsForSub.mSubId).isAllowUserControl();
            boolean z3 = true;
            boolean z4 = WifiCallingSettingsForSub.this.mSwitchBar.isChecked() && isAllowUserControl;
            WifiCallingSettingsForSub wifiCallingSettingsForSub2 = WifiCallingSettingsForSub.this;
            boolean z5 = wifiCallingSettingsForSub2.getTelephonyManagerForSub(wifiCallingSettingsForSub2.mSubId).getCallState() == 0;
            WifiCallingSettingsForSub.this.mSwitchBar.setEnabled(z5 && isAllowUserControl);
            CarrierConfigManager carrierConfigManager = (CarrierConfigManager) settingsActivity.getSystemService("carrier_config");
            if (carrierConfigManager == null || (configForSubId = carrierConfigManager.getConfigForSubId(WifiCallingSettingsForSub.this.mSubId)) == null) {
                z2 = true;
                z = false;
            } else {
                z2 = configForSubId.getBoolean("editable_wfc_mode_bool");
                z = configForSubId.getBoolean("editable_wfc_roaming_mode_bool");
            }
            Preference findPreference = WifiCallingSettingsForSub.this.getPreferenceScreen().findPreference("wifi_calling_mode");
            if (findPreference != null) {
                findPreference.setEnabled(z4 && z2 && z5);
            }
            Preference findPreference2 = WifiCallingSettingsForSub.this.getPreferenceScreen().findPreference("wifi_calling_roaming_mode");
            if (findPreference2 != null) {
                if (!z4 || !z || !z5) {
                    z3 = false;
                }
                findPreference2.setEnabled(z3);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$0(Preference preference) {
        Intent carrierActivityIntent = getCarrierActivityIntent();
        if (carrierActivityIntent != null) {
            carrierActivityIntent.putExtra("EXTRA_LAUNCH_CARRIER_APP", 1);
            startActivity(carrierActivityIntent);
        }
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        SettingsMainSwitchBar settingsMainSwitchBar = (SettingsMainSwitchBar) getView().findViewById(R.id.switch_bar);
        this.mSwitchBar = settingsMainSwitchBar;
        settingsMainSwitchBar.show();
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mSwitchBar.hide();
    }

    @VisibleForTesting
    void showAlert(Intent intent) {
        FragmentActivity activity = getActivity();
        CharSequence charSequenceExtra = intent.getCharSequenceExtra("android.telephony.ims.extra.WFC_REGISTRATION_FAILURE_TITLE");
        CharSequence charSequenceExtra2 = intent.getCharSequenceExtra("android.telephony.ims.extra.WFC_REGISTRATION_FAILURE_MESSAGE");
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(charSequenceExtra2).setTitle(charSequenceExtra).setIcon(17301543).setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
        builder.create().show();
    }

    @VisibleForTesting
    TelephonyManager getTelephonyManagerForSub(int i) {
        if (this.mTelephonyManager == null) {
            this.mTelephonyManager = (TelephonyManager) getContext().getSystemService(TelephonyManager.class);
        }
        return this.mTelephonyManager.createForSubscriptionId(i);
    }

    @VisibleForTesting
    WifiCallingQueryImsState queryImsState(int i) {
        return new WifiCallingQueryImsState(getContext(), i);
    }

    @VisibleForTesting
    ProvisioningManager getImsProvisioningManager() {
        if (!SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            return null;
        }
        return ProvisioningManager.createForSubscriptionId(this.mSubId);
    }

    @VisibleForTesting
    ImsMmTelManager getImsMmTelManager() {
        if (!SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            return null;
        }
        return ImsMmTelManager.createForSubscriptionId(this.mSubId);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.wifi_calling_settings);
        if (getArguments() != null && getArguments().containsKey("subId")) {
            this.mSubId = getArguments().getInt("subId");
        } else if (bundle != null) {
            this.mSubId = bundle.getInt("subId", -1);
        }
        this.mProvisioningManager = getImsProvisioningManager();
        this.mImsMmTelManager = getImsMmTelManager();
        ListWithEntrySummaryPreference listWithEntrySummaryPreference = (ListWithEntrySummaryPreference) findPreference("wifi_calling_mode");
        this.mButtonWfcMode = listWithEntrySummaryPreference;
        listWithEntrySummaryPreference.setOnPreferenceChangeListener(this);
        ListWithEntrySummaryPreference listWithEntrySummaryPreference2 = (ListWithEntrySummaryPreference) findPreference("wifi_calling_roaming_mode");
        this.mButtonWfcRoamingMode = listWithEntrySummaryPreference2;
        listWithEntrySummaryPreference2.setOnPreferenceChangeListener(this);
        Preference findPreference = findPreference("emergency_address_key");
        this.mUpdateAddress = findPreference;
        findPreference.setOnPreferenceClickListener(this.mUpdateAddressListener);
        IntentFilter intentFilter = new IntentFilter();
        this.mIntentFilter = intentFilter;
        intentFilter.addAction("android.telephony.ims.action.WFC_IMS_REGISTRATION_ERROR");
        updateDescriptionForOptions(List.of(this.mButtonWfcMode, this.mButtonWfcRoamingMode, this.mUpdateAddress));
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putInt("subId", this.mSubId);
        super.onSaveInstanceState(bundle);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.wifi_calling_settings_preferences, viewGroup, false);
        ViewGroup viewGroup2 = (ViewGroup) inflate.findViewById(16908305);
        Utils.prepareCustomPreferencesList(viewGroup, inflate, viewGroup2, false);
        viewGroup2.addView(super.onCreateView(layoutInflater, viewGroup2, bundle));
        return inflate;
    }

    @VisibleForTesting
    boolean isWfcProvisionedOnDevice() {
        return queryImsState(this.mSubId).isWifiCallingProvisioned();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBody() {
        boolean z;
        PersistableBundle configForSubId;
        if (!isWfcProvisionedOnDevice()) {
            finish();
            return;
        }
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) getSystemService("carrier_config");
        boolean z2 = false;
        if (carrierConfigManager == null || (configForSubId = carrierConfigManager.getConfigForSubId(this.mSubId)) == null) {
            z = true;
        } else {
            this.mEditableWfcMode = configForSubId.getBoolean("editable_wfc_mode_bool");
            this.mEditableWfcRoamingMode = configForSubId.getBoolean("editable_wfc_roaming_mode_bool");
            this.mUseWfcHomeModeForRoaming = configForSubId.getBoolean("use_wfc_home_network_mode_in_roaming_network_bool", false);
            z = configForSubId.getBoolean("carrier_wfc_supports_wifi_only_bool", true);
        }
        Resources resourcesForSubId = getResourcesForSubId();
        this.mButtonWfcMode.setTitle(resourcesForSubId.getString(R.string.wifi_calling_mode_title));
        this.mButtonWfcMode.setDialogTitle(resourcesForSubId.getString(R.string.wifi_calling_mode_dialog_title));
        this.mButtonWfcRoamingMode.setTitle(resourcesForSubId.getString(R.string.wifi_calling_roaming_mode_title));
        this.mButtonWfcRoamingMode.setDialogTitle(resourcesForSubId.getString(R.string.wifi_calling_roaming_mode_dialog_title));
        if (z) {
            this.mButtonWfcMode.setEntries(resourcesForSubId.getStringArray(R.array.wifi_calling_mode_choices));
            this.mButtonWfcMode.setEntryValues(resourcesForSubId.getStringArray(R.array.wifi_calling_mode_values));
            this.mButtonWfcMode.setEntrySummaries(resourcesForSubId.getStringArray(R.array.wifi_calling_mode_summaries));
            this.mButtonWfcRoamingMode.setEntries(resourcesForSubId.getStringArray(R.array.wifi_calling_mode_choices_v2));
            this.mButtonWfcRoamingMode.setEntryValues(resourcesForSubId.getStringArray(R.array.wifi_calling_mode_values));
            this.mButtonWfcRoamingMode.setEntrySummaries(resourcesForSubId.getStringArray(R.array.wifi_calling_mode_summaries));
        } else {
            this.mButtonWfcMode.setEntries(resourcesForSubId.getStringArray(R.array.wifi_calling_mode_choices_without_wifi_only));
            this.mButtonWfcMode.setEntryValues(resourcesForSubId.getStringArray(R.array.wifi_calling_mode_values_without_wifi_only));
            this.mButtonWfcMode.setEntrySummaries(resourcesForSubId.getStringArray(R.array.wifi_calling_mode_summaries_without_wifi_only));
            this.mButtonWfcRoamingMode.setEntries(resourcesForSubId.getStringArray(R.array.wifi_calling_mode_choices_v2_without_wifi_only));
            this.mButtonWfcRoamingMode.setEntryValues(resourcesForSubId.getStringArray(R.array.wifi_calling_mode_values_without_wifi_only));
            this.mButtonWfcRoamingMode.setEntrySummaries(resourcesForSubId.getStringArray(R.array.wifi_calling_mode_summaries_without_wifi_only));
        }
        WifiCallingQueryImsState queryImsState = queryImsState(this.mSubId);
        if (queryImsState.isEnabledByUser() && queryImsState.isAllowUserControl()) {
            z2 = true;
        }
        this.mSwitchBar.setChecked(z2);
        int voWiFiModeSetting = this.mImsMmTelManager.getVoWiFiModeSetting();
        int voWiFiRoamingModeSetting = this.mImsMmTelManager.getVoWiFiRoamingModeSetting();
        this.mButtonWfcMode.setValue(Integer.toString(voWiFiModeSetting));
        this.mButtonWfcRoamingMode.setValue(Integer.toString(voWiFiRoamingModeSetting));
        updateButtonWfcMode(z2, voWiFiModeSetting, voWiFiRoamingModeSetting);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateBody();
        FragmentActivity activity = getActivity();
        if (queryImsState(this.mSubId).isWifiCallingSupported()) {
            getTelephonyManagerForSub(this.mSubId).registerTelephonyCallback(activity.getMainExecutor(), this.mTelephonyCallback);
            this.mSwitchBar.addOnSwitchChangeListener(this);
            this.mValidListener = true;
        }
        activity.registerReceiver(this.mIntentReceiver, this.mIntentFilter, 2);
        Intent intent = getActivity().getIntent();
        if (intent.getBooleanExtra("alertShow", false)) {
            showAlert(intent);
        }
        registerProvisioningChangedCallback();
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        FragmentActivity activity = getActivity();
        if (this.mValidListener) {
            this.mValidListener = false;
            getTelephonyManagerForSub(this.mSubId).unregisterTelephonyCallback(this.mTelephonyCallback);
            this.mSwitchBar.removeOnSwitchChangeListener(this);
        }
        activity.unregisterReceiver(this.mIntentReceiver);
        unregisterProvisioningChangedCallback();
    }

    @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
    public void onSwitchChanged(Switch r3, boolean z) {
        Log.d("WifiCallingForSub", "onSwitchChanged(" + z + ")");
        if (!z) {
            updateWfcMode(false);
            return;
        }
        FragmentActivity activity = getActivity();
        Bundle bundle = new Bundle();
        bundle.putInt("EXTRA_SUB_ID", this.mSubId);
        new SubSettingLauncher(activity).setDestination(WifiCallingDisclaimerFragment.class.getName()).setArguments(bundle).setTitleRes(R.string.wifi_calling_settings_title).setSourceMetricsCategory(getMetricsCategory()).setResultListener(this, 2).launch();
    }

    private Intent getCarrierActivityIntent() {
        PersistableBundle configForSubId;
        ComponentName unflattenFromString;
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) getActivity().getSystemService(CarrierConfigManager.class);
        if (carrierConfigManager == null || (configForSubId = carrierConfigManager.getConfigForSubId(this.mSubId)) == null) {
            return null;
        }
        String string = configForSubId.getString("wfc_emergency_address_carrier_app_string");
        if (TextUtils.isEmpty(string) || (unflattenFromString = ComponentName.unflattenFromString(string)) == null) {
            return null;
        }
        Intent intent = new Intent();
        intent.setComponent(unflattenFromString);
        intent.putExtra("android.telephony.extra.SUBSCRIPTION_INDEX", this.mSubId);
        return intent;
    }

    private void updateWfcMode(boolean z) {
        Log.i("WifiCallingForSub", "updateWfcMode(" + z + ")");
        this.mImsMmTelManager.setVoWiFiSettingEnabled(z);
        int voWiFiModeSetting = this.mImsMmTelManager.getVoWiFiModeSetting();
        updateButtonWfcMode(z, voWiFiModeSetting, this.mImsMmTelManager.getVoWiFiRoamingModeSetting());
        if (z) {
            this.mMetricsFeatureProvider.action(getActivity(), getMetricsCategory(), voWiFiModeSetting);
        } else {
            this.mMetricsFeatureProvider.action(getActivity(), getMetricsCategory(), -1);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        Log.d("WifiCallingForSub", "WFC activity request = " + i + " result = " + i2);
        if (i != 1) {
            if (i != 2) {
                Log.e("WifiCallingForSub", "Unexpected request: " + i);
            } else if (i2 == -1) {
                Intent carrierActivityIntent = getCarrierActivityIntent();
                if (carrierActivityIntent != null) {
                    carrierActivityIntent.putExtra("EXTRA_LAUNCH_CARRIER_APP", 0);
                    startActivityForResult(carrierActivityIntent, 1);
                    return;
                }
                updateWfcMode(true);
            }
        } else if (i2 == -1) {
            updateWfcMode(true);
        }
    }

    private void updateButtonWfcMode(boolean z, int i, int i2) {
        this.mButtonWfcMode.setSummary(getWfcModeSummary(i));
        boolean z2 = true;
        this.mButtonWfcMode.setEnabled(z && this.mEditableWfcMode);
        this.mButtonWfcRoamingMode.setEnabled(z && this.mEditableWfcRoamingMode);
        getPreferenceScreen();
        boolean z3 = getCarrierActivityIntent() != null;
        if (z) {
            this.mButtonWfcMode.setVisible(this.mEditableWfcMode);
            ListWithEntrySummaryPreference listWithEntrySummaryPreference = this.mButtonWfcRoamingMode;
            if (!this.mEditableWfcRoamingMode || this.mUseWfcHomeModeForRoaming) {
                z2 = false;
            }
            listWithEntrySummaryPreference.setVisible(z2);
            this.mUpdateAddress.setVisible(z3);
        } else {
            this.mButtonWfcMode.setVisible(false);
            this.mButtonWfcRoamingMode.setVisible(false);
            this.mUpdateAddress.setVisible(false);
        }
        updateDescriptionForOptions(List.of(this.mButtonWfcMode, this.mButtonWfcRoamingMode, this.mUpdateAddress));
    }

    private void updateDescriptionForOptions(List<Preference> list) {
        LinkifyDescriptionPreference linkifyDescriptionPreference = (LinkifyDescriptionPreference) findPreference("no_options_description");
        if (linkifyDescriptionPreference != null) {
            boolean anyMatch = list.stream().anyMatch(WifiCallingSettingsForSub$$ExternalSyntheticLambda1.INSTANCE);
            if (!anyMatch) {
                Resources resourcesForSubId = getResourcesForSubId();
                linkifyDescriptionPreference.setSummary(resourcesForSubId.getString(R.string.wifi_calling_off_explanation, resourcesForSubId.getString(R.string.wifi_calling_off_explanation_2)));
            }
            linkifyDescriptionPreference.setVisible(!anyMatch);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mButtonWfcMode) {
            Log.d("WifiCallingForSub", "onPreferenceChange mButtonWfcMode " + obj);
            String str = (String) obj;
            this.mButtonWfcMode.setValue(str);
            int intValue = Integer.valueOf(str).intValue();
            if (intValue == this.mImsMmTelManager.getVoWiFiModeSetting()) {
                return true;
            }
            this.mImsMmTelManager.setVoWiFiModeSetting(intValue);
            this.mButtonWfcMode.setSummary(getWfcModeSummary(intValue));
            this.mMetricsFeatureProvider.action(getActivity(), getMetricsCategory(), intValue);
            if (!this.mUseWfcHomeModeForRoaming) {
                return true;
            }
            this.mImsMmTelManager.setVoWiFiRoamingModeSetting(intValue);
            return true;
        }
        ListWithEntrySummaryPreference listWithEntrySummaryPreference = this.mButtonWfcRoamingMode;
        if (preference != listWithEntrySummaryPreference) {
            return true;
        }
        String str2 = (String) obj;
        listWithEntrySummaryPreference.setValue(str2);
        int intValue2 = Integer.valueOf(str2).intValue();
        if (intValue2 == this.mImsMmTelManager.getVoWiFiRoamingModeSetting()) {
            return true;
        }
        this.mImsMmTelManager.setVoWiFiRoamingModeSetting(intValue2);
        this.mMetricsFeatureProvider.action(getActivity(), getMetricsCategory(), intValue2);
        return true;
    }

    private CharSequence getWfcModeSummary(int i) {
        int i2;
        if (queryImsState(this.mSubId).isEnabledByUser()) {
            if (i == 0) {
                i2 = 17041716;
            } else if (i == 1) {
                i2 = 17041715;
            } else if (i != 2) {
                Log.e("WifiCallingForSub", "Unexpected WFC mode value: " + i);
            } else {
                i2 = 17041717;
            }
            return getResourcesForSubId().getString(i2);
        }
        i2 = 17041747;
        return getResourcesForSubId().getString(i2);
    }

    @VisibleForTesting
    Resources getResourcesForSubId() {
        return SubscriptionManager.getResourcesForSubId(getContext(), this.mSubId);
    }

    @VisibleForTesting
    void registerProvisioningChangedCallback() {
        ProvisioningManager provisioningManager = this.mProvisioningManager;
        if (provisioningManager != null) {
            try {
                provisioningManager.registerProvisioningChangedCallback(getContext().getMainExecutor(), this.mProvisioningCallback);
            } catch (Exception unused) {
                Log.w("WifiCallingForSub", "onResume: Unable to register callback for provisioning changes.");
            }
        }
    }

    @VisibleForTesting
    void unregisterProvisioningChangedCallback() {
        ProvisioningManager provisioningManager = this.mProvisioningManager;
        if (provisioningManager != null) {
            provisioningManager.unregisterProvisioningChangedCallback(this.mProvisioningCallback);
        }
    }
}
