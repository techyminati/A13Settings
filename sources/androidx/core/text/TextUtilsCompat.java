package androidx.core.text;

import android.text.TextUtils;
import java.util.Locale;
/* loaded from: classes.dex */
public final class TextUtilsCompat {
    private static final Locale ROOT = new Locale("", "");

    public static int getLayoutDirectionFromLocale(Locale locale) {
        return TextUtils.getLayoutDirectionFromLocale(locale);
    }
}
