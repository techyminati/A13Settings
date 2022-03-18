package com.android.settings.fuelgauge;

import android.os.Bundle;
import androidx.preference.Preference;
import androidx.window.R;
import com.android.settings.Settings;
import com.android.settings.SettingsActivity;
import com.android.settings.applications.manageapplications.ManageApplications;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.fuelgauge.PowerAllowlistBackend;
/* loaded from: classes.dex */
public class BatteryOptimizationPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private PowerAllowlistBackend mBackend;
    private DashboardFragment mFragment;
    private String mPackageName;
    private SettingsActivity mSettingsActivity;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "battery_optimization";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public BatteryOptimizationPreferenceController(SettingsActivity settingsActivity, DashboardFragment dashboardFragment, String str) {
        super(settingsActivity);
        this.mFragment = dashboardFragment;
        this.mSettingsActivity = settingsActivity;
        this.mPackageName = str;
        this.mBackend = PowerAllowlistBackend.getInstance(settingsActivity);
    }

    BatteryOptimizationPreferenceController(SettingsActivity settingsActivity, DashboardFragment dashboardFragment, String str, PowerAllowlistBackend powerAllowlistBackend) {
        super(settingsActivity);
        this.mFragment = dashboardFragment;
        this.mSettingsActivity = settingsActivity;
        this.mPackageName = str;
        this.mBackend = powerAllowlistBackend;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        this.mBackend.refreshList();
        preference.setSummary(this.mBackend.isAllowlisted(this.mPackageName) ? R.string.high_power_on : R.string.high_power_off);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!"battery_optimization".equals(preference.getKey())) {
            return false;
        }
        Bundle bundle = new Bundle();
        bundle.putString("classname", Settings.HighPowerApplicationsActivity.class.getName());
        new SubSettingLauncher(this.mSettingsActivity).setDestination(ManageApplications.class.getName()).setArguments(bundle).setTitleRes(R.string.high_power_apps).setSourceMetricsCategory(this.mFragment.getMetricsCategory()).launch();
        return true;
    }
}
