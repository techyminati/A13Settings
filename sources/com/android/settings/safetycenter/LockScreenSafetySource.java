package com.android.settings.safetycenter;

import android.app.PendingIntent;
import android.content.Context;
import android.safetycenter.SafetySourceData;
import android.safetycenter.SafetySourceStatus;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.password.ChooseLockGeneric;
/* loaded from: classes.dex */
public final class LockScreenSafetySource {
    public static void sendSafetyData(Context context) {
        if (SafetyCenterStatusHolder.get().isEnabled(context)) {
            SafetyCenterManagerWrapper.get().sendSafetyCenterUpdate(context, new SafetySourceData.Builder("LockScreenSafetySource").setStatus(new SafetySourceStatus.Builder("Lock Screen", "Lock screen settings", 200, PendingIntent.getActivity(context, 0, new SubSettingLauncher(context).setDestination(ChooseLockGeneric.ChooseLockGenericFragment.class.getName()).setSourceMetricsCategory(1884).setTransitionType(1).toIntent(), 67108864)).build()).build());
        }
    }
}
