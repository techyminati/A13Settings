package com.android.settings.dashboard.profileselector;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.storage.DiskInfo;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.os.storage.VolumeRecord;
import android.text.TextUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.window.R;
import com.android.settings.Utils;
import com.android.settings.dashboard.profileselector.ProfileSelectStorageFragment;
import com.android.settings.deviceinfo.StorageCategoryFragment;
import com.android.settings.deviceinfo.VolumeOptionMenuController;
import com.android.settings.deviceinfo.storage.AutomaticStorageManagementSwitchPreferenceController;
import com.android.settings.deviceinfo.storage.DiskInitFragment;
import com.android.settings.deviceinfo.storage.StorageEntry;
import com.android.settings.deviceinfo.storage.StorageSelectionPreferenceController;
import com.android.settings.deviceinfo.storage.StorageUsageProgressBarPreferenceController;
import com.android.settings.deviceinfo.storage.StorageUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class ProfileSelectStorageFragment extends ProfileSelectFragment {
    private Fragment[] mFragments;
    private VolumeOptionMenuController mOptionMenuController;
    private StorageEntry mSelectedStorageEntry;
    private final List<StorageEntry> mStorageEntries = new ArrayList();
    private final StorageEventListener mStorageEventListener = new AnonymousClass1();
    private StorageManager mStorageManager;
    private StorageSelectionPreferenceController mStorageSelectionController;
    private StorageUsageProgressBarPreferenceController mStorageUsageProgressBarController;

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_url_storage_dashboard;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.profileselector.ProfileSelectFragment, com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "ProfileSelStorageFrag";
    }

    @Override // com.android.settings.dashboard.profileselector.ProfileSelectFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 745;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.profileselector.ProfileSelectFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.storage_dashboard_header_fragment;
    }

    /* renamed from: com.android.settings.dashboard.profileselector.ProfileSelectStorageFragment$1  reason: invalid class name */
    /* loaded from: classes.dex */
    class AnonymousClass1 extends StorageEventListener {
        AnonymousClass1() {
        }

        public void onVolumeStateChanged(VolumeInfo volumeInfo, int i, int i2) {
            if (StorageUtils.isStorageSettingsInterestedVolume(volumeInfo)) {
                final StorageEntry storageEntry = new StorageEntry(ProfileSelectStorageFragment.this.getContext(), volumeInfo);
                int state = volumeInfo.getState();
                if (state != 0) {
                    if (!(state == 2 || state == 3)) {
                        if (state != 5) {
                            if (state != 6) {
                                if (!(state == 7 || state == 8)) {
                                    return;
                                }
                            }
                        }
                    }
                    ProfileSelectStorageFragment.this.mStorageEntries.removeIf(new Predicate() { // from class: com.android.settings.dashboard.profileselector.ProfileSelectStorageFragment$1$$ExternalSyntheticLambda1
                        @Override // java.util.function.Predicate
                        public final boolean test(Object obj) {
                            boolean equals;
                            equals = ((StorageEntry) obj).equals(StorageEntry.this);
                            return equals;
                        }
                    });
                    ProfileSelectStorageFragment.this.mStorageEntries.add(storageEntry);
                    if (storageEntry.equals(ProfileSelectStorageFragment.this.mSelectedStorageEntry)) {
                        ProfileSelectStorageFragment.this.mSelectedStorageEntry = storageEntry;
                    }
                    ProfileSelectStorageFragment.this.refreshUi();
                    return;
                }
                if (ProfileSelectStorageFragment.this.mStorageEntries.remove(storageEntry)) {
                    if (storageEntry.equals(ProfileSelectStorageFragment.this.mSelectedStorageEntry)) {
                        ProfileSelectStorageFragment profileSelectStorageFragment = ProfileSelectStorageFragment.this;
                        profileSelectStorageFragment.mSelectedStorageEntry = StorageEntry.getDefaultInternalStorageEntry(profileSelectStorageFragment.getContext());
                    }
                    ProfileSelectStorageFragment.this.refreshUi();
                }
            }
        }

        public void onVolumeRecordChanged(final VolumeRecord volumeRecord) {
            if (StorageUtils.isVolumeRecordMissed(ProfileSelectStorageFragment.this.mStorageManager, volumeRecord)) {
                StorageEntry storageEntry = new StorageEntry(volumeRecord);
                if (!ProfileSelectStorageFragment.this.mStorageEntries.contains(storageEntry)) {
                    ProfileSelectStorageFragment.this.mStorageEntries.add(storageEntry);
                    ProfileSelectStorageFragment.this.refreshUi();
                    return;
                }
                return;
            }
            VolumeInfo findVolumeByUuid = ProfileSelectStorageFragment.this.mStorageManager.findVolumeByUuid(volumeRecord.getFsUuid());
            if (findVolumeByUuid != null && ProfileSelectStorageFragment.this.mStorageEntries.removeIf(new Predicate() { // from class: com.android.settings.dashboard.profileselector.ProfileSelectStorageFragment$1$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$onVolumeRecordChanged$1;
                    lambda$onVolumeRecordChanged$1 = ProfileSelectStorageFragment.AnonymousClass1.lambda$onVolumeRecordChanged$1(volumeRecord, (StorageEntry) obj);
                    return lambda$onVolumeRecordChanged$1;
                }
            })) {
                ProfileSelectStorageFragment.this.mStorageEntries.add(new StorageEntry(ProfileSelectStorageFragment.this.getContext(), findVolumeByUuid));
                ProfileSelectStorageFragment.this.refreshUi();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ boolean lambda$onVolumeRecordChanged$1(VolumeRecord volumeRecord, StorageEntry storageEntry) {
            return storageEntry.isVolumeInfo() && TextUtils.equals(storageEntry.getFsUuid(), volumeRecord.getFsUuid());
        }

        public void onVolumeForgotten(String str) {
            StorageEntry storageEntry = new StorageEntry(new VolumeRecord(0, str));
            if (ProfileSelectStorageFragment.this.mStorageEntries.remove(storageEntry)) {
                if (ProfileSelectStorageFragment.this.mSelectedStorageEntry.equals(storageEntry)) {
                    ProfileSelectStorageFragment profileSelectStorageFragment = ProfileSelectStorageFragment.this;
                    profileSelectStorageFragment.mSelectedStorageEntry = StorageEntry.getDefaultInternalStorageEntry(profileSelectStorageFragment.getContext());
                }
                ProfileSelectStorageFragment.this.refreshUi();
            }
        }

        public void onDiskScanned(DiskInfo diskInfo, int i) {
            if (StorageUtils.isDiskUnsupported(diskInfo)) {
                StorageEntry storageEntry = new StorageEntry(diskInfo);
                if (!ProfileSelectStorageFragment.this.mStorageEntries.contains(storageEntry)) {
                    ProfileSelectStorageFragment.this.mStorageEntries.add(storageEntry);
                    ProfileSelectStorageFragment.this.refreshUi();
                }
            }
        }

        public void onDiskDestroyed(DiskInfo diskInfo) {
            StorageEntry storageEntry = new StorageEntry(diskInfo);
            if (ProfileSelectStorageFragment.this.mStorageEntries.remove(storageEntry)) {
                if (ProfileSelectStorageFragment.this.mSelectedStorageEntry.equals(storageEntry)) {
                    ProfileSelectStorageFragment profileSelectStorageFragment = ProfileSelectStorageFragment.this;
                    profileSelectStorageFragment.mSelectedStorageEntry = StorageEntry.getDefaultInternalStorageEntry(profileSelectStorageFragment.getContext());
                }
                ProfileSelectStorageFragment.this.refreshUi();
            }
        }
    }

    @Override // com.android.settings.dashboard.profileselector.ProfileSelectFragment
    public Fragment[] getFragments() {
        Fragment[] fragmentArr = this.mFragments;
        if (fragmentArr != null) {
            return fragmentArr;
        }
        Bundle bundle = new Bundle();
        bundle.putInt("profile", 2);
        StorageCategoryFragment storageCategoryFragment = new StorageCategoryFragment();
        storageCategoryFragment.setArguments(bundle);
        Bundle bundle2 = new Bundle();
        bundle2.putInt("profile", 1);
        StorageCategoryFragment storageCategoryFragment2 = new StorageCategoryFragment();
        storageCategoryFragment2.setArguments(bundle2);
        Fragment[] fragmentArr2 = {storageCategoryFragment2, storageCategoryFragment};
        this.mFragments = fragmentArr2;
        return fragmentArr2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshUi() {
        Fragment[] fragments;
        this.mStorageSelectionController.setStorageEntries(this.mStorageEntries);
        this.mStorageSelectionController.setSelectedStorageEntry(this.mSelectedStorageEntry);
        this.mStorageUsageProgressBarController.setSelectedStorageEntry(this.mSelectedStorageEntry);
        for (Fragment fragment : getFragments()) {
            if (fragment instanceof StorageCategoryFragment) {
                ((StorageCategoryFragment) fragment).refreshUi(this.mSelectedStorageEntry);
            } else {
                throw new IllegalStateException("Wrong fragment type to refreshUi");
            }
        }
        this.mOptionMenuController.setSelectedStorageEntry(this.mSelectedStorageEntry);
        getActivity().invalidateOptionsMenu();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        StorageEntry storageEntry;
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        StorageManager storageManager = (StorageManager) activity.getSystemService(StorageManager.class);
        this.mStorageManager = storageManager;
        if (bundle == null) {
            VolumeInfo maybeInitializeVolume = Utils.maybeInitializeVolume(storageManager, getArguments());
            if (maybeInitializeVolume == null) {
                storageEntry = StorageEntry.getDefaultInternalStorageEntry(getContext());
            } else {
                storageEntry = new StorageEntry(getContext(), maybeInitializeVolume);
            }
            this.mSelectedStorageEntry = storageEntry;
        } else {
            this.mSelectedStorageEntry = (StorageEntry) bundle.getParcelable("selected_storage_entry_key");
        }
        initializeOptionsMenu(activity);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((AutomaticStorageManagementSwitchPreferenceController) use(AutomaticStorageManagementSwitchPreferenceController.class)).setFragmentManager(getFragmentManager());
        StorageSelectionPreferenceController storageSelectionPreferenceController = (StorageSelectionPreferenceController) use(StorageSelectionPreferenceController.class);
        this.mStorageSelectionController = storageSelectionPreferenceController;
        storageSelectionPreferenceController.setOnItemSelectedListener(new StorageSelectionPreferenceController.OnItemSelectedListener() { // from class: com.android.settings.dashboard.profileselector.ProfileSelectStorageFragment$$ExternalSyntheticLambda0
            @Override // com.android.settings.deviceinfo.storage.StorageSelectionPreferenceController.OnItemSelectedListener
            public final void onItemSelected(StorageEntry storageEntry) {
                ProfileSelectStorageFragment.this.lambda$onAttach$0(storageEntry);
            }
        });
        this.mStorageUsageProgressBarController = (StorageUsageProgressBarPreferenceController) use(StorageUsageProgressBarPreferenceController.class);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$0(StorageEntry storageEntry) {
        this.mSelectedStorageEntry = storageEntry;
        refreshUi();
        if (storageEntry.isDiskInfoUnsupported() || storageEntry.isUnmountable()) {
            DiskInitFragment.show(this, R.string.storage_dialog_unmountable, storageEntry.getDiskId());
        } else if (storageEntry.isVolumeRecordMissed()) {
            StorageUtils.launchForgetMissingVolumeRecordFragment(getContext(), storageEntry);
        }
    }

    void initializeOptionsMenu(Activity activity) {
        this.mOptionMenuController = new VolumeOptionMenuController(activity, this, this.mSelectedStorageEntry);
        getSettingsLifecycle().addObserver(this.mOptionMenuController);
        setHasOptionsMenu(true);
        activity.invalidateOptionsMenu();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mStorageEntries.clear();
        this.mStorageEntries.addAll(StorageUtils.getAllStorageEntries(getContext(), this.mStorageManager));
        refreshUi();
        this.mStorageManager.registerListener(this.mStorageEventListener);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mStorageManager.unregisterListener(this.mStorageEventListener);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putParcelable("selected_storage_entry_key", this.mSelectedStorageEntry);
        super.onSaveInstanceState(bundle);
    }
}
