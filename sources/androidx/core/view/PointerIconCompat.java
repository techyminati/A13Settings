package androidx.core.view;

import android.content.Context;
import android.view.PointerIcon;
/* loaded from: classes.dex */
public final class PointerIconCompat {
    private Object mPointerIcon;

    private PointerIconCompat(Object obj) {
        this.mPointerIcon = obj;
    }

    public Object getPointerIcon() {
        return this.mPointerIcon;
    }

    public static PointerIconCompat getSystemIcon(Context context, int i) {
        return new PointerIconCompat(PointerIcon.getSystemIcon(context, i));
    }
}
