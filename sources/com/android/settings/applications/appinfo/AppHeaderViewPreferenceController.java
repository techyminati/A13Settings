package com.android.settings.applications.appinfo;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import androidx.preference.PreferenceScreen;
import androidx.window.R;
import com.android.settings.applications.appinfo.AppInfoDashboardFragment;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.widget.EntityHeaderController;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.widget.LayoutPreference;
/* loaded from: classes.dex */
public class AppHeaderViewPreferenceController extends BasePreferenceController implements AppInfoDashboardFragment.Callback, LifecycleObserver {
    private static final String KEY_HEADER = "header_view";
    private EntityHeaderController mEntityHeaderController;
    private LayoutPreference mHeader;
    private final Lifecycle mLifecycle;
    private final String mPackageName;
    private final AppInfoDashboardFragment mParent;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AppHeaderViewPreferenceController(Context context, AppInfoDashboardFragment appInfoDashboardFragment, String str, Lifecycle lifecycle) {
        super(context, KEY_HEADER);
        this.mParent = appInfoDashboardFragment;
        this.mPackageName = str;
        this.mLifecycle = lifecycle;
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mHeader = (LayoutPreference) preferenceScreen.findPreference(KEY_HEADER);
        this.mEntityHeaderController = EntityHeaderController.newInstance(this.mParent.getActivity(), this.mParent, this.mHeader.findViewById(R.id.entity_header)).setRecyclerView(this.mParent.getListView(), this.mLifecycle).setPackageName(this.mPackageName).setButtonActions(0, 0).bindHeaderButtons();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoDashboardFragment.Callback
    public void refreshUi() {
        setAppLabelAndIcon(this.mParent.getPackageInfo(), this.mParent.getAppEntry());
    }

    private void setAppLabelAndIcon(PackageInfo packageInfo, ApplicationsState.AppEntry appEntry) {
        this.mEntityHeaderController.setLabel(appEntry).setIcon(appEntry).setIsInstantApp(AppUtils.isInstant(packageInfo.applicationInfo)).done((Activity) this.mParent.getActivity(), false);
    }
}
