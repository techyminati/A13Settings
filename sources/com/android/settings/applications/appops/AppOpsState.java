package com.android.settings.applications.appops;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.SparseArray;
import androidx.window.R;
import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
/* loaded from: classes.dex */
public class AppOpsState {
    public static final OpsTemplate[] ALL_TEMPLATES;
    public static final OpsTemplate DEVICE_TEMPLATE;
    public static final OpsTemplate LOCATION_TEMPLATE;
    public static final OpsTemplate MEDIA_TEMPLATE;
    public static final OpsTemplate MESSAGING_TEMPLATE;
    public static final OpsTemplate PERSONAL_TEMPLATE;
    public static final OpsTemplate RUN_IN_BACKGROUND_TEMPLATE;
    final AppOpsManager mAppOps;
    final Context mContext;
    final CharSequence[] mOpLabels;
    final CharSequence[] mOpSummaries;
    final PackageManager mPm;
    public static final Comparator<AppOpEntry> RECENCY_COMPARATOR = new Comparator<AppOpEntry>() { // from class: com.android.settings.applications.appops.AppOpsState.1
        private final Collator sCollator = Collator.getInstance();

        public int compare(AppOpEntry appOpEntry, AppOpEntry appOpEntry2) {
            if (appOpEntry.getSwitchOrder() != appOpEntry2.getSwitchOrder()) {
                return appOpEntry.getSwitchOrder() < appOpEntry2.getSwitchOrder() ? -1 : 1;
            }
            if (appOpEntry.isRunning() != appOpEntry2.isRunning()) {
                return appOpEntry.isRunning() ? -1 : 1;
            }
            if (appOpEntry.getTime() != appOpEntry2.getTime()) {
                return appOpEntry.getTime() > appOpEntry2.getTime() ? -1 : 1;
            }
            return this.sCollator.compare(appOpEntry.getAppEntry().getLabel(), appOpEntry2.getAppEntry().getLabel());
        }
    };
    public static final Comparator<AppOpEntry> LABEL_COMPARATOR = new Comparator<AppOpEntry>() { // from class: com.android.settings.applications.appops.AppOpsState.2
        private final Collator sCollator = Collator.getInstance();

        public int compare(AppOpEntry appOpEntry, AppOpEntry appOpEntry2) {
            return this.sCollator.compare(appOpEntry.getAppEntry().getLabel(), appOpEntry2.getAppEntry().getLabel());
        }
    };

    public AppOpsState(Context context) {
        this.mContext = context;
        this.mAppOps = (AppOpsManager) context.getSystemService("appops");
        this.mPm = context.getPackageManager();
        this.mOpSummaries = context.getResources().getTextArray(R.array.app_ops_summaries);
        this.mOpLabels = context.getResources().getTextArray(R.array.app_ops_labels);
    }

    /* loaded from: classes.dex */
    public static class OpsTemplate implements Parcelable {
        public static final Parcelable.Creator<OpsTemplate> CREATOR = new Parcelable.Creator<OpsTemplate>() { // from class: com.android.settings.applications.appops.AppOpsState.OpsTemplate.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public OpsTemplate createFromParcel(Parcel parcel) {
                return new OpsTemplate(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public OpsTemplate[] newArray(int i) {
                return new OpsTemplate[i];
            }
        };
        public final int[] ops;
        public final boolean[] showPerms;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public OpsTemplate(int[] iArr, boolean[] zArr) {
            this.ops = iArr;
            this.showPerms = zArr;
        }

        OpsTemplate(Parcel parcel) {
            this.ops = parcel.createIntArray();
            this.showPerms = parcel.createBooleanArray();
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeIntArray(this.ops);
            parcel.writeBooleanArray(this.showPerms);
        }
    }

    static {
        OpsTemplate opsTemplate = new OpsTemplate(new int[]{0, 1, 2, 10, 12, 41, 42}, new boolean[]{true, true, false, false, false, false, false});
        LOCATION_TEMPLATE = opsTemplate;
        OpsTemplate opsTemplate2 = new OpsTemplate(new int[]{4, 5, 6, 7, 8, 9, 29, 30}, new boolean[]{true, true, true, true, true, true, false, false});
        PERSONAL_TEMPLATE = opsTemplate2;
        OpsTemplate opsTemplate3 = new OpsTemplate(new int[]{14, 16, 17, 18, 19, 15, 20, 21, 22}, new boolean[]{true, true, true, true, true, true, true, true, true});
        MESSAGING_TEMPLATE = opsTemplate3;
        OpsTemplate opsTemplate4 = new OpsTemplate(new int[]{3, 26, 27, 28, 31, 32, 33, 34, 35, 36, 37, 38, 39, 64, 44}, new boolean[]{false, true, true, false, false, false, false, false, false, false, false, false, false, false});
        MEDIA_TEMPLATE = opsTemplate4;
        OpsTemplate opsTemplate5 = new OpsTemplate(new int[]{11, 25, 13, 23, 24, 40, 46, 47, 49, 50}, new boolean[]{false, true, true, true, true, true, false, false, false, false});
        DEVICE_TEMPLATE = opsTemplate5;
        OpsTemplate opsTemplate6 = new OpsTemplate(new int[]{63}, new boolean[]{false});
        RUN_IN_BACKGROUND_TEMPLATE = opsTemplate6;
        ALL_TEMPLATES = new OpsTemplate[]{opsTemplate, opsTemplate2, opsTemplate3, opsTemplate4, opsTemplate5, opsTemplate6};
    }

