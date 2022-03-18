package com.android.settings.applications;

import android.content.pm.PackageInfo;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class AppPermissionsPreferenceController$$ExternalSyntheticLambda0 implements Predicate {
    public static final /* synthetic */ AppPermissionsPreferenceController$$ExternalSyntheticLambda0 INSTANCE = new AppPermissionsPreferenceController$$ExternalSyntheticLambda0();

    private /* synthetic */ AppPermissionsPreferenceController$$ExternalSyntheticLambda0() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean lambda$queryPermissionSummary$0;
        lambda$queryPermissionSummary$0 = AppPermissionsPreferenceController.lambda$queryPermissionSummary$0((PackageInfo) obj);
        return lambda$queryPermissionSummary$0;
    }
}
