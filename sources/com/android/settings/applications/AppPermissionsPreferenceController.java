package com.android.settings.applications;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.icu.text.ListFormatter;
import android.util.ArraySet;
import androidx.preference.Preference;
import androidx.window.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.applications.PermissionsSummaryHelper;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class AppPermissionsPreferenceController extends BasePreferenceController {
    private static final int NUM_PACKAGE_TO_CHECK = 4;
    static int NUM_PERMISSIONS_TO_SHOW = 3;
    private static final String TAG = "AppPermissionPrefCtrl";
    int mNumPackageChecked;
    private final PackageManager mPackageManager;
    private Preference mPreference;
    private final PermissionsSummaryHelper.PermissionsResultCallback mPermissionsCallback = new PermissionsSummaryHelper.PermissionsResultCallback() { // from class: com.android.settings.applications.AppPermissionsPreferenceController.1
        @Override // com.android.settingslib.applications.PermissionsSummaryHelper.PermissionsResultCallback
        public void onPermissionSummaryResult(int i, int i2, int i3, List<CharSequence> list) {
            AppPermissionsPreferenceController.this.updateSummary(list);
        }
    };
    private final Set<CharSequence> mPermissionGroups = new ArraySet();

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

    public AppPermissionsPreferenceController(Context context, String str) {
        super(context, str);
        this.mPackageManager = context.getPackageManager();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        this.mPreference = preference;
        this.mNumPackageChecked = 0;
        queryPermissionSummary();
    }

    void queryPermissionSummary() {
        for (PackageInfo packageInfo : (List) this.mPackageManager.getInstalledPackages(4096).stream().filter(AppPermissionsPreferenceController$$ExternalSyntheticLambda0.INSTANCE).limit(4L).collect(Collectors.toList())) {
            PermissionsSummaryHelper.getPermissionSummary(this.mContext, packageInfo.packageName, this.mPermissionsCallback);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$queryPermissionSummary$0(PackageInfo packageInfo) {
        return packageInfo.permissions != null;
    }

    void updateSummary(List<CharSequence> list) {
        String str;
        this.mPermissionGroups.addAll(list);
        int i = this.mNumPackageChecked + 1;
        this.mNumPackageChecked = i;
        if (i >= 4) {
            List list2 = (List) this.mPermissionGroups.stream().limit(NUM_PERMISSIONS_TO_SHOW).collect(Collectors.toList());
            boolean z = this.mPermissionGroups.size() > NUM_PERMISSIONS_TO_SHOW;
            if (list2.isEmpty()) {
                str = this.mContext.getString(R.string.runtime_permissions_summary_no_permissions_granted);
            } else if (z) {
                str = this.mContext.getString(R.string.app_permissions_summary_more, ListFormatter.getInstance().format(list2).toLowerCase());
            } else {
                str = this.mContext.getString(R.string.app_permissions_summary, ListFormatter.getInstance().format(list2).toLowerCase());
            }
            this.mPreference.setSummary(str);
        }
    }
}
