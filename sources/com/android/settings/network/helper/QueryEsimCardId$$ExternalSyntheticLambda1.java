package com.android.settings.network.helper;

import android.telephony.UiccCardInfo;
import java.util.Objects;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class QueryEsimCardId$$ExternalSyntheticLambda1 implements Predicate {
    public static final /* synthetic */ QueryEsimCardId$$ExternalSyntheticLambda1 INSTANCE = new QueryEsimCardId$$ExternalSyntheticLambda1();

    private /* synthetic */ QueryEsimCardId$$ExternalSyntheticLambda1() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        return Objects.nonNull((UiccCardInfo) obj);
    }
}