    /* loaded from: classes.dex */
    public static class AppEntry {
        private final File mApkFile;
        private Drawable mIcon;
        private final ApplicationInfo mInfo;
        private String mLabel;
        private boolean mMounted;
        private final AppOpsState mState;
        private final SparseArray<AppOpsManager.OpEntry> mOps = new SparseArray<>();
        private final SparseArray<AppOpEntry> mOpSwitches = new SparseArray<>();

        public AppEntry(AppOpsState appOpsState, ApplicationInfo applicationInfo) {
            this.mState = appOpsState;
            this.mInfo = applicationInfo;
            this.mApkFile = new File(applicationInfo.sourceDir);
        }

        public void addOp(AppOpEntry appOpEntry, AppOpsManager.OpEntry opEntry) {
            this.mOps.put(opEntry.getOp(), opEntry);
            this.mOpSwitches.put(AppOpsManager.opToSwitch(opEntry.getOp()), appOpEntry);
        }

        public boolean hasOp(int i) {
            return this.mOps.indexOfKey(i) >= 0;
        }

        public AppOpEntry getOpSwitch(int i) {
            return this.mOpSwitches.get(AppOpsManager.opToSwitch(i));
        }

        public ApplicationInfo getApplicationInfo() {
            return this.mInfo;
        }

        public String getLabel() {
            return this.mLabel;
        }

        public Drawable getIcon() {
            Drawable drawable = this.mIcon;
            if (drawable == null) {
                if (this.mApkFile.exists()) {
                    Drawable loadIcon = this.mInfo.loadIcon(this.mState.mPm);
                    this.mIcon = loadIcon;
                    return loadIcon;
                }
                this.mMounted = false;
            } else if (this.mMounted) {
                return drawable;
            } else {
                if (this.mApkFile.exists()) {
                    this.mMounted = true;
                    Drawable loadIcon2 = this.mInfo.loadIcon(this.mState.mPm);
                    this.mIcon = loadIcon2;
                    return loadIcon2;
                }
            }
            return this.mState.mContext.getDrawable(17301651);
        }

        public String toString() {
            return this.mLabel;
        }

        void loadLabel(Context context) {
            if (this.mLabel != null && this.mMounted) {
                return;
            }
            if (!this.mApkFile.exists()) {
                this.mMounted = false;
                this.mLabel = this.mInfo.packageName;
                return;
            }
            this.mMounted = true;
            CharSequence loadLabel = this.mInfo.loadLabel(context.getPackageManager());
            this.mLabel = loadLabel != null ? loadLabel.toString() : this.mInfo.packageName;
        }
    }

    /* loaded from: classes.dex */
    public static class AppOpEntry {
        private final AppEntry mApp;
        private final ArrayList<AppOpsManager.OpEntry> mOps;
        private int mOverriddenPrimaryMode = -1;
        private final AppOpsManager.PackageOps mPkgOps;
        private final ArrayList<AppOpsManager.OpEntry> mSwitchOps;
        private final int mSwitchOrder;

        public AppOpEntry(AppOpsManager.PackageOps packageOps, AppOpsManager.OpEntry opEntry, AppEntry appEntry, int i) {
            ArrayList<AppOpsManager.OpEntry> arrayList = new ArrayList<>();
            this.mOps = arrayList;
            ArrayList<AppOpsManager.OpEntry> arrayList2 = new ArrayList<>();
            this.mSwitchOps = arrayList2;
            this.mPkgOps = packageOps;
            this.mApp = appEntry;
            this.mSwitchOrder = i;
            appEntry.addOp(this, opEntry);
            arrayList.add(opEntry);
            arrayList2.add(opEntry);
        }

