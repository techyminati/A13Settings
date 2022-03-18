package com.android.settings.safetycenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.google.common.collect.ImmutableList;
import java.util.List;
/* loaded from: classes.dex */
public class SafetySourceBroadcastReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (SafetyCenterStatusHolder.get().isEnabled(context)) {
            if ("android.safetycenter.action.REFRESH_SAFETY_SOURCES".equals(intent.getAction())) {
                String[] stringArrayExtra = intent.getStringArrayExtra("android.safetycenter.extra.REFRESH_SAFETY_SOURCE_IDS");
                if (stringArrayExtra != null && stringArrayExtra.length > 0) {
                    refreshSafetySources(context, ImmutableList.copyOf(stringArrayExtra));
                }
            } else if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
                refreshAllSafetySources(context);
            }
        }
    }

    private static void refreshSafetySources(Context context, List<String> list) {
        if (list.contains("LockScreenSafetySource")) {
            LockScreenSafetySource.sendSafetyData(context);
        }
        if (list.contains("BiometricsSafetySource")) {
            BiometricsSafetySource.sendSafetyData(context);
        }
    }

    private static void refreshAllSafetySources(Context context) {
        LockScreenSafetySource.sendSafetyData(context);
        BiometricsSafetySource.sendSafetyData(context);
    }
}
