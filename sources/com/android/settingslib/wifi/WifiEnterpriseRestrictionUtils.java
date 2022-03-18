package com.android.settingslib.wifi;

import android.content.Context;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
/* loaded from: classes.dex */
public class WifiEnterpriseRestrictionUtils {
    private static boolean isAtLeastT() {
        return true;
    }

    public static boolean isWifiTetheringAllowed(Context context) {
        Bundle userRestrictions = ((UserManager) context.getSystemService(UserManager.class)).getUserRestrictions();
        if (!isAtLeastT() || !userRestrictions.getBoolean("no_wifi_tethering")) {
            return true;
        }
        Log.i("WifiEntResUtils", "Wi-Fi Tethering isn't available due to user restriction.");
        return false;
    }

    public static boolean isWifiDirectAllowed(Context context) {
        Bundle userRestrictions = ((UserManager) context.getSystemService(UserManager.class)).getUserRestrictions();
        if (!isAtLeastT() || !userRestrictions.getBoolean("no_wifi_direct")) {
            return true;
        }
        Log.i("WifiEntResUtils", "Wi-Fi Direct isn't available due to user restriction.");
        return false;
    }

    public static boolean isAddWifiConfigAllowed(Context context) {
        Bundle userRestrictions = ((UserManager) context.getSystemService(UserManager.class)).getUserRestrictions();
        if (!isAtLeastT() || !userRestrictions.getBoolean("no_add_wifi_config")) {
            return true;
        }
        Log.i("WifiEntResUtils", "Wi-Fi Add network isn't available due to user restriction.");
        return false;
    }
}