        private static void addOp(ArrayList<AppOpsManager.OpEntry> arrayList, AppOpsManager.OpEntry opEntry) {
            for (int i = 0; i < arrayList.size(); i++) {
                AppOpsManager.OpEntry opEntry2 = arrayList.get(i);
                if (opEntry2.isRunning() != opEntry.isRunning()) {
                    if (opEntry.isRunning()) {
                        arrayList.add(i, opEntry);
                        return;
                    }
                } else if (opEntry2.getTime() < opEntry.getTime()) {
                    arrayList.add(i, opEntry);
                    return;
                }
            }
            arrayList.add(opEntry);
        }

        public void addOp(AppOpsManager.OpEntry opEntry) {
            this.mApp.addOp(this, opEntry);
            addOp(this.mOps, opEntry);
            if (this.mApp.getOpSwitch(AppOpsManager.opToSwitch(opEntry.getOp())) == null) {
                addOp(this.mSwitchOps, opEntry);
            }
        }

        public AppEntry getAppEntry() {
            return this.mApp;
        }

        public int getSwitchOrder() {
            return this.mSwitchOrder;
        }

        public AppOpsManager.OpEntry getOpEntry(int i) {
            return this.mOps.get(i);
        }

        public int getPrimaryOpMode() {
            int i = this.mOverriddenPrimaryMode;
            return i >= 0 ? i : this.mOps.get(0).getMode();
        }

        public void overridePrimaryOpMode(int i) {
            this.mOverriddenPrimaryMode = i;
        }

        public CharSequence getTimeText(Resources resources, boolean z) {
            if (isRunning()) {
                return resources.getText(R.string.app_ops_running);
            }
            if (getTime() > 0) {
                return DateUtils.getRelativeTimeSpanString(getTime(), System.currentTimeMillis(), 60000L, 262144);
            }
            return z ? resources.getText(R.string.app_ops_never_used) : "";
        }

        public boolean isRunning() {
            return this.mOps.get(0).isRunning();
        }

        public long getTime() {
            return this.mOps.get(0).getTime();
        }

        public String toString() {
            return this.mApp.getLabel();
        }
    }

    private void addOp(List<AppOpEntry> list, AppOpsManager.PackageOps packageOps, AppEntry appEntry, AppOpsManager.OpEntry opEntry, boolean z, int i) {
        if (z && list.size() > 0) {
            boolean z2 = true;
            AppOpEntry appOpEntry = list.get(list.size() - 1);
            if (appOpEntry.getAppEntry() == appEntry) {
                boolean z3 = appOpEntry.getTime() != 0;
                if (opEntry.getTime() == 0) {
                    z2 = false;
                }
                if (z3 == z2) {
                    appOpEntry.addOp(opEntry);
                    return;
                }
            }
        }
        AppOpEntry opSwitch = appEntry.getOpSwitch(opEntry.getOp());
        if (opSwitch != null) {
            opSwitch.addOp(opEntry);
        } else {
            list.add(new AppOpEntry(packageOps, opEntry, appEntry, i));
        }
    }

    public AppOpsManager getAppOpsManager() {
        return this.mAppOps;
    }

    private AppEntry getAppEntry(Context context, HashMap<String, AppEntry> hashMap, String str, ApplicationInfo applicationInfo) {
        AppEntry appEntry = hashMap.get(str);
        if (appEntry != null) {
            return appEntry;
        }
        if (applicationInfo == null) {
            try {
                applicationInfo = this.mPm.getApplicationInfo(str, 4194816);
            } catch (PackageManager.NameNotFoundException unused) {
                Log.w("AppOpsState", "Unable to find info for package " + str);
                return null;
            }
        }
        AppEntry appEntry2 = new AppEntry(this, applicationInfo);
        appEntry2.loadLabel(context);
        hashMap.put(str, appEntry2);
        return appEntry2;
    }

