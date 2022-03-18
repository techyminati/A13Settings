package com.android.settings.network;

import android.content.Context;
import android.os.UserManager;
import androidx.window.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.network.telephony.CallsDefaultSubscriptionController;
import com.android.settings.network.telephony.NetworkProviderBackupCallingPreferenceController;
import com.android.settings.network.telephony.NetworkProviderWifiCallingPreferenceController;
import com.android.settings.network.telephony.SmsDefaultSubscriptionController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class NetworkProviderCallsSmsFragment extends DashboardFragment {
    static final String KEY_PREFERENCE_CALLS = "provider_model_calls_preference";
    static final String KEY_PREFERENCE_CATEGORY_BACKUP_CALLING = "provider_model_backup_calling_category";
    static final String KEY_PREFERENCE_CATEGORY_CALLING = "provider_model_calling_category";
    static final String KEY_PREFERENCE_SMS = "provider_model_sms_preference";
    static final String LOG_TAG = "NetworkProviderCallsSmsFragment";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.network_provider_calls_sms) { // from class: com.android.settings.network.NetworkProviderCallsSmsFragment.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return ((UserManager) context.getSystemService(UserManager.class)).isAdminUser();
        }
    };
    private NetworkProviderWifiCallingPreferenceController mNetworkProviderWifiCallingPreferenceController;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return LOG_TAG;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.network_provider_calls_sms;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new CallsDefaultSubscriptionController(context, KEY_PREFERENCE_CALLS));
        arrayList.add(new SmsDefaultSubscriptionController(context, KEY_PREFERENCE_SMS));
        NetworkProviderWifiCallingPreferenceController networkProviderWifiCallingPreferenceController = new NetworkProviderWifiCallingPreferenceController(context, KEY_PREFERENCE_CATEGORY_CALLING);
        this.mNetworkProviderWifiCallingPreferenceController = networkProviderWifiCallingPreferenceController;
        networkProviderWifiCallingPreferenceController.init(getSettingsLifecycle());
        arrayList.add(this.mNetworkProviderWifiCallingPreferenceController);
        NetworkProviderBackupCallingPreferenceController networkProviderBackupCallingPreferenceController = new NetworkProviderBackupCallingPreferenceController(context, KEY_PREFERENCE_CATEGORY_BACKUP_CALLING);
        networkProviderBackupCallingPreferenceController.init(getSettingsLifecycle());
        arrayList.add(networkProviderBackupCallingPreferenceController);
        return arrayList;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updatePreferenceStates();
    }
}
