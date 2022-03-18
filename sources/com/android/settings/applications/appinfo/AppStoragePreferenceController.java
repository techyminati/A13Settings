package com.android.settings.applications.appinfo;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.format.Formatter;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.preference.Preference;
import androidx.window.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.applications.AppStorageSettings;
import com.android.settings.applications.FetchPackageStorageAsyncLoader;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.applications.StorageStatsSource;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
/* loaded from: classes.dex */
public class AppStoragePreferenceController extends AppInfoPreferenceControllerBase implements LoaderManager.LoaderCallbacks<StorageStatsSource.AppStorageStats>, LifecycleObserver, OnResume, OnPause {
    private StorageStatsSource.AppStorageStats mLastResult;

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoaderReset(Loader<StorageStatsSource.AppStorageStats> loader) {
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AppStoragePreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ApplicationInfo applicationInfo;
        ApplicationsState.AppEntry appEntry = this.mParent.getAppEntry();
        if (appEntry != null && (applicationInfo = appEntry.info) != null) {
            preference.setSummary(getStorageSummary(this.mLastResult, (applicationInfo.flags & 262144) != 0));
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        this.mParent.getLoaderManager().restartLoader(3, Bundle.EMPTY, this);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        this.mParent.getLoaderManager().destroyLoader(3);
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase
    protected Class<? extends SettingsPreferenceFragment> getDetailFragmentClass() {
        return AppStorageSettings.class;
    }

    CharSequence getStorageSummary(StorageStatsSource.AppStorageStats appStorageStats, boolean z) {
        if (appStorageStats == null) {
            return this.mContext.getText(R.string.computing_size);
        }
        String string = this.mContext.getString(z ? R.string.storage_type_external : R.string.storage_type_internal);
        Context context = this.mContext;
        return context.getString(R.string.storage_summary_format, Formatter.formatFileSize(context, appStorageStats.getTotalBytes()), string.toString());
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public Loader<StorageStatsSource.AppStorageStats> onCreateLoader(int i, Bundle bundle) {
        Context context = this.mContext;
        return new FetchPackageStorageAsyncLoader(context, new StorageStatsSource(context), this.mParent.getAppEntry().info, UserHandle.of(UserHandle.myUserId()));
    }

    public void onLoadFinished(Loader<StorageStatsSource.AppStorageStats> loader, StorageStatsSource.AppStorageStats appStorageStats) {
        this.mLastResult = appStorageStats;
        updateState(this.mPreference);
    }
}
