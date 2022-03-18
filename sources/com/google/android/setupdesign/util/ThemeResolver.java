package com.google.android.setupdesign.util;

import android.app.Activity;
import android.content.Intent;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.google.android.setupdesign.R$style;
/* loaded from: classes2.dex */
public class ThemeResolver {
    private static ThemeResolver defaultResolver;
    private final int defaultTheme;
    private final ThemeSupplier defaultThemeSupplier;
    private final String oldestSupportedTheme;
    private final boolean useDayNight;

    /* loaded from: classes2.dex */
    public interface ThemeSupplier {
        String getTheme();
    }

    public static ThemeResolver getDefault() {
        if (defaultResolver == null) {
            defaultResolver = new Builder().setDefaultTheme(R$style.SudThemeGlif_DayNight).setUseDayNight(true).build();
        }
        return defaultResolver;
    }

    private ThemeResolver(int i, String str, ThemeSupplier themeSupplier, boolean z) {
        this.defaultTheme = i;
        this.oldestSupportedTheme = str;
        this.defaultThemeSupplier = themeSupplier;
        this.useDayNight = z;
    }

    public int resolve(Intent intent) {
        return resolve(intent.getStringExtra("theme"), WizardManagerHelper.isAnySetupWizard(intent));
    }

    public int resolve(Intent intent, boolean z) {
        return resolve(intent.getStringExtra("theme"), z);
    }

    public int resolve(String str, boolean z) {
        int themeRes = (!this.useDayNight || z) ? getThemeRes(str) : getDayNightThemeRes(str);
        if (themeRes == 0) {
            ThemeSupplier themeSupplier = this.defaultThemeSupplier;
            if (themeSupplier != null) {
                str = themeSupplier.getTheme();
                themeRes = (!this.useDayNight || z) ? getThemeRes(str) : getDayNightThemeRes(str);
            }
            if (themeRes == 0) {
                return this.defaultTheme;
            }
        }
        String str2 = this.oldestSupportedTheme;
        return (str2 == null || compareThemes(str, str2) >= 0) ? themeRes : this.defaultTheme;
    }

    public void applyTheme(Activity activity) {
        activity.setTheme(resolve(activity.getIntent(), WizardManagerHelper.isAnySetupWizard(activity.getIntent()) && !ThemeHelper.isSetupWizardDayNightEnabled(activity)));
    }

