package com.android.settings.applications;

import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.usb.IUsbManager;
import android.os.Bundle;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.applications.appinfo.AppButtonsPreferenceController;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.applications.ApplicationsState;
import java.util.ArrayList;
/* loaded from: classes.dex */
public abstract class AppInfoBase extends SettingsPreferenceFragment implements ApplicationsState.Callbacks {
    protected ApplicationsState.AppEntry mAppEntry;
    protected ApplicationFeatureProvider mApplicationFeatureProvider;
    protected RestrictedLockUtils.EnforcedAdmin mAppsControlDisallowedAdmin;
    protected boolean mAppsControlDisallowedBySystem;
    protected DevicePolicyManager mDpm;
    protected boolean mFinishing;
    protected boolean mListeningToPackageRemove;
    protected PackageInfo mPackageInfo;
    protected String mPackageName;
    protected final BroadcastReceiver mPackageRemovedReceiver = new BroadcastReceiver() { // from class: com.android.settings.applications.AppInfoBase.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            ApplicationInfo applicationInfo;
            String schemeSpecificPart = intent.getData().getSchemeSpecificPart();
            AppInfoBase appInfoBase = AppInfoBase.this;
            if (!appInfoBase.mFinishing) {
                ApplicationsState.AppEntry appEntry = appInfoBase.mAppEntry;
                if (appEntry == null || (applicationInfo = appEntry.info) == null || TextUtils.equals(applicationInfo.packageName, schemeSpecificPart)) {
                    AppInfoBase.this.onPackageRemoved();
                }
            }
        }
    };
    protected PackageManager mPm;
    protected ApplicationsState.Session mSession;
    protected ApplicationsState mState;
    protected IUsbManager mUsbManager;
    protected int mUserId;
    protected UserManager mUserManager;

    protected abstract AlertDialog createDialog(int i, int i2);

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onAllSizesComputed() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onLauncherInfoChanged() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onLoadEntriesCompleted() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageIconChanged() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageSizeChanged(String str) {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> arrayList) {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onRunningStateChanged(boolean z) {
    }

    protected abstract boolean refreshUi();

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mFinishing = false;
        FragmentActivity activity = getActivity();
        this.mApplicationFeatureProvider = FeatureFactory.getFactory(activity).getApplicationFeatureProvider(activity);
        ApplicationsState instance = ApplicationsState.getInstance(activity.getApplication());
        this.mState = instance;
        this.mSession = instance.newSession(this, getSettingsLifecycle());
        this.mDpm = (DevicePolicyManager) activity.getSystemService("device_policy");
        this.mUserManager = (UserManager) activity.getSystemService("user");
        this.mPm = activity.getPackageManager();
        this.mUsbManager = IUsbManager.Stub.asInterface(ServiceManager.getService("usb"));
        retrieveAppEntry();
        startListeningToPackageRemove();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mAppsControlDisallowedAdmin = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(getActivity(), "no_control_apps", this.mUserId);
        this.mAppsControlDisallowedBySystem = RestrictedLockUtilsInternal.hasBaseUserRestriction(getActivity(), "no_control_apps", this.mUserId);
        if (!refreshUi()) {
            setIntentAndFinish(true);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        stopListeningToPackageRemove();
        super.onDestroy();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String retrieveAppEntry() {
        Bundle arguments = getArguments();
        this.mPackageName = arguments != null ? arguments.getString("package") : null;
        Intent intent = arguments == null ? getIntent() : (Intent) arguments.getParcelable("intent");
        if (!(this.mPackageName != null || intent == null || intent.getData() == null)) {
            this.mPackageName = intent.getData().getSchemeSpecificPart();
        }
        if (intent == null || !intent.hasExtra("android.intent.extra.user_handle")) {
            this.mUserId = UserHandle.myUserId();
        } else {
            this.mUserId = ((UserHandle) intent.getParcelableExtra("android.intent.extra.user_handle")).getIdentifier();
        }
        ApplicationsState.AppEntry entry = this.mState.getEntry(this.mPackageName, this.mUserId);
        this.mAppEntry = entry;
        if (entry != null) {
            try {
                this.mPackageInfo = this.mPm.getPackageInfoAsUser(entry.info.packageName, 134222336, this.mUserId);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("AppInfoBase", "Exception when retrieving package:" + this.mAppEntry.info.packageName, e);
            }
        } else {
            Log.w("AppInfoBase", "Missing AppEntry; maybe reinstalling?");
            this.mPackageInfo = null;
        }
        return this.mPackageName;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setIntentAndFinish(boolean z) {
        Log.i("AppInfoBase", "appChanged=" + z);
        Intent intent = new Intent();
        intent.putExtra(AppButtonsPreferenceController.APP_CHG, z);
        ((SettingsActivity) getActivity()).finishPreferencePanel(-1, intent);
        this.mFinishing = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void showDialogInner(int i, int i2) {
        MyAlertDialogFragment newInstance = MyAlertDialogFragment.newInstance(i, i2);
        newInstance.setTargetFragment(this, 0);
        FragmentManager fragmentManager = getFragmentManager();
        newInstance.show(fragmentManager, "dialog " + i);
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageListChanged() {
        if (!refreshUi()) {
            setIntentAndFinish(true);
        }
    }

    public static void startAppInfoFragment(Class<?> cls, String str, String str2, int i, Fragment fragment, int i2, int i3) {
        Bundle bundle = new Bundle();
        bundle.putString("package", str2);
        bundle.putInt("uid", i);
        new SubSettingLauncher(fragment.getContext()).setDestination(cls.getName()).setSourceMetricsCategory(i3).setTitleText(str).setArguments(bundle).setUserHandle(new UserHandle(UserHandle.getUserId(i))).setResultListener(fragment, i2).launch();
    }

    /* loaded from: classes.dex */
    public static class MyAlertDialogFragment extends InstrumentedDialogFragment {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 558;
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            int i = getArguments().getInt("id");
            AlertDialog createDialog = ((AppInfoBase) getTargetFragment()).createDialog(i, getArguments().getInt("moveError"));
            if (createDialog != null) {
                return createDialog;
            }
            throw new IllegalArgumentException("unknown id " + i);
        }

        public static MyAlertDialogFragment newInstance(int i, int i2) {
            MyAlertDialogFragment myAlertDialogFragment = new MyAlertDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("id", i);
            bundle.putInt("moveError", i2);
            myAlertDialogFragment.setArguments(bundle);
            return myAlertDialogFragment;
        }
    }

    protected void startListeningToPackageRemove() {
        if (!this.mListeningToPackageRemove) {
            this.mListeningToPackageRemove = true;
            IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_REMOVED");
            intentFilter.addDataScheme("package");
            getContext().registerReceiver(this.mPackageRemovedReceiver, intentFilter);
        }
    }

    protected void stopListeningToPackageRemove() {
        if (this.mListeningToPackageRemove) {
            this.mListeningToPackageRemove = false;
            getContext().unregisterReceiver(this.mPackageRemovedReceiver);
        }
    }

    protected void onPackageRemoved() {
        getActivity().finishAndRemoveTask();
    }
}
