package com.android.settings.network.helper;

import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class SelectableSubscriptions$$ExternalSyntheticLambda6 implements Predicate {
    public static final /* synthetic */ SelectableSubscriptions$$ExternalSyntheticLambda6 INSTANCE = new SelectableSubscriptions$$ExternalSyntheticLambda6();

    private /* synthetic */ SelectableSubscriptions$$ExternalSyntheticLambda6() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean isActive;
        isActive = ((SubscriptionAnnotation) obj).isActive();
        return isActive;
    }
}
