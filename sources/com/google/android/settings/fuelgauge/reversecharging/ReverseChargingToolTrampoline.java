package com.google.android.settings.fuelgauge.reversecharging;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.core.SettingsBaseActivity;
/* loaded from: classes2.dex */
public class ReverseChargingToolTrampoline extends SettingsBaseActivity {
    @VisibleForTesting
    static final String EXTRA_REVERSE_CHARGING_IS_SUPPORTED = "extra_reverse_charging_is_supported";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.SettingsBaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getIntent() == null) {
            Log.w("RCToolTrampoline", "Intent is null!");
            finish();
        }
        handleIntent(getIntent());
    }

    @VisibleForTesting
    void handleIntent(Intent intent) {
        if (intent.getBooleanExtra(EXTRA_REVERSE_CHARGING_IS_SUPPORTED, false)) {
            Intent intent2 = new Intent();
            intent2.putExtra(EXTRA_REVERSE_CHARGING_IS_SUPPORTED, ReverseChargingManager.getInstance(getBaseContext()).isSupportedReverseCharging());
            setResult(10, intent2);
            finish();
        }
    }
}