    private static int getDayNightThemeRes(String str) {
        if (str != null) {
            char c = 65535;
            switch (str.hashCode()) {
                case -2128555920:
                    if (str.equals("glif_v2_light")) {
                        c = 0;
                        break;
                    }
                    break;
                case -1270463490:
                    if (str.equals("material_light")) {
                        c = 1;
                        break;
                    }
                    break;
                case -1241052239:
                    if (str.equals("glif_v3_light")) {
                        c = 2;
                        break;
                    }
                    break;
                case -353548558:
                    if (str.equals("glif_v4_light")) {
                        c = 3;
                        break;
                    }
                    break;
                case 3175618:
                    if (str.equals("glif")) {
                        c = 4;
                        break;
                    }
                    break;
                case 115650329:
                    if (str.equals("glif_v2")) {
                        c = 5;
                        break;
                    }
                    break;
                case 115650330:
                    if (str.equals("glif_v3")) {
                        c = 6;
                        break;
                    }
                    break;
                case 115650331:
                    if (str.equals("glif_v4")) {
                        c = 7;
                        break;
                    }
                    break;
                case 299066663:
                    if (str.equals("material")) {
                        c = '\b';
                        break;
                    }
                    break;
                case 767685465:
                    if (str.equals("glif_light")) {
                        c = '\t';
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                case 5:
                    return R$style.SudThemeGlifV2_DayNight;
                case 1:
                case '\b':
                    return R$style.SudThemeMaterial_DayNight;
                case 2:
                case 6:
                    return R$style.SudThemeGlifV3_DayNight;
                case 3:
                case 7:
                    return R$style.SudThemeGlifV4_DayNight;
                case 4:
                case '\t':
                    return R$style.SudThemeGlif_DayNight;
            }
        }
        return 0;
    }

    private static int getThemeRes(String str) {
        if (str != null) {
            char c = 65535;
            switch (str.hashCode()) {
                case -2128555920:
                    if (str.equals("glif_v2_light")) {
                        c = 0;
                        break;
                    }
                    break;
                case -1270463490:
                    if (str.equals("material_light")) {
                        c = 1;
                        break;
                    }
                    break;
                case -1241052239:
                    if (str.equals("glif_v3_light")) {
                        c = 2;
                        break;
                    }
                    break;
                case -353548558:
                    if (str.equals("glif_v4_light")) {
                        c = 3;
                        break;
                    }
                    break;
                case 3175618:
                    if (str.equals("glif")) {
                        c = 4;
                        break;
                    }
                    break;
                case 115650329:
                    if (str.equals("glif_v2")) {
                        c = 5;
                        break;
                    }
                    break;
                case 115650330:
                    if (str.equals("glif_v3")) {
                        c = 6;
                        break;
                    }
                    break;
                case 115650331:
                    if (str.equals("glif_v4")) {
                        c = 7;
                        break;
                    }
                    break;
                case 299066663:
                    if (str.equals("material")) {
                        c = '\b';
                        break;
                    }
                    break;
                case 767685465:
                    if (str.equals("glif_light")) {
                        c = '\t';
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    return R$style.SudThemeGlifV2_Light;
                case 1:
                    return R$style.SudThemeMaterial_Light;
                case 2:
                    return R$style.SudThemeGlifV3_Light;
                case 3:
                    return R$style.SudThemeGlifV4_Light;
                case 4:
                    return R$style.SudThemeGlif;
                case 5:
                    return R$style.SudThemeGlifV2;
                case 6:
                    return R$style.SudThemeGlifV3;
                case 7:
                    return R$style.SudThemeGlifV4;
                case '\b':
                    return R$style.SudThemeMaterial;
                case '\t':
                    return R$style.SudThemeGlif_Light;
            }
        }
        return 0;
    }

    private static int compareThemes(String str, String str2) {
        return Integer.valueOf(getThemeVersion(str)).compareTo(Integer.valueOf(getThemeVersion(str2)));
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static int getThemeVersion(String str) {
        char c;
        if (str != null) {
            switch (str.hashCode()) {
                case -2128555920:
                    if (str.equals("glif_v2_light")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case -1270463490:
                    if (str.equals("material_light")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case -1241052239:
                    if (str.equals("glif_v3_light")) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case -353548558:
                    if (str.equals("glif_v4_light")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case 3175618:
                    if (str.equals("glif")) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                case 115650329:
                    if (str.equals("glif_v2")) {
                        c = 5;
                        break;
                    }
                    c = 65535;
                    break;
                case 115650330:
                    if (str.equals("glif_v3")) {
                        c = 6;
                        break;
                    }
                    c = 65535;
                    break;
                case 115650331:
                    if (str.equals("glif_v4")) {
                        c = 7;
                        break;
                    }
                    c = 65535;
                    break;
                case 299066663:
                    if (str.equals("material")) {
                        c = '\b';
                        break;
                    }
                    c = 65535;
                    break;
                case 767685465:
                    if (str.equals("glif_light")) {
                        c = '\t';
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                case 5:
                    return 3;
                case 1:
                case '\b':
                    return 1;
                case 2:
                case 6:
                    return 4;
                case 3:
                case 7:
                    return 5;
                case 4:
                case '\t':
                    return 2;
            }
        }
        return -1;
    }

    /* loaded from: classes2.dex */
    public static class Builder {
        private ThemeSupplier defaultThemeSupplier;
        private int defaultTheme = R$style.SudThemeGlif_DayNight;
        private String oldestSupportedTheme = null;
        private boolean useDayNight = true;

        public Builder setDefaultTheme(int i) {
            this.defaultTheme = i;
            return this;
        }

        public Builder setUseDayNight(boolean z) {
            this.useDayNight = z;
            return this;
        }

        public ThemeResolver build() {
            return new ThemeResolver(this.defaultTheme, this.oldestSupportedTheme, this.defaultThemeSupplier, this.useDayNight);
        }
    }
}
