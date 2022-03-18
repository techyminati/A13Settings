package com.android.settings.fuelgauge;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.window.R;
import com.android.settings.core.SubSettingLauncher;
import com.android.settingslib.Utils;
/* loaded from: classes.dex */
public class AdvancedPowerUsageDetailActivity extends AppCompatActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        String str = null;
        Uri data = intent == null ? null : intent.getData();
        if (data != null) {
            str = data.getSchemeSpecificPart();
        }
        if (str != null) {
            Bundle bundle2 = new Bundle(4);
            PackageManager packageManager = getPackageManager();
            bundle2.putString("extra_package_name", str);
            bundle2.putString("extra_power_usage_percent", Utils.formatPercentage(0));
            if (intent.getBooleanExtra("request_ignore_background_restriction", false)) {
                bundle2.putString(":settings:fragment_args_key", "background_activity");
            }
            try {
                bundle2.putInt("extra_uid", packageManager.getPackageUid(str, 0));
            } catch (PackageManager.NameNotFoundException e) {
                Log.w("AdvancedPowerDetailActivity", "Cannot find package: " + str, e);
            }
            new SubSettingLauncher(this).setDestination(AdvancedPowerUsageDetail.class.getName()).setTitleRes(R.string.battery_details_title).setArguments(bundle2).setSourceMetricsCategory(20).launch();
        }
        finish();
    }
}
