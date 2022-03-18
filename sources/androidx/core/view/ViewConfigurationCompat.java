package androidx.core.view;

import android.content.Context;
import android.view.ViewConfiguration;
/* loaded from: classes.dex */
public final class ViewConfigurationCompat {
    public static float getScaledHorizontalScrollFactor(ViewConfiguration viewConfiguration, Context context) {
        return viewConfiguration.getScaledHorizontalScrollFactor();
    }

    public static float getScaledVerticalScrollFactor(ViewConfiguration viewConfiguration, Context context) {
        return viewConfiguration.getScaledVerticalScrollFactor();
    }

    public static boolean shouldShowMenuShortcutsWhenKeyboardPresent(ViewConfiguration viewConfiguration, Context context) {
        return viewConfiguration.shouldShowMenuShortcutsWhenKeyboardPresent();
    }
}
