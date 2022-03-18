package com.android.settings.notification.zen;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import androidx.core.text.BidiFormatter;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.window.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.notification.app.AppChannelsBypassingDndSettings;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.AppPreference;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class ZenModeAddBypassingAppsPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private Preference mAddPreference;
    private ApplicationsState.Session mAppSession;
    private final ApplicationsState.Callbacks mAppSessionCallbacks;
    ApplicationsState mApplicationsState;
    private Fragment mHostFragment;
    private final NotificationBackend mNotificationBackend;
    Context mPrefContext;
    PreferenceCategory mPreferenceCategory;
    PreferenceScreen mPreferenceScreen;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "zen_mode_non_bypassing_apps_list";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public ZenModeAddBypassingAppsPreferenceController(Context context, Application application, Fragment fragment, NotificationBackend notificationBackend) {
        this(context, application == null ? null : ApplicationsState.getInstance(application), fragment, notificationBackend);
    }

    private ZenModeAddBypassingAppsPreferenceController(Context context, ApplicationsState applicationsState, Fragment fragment, NotificationBackend notificationBackend) {
        super(context);
        this.mAppSessionCallbacks = new ApplicationsState.Callbacks() { // from class: com.android.settings.notification.zen.ZenModeAddBypassingAppsPreferenceController.2
            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onAllSizesComputed() {
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onRunningStateChanged(boolean z) {
                ZenModeAddBypassingAppsPreferenceController.this.updateAppList();
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onPackageListChanged() {
                ZenModeAddBypassingAppsPreferenceController.this.updateAppList();
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> arrayList) {
                ZenModeAddBypassingAppsPreferenceController.this.updateAppList(arrayList);
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onPackageIconChanged() {
                ZenModeAddBypassingAppsPreferenceController.this.updateAppList();
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onPackageSizeChanged(String str) {
                ZenModeAddBypassingAppsPreferenceController.this.updateAppList();
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onLauncherInfoChanged() {
                ZenModeAddBypassingAppsPreferenceController.this.updateAppList();
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onLoadEntriesCompleted() {
                ZenModeAddBypassingAppsPreferenceController.this.updateAppList();
            }
        };
        this.mNotificationBackend = notificationBackend;
        this.mApplicationsState = applicationsState;
        this.mHostFragment = fragment;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mPreferenceScreen = preferenceScreen;
        Preference findPreference = preferenceScreen.findPreference("zen_mode_bypassing_apps_add");
        this.mAddPreference = findPreference;
        findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.zen.ZenModeAddBypassingAppsPreferenceController.1
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                ZenModeAddBypassingAppsPreferenceController.this.mAddPreference.setVisible(false);
                ZenModeAddBypassingAppsPreferenceController zenModeAddBypassingAppsPreferenceController = ZenModeAddBypassingAppsPreferenceController.this;
                if (zenModeAddBypassingAppsPreferenceController.mApplicationsState == null || zenModeAddBypassingAppsPreferenceController.mHostFragment == null) {
                    return true;
                }
                ZenModeAddBypassingAppsPreferenceController zenModeAddBypassingAppsPreferenceController2 = ZenModeAddBypassingAppsPreferenceController.this;
                zenModeAddBypassingAppsPreferenceController2.mAppSession = zenModeAddBypassingAppsPreferenceController2.mApplicationsState.newSession(zenModeAddBypassingAppsPreferenceController2.mAppSessionCallbacks, ZenModeAddBypassingAppsPreferenceController.this.mHostFragment.getLifecycle());
                return true;
            }
        });
        this.mPrefContext = preferenceScreen.getContext();
        super.displayPreference(preferenceScreen);
    }

    public void updateAppList() {
        ApplicationsState.Session session = this.mAppSession;
        if (session != null) {
            updateAppList(session.rebuild(ApplicationsState.FILTER_ALL_ENABLED, ApplicationsState.ALPHA_COMPARATOR));
        }
    }

    void updateAppList(List<ApplicationsState.AppEntry> list) {
        if (list != null) {
            if (this.mPreferenceCategory == null) {
                PreferenceCategory preferenceCategory = new PreferenceCategory(this.mPrefContext);
                this.mPreferenceCategory = preferenceCategory;
                preferenceCategory.setTitle(R.string.zen_mode_bypassing_apps_add_header);
                this.mPreferenceScreen.addPreference(this.mPreferenceCategory);
            }
            ArrayList<Preference> arrayList = new ArrayList();
            for (final ApplicationsState.AppEntry appEntry : list) {
                String str = appEntry.info.packageName;
                this.mApplicationsState.ensureIcon(appEntry);
                int channelCount = this.mNotificationBackend.getChannelCount(str, appEntry.info.uid);
                if (this.mNotificationBackend.getNotificationChannelsBypassingDnd(str, appEntry.info.uid).getList().size() == 0 && channelCount > 0) {
                    String key = ZenModeAllBypassingAppsPreferenceController.getKey(str);
                    Preference findPreference = this.mPreferenceCategory.findPreference("");
                    if (findPreference == null) {
                        findPreference = new AppPreference(this.mPrefContext);
                        findPreference.setKey(key);
                        findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.zen.ZenModeAddBypassingAppsPreferenceController$$ExternalSyntheticLambda0
                            @Override // androidx.preference.Preference.OnPreferenceClickListener
                            public final boolean onPreferenceClick(Preference preference) {
                                boolean lambda$updateAppList$0;
                                lambda$updateAppList$0 = ZenModeAddBypassingAppsPreferenceController.this.lambda$updateAppList$0(appEntry, preference);
                                return lambda$updateAppList$0;
                            }
                        });
                    }
                    findPreference.setTitle(BidiFormatter.getInstance().unicodeWrap(appEntry.label));
                    findPreference.setIcon(appEntry.icon);
                    arrayList.add(findPreference);
                }
            }
            if (arrayList.size() == 0) {
                PreferenceCategory preferenceCategory2 = this.mPreferenceCategory;
                String str2 = ZenModeAllBypassingAppsPreferenceController.KEY_NO_APPS;
                Preference findPreference2 = preferenceCategory2.findPreference(str2);
                if (findPreference2 == null) {
                    findPreference2 = new Preference(this.mPrefContext);
                    findPreference2.setKey(str2);
                    findPreference2.setTitle(R.string.zen_mode_bypassing_apps_subtext_none);
                }
                this.mPreferenceCategory.addPreference(findPreference2);
            }
            if (ZenModeAllBypassingAppsPreferenceController.hasAppListChanged(arrayList, this.mPreferenceCategory)) {
                this.mPreferenceCategory.removeAll();
                for (Preference preference : arrayList) {
                    this.mPreferenceCategory.addPreference(preference);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updateAppList$0(ApplicationsState.AppEntry appEntry, Preference preference) {
        Bundle bundle = new Bundle();
        bundle.putString("package", appEntry.info.packageName);
        bundle.putInt("uid", appEntry.info.uid);
        new SubSettingLauncher(this.mContext).setDestination(AppChannelsBypassingDndSettings.class.getName()).setArguments(bundle).setResultListener(this.mHostFragment, 0).setUserHandle(new UserHandle(UserHandle.getUserId(appEntry.info.uid))).setSourceMetricsCategory(1589).launch();
        return true;
    }
}
