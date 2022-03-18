package com.android.settingslib.utils;

import android.os.Build;
/* loaded from: classes.dex */
public final class BuildCompatUtils {
    public static boolean isAtLeastS() {
        String str = Build.VERSION.CODENAME;
        if (str.equals("REL")) {
            return true;
        }
        return str.length() >= 1 && str.toUpperCase().charAt(0) >= 'S' && str.toUpperCase().charAt(0) <= 'Z';
    }
}
