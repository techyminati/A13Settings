package androidx.window.layout;

import androidx.window.extensions.WindowExtensionsProvider;
import androidx.window.extensions.layout.WindowLayoutComponent;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.Nullable;
/* compiled from: SafeWindowLayoutComponentProvider.kt */
/* loaded from: classes.dex */
final class SafeWindowLayoutComponentProvider$windowLayoutComponent$2 extends Lambda implements Function0<WindowLayoutComponent> {
    public static final SafeWindowLayoutComponentProvider$windowLayoutComponent$2 INSTANCE = new SafeWindowLayoutComponentProvider$windowLayoutComponent$2();

    SafeWindowLayoutComponentProvider$windowLayoutComponent$2() {
        super(0);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // kotlin.jvm.functions.Function0
    @Nullable
    public final WindowLayoutComponent invoke() {
        boolean canUseWindowLayoutComponent;
        ClassLoader classLoader = SafeWindowLayoutComponentProvider.class.getClassLoader();
        if (classLoader != null) {
            canUseWindowLayoutComponent = SafeWindowLayoutComponentProvider.INSTANCE.canUseWindowLayoutComponent(classLoader);
            if (canUseWindowLayoutComponent) {
                try {
                    return WindowExtensionsProvider.getWindowExtensions().getWindowLayoutComponent();
                } catch (UnsupportedOperationException unused) {
                    return null;
                }
            }
        }
        return null;
    }
}
