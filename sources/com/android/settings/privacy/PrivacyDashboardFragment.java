package com.android.settings.privacy;

import android.content.Context;
import android.os.Bundle;
import androidx.window.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.notification.LockScreenNotificationPreferenceController;
import com.android.settings.safetycenter.SafetyCenterStatusHolder;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class PrivacyDashboardFragment extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.privacy_dashboard_settings) { // from class: com.android.settings.privacy.PrivacyDashboardFragment.1
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return PrivacyDashboardFragment.buildPreferenceControllers(context, null);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return !SafetyCenterStatusHolder.get().isEnabled(context);
        }
    };

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_url_privacy_dashboard;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "PrivacyDashboardFrag";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1587;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.privacy_dashboard_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        replaceEnterpriseStringTitle("privacy_lock_screen_work_profile_notifications", "Settings.WORK_PROFILE_LOCKED_NOTIFICATION_TITLE", R.string.locked_work_profile_notification_title);
        replaceEnterpriseStringTitle("interact_across_profiles_privacy", "Settings.CONNECTED_WORK_AND_PERSONAL_APPS_TITLE", R.string.interact_across_profiles_title);
        replaceEnterpriseStringTitle("privacy_work_profile_notifications_category", "Settings.WORK_PROFILE_NOTIFICATIONS_SECTION_HEADER", R.string.profile_section_header);
        replaceEnterpriseStringTitle("work_policy_info", "Settings.WORK_PROFILE_PRIVACY_POLICY_INFO", R.string.work_policy_privacy_settings);
        replaceEnterpriseStringSummary("work_policy_info", "Settings.WORK_PROFILE_PRIVACY_POLICY_INFO_SUMMARY", R.string.work_policy_privacy_settings_summary);
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle) {
        ArrayList arrayList = new ArrayList();
        LockScreenNotificationPreferenceController lockScreenNotificationPreferenceController = new LockScreenNotificationPreferenceController(context, "privacy_lock_screen_notifications", "privacy_work_profile_notifications_category", "privacy_lock_screen_work_profile_notifications");
        if (lifecycle != null) {
            lifecycle.addObserver(lockScreenNotificationPreferenceController);
        }
        arrayList.add(lockScreenNotificationPreferenceController);
        return arrayList;
    }
}
