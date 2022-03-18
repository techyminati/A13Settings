package androidx.core.os;

import android.os.Build;
/* loaded from: classes.dex */
public class BuildCompat {
    @Deprecated
    public static boolean isAtLeastR() {
        return true;
    }

    public static boolean isAtLeastS() {
        return true;
    }

    protected static boolean isAtLeastPreReleaseCodename(String str, String str2) {
        return !"REL".equals(str2) && str2.compareTo(str) >= 0;
    }

    public static boolean isAtLeastT() {
        return isAtLeastPreReleaseCodename("T", Build.VERSION.CODENAME);
    }
}
