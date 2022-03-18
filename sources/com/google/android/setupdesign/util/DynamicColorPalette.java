package com.google.android.setupdesign.util;

import android.content.Context;
import com.google.android.setupdesign.R$color;
/* loaded from: classes2.dex */
public final class DynamicColorPalette {
    private static int colorRes;

    public static int getColor(Context context, int i) {
        switch (i) {
            case 0:
                colorRes = R$color.sud_dynamic_color_accent_glif_v3;
                break;
            case 1:
                colorRes = R$color.sud_system_primary_text;
                break;
            case 2:
                colorRes = R$color.sud_system_secondary_text;
                break;
            case 3:
                colorRes = R$color.sud_system_tertiary_text_inactive;
                break;
            case 4:
                colorRes = R$color.sud_system_error_warning;
                break;
            case 5:
                colorRes = R$color.sud_system_success_done;
                break;
            case 6:
                colorRes = R$color.sud_system_fallback_accent;
                break;
            case 7:
                colorRes = R$color.sud_system_background_surface;
                break;
        }
        return context.getResources().getColor(colorRes);
    }
}
