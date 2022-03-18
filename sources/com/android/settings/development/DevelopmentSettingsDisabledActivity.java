package com.android.settings.development;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import androidx.window.R;
/* loaded from: classes.dex */
public class DevelopmentSettingsDisabledActivity extends Activity {
    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Toast.makeText(this, (int) R.string.dev_settings_disabled_warning, 0).show();
        finish();
    }
}
