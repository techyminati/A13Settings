package com.android.settings.applications;

import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.ArrayMap;
import android.util.IconDrawableFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.window.R;
import com.android.settings.CancellablePreference;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.SummaryPreference;
import com.android.settings.applications.ProcStatsEntry;
import com.android.settings.widget.EntityHeaderController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/* loaded from: classes.dex */
public class ProcessStatsDetail extends SettingsPreferenceFragment {
    static final Comparator<ProcStatsEntry> sEntryCompare = new Comparator<ProcStatsEntry>() { // from class: com.android.settings.applications.ProcessStatsDetail.2
        public int compare(ProcStatsEntry procStatsEntry, ProcStatsEntry procStatsEntry2) {
            double d = procStatsEntry.mRunWeight;
            double d2 = procStatsEntry2.mRunWeight;
            if (d < d2) {
                return 1;
            }
            return d > d2 ? -1 : 0;
        }
    };
    static final Comparator<ProcStatsEntry.Service> sServiceCompare = new Comparator<ProcStatsEntry.Service>() { // from class: com.android.settings.applications.ProcessStatsDetail.3
        public int compare(ProcStatsEntry.Service service, ProcStatsEntry.Service service2) {
            long j = service.mDuration;
            long j2 = service2.mDuration;
            if (j < j2) {
                return 1;
            }
            return j > j2 ? -1 : 0;
        }
    };
    static final Comparator<PkgService> sServicePkgCompare = new Comparator<PkgService>() { // from class: com.android.settings.applications.ProcessStatsDetail.4
        public int compare(PkgService pkgService, PkgService pkgService2) {
            long j = pkgService.mDuration;
            long j2 = pkgService2.mDuration;
            if (j < j2) {
                return 1;
            }
            return j > j2 ? -1 : 0;
        }
    };
    private ProcStatsPackageEntry mApp;
    private DevicePolicyManager mDpm;
    private MenuItem mForceStop;
    private double mMaxMemoryUsage;
    private long mOnePercentTime;
    private PackageManager mPm;
    private PreferenceCategory mProcGroup;
    private final ArrayMap<ComponentName, CancellablePreference> mServiceMap = new ArrayMap<>();
    private double mTotalScale;
    private long mTotalTime;
    private double mWeightToRam;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 21;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mPm = getActivity().getPackageManager();
        this.mDpm = (DevicePolicyManager) getActivity().getSystemService("device_policy");
        Bundle arguments = getArguments();
        ProcStatsPackageEntry procStatsPackageEntry = (ProcStatsPackageEntry) arguments.getParcelable("package_entry");
        this.mApp = procStatsPackageEntry;
        procStatsPackageEntry.retrieveUiData(getActivity(), this.mPm);
        this.mWeightToRam = arguments.getDouble("weight_to_ram");
        this.mTotalTime = arguments.getLong("total_time");
        this.mMaxMemoryUsage = arguments.getDouble("max_memory_usage");
        this.mTotalScale = arguments.getDouble("total_scale");
        this.mOnePercentTime = this.mTotalTime / 100;
        this.mServiceMap.clear();
        createDetails();
        setHasOptionsMenu(true);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        Drawable drawable;
        super.onViewCreated(view, bundle);
        if (this.mApp.mUiTargetApp == null) {
            finish();
            return;
        }
        FragmentActivity activity = getActivity();
        EntityHeaderController recyclerView = EntityHeaderController.newInstance(activity, this, null).setRecyclerView(getListView(), getSettingsLifecycle());
        if (this.mApp.mUiTargetApp != null) {
            drawable = IconDrawableFactory.newInstance(activity).getBadgedIcon(this.mApp.mUiTargetApp);
        } else {
            drawable = new ColorDrawable(0);
        }
        EntityHeaderController packageName = recyclerView.setIcon(drawable).setLabel(this.mApp.mUiLabel).setPackageName(this.mApp.mPackage);
        ApplicationInfo applicationInfo = this.mApp.mUiTargetApp;
        getPreferenceScreen().addPreference(packageName.setUid(applicationInfo != null ? applicationInfo.uid : -10000).setHasAppInfoLink(true).setButtonActions(0, 0).done(activity, getPrefContext()));
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        checkForceStop();
        updateRunningServices();
    }

    private void updateRunningServices() {
        final ComponentName componentName;
        CancellablePreference cancellablePreference;
        List<ActivityManager.RunningServiceInfo> runningServices = ((ActivityManager) getActivity().getSystemService("activity")).getRunningServices(Integer.MAX_VALUE);
        int size = this.mServiceMap.size();
        for (int i = 0; i < size; i++) {
            this.mServiceMap.valueAt(i).setCancellable(false);
        }
        int size2 = runningServices.size();
        for (int i2 = 0; i2 < size2; i2++) {
            ActivityManager.RunningServiceInfo runningServiceInfo = runningServices.get(i2);
            if ((runningServiceInfo.started || runningServiceInfo.clientLabel != 0) && (runningServiceInfo.flags & 8) == 0 && (cancellablePreference = this.mServiceMap.get((componentName = runningServiceInfo.service))) != null) {
                cancellablePreference.setOnCancelListener(new CancellablePreference.OnCancelListener() { // from class: com.android.settings.applications.ProcessStatsDetail.1
                    @Override // com.android.settings.CancellablePreference.OnCancelListener
                    public void onCancel(CancellablePreference cancellablePreference2) {
                        ProcessStatsDetail.this.stopService(componentName.getPackageName(), componentName.getClassName());
                    }
                });
                cancellablePreference.setCancellable(true);
            }
        }
    }

    private void createDetails() {
        addPreferencesFromResource(R.xml.app_memory_settings);
        this.mProcGroup = (PreferenceCategory) findPreference("processes");
        fillProcessesSection();
        SummaryPreference summaryPreference = (SummaryPreference) findPreference("status_header");
        ProcStatsPackageEntry procStatsPackageEntry = this.mApp;
        double d = procStatsPackageEntry.mRunWeight;
        double d2 = procStatsPackageEntry.mBgWeight;
        if (!(d > d2)) {
            d = d2;
        }
        double d3 = d * this.mWeightToRam;
        float f = (float) (d3 / this.mMaxMemoryUsage);
        FragmentActivity activity = getActivity();
        summaryPreference.setRatios(f, 0.0f, 1.0f - f);
        Formatter.BytesResult formatBytes = Formatter.formatBytes(activity.getResources(), (long) d3, 1);
        summaryPreference.setAmount(formatBytes.value);
        summaryPreference.setUnits(formatBytes.units);
        ProcStatsPackageEntry procStatsPackageEntry2 = this.mApp;
        findPreference("frequency").setSummary(ProcStatsPackageEntry.getFrequency(((float) Math.max(procStatsPackageEntry2.mRunDuration, procStatsPackageEntry2.mBgDuration)) / ((float) this.mTotalTime), getActivity()));
        ProcStatsPackageEntry procStatsPackageEntry3 = this.mApp;
        findPreference("max_usage").setSummary(Formatter.formatShortFileSize(getContext(), (long) (Math.max(procStatsPackageEntry3.mMaxBgMem, procStatsPackageEntry3.mMaxRunMem) * this.mTotalScale * 1024.0d)));
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        this.mForceStop = menu.add(0, 1, 0, R.string.force_stop);
        checkForceStop();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 1) {
            return false;
        }
        killProcesses();
        return true;
    }

    private void fillProcessesSection() {
        this.mProcGroup.removeAll();
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.mApp.mEntries.size(); i++) {
            ProcStatsEntry procStatsEntry = this.mApp.mEntries.get(i);
            if (procStatsEntry.mPackage.equals("os")) {
                procStatsEntry.mLabel = procStatsEntry.mName;
            } else {
                procStatsEntry.mLabel = getProcessName(this.mApp.mUiLabel, procStatsEntry);
            }
            arrayList.add(procStatsEntry);
        }
        Collections.sort(arrayList, sEntryCompare);
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            ProcStatsEntry procStatsEntry2 = (ProcStatsEntry) arrayList.get(i2);
            Preference preference = new Preference(getPrefContext());
            preference.setTitle(procStatsEntry2.mLabel);
            preference.setSelectable(false);
            long max = Math.max(procStatsEntry2.mRunDuration, procStatsEntry2.mBgDuration);
            double d = procStatsEntry2.mRunWeight;
            double d2 = this.mWeightToRam;
            preference.setSummary(getString(R.string.memory_use_running_format, Formatter.formatShortFileSize(getActivity(), Math.max((long) (d * d2), (long) (procStatsEntry2.mBgWeight * d2))), ProcStatsPackageEntry.getFrequency(((float) max) / ((float) this.mTotalTime), getActivity())));
            this.mProcGroup.addPreference(preference);
        }
        if (this.mProcGroup.getPreferenceCount() < 2) {
            getPreferenceScreen().removePreference(this.mProcGroup);
        }
    }

    private static String capitalize(String str) {
        char charAt = str.charAt(0);
        if (!Character.isLowerCase(charAt)) {
            return str;
        }
        return Character.toUpperCase(charAt) + str.substring(1);
    }

    private static String getProcessName(String str, ProcStatsEntry procStatsEntry) {
        String str2 = procStatsEntry.mName;
        if (str2.contains(":")) {
            return capitalize(str2.substring(str2.lastIndexOf(58) + 1));
        }
        if (!str2.startsWith(procStatsEntry.mPackage)) {
            return str2;
        }
        if (str2.length() == procStatsEntry.mPackage.length()) {
            return str;
        }
        int length = procStatsEntry.mPackage.length();
        if (str2.charAt(length) == '.') {
            length++;
        }
        return capitalize(str2.substring(length));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class PkgService {
        long mDuration;
        final ArrayList<ProcStatsEntry.Service> mServices = new ArrayList<>();

        PkgService() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopService(String str, String str2) {
        try {
            if ((getActivity().getPackageManager().getApplicationInfo(str, 0).flags & 1) != 0) {
                showStopServiceDialog(str, str2);
            } else {
                doStopService(str, str2);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("ProcessStatsDetail", "Can't find app " + str, e);
        }
    }

    private void showStopServiceDialog(final String str, final String str2) {
        new AlertDialog.Builder(getActivity()).setTitle(R.string.runningservicedetails_stop_dlg_title).setMessage(R.string.runningservicedetails_stop_dlg_text).setPositiveButton(R.string.dlg_ok, new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.ProcessStatsDetail.5
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                ProcessStatsDetail.this.doStopService(str, str2);
            }
        }).setNegativeButton(R.string.dlg_cancel, (DialogInterface.OnClickListener) null).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doStopService(String str, String str2) {
        getActivity().stopService(new Intent().setClassName(str, str2));
        updateRunningServices();
    }

    private void killProcesses() {
        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService("activity");
        for (int i = 0; i < this.mApp.mEntries.size(); i++) {
            ProcStatsEntry procStatsEntry = this.mApp.mEntries.get(i);
            for (int i2 = 0; i2 < procStatsEntry.mPackages.size(); i2++) {
                activityManager.forceStopPackage(procStatsEntry.mPackages.get(i2));
            }
        }
    }

    private void checkForceStop() {
        if (this.mForceStop != null) {
            if (this.mApp.mEntries.get(0).mUid < 10000) {
                this.mForceStop.setVisible(false);
                return;
            }
            boolean z = false;
            for (int i = 0; i < this.mApp.mEntries.size(); i++) {
                ProcStatsEntry procStatsEntry = this.mApp.mEntries.get(i);
                for (int i2 = 0; i2 < procStatsEntry.mPackages.size(); i2++) {
                    String str = procStatsEntry.mPackages.get(i2);
                    if (this.mDpm.packageHasActiveAdmins(str)) {
                        this.mForceStop.setEnabled(false);
                        return;
                    }
                    try {
                        if ((this.mPm.getApplicationInfo(str, 0).flags & 2097152) == 0) {
                            z = true;
                        }
                    } catch (PackageManager.NameNotFoundException unused) {
                    }
                }
            }
            if (z) {
                this.mForceStop.setVisible(true);
            }
        }
    }
}
