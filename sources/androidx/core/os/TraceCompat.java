package androidx.core.os;

import android.os.Trace;
@Deprecated
/* loaded from: classes.dex */
public final class TraceCompat {
    public static void beginSection(String str) {
        Trace.beginSection(str);
    }

    public static void endSection() {
        Trace.endSection();
    }
}
