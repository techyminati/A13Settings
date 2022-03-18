package com.android.settings.network.helper;

import android.telephony.UiccCardInfo;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class QueryEsimCardId$$ExternalSyntheticLambda0 implements Predicate {
    public static final /* synthetic */ QueryEsimCardId$$ExternalSyntheticLambda0 INSTANCE = new QueryEsimCardId$$ExternalSyntheticLambda0();

    private /* synthetic */ QueryEsimCardId$$ExternalSyntheticLambda0() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean lambda$call$0;
        lambda$call$0 = QueryEsimCardId.lambda$call$0((UiccCardInfo) obj);
        return lambda$call$0;
    }
}
