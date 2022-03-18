package com.android.settings.development;

import android.content.om.OverlayInfo;
import java.util.function.Function;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class OverlayCategoryPreferenceController$$ExternalSyntheticLambda0 implements Function {
    public static final /* synthetic */ OverlayCategoryPreferenceController$$ExternalSyntheticLambda0 INSTANCE = new OverlayCategoryPreferenceController$$ExternalSyntheticLambda0();

    private /* synthetic */ OverlayCategoryPreferenceController$$ExternalSyntheticLambda0() {
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        String str;
        str = ((OverlayInfo) obj).packageName;
        return str;
    }
}
