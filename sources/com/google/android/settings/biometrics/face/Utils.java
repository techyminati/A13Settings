package com.google.android.settings.biometrics.face;

import android.content.Context;
/* loaded from: classes2.dex */
public class Utils {
    public static float dpToPx(Context context, int i) {
        return i * (context.getResources().getDisplayMetrics().densityDpi / 160.0f);
    }
}
