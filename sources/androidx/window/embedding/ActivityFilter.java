package androidx.window.embedding;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import androidx.window.core.ExperimentalWindowApi;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ActivityFilter.kt */
@ExperimentalWindowApi
/* loaded from: classes.dex */
public final class ActivityFilter {
    @NotNull
    private final ComponentName componentName;
    @Nullable
    private final String intentAction;

    /* JADX WARN: Removed duplicated region for block: B:19:0x0058  */
    /* JADX WARN: Removed duplicated region for block: B:28:0x0081  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public ActivityFilter(@org.jetbrains.annotations.NotNull android.content.ComponentName r11, @org.jetbrains.annotations.Nullable java.lang.String r12) {
        /*
            r10 = this;
            java.lang.String r0 = "componentName"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r11, r0)
            r10.<init>()
            r10.componentName = r11
            r10.intentAction = r12
            java.lang.String r10 = r11.getPackageName()
            java.lang.String r12 = "componentName.packageName"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r10, r12)
            java.lang.String r11 = r11.getClassName()
            java.lang.String r12 = "componentName.className"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r11, r12)
            int r12 = r10.length()
            r7 = 1
            r8 = 0
            if (r12 <= 0) goto L_0x0028
            r12 = r7
            goto L_0x0029
        L_0x0028:
            r12 = r8
        L_0x0029:
            if (r12 == 0) goto L_0x0099
            int r12 = r11.length()
            if (r12 <= 0) goto L_0x0033
            r12 = r7
            goto L_0x0034
        L_0x0033:
            r12 = r8
        L_0x0034:
            if (r12 == 0) goto L_0x008d
            java.lang.String r12 = "*"
            r0 = 2
            r9 = 0
            boolean r1 = kotlin.text.StringsKt.contains$default(r10, r12, r8, r0, r9)
            if (r1 == 0) goto L_0x0055
            r3 = 0
            r4 = 0
            r5 = 6
            r6 = 0
            java.lang.String r2 = "*"
            r1 = r10
            int r1 = kotlin.text.StringsKt.indexOf$default(r1, r2, r3, r4, r5, r6)
            int r10 = r10.length()
            int r10 = r10 - r7
            if (r1 != r10) goto L_0x0053
            goto L_0x0055
        L_0x0053:
            r10 = r8
            goto L_0x0056
        L_0x0055:
            r10 = r7
        L_0x0056:
            if (r10 == 0) goto L_0x0081
            boolean r10 = kotlin.text.StringsKt.contains$default(r11, r12, r8, r0, r9)
            if (r10 == 0) goto L_0x0072
            r2 = 0
            r3 = 0
            r4 = 6
            r5 = 0
            java.lang.String r1 = "*"
            r0 = r11
            int r10 = kotlin.text.StringsKt.indexOf$default(r0, r1, r2, r3, r4, r5)
            int r11 = r11.length()
            int r11 = r11 - r7
            if (r10 != r11) goto L_0x0071
            goto L_0x0072
        L_0x0071:
            r7 = r8
        L_0x0072:
            if (r7 == 0) goto L_0x0075
            return
        L_0x0075:
            java.lang.IllegalArgumentException r10 = new java.lang.IllegalArgumentException
            java.lang.String r11 = "Wildcard in class name is only allowed at the end."
            java.lang.String r11 = r11.toString()
            r10.<init>(r11)
            throw r10
        L_0x0081:
            java.lang.IllegalArgumentException r10 = new java.lang.IllegalArgumentException
            java.lang.String r11 = "Wildcard in package name is only allowed at the end."
            java.lang.String r11 = r11.toString()
            r10.<init>(r11)
            throw r10
        L_0x008d:
            java.lang.IllegalArgumentException r10 = new java.lang.IllegalArgumentException
            java.lang.String r11 = "Activity class name must not be empty."
            java.lang.String r11 = r11.toString()
            r10.<init>(r11)
            throw r10
        L_0x0099:
            java.lang.IllegalArgumentException r10 = new java.lang.IllegalArgumentException
            java.lang.String r11 = "Package name must not be empty"
            java.lang.String r11 = r11.toString()
            r10.<init>(r11)
            throw r10
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.window.embedding.ActivityFilter.<init>(android.content.ComponentName, java.lang.String):void");
    }

    @NotNull
    public final ComponentName getComponentName() {
        return this.componentName;
    }

    @Nullable
    public final String getIntentAction() {
        return this.intentAction;
    }

    public final boolean matchesIntent(@NotNull Intent intent) {
        Intrinsics.checkNotNullParameter(intent, "intent");
        if (!MatcherUtils.INSTANCE.areComponentsMatching$window_release(intent.getComponent(), this.componentName)) {
            return false;
        }
        String str = this.intentAction;
        return str == null || Intrinsics.areEqual(str, intent.getAction());
    }

    public final boolean matchesActivity(@NotNull Activity activity) {
        Intrinsics.checkNotNullParameter(activity, "activity");
        if (MatcherUtils.INSTANCE.areActivityOrIntentComponentsMatching$window_release(activity, this.componentName)) {
            String str = this.intentAction;
            if (str != null) {
                Intent intent = activity.getIntent();
                if (Intrinsics.areEqual(str, intent == null ? null : intent.getAction())) {
                }
            }
            return true;
        }
        return false;
    }

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActivityFilter)) {
            return false;
        }
        ActivityFilter activityFilter = (ActivityFilter) obj;
        return Intrinsics.areEqual(this.componentName, activityFilter.componentName) && Intrinsics.areEqual(this.intentAction, activityFilter.intentAction);
    }

    public int hashCode() {
        int hashCode = this.componentName.hashCode() * 31;
        String str = this.intentAction;
        return hashCode + (str == null ? 0 : str.hashCode());
    }

    @NotNull
    public String toString() {
        return "ActivityFilter(componentName=" + this.componentName + ", intentAction=" + ((Object) this.intentAction) + ')';
    }
}
