package androidx.window.layout;

import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;
/* compiled from: WindowMetricsCalculator.kt */
/* loaded from: classes.dex */
final class WindowMetricsCalculator$Companion$reset$1 extends Lambda implements Function1<WindowMetricsCalculator, WindowMetricsCalculator> {
    public static final WindowMetricsCalculator$Companion$reset$1 INSTANCE = new WindowMetricsCalculator$Companion$reset$1();

    WindowMetricsCalculator$Companion$reset$1() {
        super(1);
    }

    @NotNull
    public final WindowMetricsCalculator invoke(@NotNull WindowMetricsCalculator it) {
        Intrinsics.checkNotNullParameter(it, "it");
        return it;
    }
}
