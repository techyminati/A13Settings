package androidx.core.text;

import android.annotation.SuppressLint;
import android.text.Html;
import android.text.Spanned;
@SuppressLint({"InlinedApi"})
/* loaded from: classes.dex */
public final class HtmlCompat {
    public static Spanned fromHtml(String str, int i) {
        return Html.fromHtml(str, i);
    }

    public static String toHtml(Spanned spanned, int i) {
        return Html.toHtml(spanned, i);
    }
}
