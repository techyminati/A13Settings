package com.android.settings.development;

import android.content.om.OverlayInfo;
import java.util.function.ToIntFunction;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class OverlayCategoryPreferenceController$$ExternalSyntheticLambda2 implements ToIntFunction {
    public static final /* synthetic */ OverlayCategoryPreferenceController$$ExternalSyntheticLambda2 INSTANCE = new OverlayCategoryPreferenceController$$ExternalSyntheticLambda2();

    private /* synthetic */ OverlayCategoryPreferenceController$$ExternalSyntheticLambda2() {
    }

    @Override // java.util.function.ToIntFunction
    public final int applyAsInt(Object obj) {
        int i;
        i = ((OverlayInfo) obj).priority;
        return i;
    }
}
