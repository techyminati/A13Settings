package androidx.core.view;

import android.view.DisplayCutout;
import androidx.core.util.ObjectsCompat;
/* loaded from: classes.dex */
public final class DisplayCutoutCompat {
    private final Object mDisplayCutout;

    private DisplayCutoutCompat(Object obj) {
        this.mDisplayCutout = obj;
    }

    public int getSafeInsetTop() {
        return ((DisplayCutout) this.mDisplayCutout).getSafeInsetTop();
    }

    public int getSafeInsetBottom() {
        return ((DisplayCutout) this.mDisplayCutout).getSafeInsetBottom();
    }

    public int getSafeInsetLeft() {
        return ((DisplayCutout) this.mDisplayCutout).getSafeInsetLeft();
    }

    public int getSafeInsetRight() {
        return ((DisplayCutout) this.mDisplayCutout).getSafeInsetRight();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || DisplayCutoutCompat.class != obj.getClass()) {
            return false;
        }
        return ObjectsCompat.equals(this.mDisplayCutout, ((DisplayCutoutCompat) obj).mDisplayCutout);
    }

    public int hashCode() {
        Object obj = this.mDisplayCutout;
        if (obj == null) {
            return 0;
        }
        return obj.hashCode();
    }

    public String toString() {
        return "DisplayCutoutCompat{" + this.mDisplayCutout + "}";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static DisplayCutoutCompat wrap(Object obj) {
        if (obj == null) {
            return null;
        }
        return new DisplayCutoutCompat(obj);
    }
}
