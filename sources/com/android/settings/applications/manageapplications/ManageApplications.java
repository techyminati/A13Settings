package com.android.settings.applications.manageapplications;

import android.app.ActivityManager;
import android.app.usage.IUsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageItemInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.preference.PreferenceFrameLayout;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.IconDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.window.R;
import com.android.internal.compat.IPlatformCompat;
import com.android.settings.Settings;
import com.android.settings.Utils;
import com.android.settings.applications.AppInfoBase;
import com.android.settings.applications.AppStateAlarmsAndRemindersBridge;
import com.android.settings.applications.AppStateAppOpsBridge;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settings.applications.AppStateInstallAppsBridge;
import com.android.settings.applications.AppStateLocaleBridge;
import com.android.settings.applications.AppStateManageExternalStorageBridge;
import com.android.settings.applications.AppStateMediaManagementAppsBridge;
import com.android.settings.applications.AppStateNotificationBridge;
import com.android.settings.applications.AppStateOverlayBridge;
import com.android.settings.applications.AppStatePowerBridge;
import com.android.settings.applications.AppStateUsageBridge;
import com.android.settings.applications.AppStateWriteSettingsBridge;
import com.android.settings.applications.AppStorageSettings;
import com.android.settings.applications.UsageAccessDetails;
import com.android.settings.applications.appinfo.AlarmsAndRemindersDetails;
import com.android.settings.applications.appinfo.AppInfoDashboardFragment;
import com.android.settings.applications.appinfo.AppLocaleDetails;
import com.android.settings.applications.appinfo.DrawOverlayDetails;
import com.android.settings.applications.appinfo.ExternalSourcesDetails;
import com.android.settings.applications.appinfo.ManageExternalStorageDetails;
import com.android.settings.applications.appinfo.MediaManagementAppsDetails;
import com.android.settings.applications.appinfo.WriteSettingsDetails;
import com.android.settings.applications.manageapplications.ManageApplications;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.fuelgauge.HighPowerDetail;
import com.android.settings.notification.ConfigureNotificationSettings;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.notification.app.AppNotificationSettings;
import com.android.settings.widget.LoadingViewController;
import com.android.settings.wifi.AppStateChangeWifiStateBridge;
import com.android.settings.wifi.ChangeWifiStateDetails;
import com.android.settingslib.applications.AppIconCacheManager;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.fuelgauge.PowerAllowlistBackend;
import com.android.settingslib.utils.ThreadUtils;
import com.android.settingslib.widget.SettingsSpinnerAdapter;
import com.google.android.material.appbar.AppBarLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
/* loaded from: classes.dex */
public class ManageApplications extends InstrumentedFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {
    static final String EXTRA_EXPAND_SEARCH_VIEW = "expand_search_view";
    private AppBarLayout mAppBarLayout;
    private ApplicationsAdapter mApplications;
    private ApplicationsState mApplicationsState;
    private String mCurrentPkgName;
    private int mCurrentUid;
    private View mEmptyView;
    boolean mExpandSearch;
    private AppFilterItem mFilter;
    FilterSpinnerAdapter mFilterAdapter;
    private Spinner mFilterSpinner;
    private int mFilterType;
    CharSequence mInvalidSizeStr;
    private boolean mIsPersonalOnly;
    private boolean mIsWorkOnly;
    public int mListType;
    private View mLoadingContainer;
    private NotificationBackend mNotificationBackend;
    private Menu mOptionsMenu;
    RecyclerView mRecyclerView;
    private ResetAppsHelper mResetAppsHelper;
    private View mRootView;
    private SearchView mSearchView;
    private boolean mShowSystem;
    int mSortOrder = R.id.sort_order_alpha;
    View mSpinnerHeader;
    private int mStorageType;
    private IUsageStatsManager mUsageStatsManager;
    private UserManager mUserManager;
    private String mVolumeUuid;
    private int mWorkUserId;
    static final boolean DEBUG = Build.IS_DEBUGGABLE;
    public static final Set<Integer> LIST_TYPES_WITH_INSTANT = new ArraySet(Arrays.asList(0, 3));

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override // android.widget.SearchView.OnQueryTextListener
    public boolean onQueryTextSubmit(String str) {
        return false;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        boolean z = true;
        setHasOptionsMenu(true);
        FragmentActivity activity = getActivity();
        this.mUserManager = (UserManager) activity.getSystemService(UserManager.class);
        this.mApplicationsState = ApplicationsState.getInstance(activity.getApplication());
        Intent intent = activity.getIntent();
        Bundle arguments = getArguments();
        int titleResId = getTitleResId(intent, arguments);
        String string = arguments != null ? arguments.getString("classname") : null;
        if (string == null) {
            string = intent.getComponent().getClassName();
        }
        if (string.equals(Settings.StorageUseActivity.class.getName())) {
            if (arguments == null || !arguments.containsKey("volumeUuid")) {
                this.mListType = 0;
            } else {
                this.mVolumeUuid = arguments.getString("volumeUuid");
                this.mStorageType = arguments.getInt("storageType", 0);
                this.mListType = 3;
            }
            this.mSortOrder = R.id.sort_order_size;
        } else if (string.equals(Settings.UsageAccessSettingsActivity.class.getName())) {
            this.mListType = 4;
        } else if (string.equals(Settings.HighPowerApplicationsActivity.class.getName())) {
            this.mListType = 5;
            this.mShowSystem = true;
        } else if (string.equals(Settings.OverlaySettingsActivity.class.getName())) {
            this.mListType = 6;
            reportIfRestrictedSawIntent(intent);
        } else if (string.equals(Settings.WriteSettingsActivity.class.getName())) {
            this.mListType = 7;
        } else if (string.equals(Settings.ManageExternalSourcesActivity.class.getName())) {
            this.mListType = 8;
        } else if (string.equals(Settings.GamesStorageActivity.class.getName())) {
            this.mListType = 9;
            this.mSortOrder = R.id.sort_order_size;
        } else if (string.equals(Settings.ChangeWifiStateActivity.class.getName())) {
            this.mListType = 10;
        } else if (string.equals(Settings.ManageExternalStorageActivity.class.getName())) {
            this.mListType = 11;
        } else if (string.equals(Settings.MediaManagementAppsActivity.class.getName())) {
            this.mListType = 13;
        } else if (string.equals(Settings.AlarmsAndRemindersActivity.class.getName())) {
            this.mListType = 12;
        } else if (string.equals(Settings.NotificationAppListActivity.class.getName())) {
            this.mListType = 1;
            this.mUsageStatsManager = IUsageStatsManager.Stub.asInterface(ServiceManager.getService("usagestats"));
            this.mNotificationBackend = new NotificationBackend();
            this.mSortOrder = R.id.sort_order_recent_notification;
        } else if (string.equals(AppLocaleDetails.class.getName())) {
            this.mListType = 14;
        } else {
            this.mListType = 0;
        }
        AppFilterRegistry instance = AppFilterRegistry.getInstance();
        this.mFilter = instance.get(instance.getDefaultFilterType(this.mListType));
        this.mIsPersonalOnly = arguments != null && arguments.getInt("profile") == 1;
        if (arguments == null || arguments.getInt("profile") != 2) {
            z = false;
        }
        this.mIsWorkOnly = z;
        int i = arguments != null ? arguments.getInt("workId") : UserHandle.myUserId();
        this.mWorkUserId = i;
        if (this.mIsWorkOnly && i == UserHandle.myUserId()) {
            this.mWorkUserId = Utils.getManagedProfileId(this.mUserManager, UserHandle.myUserId());
        }
        this.mExpandSearch = activity.getIntent().getBooleanExtra(EXTRA_EXPAND_SEARCH_VIEW, false);
        if (bundle != null) {
            this.mSortOrder = bundle.getInt("sortOrder", this.mSortOrder);
            this.mShowSystem = bundle.getBoolean("showSystem", this.mShowSystem);
            this.mFilterType = bundle.getInt("filterType", 4);
            this.mExpandSearch = bundle.getBoolean(EXTRA_EXPAND_SEARCH_VIEW);
        }
        this.mInvalidSizeStr = activity.getText(R.string.invalid_size_value);
        this.mResetAppsHelper = new ResetAppsHelper(activity);
        if (titleResId > 0) {
            activity.setTitle(titleResId);
        }
    }

