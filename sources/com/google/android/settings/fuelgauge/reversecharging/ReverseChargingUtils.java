package com.google.android.settings.fuelgauge.reversecharging;

import android.content.Context;
/* loaded from: classes2.dex */
final class ReverseChargingUtils {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getAvailability(Context context, ReverseChargingManager reverseChargingManager) {
        if (reverseChargingManager == null && context != null) {
            reverseChargingManager = ReverseChargingManager.getInstance(context);
        }
        return (reverseChargingManager == null || !reverseChargingManager.isSupportedReverseCharging()) ? 3 : 0;
    }
}
