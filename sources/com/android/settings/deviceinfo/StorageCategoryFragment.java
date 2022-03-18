package com.android.settings.deviceinfo;

import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.util.Pair;
import android.util.SparseArray;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.preference.Preference;
import androidx.window.R;
import com.android.settings.Utils;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.deviceinfo.StorageCategoryFragment;
import com.android.settings.deviceinfo.storage.SecondaryUserController;
import com.android.settings.deviceinfo.storage.StorageAsyncLoader;
import com.android.settings.deviceinfo.storage.StorageEntry;
import com.android.settings.deviceinfo.storage.StorageItemPreferenceController;
import com.android.settings.deviceinfo.storage.UserIconLoader;
import com.android.settings.deviceinfo.storage.VolumeSizesLoader;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.applications.StorageStatsSource;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.deviceinfo.PrivateStorageInfo;
import com.android.settingslib.deviceinfo.StorageManagerVolumeProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class StorageCategoryFragment extends DashboardFragment implements LoaderManager.LoaderCallbacks<SparseArray<StorageAsyncLoader.StorageResult>>, Preference.OnPreferenceClickListener {
    private SparseArray<StorageAsyncLoader.StorageResult> mAppsResult;
    private Preference mFreeUpSpacePreference;
    private boolean mIsWorkProfile;
    private StorageItemPreferenceController mPreferenceController;
    private List<AbstractPreferenceController> mSecondaryUsers;
    private StorageEntry mSelectedStorageEntry;
    private PrivateStorageInfo mStorageInfo;
    private StorageManager mStorageManager;
    private int mUserId;
    private UserManager mUserManager;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "StorageCategoryFrag";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 745;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.storage_category_fragment;
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoaderReset(Loader<SparseArray<StorageAsyncLoader.StorageResult>> loader) {
    }

    public void refreshUi(StorageEntry storageEntry) {
        this.mSelectedStorageEntry = storageEntry;
        if (this.mPreferenceController != null) {
            setSecondaryUsersVisible(false);
            if (!this.mSelectedStorageEntry.isMounted()) {
                this.mPreferenceController.setVolume(null);
            } else if (this.mSelectedStorageEntry.isPrivate()) {
                this.mStorageInfo = null;
                this.mAppsResult = null;
                maybeSetLoading(isQuotaSupported());
                this.mPreferenceController.setVolume(null);
                LoaderManager loaderManager = getLoaderManager();
                Bundle bundle = Bundle.EMPTY;
                loaderManager.restartLoader(0, bundle, this);
                getLoaderManager().restartLoader(2, bundle, new VolumeSizeCallbacks());
                getLoaderManager().restartLoader(1, bundle, new IconLoaderCallbacks());
            } else {
                this.mPreferenceController.setVolume(this.mSelectedStorageEntry.getVolumeInfo());
            }
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mStorageManager = (StorageManager) getActivity().getSystemService(StorageManager.class);
        if (bundle != null) {
            this.mSelectedStorageEntry = (StorageEntry) bundle.getParcelable("selected_storage_entry_key");
        }
        initializePreference();
    }

    private void initializePreference() {
        Preference findPreference = getPreferenceScreen().findPreference("free_up_space");
        this.mFreeUpSpacePreference = findPreference;
        findPreference.setOnPreferenceClickListener(this);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
        boolean z = getArguments().getInt("profile") == 2;
        this.mIsWorkProfile = z;
        this.mUserId = Utils.getCurrentUserId(this.mUserManager, z);
        super.onAttach(context);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        StorageEntry storageEntry = this.mSelectedStorageEntry;
        if (storageEntry != null) {
            refreshUi(storageEntry);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putParcelable("selected_storage_entry_key", this.mSelectedStorageEntry);
        super.onSaveInstanceState(bundle);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onReceivedSizes() {
        if (!(this.mStorageInfo == null || this.mAppsResult == null)) {
            if (getView().findViewById(R.id.loading_container).getVisibility() == 0) {
                setLoading(false, true);
            }
            PrivateStorageInfo privateStorageInfo = this.mStorageInfo;
            long j = privateStorageInfo.totalBytes - privateStorageInfo.freeBytes;
            this.mPreferenceController.setVolume(this.mSelectedStorageEntry.getVolumeInfo());
            this.mPreferenceController.setUsedSize(j);
            this.mPreferenceController.setTotalSize(this.mStorageInfo.totalBytes);
            int size = this.mSecondaryUsers.size();
            for (int i = 0; i < size; i++) {
                AbstractPreferenceController abstractPreferenceController = this.mSecondaryUsers.get(i);
                if (abstractPreferenceController instanceof SecondaryUserController) {
                    ((SecondaryUserController) abstractPreferenceController).setTotalSize(this.mStorageInfo.totalBytes);
                }
            }
            this.mPreferenceController.onLoadFinished(this.mAppsResult, this.mUserId);
            updateSecondaryUserControllers(this.mSecondaryUsers, this.mAppsResult);
            setSecondaryUsersVisible(true);
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        StorageItemPreferenceController storageItemPreferenceController = new StorageItemPreferenceController(context, this, null, new StorageManagerVolumeProvider((StorageManager) context.getSystemService(StorageManager.class)), this.mIsWorkProfile);
        this.mPreferenceController = storageItemPreferenceController;
        arrayList.add(storageItemPreferenceController);
        List<AbstractPreferenceController> secondaryUserControllers = SecondaryUserController.getSecondaryUserControllers(context, this.mUserManager, this.mIsWorkProfile);
        this.mSecondaryUsers = secondaryUserControllers;
        arrayList.addAll(secondaryUserControllers);
        return arrayList;
    }

    private void updateSecondaryUserControllers(List<AbstractPreferenceController> list, SparseArray<StorageAsyncLoader.StorageResult> sparseArray) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            AbstractPreferenceController abstractPreferenceController = list.get(i);
            if (abstractPreferenceController instanceof StorageAsyncLoader.ResultHandler) {
                ((StorageAsyncLoader.ResultHandler) abstractPreferenceController).handleResult(sparseArray);
            }
        }
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public Loader<SparseArray<StorageAsyncLoader.StorageResult>> onCreateLoader(int i, Bundle bundle) {
        Context context = getContext();
        return new StorageAsyncLoader(context, this.mUserManager, this.mSelectedStorageEntry.getFsUuid(), new StorageStatsSource(context), context.getPackageManager());
    }

    public void onLoadFinished(Loader<SparseArray<StorageAsyncLoader.StorageResult>> loader, SparseArray<StorageAsyncLoader.StorageResult> sparseArray) {
        this.mAppsResult = sparseArray;
        onReceivedSizes();
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (preference != this.mFreeUpSpacePreference) {
            return false;
        }
        Context context = getContext();
        MetricsFeatureProvider metricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
        metricsFeatureProvider.logClickedPreference(preference, getMetricsCategory());
        metricsFeatureProvider.action(context, 840, new Pair[0]);
        Intent intent = new Intent("android.os.storage.action.MANAGE_STORAGE");
        intent.addFlags(268435456);
        context.startActivityAsUser(intent, new UserHandle(this.mUserId));
        return true;
    }

    public PrivateStorageInfo getPrivateStorageInfo() {
        return this.mStorageInfo;
    }

    public void setPrivateStorageInfo(PrivateStorageInfo privateStorageInfo) {
        this.mStorageInfo = privateStorageInfo;
    }

    public SparseArray<StorageAsyncLoader.StorageResult> getStorageResult() {
        return this.mAppsResult;
    }

    public void setStorageResult(SparseArray<StorageAsyncLoader.StorageResult> sparseArray) {
        this.mAppsResult = sparseArray;
    }

    public void maybeSetLoading(boolean z) {
        if ((z && (this.mStorageInfo == null || this.mAppsResult == null)) || (!z && this.mStorageInfo == null)) {
            setLoading(true, false);
        }
    }

    private boolean isQuotaSupported() {
        return this.mSelectedStorageEntry.isMounted() && ((StorageStatsManager) getActivity().getSystemService(StorageStatsManager.class)).isQuotaSupported(this.mSelectedStorageEntry.getFsUuid());
    }

    private void setSecondaryUsersVisible(boolean z) {
        Optional findAny = this.mSecondaryUsers.stream().filter(StorageCategoryFragment$$ExternalSyntheticLambda1.INSTANCE).map(StorageCategoryFragment$$ExternalSyntheticLambda0.INSTANCE).findAny();
        if (findAny.isPresent()) {
            ((SecondaryUserController) findAny.get()).setPreferenceGroupVisible(z);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$setSecondaryUsersVisible$0(AbstractPreferenceController abstractPreferenceController) {
        return abstractPreferenceController instanceof SecondaryUserController;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ SecondaryUserController lambda$setSecondaryUsersVisible$1(AbstractPreferenceController abstractPreferenceController) {
        return (SecondaryUserController) abstractPreferenceController;
    }

    /* loaded from: classes.dex */
    public final class IconLoaderCallbacks implements LoaderManager.LoaderCallbacks<SparseArray<Drawable>> {
        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<SparseArray<Drawable>> loader) {
        }

        public IconLoaderCallbacks() {
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<SparseArray<Drawable>> onCreateLoader(int i, Bundle bundle) {
            return new UserIconLoader(StorageCategoryFragment.this.getContext(), new UserIconLoader.FetchUserIconTask() { // from class: com.android.settings.deviceinfo.StorageCategoryFragment$IconLoaderCallbacks$$ExternalSyntheticLambda0
                @Override // com.android.settings.deviceinfo.storage.UserIconLoader.FetchUserIconTask
                public final SparseArray getUserIcons() {
                    SparseArray lambda$onCreateLoader$0;
                    lambda$onCreateLoader$0 = StorageCategoryFragment.IconLoaderCallbacks.this.lambda$onCreateLoader$0();
                    return lambda$onCreateLoader$0;
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ SparseArray lambda$onCreateLoader$0() {
            return UserIconLoader.loadUserIconsWithContext(StorageCategoryFragment.this.getContext());
        }

        public void onLoadFinished(Loader<SparseArray<Drawable>> loader, final SparseArray<Drawable> sparseArray) {
            StorageCategoryFragment.this.mSecondaryUsers.stream().filter(StorageCategoryFragment$IconLoaderCallbacks$$ExternalSyntheticLambda2.INSTANCE).forEach(new Consumer() { // from class: com.android.settings.deviceinfo.StorageCategoryFragment$IconLoaderCallbacks$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    StorageCategoryFragment.IconLoaderCallbacks.lambda$onLoadFinished$2(sparseArray, (AbstractPreferenceController) obj);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ boolean lambda$onLoadFinished$1(AbstractPreferenceController abstractPreferenceController) {
            return abstractPreferenceController instanceof UserIconLoader.UserIconHandler;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$onLoadFinished$2(SparseArray sparseArray, AbstractPreferenceController abstractPreferenceController) {
            ((UserIconLoader.UserIconHandler) abstractPreferenceController).handleUserIcons(sparseArray);
        }
    }

    /* loaded from: classes.dex */
    public final class VolumeSizeCallbacks implements LoaderManager.LoaderCallbacks<PrivateStorageInfo> {
        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<PrivateStorageInfo> loader) {
        }

        public VolumeSizeCallbacks() {
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<PrivateStorageInfo> onCreateLoader(int i, Bundle bundle) {
            Context context = StorageCategoryFragment.this.getContext();
            return new VolumeSizesLoader(context, new StorageManagerVolumeProvider(StorageCategoryFragment.this.mStorageManager), (StorageStatsManager) context.getSystemService(StorageStatsManager.class), StorageCategoryFragment.this.mSelectedStorageEntry.getVolumeInfo());
        }

        public void onLoadFinished(Loader<PrivateStorageInfo> loader, PrivateStorageInfo privateStorageInfo) {
            if (privateStorageInfo == null) {
                StorageCategoryFragment.this.getActivity().finish();
                return;
            }
            StorageCategoryFragment.this.mStorageInfo = privateStorageInfo;
            StorageCategoryFragment.this.onReceivedSizes();
        }
    }
}
