package androidx.window.layout;

import androidx.window.sidecar.SidecarDisplayFeature;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;
/* compiled from: SidecarAdapter.kt */
/* loaded from: classes.dex */
final class SidecarAdapter$translate$checkedFeature$3 extends Lambda implements Function1<SidecarDisplayFeature, Boolean> {
    public static final SidecarAdapter$translate$checkedFeature$3 INSTANCE = new SidecarAdapter$translate$checkedFeature$3();

    SidecarAdapter$translate$checkedFeature$3() {
        super(1);
    }

    @NotNull
    public final Boolean invoke(@NotNull SidecarDisplayFeature require) {
        Intrinsics.checkNotNullParameter(require, "$this$require");
        boolean z = true;
        if (!(require.getType() != 1 || require.getRect().width() == 0 || require.getRect().height() == 0)) {
            z = false;
        }
        return Boolean.valueOf(z);
    }
}
