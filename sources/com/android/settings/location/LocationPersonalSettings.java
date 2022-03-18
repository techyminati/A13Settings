package com.android.settings.location;

import android.content.Context;
import androidx.window.R;
import com.android.settings.dashboard.DashboardFragment;
/* loaded from: classes.dex */
public class LocationPersonalSettings extends DashboardFragment {
    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_url_location_access;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "LocationPersonal";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 63;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.location_settings_personal;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((AppLocationPermissionPreferenceController) use(AppLocationPermissionPreferenceController.class)).init(this);
        ((LocationSettingsFooterPreferenceController) use(LocationSettingsFooterPreferenceController.class)).init(this);
        ((RecentLocationAccessSeeAllButtonPreferenceController) use(RecentLocationAccessSeeAllButtonPreferenceController.class)).init(this);
        int i = getArguments().getInt("profile");
        RecentLocationAccessPreferenceController recentLocationAccessPreferenceController = (RecentLocationAccessPreferenceController) use(RecentLocationAccessPreferenceController.class);
        recentLocationAccessPreferenceController.init(this);
        recentLocationAccessPreferenceController.setProfileType(i);
    }
}
