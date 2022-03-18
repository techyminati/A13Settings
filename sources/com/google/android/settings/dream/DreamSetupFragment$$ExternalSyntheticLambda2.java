package com.google.android.settings.dream;

import com.android.settingslib.dream.DreamBackend;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes2.dex */
public final /* synthetic */ class DreamSetupFragment$$ExternalSyntheticLambda2 implements Predicate {
    public static final /* synthetic */ DreamSetupFragment$$ExternalSyntheticLambda2 INSTANCE = new DreamSetupFragment$$ExternalSyntheticLambda2();

    private /* synthetic */ DreamSetupFragment$$ExternalSyntheticLambda2() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean z;
        z = ((DreamBackend.DreamInfo) obj).isActive;
        return z;
    }
}
