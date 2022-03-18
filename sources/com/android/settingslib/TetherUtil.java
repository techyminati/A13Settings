package com.android.settingslib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.UserHandle;
/* loaded from: classes.dex */
public class TetherUtil {
    public static boolean isTetherAvailable(Context context) {
        return (((ConnectivityManager) context.getSystemService(ConnectivityManager.class)).isTetheringSupported() || (RestrictedLockUtilsInternal.checkIfRestrictionEnforced(context, "no_config_tethering", UserHandle.myUserId()) != null)) && !RestrictedLockUtilsInternal.hasBaseUserRestriction(context, "no_config_tethering", UserHandle.myUserId());
    }
}
