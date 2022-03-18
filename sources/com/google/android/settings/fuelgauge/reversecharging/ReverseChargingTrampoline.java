package com.google.android.settings.fuelgauge.reversecharging;

import android.os.Bundle;
import androidx.window.R;
import com.android.settings.core.SettingsBaseActivity;
import com.android.settings.core.SubSettingLauncher;
/* loaded from: classes2.dex */
public class ReverseChargingTrampoline extends SettingsBaseActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.SettingsBaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        startFragmentIfNecessary(ReverseChargingManager.getInstance(this));
    }

    void startFragmentIfNecessary(ReverseChargingManager reverseChargingManager) {
        if (reverseChargingManager.isSupportedReverseCharging()) {
            new SubSettingLauncher(getApplicationContext()).setDestination(ReverseChargingDashboardFragment.class.getName()).setTitleRes(R.string.reverse_charging_title).addFlags(268435456).setSourceMetricsCategory(1842).launch();
        }
        finish();
    }
}