    private void reportIfRestrictedSawIntent(Intent intent) {
        try {
            Uri data = intent.getData();
            if (data != null && TextUtils.equals("package", data.getScheme())) {
                int launchedFromUid = ActivityManager.getService().getLaunchedFromUid(getActivity().getActivityToken());
                if (launchedFromUid == -1) {
                    Log.w("ManageApplications", "Error obtaining calling uid");
                    return;
                }
                IPlatformCompat asInterface = IPlatformCompat.Stub.asInterface(ServiceManager.getService("platform_compat"));
                if (asInterface == null) {
                    Log.w("ManageApplications", "Error obtaining IPlatformCompat service");
                } else {
                    asInterface.reportChangeByUid(135920175L, launchedFromUid);
                }
            }
        } catch (RemoteException e) {
            Log.w("ManageApplications", "Error reporting SAW intent restriction", e);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        if (this.mListType != 6 || Utils.isSystemAlertWindowEnabled(getContext())) {
            View inflate = layoutInflater.inflate(R.layout.manage_applications_apps, (ViewGroup) null);
            this.mRootView = inflate;
            this.mLoadingContainer = inflate.findViewById(R.id.loading_container);
            this.mEmptyView = this.mRootView.findViewById(16908292);
            this.mRecyclerView = (RecyclerView) this.mRootView.findViewById(R.id.apps_list);
            ApplicationsAdapter applicationsAdapter = new ApplicationsAdapter(this.mApplicationsState, this, this.mFilter, bundle);
            this.mApplications = applicationsAdapter;
            if (bundle != null) {
                applicationsAdapter.mHasReceivedLoadEntries = bundle.getBoolean("hasEntries", false);
                this.mApplications.mHasReceivedBridgeCallback = bundle.getBoolean("hasBridge", false);
            }
            this.mRecyclerView.setItemAnimator(null);
            this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
            this.mRecyclerView.setAdapter(this.mApplications);
            if (viewGroup instanceof PreferenceFrameLayout) {
                this.mRootView.getLayoutParams().removeBorders = true;
            }
            createHeader();
            this.mResetAppsHelper.onRestoreInstanceState(bundle);
            this.mAppBarLayout = (AppBarLayout) getActivity().findViewById(R.id.app_bar);
            disableToolBarScrollableBehavior();
            return this.mRootView;
        }
        this.mRootView = layoutInflater.inflate(R.layout.manage_applications_apps_unsupported, (ViewGroup) null);
        setHasOptionsMenu(false);
        return this.mRootView;
    }

    void createHeader() {
        FragmentActivity activity = getActivity();
        FrameLayout frameLayout = (FrameLayout) this.mRootView.findViewById(R.id.pinned_header);
        View inflate = activity.getLayoutInflater().inflate(R.layout.manage_apps_filter_spinner, (ViewGroup) frameLayout, false);
        this.mSpinnerHeader = inflate;
        this.mFilterSpinner = (Spinner) inflate.findViewById(R.id.filter_spinner);
        FilterSpinnerAdapter filterSpinnerAdapter = new FilterSpinnerAdapter(this);
        this.mFilterAdapter = filterSpinnerAdapter;
        this.mFilterSpinner.setAdapter((SpinnerAdapter) filterSpinnerAdapter);
        this.mFilterSpinner.setOnItemSelectedListener(this);
        frameLayout.addView(this.mSpinnerHeader, 0);
        this.mFilterAdapter.enableFilter(AppFilterRegistry.getInstance().getDefaultFilterType(this.mListType));
        if (this.mListType == 0 && UserManager.get(getActivity()).getUserProfiles().size() > 1 && !this.mIsWorkOnly && !this.mIsPersonalOnly) {
            this.mFilterAdapter.enableFilter(8);
            this.mFilterAdapter.enableFilter(9);
        }
        if (this.mListType == 1) {
            this.mFilterAdapter.enableFilter(2);
            this.mFilterAdapter.enableFilter(3);
            this.mFilterAdapter.enableFilter(16);
            this.mFilterAdapter.enableFilter(4);
        }
        if (this.mListType == 5) {
            this.mFilterAdapter.enableFilter(1);
        }
        setCompositeFilter();
    }

    static ApplicationsState.AppFilter getCompositeFilter(int i, int i2, String str) {
        ApplicationsState.VolumeFilter volumeFilter = new ApplicationsState.VolumeFilter(str);
        if (i == 3) {
            return i2 == 0 ? new ApplicationsState.CompoundFilter(ApplicationsState.FILTER_APPS_EXCEPT_GAMES, volumeFilter) : volumeFilter;
        }
        if (i == 9) {
            return new ApplicationsState.CompoundFilter(ApplicationsState.FILTER_GAMES, volumeFilter);
        }
        return null;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        switch (this.mListType) {
            case 0:
                return 65;
            case 1:
                return 133;
            case 2:
            default:
                return 0;
            case 3:
                return 182;
            case 4:
                return 95;
            case 5:
                return 184;
            case 6:
            case 7:
                return 221;
            case 8:
                return 808;
            case 9:
                return 838;
            case 10:
                return 338;
            case 11:
                return 1822;
            case 12:
                return 1869;
            case 13:
                return 1874;
            case 14:
                return 1911;
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        updateView();
        ApplicationsAdapter applicationsAdapter = this.mApplications;
        if (applicationsAdapter != null) {
            applicationsAdapter.resume(this.mSortOrder);
            this.mApplications.updateLoading();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        this.mResetAppsHelper.onSaveInstanceState(bundle);
        bundle.putInt("sortOrder", this.mSortOrder);
        bundle.putInt("filterType", this.mFilter.getFilterType());
        bundle.putBoolean("showSystem", this.mShowSystem);
        bundle.putBoolean("hasEntries", this.mApplications.mHasReceivedLoadEntries);
        bundle.putBoolean("hasBridge", this.mApplications.mHasReceivedBridgeCallback);
        SearchView searchView = this.mSearchView;
        if (searchView != null) {
            bundle.putBoolean(EXTRA_EXPAND_SEARCH_VIEW, !searchView.isIconified());
        }
        ApplicationsAdapter applicationsAdapter = this.mApplications;
        if (applicationsAdapter != null) {
            applicationsAdapter.onSaveInstanceState(bundle);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        ApplicationsAdapter applicationsAdapter = this.mApplications;
        if (applicationsAdapter != null) {
            applicationsAdapter.pause();
        }
        this.mResetAppsHelper.stop();
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        ApplicationsAdapter applicationsAdapter = this.mApplications;
        if (applicationsAdapter != null) {
            applicationsAdapter.release();
        }
        this.mRootView = null;
        AppIconCacheManager.getInstance();
        AppIconCacheManager.release();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        String str;
        if (i == 1 && (str = this.mCurrentPkgName) != null) {
            int i3 = this.mListType;
            if (i3 == 1) {
                this.mApplications.mExtraInfoBridge.forceUpdate(this.mCurrentPkgName, this.mCurrentUid);
            } else if (i3 == 5 || i3 == 6 || i3 == 7) {
                this.mApplications.mExtraInfoBridge.forceUpdate(this.mCurrentPkgName, this.mCurrentUid);
            } else {
                this.mApplicationsState.requestSize(str, UserHandle.getUserId(this.mCurrentUid));
            }
        }
    }

    private void setCompositeFilter() {
        ApplicationsState.CompoundFilter compositeFilter = getCompositeFilter(this.mListType, this.mStorageType, this.mVolumeUuid);
        if (compositeFilter == null) {
            compositeFilter = this.mFilter.getFilter();
        }
        if (this.mIsWorkOnly) {
            compositeFilter = new ApplicationsState.CompoundFilter(compositeFilter, ApplicationsState.FILTER_WORK);
        }
        if (this.mIsPersonalOnly) {
            compositeFilter = new ApplicationsState.CompoundFilter(compositeFilter, ApplicationsState.FILTER_PERSONAL);
        }
        this.mApplications.setCompositeFilter(compositeFilter);
    }

    private void startApplicationDetailsActivity() {
        switch (this.mListType) {
            case 1:
                startAppInfoFragment(AppNotificationSettings.class, R.string.notifications_title);
                return;
            case 2:
            default:
                startAppInfoFragment(AppInfoDashboardFragment.class, R.string.application_info_label);
                return;
            case 3:
                startAppInfoFragment(AppStorageSettings.class, R.string.storage_settings);
                return;
            case 4:
                startAppInfoFragment(UsageAccessDetails.class, R.string.usage_access);
                return;
            case 5:
                HighPowerDetail.show(this, this.mCurrentUid, this.mCurrentPkgName, 1);
                return;
            case 6:
                startAppInfoFragment(DrawOverlayDetails.class, R.string.overlay_settings);
                return;
            case 7:
                startAppInfoFragment(WriteSettingsDetails.class, R.string.write_system_settings);
                return;
            case 8:
                startAppInfoFragment(ExternalSourcesDetails.class, R.string.install_other_apps);
                return;
            case 9:
                startAppInfoFragment(AppStorageSettings.class, R.string.game_storage_settings);
                return;
            case 10:
                startAppInfoFragment(ChangeWifiStateDetails.class, R.string.change_wifi_state_title);
                return;
            case 11:
                startAppInfoFragment(ManageExternalStorageDetails.class, R.string.manage_external_storage_title);
                return;
            case 12:
                startAppInfoFragment(AlarmsAndRemindersDetails.class, R.string.alarms_and_reminders_label);
                return;
            case 13:
                startAppInfoFragment(MediaManagementAppsDetails.class, R.string.media_management_apps_title);
                return;
            case 14:
                startAppInfoFragment(AppLocaleDetails.class, R.string.app_locale_picker_title);
                return;
        }
    }

    private void startAppInfoFragment(Class<?> cls, int i) {
        AppInfoBase.startAppInfoFragment(cls, getString(i), this.mCurrentPkgName, this.mCurrentUid, this, 1, getMetricsCategory());
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        if (getActivity() != null) {
            this.mOptionsMenu = menu;
            menuInflater.inflate(R.menu.manage_apps, menu);
            MenuItem findItem = menu.findItem(R.id.search_app_list_menu);
            if (findItem != null) {
                findItem.setOnActionExpandListener(this);
                SearchView searchView = (SearchView) findItem.getActionView();
                this.mSearchView = searchView;
                searchView.setQueryHint(getText(R.string.search_settings));
                this.mSearchView.setOnQueryTextListener(this);
                if (this.mExpandSearch) {
                    findItem.expandActionView();
                }
            }
            updateOptionsMenu();
        }
    }

    @Override // android.view.MenuItem.OnActionExpandListener
    public boolean onMenuItemActionExpand(MenuItem menuItem) {
        this.mAppBarLayout.setExpanded(false, false);
        ViewCompat.setNestedScrollingEnabled(this.mRecyclerView, false);
        return true;
    }

    @Override // android.view.MenuItem.OnActionExpandListener
    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
        this.mAppBarLayout.setExpanded(false, false);
        ViewCompat.setNestedScrollingEnabled(this.mRecyclerView, true);
        return true;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onPrepareOptionsMenu(Menu menu) {
        updateOptionsMenu();
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyOptionsMenu() {
        this.mOptionsMenu = null;
    }

    void updateOptionsMenu() {
        int i;
        int i2;
        Menu menu = this.mOptionsMenu;
        if (menu != null) {
            menu.findItem(R.id.advanced).setVisible(false);
            boolean z = true;
            this.mOptionsMenu.findItem(R.id.sort_order_alpha).setVisible(this.mListType == 3 && this.mSortOrder != R.id.sort_order_alpha);
            this.mOptionsMenu.findItem(R.id.sort_order_size).setVisible(this.mListType == 3 && this.mSortOrder != R.id.sort_order_size);
            this.mOptionsMenu.findItem(R.id.show_system).setVisible((this.mShowSystem || (i2 = this.mListType) == 5 || i2 == 14) ? false : true);
            this.mOptionsMenu.findItem(R.id.hide_system).setVisible((!this.mShowSystem || (i = this.mListType) == 5 || i == 14) ? false : true);
            MenuItem findItem = this.mOptionsMenu.findItem(R.id.reset_app_preferences);
            if (this.mListType != 0) {
                z = false;
            }
            findItem.setVisible(z);
            this.mOptionsMenu.findItem(R.id.sort_order_recent_notification).setVisible(false);
            this.mOptionsMenu.findItem(R.id.sort_order_frequent_notification).setVisible(false);
            MenuItem findItem2 = this.mOptionsMenu.findItem(11);
            if (findItem2 != null) {
                findItem2.setVisible(false);
            }
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        int itemId2 = menuItem.getItemId();
        if (itemId2 == R.id.sort_order_alpha || itemId2 == R.id.sort_order_size) {
            ApplicationsAdapter applicationsAdapter = this.mApplications;
            if (applicationsAdapter != null) {
                applicationsAdapter.rebuild(itemId, false);
            }
        } else if (itemId2 == R.id.show_system || itemId2 == R.id.hide_system) {
            this.mShowSystem = !this.mShowSystem;
            this.mApplications.rebuild();
        } else if (itemId2 == R.id.reset_app_preferences) {
            this.mResetAppsHelper.buildResetDialog();
            return true;
        } else if (itemId2 != R.id.advanced) {
            return false;
        } else {
            if (this.mListType == 1) {
                new SubSettingLauncher(getContext()).setDestination(ConfigureNotificationSettings.class.getName()).setTitleRes(R.string.configure_notification_settings).setSourceMetricsCategory(getMetricsCategory()).setResultListener(this, 2).launch();
            } else {
                startActivityForResult(new Intent("android.settings.MANAGE_DEFAULT_APPS_SETTINGS"), 2);
            }
            return true;
        }
        updateOptionsMenu();
        return true;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (this.mApplications != null) {
            int childAdapterPosition = this.mRecyclerView.getChildAdapterPosition(view);
            if (childAdapterPosition == -1) {
                Log.w("ManageApplications", "Cannot find position for child, skipping onClick handling");
            } else if (this.mApplications.getApplicationCount() > childAdapterPosition) {
                ApplicationInfo applicationInfo = this.mApplications.getAppEntry(childAdapterPosition).info;
                this.mCurrentPkgName = applicationInfo.packageName;
                this.mCurrentUid = applicationInfo.uid;
                startApplicationDetailsActivity();
                ViewCompat.setNestedScrollingEnabled(this.mRecyclerView, true);
            }
        }
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        this.mFilter = this.mFilterAdapter.getFilter(i);
        setCompositeFilter();
        this.mApplications.setFilter(this.mFilter);
        if (DEBUG) {
            Log.d("ManageApplications", "Selecting filter " + ((Object) getContext().getText(this.mFilter.getTitle())));
        }
    }

    @Override // android.widget.SearchView.OnQueryTextListener
    public boolean onQueryTextChange(String str) {
        this.mApplications.filterSearch(str);
        return false;
    }

    public void updateView() {
        updateOptionsMenu();
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.invalidateOptionsMenu();
        }
    }

    public void setHasDisabled(boolean z) {
        if (this.mListType == 0) {
            this.mFilterAdapter.setFilterEnabled(5, z);
            this.mFilterAdapter.setFilterEnabled(7, z);
        }
    }

    public void setHasInstant(boolean z) {
        if (LIST_TYPES_WITH_INSTANT.contains(Integer.valueOf(this.mListType))) {
            this.mFilterAdapter.setFilterEnabled(6, z);
        }
    }

    private void disableToolBarScrollableBehavior() {
        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() { // from class: com.android.settings.applications.manageapplications.ManageApplications.1
            @Override // com.google.android.material.appbar.AppBarLayout.BaseBehavior.BaseDragCallback
            public boolean canDrag(AppBarLayout appBarLayout) {
                return false;
            }
        });
        ((CoordinatorLayout.LayoutParams) this.mAppBarLayout.getLayoutParams()).setBehavior(behavior);
    }

    public static int getTitleResId(Intent intent, Bundle bundle) {
        int intExtra = intent.getIntExtra(":settings:show_fragment_title_resid", R.string.all_apps);
        String string = bundle != null ? bundle.getString("classname") : null;
        if (string == null) {
            string = intent.getComponent().getClassName();
        }
        return string.equals(Settings.UsageAccessSettingsActivity.class.getName()) ? R.string.usage_access : string.equals(Settings.HighPowerApplicationsActivity.class.getName()) ? R.string.high_power_apps : string.equals(Settings.OverlaySettingsActivity.class.getName()) ? R.string.system_alert_window_settings : string.equals(Settings.WriteSettingsActivity.class.getName()) ? R.string.write_settings : string.equals(Settings.ManageExternalSourcesActivity.class.getName()) ? R.string.install_other_apps : string.equals(Settings.ChangeWifiStateActivity.class.getName()) ? R.string.change_wifi_state_title : string.equals(Settings.ManageExternalStorageActivity.class.getName()) ? R.string.manage_external_storage_title : string.equals(Settings.MediaManagementAppsActivity.class.getName()) ? R.string.media_management_apps_title : string.equals(Settings.AlarmsAndRemindersActivity.class.getName()) ? R.string.alarms_and_reminders_title : string.equals(Settings.NotificationAppListActivity.class.getName()) ? R.string.app_notifications_title : string.equals(AppLocaleDetails.class.getName()) ? R.string.app_locales_picker_menu_title : intExtra == -1 ? R.string.all_apps : intExtra;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class FilterSpinnerAdapter extends SettingsSpinnerAdapter<CharSequence> {
        private final Context mContext;
        private final ArrayList<AppFilterItem> mFilterOptions = new ArrayList<>();
        private final ManageApplications mManageApplications;

        public FilterSpinnerAdapter(ManageApplications manageApplications) {
            super(manageApplications.getContext());
            this.mContext = manageApplications.getContext();
            this.mManageApplications = manageApplications;
        }

        public AppFilterItem getFilter(int i) {
            return this.mFilterOptions.get(i);
        }

        public void setFilterEnabled(int i, boolean z) {
            if (z) {
                enableFilter(i);
            } else {
                disableFilter(i);
            }
        }

        public void enableFilter(int i) {
            AppFilterItem appFilterItem = AppFilterRegistry.getInstance().get(i);
            if (!this.mFilterOptions.contains(appFilterItem)) {
                boolean z = ManageApplications.DEBUG;
                if (z) {
                    Log.d("ManageApplications", "Enabling filter " + ((Object) this.mContext.getText(appFilterItem.getTitle())));
                }
                this.mFilterOptions.add(appFilterItem);
                Collections.sort(this.mFilterOptions);
                updateFilterView(this.mFilterOptions.size() > 1);
                notifyDataSetChanged();
                if (this.mFilterOptions.size() == 1) {
                    if (z) {
                        Log.d("ManageApplications", "Auto selecting filter " + appFilterItem + " " + ((Object) this.mContext.getText(appFilterItem.getTitle())));
                    }
                    this.mManageApplications.mFilterSpinner.setSelection(0);
                    this.mManageApplications.onItemSelected(null, null, 0, 0L);
                }
                if (this.mFilterOptions.size() > 1) {
                    int indexOf = this.mFilterOptions.indexOf(AppFilterRegistry.getInstance().get(this.mManageApplications.mFilterType));
                    if (indexOf != -1) {
                        this.mManageApplications.mFilterSpinner.setSelection(indexOf);
                        this.mManageApplications.onItemSelected(null, null, indexOf, 0L);
                    }
                }
            }
        }

        public void disableFilter(int i) {
            AppFilterItem appFilterItem = AppFilterRegistry.getInstance().get(i);
            if (this.mFilterOptions.remove(appFilterItem)) {
                boolean z = ManageApplications.DEBUG;
                if (z) {
                    Log.d("ManageApplications", "Disabling filter " + appFilterItem + " " + ((Object) this.mContext.getText(appFilterItem.getTitle())));
                }
                Collections.sort(this.mFilterOptions);
                boolean z2 = true;
                if (this.mFilterOptions.size() <= 1) {
                    z2 = false;
                }
                updateFilterView(z2);
                notifyDataSetChanged();
                if (this.mManageApplications.mFilter == appFilterItem && this.mFilterOptions.size() > 0) {
                    if (z) {
                        Log.d("ManageApplications", "Auto selecting filter " + this.mFilterOptions.get(0) + ((Object) this.mContext.getText(this.mFilterOptions.get(0).getTitle())));
                    }
                    this.mManageApplications.mFilterSpinner.setSelection(0);
                    this.mManageApplications.onItemSelected(null, null, 0, 0L);
                }
            }
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public int getCount() {
            return this.mFilterOptions.size();
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public CharSequence getItem(int i) {
            return this.mContext.getText(this.mFilterOptions.get(i).getTitle());
        }

        void updateFilterView(boolean z) {
            if (z) {
                this.mManageApplications.mSpinnerHeader.setVisibility(0);
            } else {
                this.mManageApplications.mSpinnerHeader.setVisibility(8);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class ApplicationsAdapter extends RecyclerView.Adapter<ApplicationViewHolder> implements ApplicationsState.Callbacks, AppStateBaseBridge.Callback {
        private AppFilterItem mAppFilter;
        private PowerAllowlistBackend mBackend;
        private ApplicationsState.AppFilter mCompositeFilter;
        private final Context mContext;
        private ArrayList<ApplicationsState.AppEntry> mEntries;
        private final AppStateBaseBridge mExtraInfoBridge;
        private boolean mHasReceivedBridgeCallback;
        private boolean mHasReceivedLoadEntries;
        private final IconDrawableFactory mIconDrawableFactory;
        private int mLastIndex;
        private final LoadingViewController mLoadingViewController;
        private final ManageApplications mManageApplications;
        OnScrollListener mOnScrollListener;
        private ArrayList<ApplicationsState.AppEntry> mOriginalEntries;
        private RecyclerView mRecyclerView;
        private boolean mResumed;
        private SearchFilter mSearchFilter;
        private final ApplicationsState.Session mSession;
        private final ApplicationsState mState;
        private int mLastSortMode = -1;
        private int mWhichSize = 0;

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            return 0;
        }

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        public void onPackageIconChanged() {
        }

        public ApplicationsAdapter(ApplicationsState applicationsState, ManageApplications manageApplications, AppFilterItem appFilterItem, Bundle bundle) {
            this.mLastIndex = -1;
            setHasStableIds(true);
            this.mState = applicationsState;
            this.mSession = applicationsState.newSession(this);
            this.mManageApplications = manageApplications;
            this.mLoadingViewController = new LoadingViewController(manageApplications.mLoadingContainer, manageApplications.mRecyclerView, manageApplications.mEmptyView);
            FragmentActivity activity = manageApplications.getActivity();
            this.mContext = activity;
            this.mIconDrawableFactory = IconDrawableFactory.newInstance(activity);
            this.mAppFilter = appFilterItem;
            PowerAllowlistBackend instance = PowerAllowlistBackend.getInstance(activity);
            this.mBackend = instance;
            int i = manageApplications.mListType;
            if (i == 1) {
                this.mExtraInfoBridge = new AppStateNotificationBridge(activity, applicationsState, this, manageApplications.mUsageStatsManager, manageApplications.mUserManager, manageApplications.mNotificationBackend);
            } else if (i == 4) {
                this.mExtraInfoBridge = new AppStateUsageBridge(activity, applicationsState, this);
            } else if (i == 5) {
                instance.refreshList();
                this.mExtraInfoBridge = new AppStatePowerBridge(activity, applicationsState, this);
            } else if (i == 6) {
                this.mExtraInfoBridge = new AppStateOverlayBridge(activity, applicationsState, this);
            } else if (i == 7) {
                this.mExtraInfoBridge = new AppStateWriteSettingsBridge(activity, applicationsState, this);
            } else if (i == 8) {
                this.mExtraInfoBridge = new AppStateInstallAppsBridge(activity, applicationsState, this);
            } else if (i == 10) {
                this.mExtraInfoBridge = new AppStateChangeWifiStateBridge(activity, applicationsState, this);
            } else if (i == 11) {
                this.mExtraInfoBridge = new AppStateManageExternalStorageBridge(activity, applicationsState, this);
            } else if (i == 12) {
                this.mExtraInfoBridge = new AppStateAlarmsAndRemindersBridge(activity, applicationsState, this);
            } else if (i == 13) {
                this.mExtraInfoBridge = new AppStateMediaManagementAppsBridge(activity, applicationsState, this);
            } else if (i == 14) {
                this.mExtraInfoBridge = new AppStateLocaleBridge(activity, applicationsState, this);
            } else {
                this.mExtraInfoBridge = null;
            }
            if (bundle != null) {
                this.mLastIndex = bundle.getInt("state_last_scroll_index");
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            this.mRecyclerView = recyclerView;
            OnScrollListener onScrollListener = new OnScrollListener(this);
            this.mOnScrollListener = onScrollListener;
            this.mRecyclerView.addOnScrollListener(onScrollListener);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);
            this.mRecyclerView.removeOnScrollListener(this.mOnScrollListener);
            this.mOnScrollListener = null;
            this.mRecyclerView = null;
        }

        public void setCompositeFilter(ApplicationsState.AppFilter appFilter) {
            this.mCompositeFilter = appFilter;
            rebuild();
        }

        public void setFilter(AppFilterItem appFilterItem) {
            this.mAppFilter = appFilterItem;
            if (this.mManageApplications.mListType != 1) {
                rebuild();
            } else if (3 == appFilterItem.getFilterType()) {
                rebuild(R.id.sort_order_frequent_notification, false);
            } else if (2 == appFilterItem.getFilterType()) {
                rebuild(R.id.sort_order_recent_notification, false);
            } else if (16 == appFilterItem.getFilterType()) {
                rebuild(R.id.sort_order_alpha, true);
            } else {
                rebuild(R.id.sort_order_alpha, true);
            }
        }

        public void resume(int i) {
            if (ManageApplications.DEBUG) {
                Log.i("ManageApplications", "Resume!  mResumed=" + this.mResumed);
            }
            if (!this.mResumed) {
                this.mResumed = true;
                this.mSession.onResume();
                this.mLastSortMode = i;
                AppStateBaseBridge appStateBaseBridge = this.mExtraInfoBridge;
                if (appStateBaseBridge != null) {
                    appStateBaseBridge.resume();
                }
                rebuild();
                return;
            }
            rebuild(i, false);
        }

        public void pause() {
            if (this.mResumed) {
                this.mResumed = false;
                this.mSession.onPause();
                AppStateBaseBridge appStateBaseBridge = this.mExtraInfoBridge;
                if (appStateBaseBridge != null) {
                    appStateBaseBridge.pause();
                }
            }
        }

        public void onSaveInstanceState(Bundle bundle) {
            bundle.putInt("state_last_scroll_index", ((LinearLayoutManager) this.mManageApplications.mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition());
        }

        public void release() {
            this.mSession.onDestroy();
            AppStateBaseBridge appStateBaseBridge = this.mExtraInfoBridge;
            if (appStateBaseBridge != null) {
                appStateBaseBridge.release();
            }
        }

        public void rebuild(int i, boolean z) {
            if (i != this.mLastSortMode || z) {
                this.mManageApplications.mSortOrder = i;
                this.mLastSortMode = i;
                rebuild();
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public ApplicationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (this.mManageApplications.mListType == 1) {
                view = ApplicationViewHolder.newView(viewGroup, true);
            } else {
                view = ApplicationViewHolder.newView(viewGroup, false);
            }
            return new ApplicationViewHolder(view);
        }

        public void rebuild() {
            final Comparator<ApplicationsState.AppEntry> comparator;
            boolean z = false;
            if (this.mHasReceivedLoadEntries && (this.mExtraInfoBridge == null || this.mHasReceivedBridgeCallback)) {
                if (Environment.isExternalStorageEmulated()) {
                    this.mWhichSize = 0;
                } else {
                    this.mWhichSize = 1;
                }
                ApplicationsState.AppFilter filter = this.mAppFilter.getFilter();
                ApplicationsState.AppFilter appFilter = this.mCompositeFilter;
                if (appFilter != null) {
                    filter = new ApplicationsState.CompoundFilter(filter, appFilter);
                }
                if (!this.mManageApplications.mShowSystem) {
                    if (ManageApplications.LIST_TYPES_WITH_INSTANT.contains(Integer.valueOf(this.mManageApplications.mListType))) {
                        filter = new ApplicationsState.CompoundFilter(filter, ApplicationsState.FILTER_DOWNLOADED_AND_LAUNCHER_AND_INSTANT);
                    } else {
                        filter = new ApplicationsState.CompoundFilter(filter, ApplicationsState.FILTER_DOWNLOADED_AND_LAUNCHER);
                    }
                }
                int i = this.mLastSortMode;
                if (i == R.id.sort_order_size) {
                    int i2 = this.mWhichSize;
                    if (i2 == 1) {
                        comparator = ApplicationsState.INTERNAL_SIZE_COMPARATOR;
                    } else if (i2 != 2) {
                        comparator = ApplicationsState.SIZE_COMPARATOR;
                    } else {
                        comparator = ApplicationsState.EXTERNAL_SIZE_COMPARATOR;
                    }
                } else if (i == R.id.sort_order_recent_notification) {
                    comparator = AppStateNotificationBridge.RECENT_NOTIFICATION_COMPARATOR;
                } else if (i == R.id.sort_order_frequent_notification) {
                    comparator = AppStateNotificationBridge.FREQUENCY_NOTIFICATION_COMPARATOR;
                } else {
                    comparator = ApplicationsState.ALPHA_COMPARATOR;
                }
                final ApplicationsState.CompoundFilter compoundFilter = new ApplicationsState.CompoundFilter(filter, ApplicationsState.FILTER_NOT_HIDE);
                ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.applications.manageapplications.ManageApplications$ApplicationsAdapter$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        ManageApplications.ApplicationsAdapter.this.lambda$rebuild$0(compoundFilter, comparator);
                    }
                });
            } else if (ManageApplications.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("Not rebuilding until all the app entries loaded. !mHasReceivedLoadEntries=");
                sb.append(!this.mHasReceivedLoadEntries);
                sb.append(" !mExtraInfoBridgeNull=");
                if (this.mExtraInfoBridge != null) {
                    z = true;
                }
                sb.append(z);
                sb.append(" !mHasReceivedBridgeCallback=");
                sb.append(!this.mHasReceivedBridgeCallback);
                Log.d("ManageApplications", sb.toString());
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$rebuild$0(ApplicationsState.AppFilter appFilter, Comparator comparator) {
            this.mSession.rebuild(appFilter, comparator, false);
        }

        void filterSearch(String str) {
            if (this.mSearchFilter == null) {
                this.mSearchFilter = new SearchFilter();
            }
            if (this.mOriginalEntries == null) {
                Log.w("ManageApplications", "Apps haven't loaded completely yet, so nothing can be filtered");
            } else {
                this.mSearchFilter.filter(str);
            }
        }

        private static boolean packageNameEquals(PackageItemInfo packageItemInfo, PackageItemInfo packageItemInfo2) {
            String str;
            String str2;
            if (packageItemInfo == null || packageItemInfo2 == null || (str = packageItemInfo.packageName) == null || (str2 = packageItemInfo2.packageName) == null) {
                return false;
            }
            return str.equals(str2);
        }

        private ArrayList<ApplicationsState.AppEntry> removeDuplicateIgnoringUser(ArrayList<ApplicationsState.AppEntry> arrayList) {
            int size = arrayList.size();
            ArrayList<ApplicationsState.AppEntry> arrayList2 = new ArrayList<>(size);
            ApplicationInfo applicationInfo = null;
            int i = 0;
            while (i < size) {
                ApplicationsState.AppEntry appEntry = arrayList.get(i);
                ApplicationInfo applicationInfo2 = appEntry.info;
                if (!packageNameEquals(applicationInfo, applicationInfo2)) {
                    arrayList2.add(appEntry);
                }
                i++;
                applicationInfo = applicationInfo2;
            }
            arrayList2.trimToSize();
            return arrayList2;
        }

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> arrayList) {
            if (ManageApplications.DEBUG) {
                Log.d("ManageApplications", "onRebuildComplete size=" + arrayList.size());
            }
            Context context = this.mContext;
            AppUtils.preloadTopIcons(context, arrayList, context.getResources().getInteger(R.integer.config_num_visible_app_icons));
            int filterType = this.mAppFilter.getFilterType();
            if (filterType == 0 || filterType == 1) {
                arrayList = removeDuplicateIgnoringUser(arrayList);
            }
            this.mEntries = arrayList;
            this.mOriginalEntries = arrayList;
            notifyDataSetChanged();
            if (getItemCount() == 0) {
                this.mLoadingViewController.showEmpty(false);
            } else {
                this.mLoadingViewController.showContent(false);
                if (this.mManageApplications.mSearchView != null && this.mManageApplications.mSearchView.isVisibleToUser()) {
                    CharSequence query = this.mManageApplications.mSearchView.getQuery();
                    if (!TextUtils.isEmpty(query)) {
                        filterSearch(query.toString());
                    }
                }
            }
            if (this.mLastIndex != -1 && getItemCount() > this.mLastIndex) {
                this.mManageApplications.mRecyclerView.getLayoutManager().scrollToPosition(this.mLastIndex);
                this.mLastIndex = -1;
            }
            ManageApplications manageApplications = this.mManageApplications;
            if (manageApplications.mListType != 4) {
                manageApplications.setHasDisabled(this.mState.haveDisabledApps());
                this.mManageApplications.setHasInstant(this.mState.haveInstantApps());
            }
        }

        void updateLoading() {
            if (this.mHasReceivedLoadEntries && this.mSession.getAllApps().size() != 0) {
                this.mLoadingViewController.showContent(false);
            } else {
                this.mLoadingViewController.showLoadingViewDelayed();
            }
        }

        @Override // com.android.settings.applications.AppStateBaseBridge.Callback
        public void onExtraInfoUpdated() {
            this.mHasReceivedBridgeCallback = true;
            rebuild();
        }

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        public void onRunningStateChanged(boolean z) {
            this.mManageApplications.getActivity().setProgressBarIndeterminateVisibility(z);
        }

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        public void onPackageListChanged() {
            rebuild();
        }

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        public void onLoadEntriesCompleted() {
            this.mHasReceivedLoadEntries = true;
            rebuild();
        }

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        public void onPackageSizeChanged(String str) {
            ArrayList<ApplicationsState.AppEntry> arrayList = this.mEntries;
            if (arrayList != null) {
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    ApplicationInfo applicationInfo = this.mEntries.get(i).info;
                    if (applicationInfo != null || TextUtils.equals(str, applicationInfo.packageName)) {
                        if (TextUtils.equals(this.mManageApplications.mCurrentPkgName, applicationInfo.packageName)) {
                            rebuild();
                            return;
                        }
                        this.mOnScrollListener.postNotifyItemChange(i);
                    }
                }
            }
        }

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        public void onLauncherInfoChanged() {
            if (!this.mManageApplications.mShowSystem) {
                rebuild();
            }
        }

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        public void onAllSizesComputed() {
            if (this.mLastSortMode == R.id.sort_order_size) {
                rebuild();
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            ArrayList<ApplicationsState.AppEntry> arrayList = this.mEntries;
            if (arrayList == null) {
                return 0;
            }
            return arrayList.size();
        }

        public int getApplicationCount() {
            ArrayList<ApplicationsState.AppEntry> arrayList = this.mEntries;
            if (arrayList != null) {
                return arrayList.size();
            }
            return 0;
        }

        public ApplicationsState.AppEntry getAppEntry(int i) {
            return this.mEntries.get(i);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int i) {
            if (i == this.mEntries.size()) {
                return -1L;
            }
            return this.mEntries.get(i).id;
        }

        public boolean isEnabled(int i) {
            if (getItemViewType(i) == 1 || this.mManageApplications.mListType != 5) {
                return true;
            }
            ApplicationsState.AppEntry appEntry = this.mEntries.get(i);
            return !this.mBackend.isSysAllowlisted(appEntry.info.packageName) && !this.mBackend.isDefaultActiveApp(appEntry.info.packageName);
        }

        public void onBindViewHolder(ApplicationViewHolder applicationViewHolder, int i) {
            ApplicationsState.AppEntry appEntry = this.mEntries.get(i);
            synchronized (appEntry) {
                this.mState.ensureLabelDescription(appEntry);
                applicationViewHolder.setTitle(appEntry.label, appEntry.labelDescription);
                updateIcon(applicationViewHolder, appEntry);
                updateSummary(applicationViewHolder, appEntry);
                updateSwitch(applicationViewHolder, appEntry);
                applicationViewHolder.updateDisableView(appEntry.info);
            }
            applicationViewHolder.setEnabled(isEnabled(i));
            applicationViewHolder.itemView.setOnClickListener(this.mManageApplications);
        }

        private void updateIcon(final ApplicationViewHolder applicationViewHolder, final ApplicationsState.AppEntry appEntry) {
            Drawable iconFromCache = AppUtils.getIconFromCache(appEntry);
            if (iconFromCache == null || !appEntry.mounted) {
                ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.applications.manageapplications.ManageApplications$ApplicationsAdapter$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        ManageApplications.ApplicationsAdapter.this.lambda$updateIcon$2(appEntry, applicationViewHolder);
                    }
                });
            } else {
                applicationViewHolder.setIcon(iconFromCache);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$updateIcon$2(ApplicationsState.AppEntry appEntry, final ApplicationViewHolder applicationViewHolder) {
            final Drawable icon = AppUtils.getIcon(this.mContext, appEntry);
            if (icon != null) {
                ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.applications.manageapplications.ManageApplications$ApplicationsAdapter$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ApplicationViewHolder.this.setIcon(icon);
                    }
                });
            }
        }

        private void updateSummary(ApplicationViewHolder applicationViewHolder, ApplicationsState.AppEntry appEntry) {
            ManageApplications manageApplications = this.mManageApplications;
            switch (manageApplications.mListType) {
                case 1:
                    Object obj = appEntry.extraInfo;
                    if (obj == null || !(obj instanceof AppStateNotificationBridge.NotificationsSentState)) {
                        applicationViewHolder.setSummary((CharSequence) null);
                        return;
                    } else {
                        applicationViewHolder.setSummary(AppStateNotificationBridge.getSummary(this.mContext, (AppStateNotificationBridge.NotificationsSentState) obj, this.mLastSortMode));
                        return;
                    }
                case 2:
                case 3:
                case 9:
                default:
                    applicationViewHolder.updateSizeText(appEntry, manageApplications.mInvalidSizeStr, this.mWhichSize);
                    return;
                case 4:
                    Object obj2 = appEntry.extraInfo;
                    if (obj2 != null) {
                        applicationViewHolder.setSummary(new AppStateUsageBridge.UsageState((AppStateAppOpsBridge.PermissionState) obj2).isPermissible() ? R.string.app_permission_summary_allowed : R.string.app_permission_summary_not_allowed);
                        return;
                    } else {
                        applicationViewHolder.setSummary((CharSequence) null);
                        return;
                    }
                case 5:
                    applicationViewHolder.setSummary(HighPowerDetail.getSummary(this.mContext, appEntry));
                    return;
                case 6:
                    applicationViewHolder.setSummary(DrawOverlayDetails.getSummary(this.mContext, appEntry));
                    return;
                case 7:
                    applicationViewHolder.setSummary(WriteSettingsDetails.getSummary(this.mContext, appEntry));
                    return;
                case 8:
                    applicationViewHolder.setSummary(ExternalSourcesDetails.getPreferenceSummary(this.mContext, appEntry));
                    return;
                case 10:
                    applicationViewHolder.setSummary(ChangeWifiStateDetails.getSummary(this.mContext, appEntry));
                    return;
                case 11:
                    applicationViewHolder.setSummary(ManageExternalStorageDetails.getSummary(this.mContext, appEntry));
                    return;
                case 12:
                    applicationViewHolder.setSummary(AlarmsAndRemindersDetails.getSummary(this.mContext, appEntry));
                    return;
                case 13:
                    applicationViewHolder.setSummary(MediaManagementAppsDetails.getSummary(this.mContext, appEntry));
                    return;
                case 14:
                    applicationViewHolder.setSummary(AppLocaleDetails.getSummary(this.mContext, appEntry.info.packageName));
                    return;
            }
        }

        private void updateSwitch(ApplicationViewHolder applicationViewHolder, ApplicationsState.AppEntry appEntry) {
            if (this.mManageApplications.mListType == 1) {
                applicationViewHolder.updateSwitch(((AppStateNotificationBridge) this.mExtraInfoBridge).getSwitchOnCheckedListener(appEntry), AppStateNotificationBridge.enableSwitch(appEntry), AppStateNotificationBridge.checkSwitch(appEntry));
                Object obj = appEntry.extraInfo;
                if (obj == null || !(obj instanceof AppStateNotificationBridge.NotificationsSentState)) {
                    applicationViewHolder.setSummary((CharSequence) null);
                } else {
                    applicationViewHolder.setSummary(AppStateNotificationBridge.getSummary(this.mContext, (AppStateNotificationBridge.NotificationsSentState) obj, this.mLastSortMode));
                }
            }
        }

        /* loaded from: classes.dex */
        public static class OnScrollListener extends RecyclerView.OnScrollListener {
            private ApplicationsAdapter mAdapter;
            private boolean mDelayNotifyDataChange;
            private int mScrollState = 0;

            public OnScrollListener(ApplicationsAdapter applicationsAdapter) {
                this.mAdapter = applicationsAdapter;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                this.mScrollState = i;
                if (i == 0 && this.mDelayNotifyDataChange) {
                    this.mDelayNotifyDataChange = false;
                    this.mAdapter.notifyDataSetChanged();
                }
            }

            public void postNotifyItemChange(int i) {
                if (this.mScrollState == 0) {
                    this.mAdapter.notifyItemChanged(i);
                } else {
                    this.mDelayNotifyDataChange = true;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public class SearchFilter extends Filter {
            private SearchFilter() {
            }

            @Override // android.widget.Filter
            protected Filter.FilterResults performFiltering(CharSequence charSequence) {
                ArrayList arrayList;
                if (TextUtils.isEmpty(charSequence)) {
                    arrayList = ApplicationsAdapter.this.mOriginalEntries;
                } else {
                    ArrayList arrayList2 = new ArrayList();
                    Iterator it = ApplicationsAdapter.this.mOriginalEntries.iterator();
                    while (it.hasNext()) {
                        ApplicationsState.AppEntry appEntry = (ApplicationsState.AppEntry) it.next();
                        if (appEntry.label.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            arrayList2.add(appEntry);
                        }
                    }
                    arrayList = arrayList2;
                }
                Filter.FilterResults filterResults = new Filter.FilterResults();
                filterResults.values = arrayList;
                filterResults.count = arrayList.size();
                return filterResults;
            }

            @Override // android.widget.Filter
            protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
                ApplicationsAdapter.this.mEntries = (ArrayList) filterResults.values;
                ApplicationsAdapter.this.notifyDataSetChanged();
            }
        }
    }
}
