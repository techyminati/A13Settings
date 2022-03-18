package com.android.settings.network.telephony;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.window.R;
import com.android.settings.Settings;
import com.android.settings.datausage.BillingCyclePreferenceController;
import com.android.settings.datausage.DataUsageSummaryPreferenceController;
import com.android.settings.network.ActiveSubscriptionsListener;
import com.android.settings.network.CarrierWifiTogglePreferenceController;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.network.telephony.cdma.CdmaSubscriptionPreferenceController;
import com.android.settings.network.telephony.cdma.CdmaSystemSelectPreferenceController;
import com.android.settings.network.telephony.gsm.AutoSelectPreferenceController;
import com.android.settings.network.telephony.gsm.OpenNetworkSelectPagePreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.wifi.WifiPickerTrackerHelper;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.utils.ThreadUtils;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes.dex */
public class MobileNetworkSettings extends AbstractMobileNetworkSettings {
    static final String KEY_CLICKED_PREF = "key_clicked_pref";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.mobile_network_settings) { // from class: com.android.settings.network.telephony.MobileNetworkSettings.2
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return ((UserManager) context.getSystemService(UserManager.class)).isAdminUser();
        }
    };
    private ActiveSubscriptionsListener mActiveSubscriptionsListener;
    private int mActiveSubscriptionsListenerCount;
    private CdmaSubscriptionPreferenceController mCdmaSubscriptionPreferenceController;
    private CdmaSystemSelectPreferenceController mCdmaSystemSelectPreferenceController;
    private String mClickedPrefKey;
    private boolean mDropFirstSubscriptionChangeNotify;
    private int mSubId = -1;
    private TelephonyManager mTelephonyManager;
    private UserManager mUserManager;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "NetworkSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1571;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.mobile_network_settings;
    }

    @Override // com.android.settings.network.telephony.AbstractMobileNetworkSettings, com.android.settings.dashboard.DashboardFragment, androidx.preference.PreferenceGroup.OnExpandButtonClickListener
    public /* bridge */ /* synthetic */ void onExpandButtonClick() {
        super.onExpandButtonClick();
    }

    public MobileNetworkSettings() {
        super("no_config_mobile_networks");
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (super.onPreferenceTreeClick(preference)) {
            return true;
        }
        String key = preference.getKey();
        if (!TextUtils.equals(key, "cdma_system_select_key") && !TextUtils.equals(key, "cdma_subscription_key")) {
            return false;
        }
        if (this.mTelephonyManager.getEmergencyCallbackMode()) {
            startActivityForResult(new Intent("android.telephony.action.SHOW_NOTICE_ECM_BLOCK_OTHERS", (Uri) null), 17);
            this.mClickedPrefKey = key;
        }
        return true;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        if (getArguments() == null) {
            Intent intent = getIntent();
            if (intent != null) {
                this.mSubId = intent.getIntExtra("android.provider.extra.SUB_ID", MobileNetworkUtils.getSearchableSubscriptionId(context));
                Log.d("NetworkSettings", "display subId from intent: " + this.mSubId);
            } else {
                Log.d("NetworkSettings", "intent is null, can not get the subId from intent.");
            }
        } else {
            this.mSubId = getArguments().getInt("android.provider.extra.SUB_ID", MobileNetworkUtils.getSearchableSubscriptionId(context));
            Log.d("NetworkSettings", "display subId from getArguments(): " + this.mSubId);
        }
        if (!SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            return Arrays.asList(new AbstractPreferenceController[0]);
        }
        return Arrays.asList(new DataUsageSummaryPreferenceController(getActivity(), getSettingsLifecycle(), this, this.mSubId));
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        Intent intent = getIntent();
        SubscriptionInfo subscriptionOrDefault = SubscriptionUtil.getSubscriptionOrDefault(context, this.mSubId);
        if (subscriptionOrDefault == null) {
            Log.d("NetworkSettings", "Invalid subId request " + this.mSubId);
            return;
        }
        int i = this.mSubId;
        updateSubscriptions(subscriptionOrDefault);
        if (!Settings.MobileNetworkActivity.doesIntentContainOptInAction(intent)) {
            removeContactDiscoveryDialog(i);
        }
        if (Settings.MobileNetworkActivity.doesIntentContainOptInAction(intent)) {
            showContactDiscoveryDialog(SubscriptionUtil.getSubscriptionOrDefault(context, this.mSubId));
        }
        DataUsageSummaryPreferenceController dataUsageSummaryPreferenceController = (DataUsageSummaryPreferenceController) use(DataUsageSummaryPreferenceController.class);
        if (dataUsageSummaryPreferenceController != null) {
            dataUsageSummaryPreferenceController.init(this.mSubId);
        }
        ((CallsDefaultSubscriptionController) use(CallsDefaultSubscriptionController.class)).init(getLifecycle());
        ((SmsDefaultSubscriptionController) use(SmsDefaultSubscriptionController.class)).init(getLifecycle());
        ((MobileNetworkSwitchController) use(MobileNetworkSwitchController.class)).init(getLifecycle(), this.mSubId);
        ((CarrierSettingsVersionPreferenceController) use(CarrierSettingsVersionPreferenceController.class)).init(this.mSubId);
        ((BillingCyclePreferenceController) use(BillingCyclePreferenceController.class)).init(this.mSubId);
        ((MmsMessagePreferenceController) use(MmsMessagePreferenceController.class)).init(this.mSubId);
        ((DataDuringCallsPreferenceController) use(DataDuringCallsPreferenceController.class)).init(getLifecycle(), this.mSubId);
        ((DisabledSubscriptionController) use(DisabledSubscriptionController.class)).init(getLifecycle(), this.mSubId);
        ((DeleteSimProfilePreferenceController) use(DeleteSimProfilePreferenceController.class)).init(this.mSubId, this, 18);
        ((DisableSimFooterPreferenceController) use(DisableSimFooterPreferenceController.class)).init(this.mSubId);
        ((NrDisabledInDsdsFooterPreferenceController) use(NrDisabledInDsdsFooterPreferenceController.class)).init(this.mSubId);
        ((MobileDataPreferenceController) use(MobileDataPreferenceController.class)).init(getFragmentManager(), this.mSubId);
        ((MobileDataPreferenceController) use(MobileDataPreferenceController.class)).setWifiPickerTrackerHelper(new WifiPickerTrackerHelper(getSettingsLifecycle(), context, null));
        ((RoamingPreferenceController) use(RoamingPreferenceController.class)).init(getFragmentManager(), this.mSubId);
        ((ApnPreferenceController) use(ApnPreferenceController.class)).init(this.mSubId);
        ((CarrierPreferenceController) use(CarrierPreferenceController.class)).init(this.mSubId);
        ((DataUsagePreferenceController) use(DataUsagePreferenceController.class)).init(this.mSubId);
        ((PreferredNetworkModePreferenceController) use(PreferredNetworkModePreferenceController.class)).init(this.mSubId);
        ((EnabledNetworkModePreferenceController) use(EnabledNetworkModePreferenceController.class)).init(getLifecycle(), this.mSubId);
        ((DataServiceSetupPreferenceController) use(DataServiceSetupPreferenceController.class)).init(this.mSubId);
        ((Enable2gPreferenceController) use(Enable2gPreferenceController.class)).init(this.mSubId);
        ((CarrierWifiTogglePreferenceController) use(CarrierWifiTogglePreferenceController.class)).init(getLifecycle(), this.mSubId);
        WifiCallingPreferenceController init = ((WifiCallingPreferenceController) use(WifiCallingPreferenceController.class)).init(this.mSubId);
        ((NetworkPreferenceCategoryController) use(NetworkPreferenceCategoryController.class)).init(getLifecycle(), this.mSubId).setChildren(Arrays.asList(((AutoSelectPreferenceController) use(AutoSelectPreferenceController.class)).init(getLifecycle(), this.mSubId).addListener(((OpenNetworkSelectPagePreferenceController) use(OpenNetworkSelectPagePreferenceController.class)).init(getLifecycle(), this.mSubId))));
        CdmaSystemSelectPreferenceController cdmaSystemSelectPreferenceController = (CdmaSystemSelectPreferenceController) use(CdmaSystemSelectPreferenceController.class);
        this.mCdmaSystemSelectPreferenceController = cdmaSystemSelectPreferenceController;
        cdmaSystemSelectPreferenceController.init(getPreferenceManager(), this.mSubId);
        CdmaSubscriptionPreferenceController cdmaSubscriptionPreferenceController = (CdmaSubscriptionPreferenceController) use(CdmaSubscriptionPreferenceController.class);
        this.mCdmaSubscriptionPreferenceController = cdmaSubscriptionPreferenceController;
        cdmaSubscriptionPreferenceController.init(getPreferenceManager(), this.mSubId);
        VideoCallingPreferenceController init2 = ((VideoCallingPreferenceController) use(VideoCallingPreferenceController.class)).init(this.mSubId);
        ((CallingPreferenceCategoryController) use(CallingPreferenceCategoryController.class)).setChildren(Arrays.asList(init, init2, ((BackupCallingPreferenceController) use(BackupCallingPreferenceController.class)).init(this.mSubId)));
        ((Enhanced4gLtePreferenceController) use(Enhanced4gLtePreferenceController.class)).init(this.mSubId).addListener(init2);
        ((Enhanced4gCallingPreferenceController) use(Enhanced4gCallingPreferenceController.class)).init(this.mSubId).addListener(init2);
        ((Enhanced4gAdvancedCallingPreferenceController) use(Enhanced4gAdvancedCallingPreferenceController.class)).init(this.mSubId).addListener(init2);
        ((ContactDiscoveryPreferenceController) use(ContactDiscoveryPreferenceController.class)).init(getParentFragmentManager(), this.mSubId, getLifecycle());
        ((NrAdvancedCallingPreferenceController) use(NrAdvancedCallingPreferenceController.class)).init(this.mSubId);
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        Log.i("NetworkSettings", "onCreate:+");
        TelephonyStatusControlSession telephonyAvailabilityStatus = setTelephonyAvailabilityStatus(getPreferenceControllersAsList());
        super.onCreate(bundle);
        Context context = getContext();
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mTelephonyManager = ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mSubId);
        telephonyAvailabilityStatus.close();
        onRestoreInstance(bundle);
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        Log.d("NetworkSettings", "onResume() subId=" + this.mSubId);
        if (this.mActiveSubscriptionsListener == null) {
            this.mActiveSubscriptionsListener = new ActiveSubscriptionsListener(getContext().getMainLooper(), getContext(), this.mSubId) { // from class: com.android.settings.network.telephony.MobileNetworkSettings.1
                @Override // com.android.settings.network.ActiveSubscriptionsListener
                public void onChanged() {
                    MobileNetworkSettings.this.onSubscriptionDetailChanged();
                }
            };
            this.mDropFirstSubscriptionChangeNotify = true;
        }
        this.mActiveSubscriptionsListener.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSubscriptionDetailChanged() {
        if (this.mDropFirstSubscriptionChangeNotify) {
            this.mDropFirstSubscriptionChangeNotify = false;
            Log.d("NetworkSettings", "Callback during onResume()");
            return;
        }
        int i = this.mActiveSubscriptionsListenerCount + 1;
        this.mActiveSubscriptionsListenerCount = i;
        if (i == 1) {
            if (SubscriptionUtil.getSubscriptionOrDefault(getContext(), this.mSubId) == null) {
                finishFragment();
            } else {
                ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.network.telephony.MobileNetworkSettings$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        MobileNetworkSettings.this.lambda$onSubscriptionDetailChanged$0();
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onSubscriptionDetailChanged$0() {
        this.mActiveSubscriptionsListenerCount = 0;
        redrawPreferenceControllers();
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        ActiveSubscriptionsListener activeSubscriptionsListener = this.mActiveSubscriptionsListener;
        if (activeSubscriptionsListener != null) {
            activeSubscriptionsListener.stop();
        }
        super.onDestroy();
    }

    void onRestoreInstance(Bundle bundle) {
        if (bundle != null) {
            this.mClickedPrefKey = bundle.getString(KEY_CLICKED_PREF);
        }
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString(KEY_CLICKED_PREF, this.mClickedPrefKey);
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        Preference findPreference;
        FragmentActivity activity;
        if (i != 17) {
            if (i == 18 && i2 != 0 && (activity = getActivity()) != null && !activity.isFinishing()) {
                activity.finish();
            }
        } else if (i2 != 0 && (findPreference = getPreferenceScreen().findPreference(this.mClickedPrefKey)) != null) {
            findPreference.performClick();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        if (SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            MenuItem add = menu.add(0, R.id.edit_sim_name, 0, R.string.mobile_network_sim_name);
            add.setIcon(17302771);
            add.setShowAsAction(2);
        }
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (!SubscriptionManager.isValidSubscriptionId(this.mSubId) || menuItem.getItemId() != R.id.edit_sim_name) {
            return super.onOptionsItemSelected(menuItem);
        }
        RenameMobileNetworkDialogFragment.newInstance(this.mSubId).show(getFragmentManager(), "RenameMobileNetwork");
        return true;
    }

    private ContactDiscoveryDialogFragment getContactDiscoveryFragment(int i) {
        return (ContactDiscoveryDialogFragment) getChildFragmentManager().findFragmentByTag(ContactDiscoveryDialogFragment.getFragmentTag(i));
    }

    private void removeContactDiscoveryDialog(int i) {
        ContactDiscoveryDialogFragment contactDiscoveryFragment = getContactDiscoveryFragment(i);
        if (contactDiscoveryFragment != null) {
            contactDiscoveryFragment.dismiss();
        }
    }

    private void showContactDiscoveryDialog(SubscriptionInfo subscriptionInfo) {
        if (subscriptionInfo == null) {
            Log.d("NetworkSettings", "Invalid subId request " + this.mSubId);
            onDestroy();
            return;
        }
        CharSequence uniqueSubscriptionDisplayName = SubscriptionUtil.getUniqueSubscriptionDisplayName(subscriptionInfo, getContext());
        ContactDiscoveryDialogFragment contactDiscoveryFragment = getContactDiscoveryFragment(this.mSubId);
        if (contactDiscoveryFragment == null) {
            contactDiscoveryFragment = ContactDiscoveryDialogFragment.newInstance(this.mSubId, uniqueSubscriptionDisplayName);
        }
        if (!contactDiscoveryFragment.isAdded()) {
            contactDiscoveryFragment.show(getChildFragmentManager(), ContactDiscoveryDialogFragment.getFragmentTag(this.mSubId));
        }
    }

    private void updateSubscriptions(SubscriptionInfo subscriptionInfo) {
        if (subscriptionInfo != null) {
            this.mSubId = subscriptionInfo.getSubscriptionId();
        }
    }
}
