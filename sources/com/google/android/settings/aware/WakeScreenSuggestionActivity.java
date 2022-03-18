package com.google.android.settings.aware;

import android.app.Activity;
import android.content.Context;
import android.hardware.display.AmbientDisplayConfiguration;
import android.os.Bundle;
import android.os.UserHandle;
import com.android.settings.core.SubSettingLauncher;
/* loaded from: classes2.dex */
public class WakeScreenSuggestionActivity extends Activity {
    public static boolean isSuggestionComplete(Context context) {
        return new AmbientDisplayConfiguration(context).wakeLockScreenGestureEnabled(UserHandle.myUserId()) || !new AwareHelper(context).isGestureConfigurable();
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        new SubSettingLauncher(this).setDestination(WakeScreenGestureSettings.class.getName()).setSourceMetricsCategory(0).launch();
        finish();
    }
}
