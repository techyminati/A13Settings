package com.android.settings.datausage;

import android.app.ActivityManager;
import android.app.usage.NetworkStats;
import android.content.Intent;
import android.graphics.Color;
import android.net.NetworkPolicy;
import android.net.NetworkTemplate;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.telephony.SubscriptionInfo;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.window.R;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.datausage.CycleAdapter;
import com.android.settings.network.MobileDataEnabledListener;
import com.android.settings.network.ProxySubscriptionManager;
import com.android.settings.widget.LoadingViewController;
import com.android.settingslib.AppItem;
import com.android.settingslib.net.NetworkCycleChartData;
import com.android.settingslib.net.NetworkCycleChartDataLoader;
import com.android.settingslib.net.NetworkStatsSummaryLoader;
import com.android.settingslib.net.UidDetailProvider;
import com.google.android.settings.security.SecurityContentManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes.dex */
public class DataUsageList extends DataUsageBaseFragment implements MobileDataEnabledListener.Client {
    static final int LOADER_CHART_DATA = 2;
    static final int LOADER_SUMMARY = 3;
    private PreferenceGroup mApps;
    private ChartDataUsagePreference mChart;
    private CycleAdapter mCycleAdapter;
    private List<NetworkCycleChartData> mCycleData;
    Spinner mCycleSpinner;
    private ArrayList<Long> mCycles;
    MobileDataEnabledListener mDataStateListener;
    private View mHeader;
    LoadingViewController mLoadingViewController;
    int mNetworkType;
    NetworkTemplate mTemplate;
    private UidDetailProvider mUidDetailProvider;
    private Preference mUsageAmount;
    int mSubId = -1;
    private AdapterView.OnItemSelectedListener mCycleListener = new AdapterView.OnItemSelectedListener() { // from class: com.android.settings.datausage.DataUsageList.4
        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onNothingSelected(AdapterView<?> adapterView) {
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
            CycleAdapter.CycleItem cycleItem = (CycleAdapter.CycleItem) DataUsageList.this.mCycleSpinner.getSelectedItem();
            DataUsageList.this.mChart.setNetworkCycleData((NetworkCycleChartData) DataUsageList.this.mCycleData.get(i));
            DataUsageList.this.updateDetailData();
        }
    };
    final LoaderManager.LoaderCallbacks<List<NetworkCycleChartData>> mNetworkCycleDataCallbacks = new LoaderManager.LoaderCallbacks<List<NetworkCycleChartData>>() { // from class: com.android.settings.datausage.DataUsageList.5
        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<List<NetworkCycleChartData>> onCreateLoader(int i, Bundle bundle) {
            return NetworkCycleChartDataLoader.builder(DataUsageList.this.getContext()).setNetworkTemplate(DataUsageList.this.mTemplate).build();
        }

        public void onLoadFinished(Loader<List<NetworkCycleChartData>> loader, List<NetworkCycleChartData> list) {
            DataUsageList.this.mLoadingViewController.showContent(false);
            DataUsageList.this.mCycleData = list;
            DataUsageList.this.updatePolicy();
            DataUsageList.this.mCycleSpinner.setVisibility(0);
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<List<NetworkCycleChartData>> loader) {
            DataUsageList.this.mCycleData = null;
        }
    };
    private final LoaderManager.LoaderCallbacks<NetworkStats> mNetworkStatsDetailCallbacks = new LoaderManager.LoaderCallbacks<NetworkStats>() { // from class: com.android.settings.datausage.DataUsageList.6
        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<NetworkStats> onCreateLoader(int i, Bundle bundle) {
            return new NetworkStatsSummaryLoader.Builder(DataUsageList.this.getContext()).setStartTime(DataUsageList.this.mChart.getInspectStart()).setEndTime(DataUsageList.this.mChart.getInspectEnd()).setNetworkTemplate(DataUsageList.this.mTemplate).build();
        }

        public void onLoadFinished(Loader<NetworkStats> loader, NetworkStats networkStats) {
            DataUsageList.this.bindStats(networkStats, DataUsageList.this.services.mPolicyManager.getUidsWithPolicy(1));
            updateEmptyVisible();
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<NetworkStats> loader) {
            DataUsageList.this.bindStats(null, new int[0]);
            updateEmptyVisible();
        }

        private void updateEmptyVisible() {
            boolean z = true;
            boolean z2 = DataUsageList.this.mApps.getPreferenceCount() != 0;
            if (DataUsageList.this.getPreferenceScreen().getPreferenceCount() == 0) {
                z = false;
            }
            if (z2 == z) {
                return;
            }
            if (DataUsageList.this.mApps.getPreferenceCount() != 0) {
                DataUsageList.this.getPreferenceScreen().addPreference(DataUsageList.this.mUsageAmount);
                DataUsageList.this.getPreferenceScreen().addPreference(DataUsageList.this.mApps);
                return;
            }
            DataUsageList.this.getPreferenceScreen().removeAll();
        }
    };

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "DataUsageList";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 341;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.data_usage_list;
    }

    @Override // com.android.settings.datausage.DataUsageBaseFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        if (!isBandwidthControlEnabled()) {
            Log.w("DataUsageList", "No bandwidth control; leaving");
            activity.finish();
            return;
        }
        this.mUidDetailProvider = new UidDetailProvider(activity);
        this.mUsageAmount = findPreference("usage_amount");
        this.mChart = (ChartDataUsagePreference) findPreference("chart_data");
        this.mApps = (PreferenceGroup) findPreference("apps_group");
        Preference findPreference = findPreference("operator_warning");
        if (findPreference != null) {
            findPreference.setVisible(false);
        }
        processArgument();
        this.mDataStateListener = new MobileDataEnabledListener(activity, this);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        View pinnedHeaderView = setPinnedHeaderView(R.layout.apps_filter_spinner);
        this.mHeader = pinnedHeaderView;
        pinnedHeaderView.findViewById(R.id.filter_settings).setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.datausage.DataUsageList$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                DataUsageList.this.lambda$onViewCreated$0(view2);
            }
        });
        Spinner spinner = (Spinner) this.mHeader.findViewById(R.id.filter_spinner);
        this.mCycleSpinner = spinner;
        spinner.setVisibility(8);
        this.mCycleAdapter = new CycleAdapter(this.mCycleSpinner.getContext(), new CycleAdapter.SpinnerInterface() { // from class: com.android.settings.datausage.DataUsageList.1
            @Override // com.android.settings.datausage.CycleAdapter.SpinnerInterface
            public void setAdapter(CycleAdapter cycleAdapter) {
                DataUsageList.this.mCycleSpinner.setAdapter((SpinnerAdapter) cycleAdapter);
            }

            @Override // com.android.settings.datausage.CycleAdapter.SpinnerInterface
            public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener onItemSelectedListener) {
                DataUsageList.this.mCycleSpinner.setOnItemSelectedListener(onItemSelectedListener);
            }

            @Override // com.android.settings.datausage.CycleAdapter.SpinnerInterface
            public Object getSelectedItem() {
                return DataUsageList.this.mCycleSpinner.getSelectedItem();
            }

            @Override // com.android.settings.datausage.CycleAdapter.SpinnerInterface
            public void setSelection(int i) {
                DataUsageList.this.mCycleSpinner.setSelection(i);
            }
        }, this.mCycleListener);
        this.mCycleSpinner.setAccessibilityDelegate(new View.AccessibilityDelegate() { // from class: com.android.settings.datausage.DataUsageList.2
            @Override // android.view.View.AccessibilityDelegate
            public void sendAccessibilityEvent(View view2, int i) {
                if (i != 4) {
                    super.sendAccessibilityEvent(view2, i);
                }
            }
        });
        LoadingViewController loadingViewController = new LoadingViewController(getView().findViewById(R.id.loading_container), getListView());
        this.mLoadingViewController = loadingViewController;
        loadingViewController.showLoadingViewDelayed();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onViewCreated$0(View view) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("network_template", this.mTemplate);
        new SubSettingLauncher(getContext()).setDestination(BillingCycleSettings.class.getName()).setTitleRes(R.string.billing_cycle).setSourceMetricsCategory(getMetricsCategory()).setArguments(bundle).launch();
    }

    @Override // com.android.settings.datausage.DataUsageBaseFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mDataStateListener.start(this.mSubId);
        getLoaderManager().restartLoader(2, buildArgs(this.mTemplate), this.mNetworkCycleDataCallbacks);
        updateBody();
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mDataStateListener.stop();
        getLoaderManager().destroyLoader(2);
        getLoaderManager().destroyLoader(3);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        this.mUidDetailProvider.clearCache();
        this.mUidDetailProvider = null;
        super.onDestroy();
    }

    void processArgument() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.mSubId = arguments.getInt("sub_id", -1);
            this.mTemplate = arguments.getParcelable("network_template");
            this.mNetworkType = arguments.getInt("network_type", 0);
        }
        if (this.mTemplate == null && this.mSubId == -1) {
            Intent intent = getIntent();
            this.mSubId = intent.getIntExtra("android.provider.extra.SUB_ID", -1);
            this.mTemplate = intent.getParcelableExtra("network_template");
        }
    }

    @Override // com.android.settings.network.MobileDataEnabledListener.Client
    public void onMobileDataEnabledChange() {
        updatePolicy();
    }

    private void updateBody() {
        SubscriptionInfo activeSubscriptionInfo;
        if (isAdded()) {
            FragmentActivity activity = getActivity();
            getActivity().invalidateOptionsMenu();
            int color = activity.getColor(R.color.sim_noitification);
            if (!(this.mSubId == -1 || (activeSubscriptionInfo = ProxySubscriptionManager.getInstance(activity).getActiveSubscriptionInfo(this.mSubId)) == null)) {
                color = activeSubscriptionInfo.getIconTint();
            }
            this.mChart.setColors(color, Color.argb(127, Color.red(color), Color.green(color), Color.blue(color)));
        }
    }

    private Bundle buildArgs(NetworkTemplate networkTemplate) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("template", networkTemplate);
        bundle.putParcelable("app", null);
        return bundle;
    }

    void updatePolicy() {
        NetworkPolicy policy = this.services.mPolicyEditor.getPolicy(this.mTemplate);
        View findViewById = this.mHeader.findViewById(R.id.filter_settings);
        if (!isNetworkPolicyModifiable(policy, this.mSubId) || !isMobileDataAvailable(this.mSubId)) {
            this.mChart.setNetworkPolicy(null);
            findViewById.setVisibility(8);
        } else {
            this.mChart.setNetworkPolicy(policy);
            findViewById.setVisibility(0);
            ((ImageView) findViewById).setColorFilter(17170443);
        }
        if (this.mCycleAdapter.updateCycleList(this.mCycleData)) {
            updateDetailData();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDetailData() {
        getLoaderManager().restartLoader(3, null, this.mNetworkStatsDetailCallbacks);
        List<NetworkCycleChartData> list = this.mCycleData;
        this.mUsageAmount.setTitle(getString(R.string.data_used_template, DataUsageUtils.formatDataUsage(getActivity(), (list == null || list.isEmpty()) ? 0L : this.mCycleData.get(this.mCycleSpinner.getSelectedItemPosition()).getTotalUsage())));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void bindStats(NetworkStats networkStats, int[] iArr) {
        this.mApps.removeAll();
        if (networkStats != null) {
            ArrayList arrayList = new ArrayList();
            int currentUser = ActivityManager.getCurrentUser();
            UserManager userManager = UserManager.get(getContext());
            List<UserHandle> userProfiles = userManager.getUserProfiles();
            SparseArray sparseArray = new SparseArray();
            NetworkStats.Bucket bucket = new NetworkStats.Bucket();
            long j = 0;
            while (true) {
                int i = 0;
                if (!networkStats.hasNextBucket() || !networkStats.getNextBucket(bucket)) {
                    break;
                }
                int uid = bucket.getUid();
                int userId = UserHandle.getUserId(uid);
                int i2 = -4;
                if (!UserHandle.isApp(uid)) {
                    i2 = uid;
                    if (!(i2 == -4 || i2 == -5 || i2 == 1061)) {
                        i2 = SecurityContentManager.DEFAULT_ORDER;
                    }
                    i = 2;
                } else if (userProfiles.contains(new UserHandle(userId))) {
                    if (userId != currentUser) {
                        i2 = uid;
                        j = accumulate(UidDetailProvider.buildKeyForUser(userId), sparseArray, bucket, 0, arrayList, j);
                    } else {
                        i2 = uid;
                    }
                    i = 2;
                } else if (userManager.getUserInfo(userId) == null) {
                    i = 2;
                } else {
                    i2 = UidDetailProvider.buildKeyForUser(userId);
                }
                j = accumulate(i2, sparseArray, bucket, i, arrayList, j);
            }
            networkStats.close();
            for (int i3 : iArr) {
                if (userProfiles.contains(new UserHandle(UserHandle.getUserId(i3)))) {
                    AppItem appItem = (AppItem) sparseArray.get(i3);
                    if (appItem == null) {
                        appItem = new AppItem(i3);
                        appItem.total = -1L;
                        arrayList.add(appItem);
                        sparseArray.put(appItem.key, appItem);
                    }
                    appItem.restricted = true;
                }
            }
            Collections.sort(arrayList);
            for (int i4 = 0; i4 < arrayList.size(); i4++) {
                AppDataUsagePreference appDataUsagePreference = new AppDataUsagePreference(getContext(), (AppItem) arrayList.get(i4), j != 0 ? (int) ((((AppItem) arrayList.get(i4)).total * 100) / j) : 0, this.mUidDetailProvider);
                appDataUsagePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.datausage.DataUsageList.3
                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public boolean onPreferenceClick(Preference preference) {
                        DataUsageList.this.startAppDataUsage(((AppDataUsagePreference) preference).getItem());
                        return true;
                    }
                });
                this.mApps.addPreference(appDataUsagePreference);
            }
        }
    }

    void startAppDataUsage(AppItem appItem) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("app_item", appItem);
        bundle.putParcelable("network_template", this.mTemplate);
        if (this.mCycles == null) {
            this.mCycles = new ArrayList<>();
            for (NetworkCycleChartData networkCycleChartData : this.mCycleData) {
                if (this.mCycles.isEmpty()) {
                    this.mCycles.add(Long.valueOf(networkCycleChartData.getEndTime()));
                }
                this.mCycles.add(Long.valueOf(networkCycleChartData.getStartTime()));
            }
        }
        bundle.putSerializable("network_cycles", this.mCycles);
        bundle.putLong("selected_cycle", this.mCycleData.get(this.mCycleSpinner.getSelectedItemPosition()).getEndTime());
        new SubSettingLauncher(getContext()).setDestination(AppDataUsage.class.getName()).setTitleRes(R.string.data_usage_app_summary_title).setArguments(bundle).setSourceMetricsCategory(getMetricsCategory()).launch();
    }

    private static long accumulate(int i, SparseArray<AppItem> sparseArray, NetworkStats.Bucket bucket, int i2, ArrayList<AppItem> arrayList, long j) {
        int uid = bucket.getUid();
        AppItem appItem = sparseArray.get(i);
        if (appItem == null) {
            appItem = new AppItem(i);
            appItem.category = i2;
            arrayList.add(appItem);
            sparseArray.put(appItem.key, appItem);
        }
        appItem.addUid(uid);
        long rxBytes = appItem.total + bucket.getRxBytes() + bucket.getTxBytes();
        appItem.total = rxBytes;
        return Math.max(j, rxBytes);
    }
}