    public List<AppOpEntry> buildState(OpsTemplate opsTemplate, int i, String str, Comparator<AppOpEntry> comparator) {
        int[] iArr;
        List list;
        List<PackageInfo> list2;
        int i2;
        int i3;
        AppOpsManager.PackageOps packageOps;
        String opToPermission;
        AppOpsState appOpsState = this;
        Context context = appOpsState.mContext;
        HashMap<String, AppEntry> hashMap = new HashMap<>();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        int[] iArr2 = new int[120];
        int i4 = 0;
        int i5 = 0;
        while (true) {
            iArr = opsTemplate.ops;
            if (i5 >= iArr.length) {
                break;
            }
            if (opsTemplate.showPerms[i5] && (opToPermission = AppOpsManager.opToPermission(iArr[i5])) != null && !arrayList2.contains(opToPermission)) {
                arrayList2.add(opToPermission);
                arrayList3.add(Integer.valueOf(opsTemplate.ops[i5]));
                iArr2[opsTemplate.ops[i5]] = i5;
            }
            i5++;
        }
        if (str != null) {
            list = appOpsState.mAppOps.getOpsForPackage(i, str, iArr);
        } else {
            list = appOpsState.mAppOps.getPackagesForOps(iArr);
        }
        AppOpsManager.PackageOps packageOps2 = null;
        if (list != null) {
            int i6 = 0;
            while (i6 < list.size()) {
                AppOpsManager.PackageOps packageOps3 = (AppOpsManager.PackageOps) list.get(i6);
                AppEntry appEntry = appOpsState.getAppEntry(context, hashMap, packageOps3.getPackageName(), packageOps2);
                if (appEntry != null) {
                    int i7 = 0;
                    while (i7 < packageOps3.getOps().size()) {
                        AppOpsManager.OpEntry opEntry = (AppOpsManager.OpEntry) packageOps3.getOps().get(i7);
                        addOp(arrayList, packageOps3, appEntry, opEntry, str == null, str == null ? 0 : iArr2[opEntry.getOp()]);
                        i7++;
                        packageOps2 = packageOps2;
                        list = list;
                        i6 = i6;
                    }
                }
                i6++;
            }
        }
        if (str != null) {
            list2 = new ArrayList<>();
            try {
                list2.add(appOpsState.mPm.getPackageInfo(str, 4096));
            } catch (PackageManager.NameNotFoundException unused) {
            }
        } else {
            String[] strArr = new String[arrayList2.size()];
            arrayList2.toArray(strArr);
            list2 = appOpsState.mPm.getPackagesHoldingPermissions(strArr, 0);
        }
        int i8 = 0;
        while (i8 < list2.size()) {
            PackageInfo packageInfo = list2.get(i8);
            AppEntry appEntry2 = appOpsState.getAppEntry(context, hashMap, packageInfo.packageName, packageInfo.applicationInfo);
            if (appEntry2 == null || packageInfo.requestedPermissions == null) {
                i2 = i8;
                list2 = list2;
                i4 = i4;
            } else {
                int i9 = i4;
                AppOpsManager.PackageOps packageOps4 = packageOps2;
                while (i9 < packageInfo.requestedPermissions.length) {
                    int[] iArr3 = packageInfo.requestedPermissionsFlags;
                    if (iArr3 == null || (iArr3[i9] & 2) != 0) {
                        AppOpsManager.PackageOps packageOps5 = packageOps4;
                        int i10 = 0;
                        while (i10 < arrayList2.size()) {
                            if (((String) arrayList2.get(i10)).equals(packageInfo.requestedPermissions[i9]) && !appEntry2.hasOp(((Integer) arrayList3.get(i10)).intValue())) {
                                if (packageOps2 == null) {
                                    AppOpsManager.PackageOps arrayList4 = new ArrayList();
                                    i9 = i9;
                                    packageOps = arrayList4;
                                    packageOps5 = new AppOpsManager.PackageOps(packageInfo.packageName, packageInfo.applicationInfo.uid, arrayList4);
                                } else {
                                    i9 = i9;
                                    packageOps5 = packageOps5;
                                    packageOps = packageOps2;
                                }
                                AppOpsManager.OpEntry opEntry2 = new AppOpsManager.OpEntry(((Integer) arrayList3.get(i10)).intValue(), 0, Collections.emptyMap());
                                packageOps.add(opEntry2);
                                appEntry2 = appEntry2;
                                packageInfo = packageInfo;
                                list2 = list2;
                                addOp(arrayList, packageOps5, appEntry2, opEntry2, str == null, str == null ? 0 : iArr2[opEntry2.getOp()]);
                                packageOps2 = packageOps;
                            } else {
                                packageOps5 = packageOps5;
                                i9 = i9;
                                appEntry2 = appEntry2;
                                packageInfo = packageInfo;
                                list2 = list2;
                            }
                            i10++;
                            i8 = i8;
                        }
                        i3 = i9;
                        appEntry2 = appEntry2;
                        packageInfo = packageInfo;
                        i8 = i8;
                        list2 = list2;
                        packageOps4 = packageOps5;
                    } else {
                        i3 = i9;
                        appEntry2 = appEntry2;
                        packageInfo = packageInfo;
                        i8 = i8;
                        list2 = list2;
                    }
                    i9 = i3 + 1;
                }
                i2 = i8;
                list2 = list2;
                i4 = 0;
            }
            i8 = i2 + 1;
            appOpsState = this;
        }
        Collections.sort(arrayList, comparator);
        return arrayList;
    }
}
