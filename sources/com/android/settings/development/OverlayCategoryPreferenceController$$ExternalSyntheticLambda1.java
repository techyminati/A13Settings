package com.android.settings.development;

import android.content.om.OverlayInfo;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class OverlayCategoryPreferenceController$$ExternalSyntheticLambda1 implements Predicate {
    public static final /* synthetic */ OverlayCategoryPreferenceController$$ExternalSyntheticLambda1 INSTANCE = new OverlayCategoryPreferenceController$$ExternalSyntheticLambda1();

    private /* synthetic */ OverlayCategoryPreferenceController$$ExternalSyntheticLambda1() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean isEnabled;
        isEnabled = ((OverlayInfo) obj).isEnabled();
        return isEnabled;
    }
}
