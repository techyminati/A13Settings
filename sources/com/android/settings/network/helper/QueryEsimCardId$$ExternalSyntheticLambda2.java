package com.android.settings.network.helper;

import android.telephony.UiccCardInfo;
import java.util.function.ToIntFunction;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class QueryEsimCardId$$ExternalSyntheticLambda2 implements ToIntFunction {
    public static final /* synthetic */ QueryEsimCardId$$ExternalSyntheticLambda2 INSTANCE = new QueryEsimCardId$$ExternalSyntheticLambda2();

    private /* synthetic */ QueryEsimCardId$$ExternalSyntheticLambda2() {
    }

    @Override // java.util.function.ToIntFunction
    public final int applyAsInt(Object obj) {
        return ((UiccCardInfo) obj).getCardId();
    }
}
