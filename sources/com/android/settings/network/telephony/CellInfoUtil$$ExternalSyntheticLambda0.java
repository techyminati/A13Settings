package com.android.settings.network.telephony;

import android.telephony.CellInfo;
import java.util.function.Function;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class CellInfoUtil$$ExternalSyntheticLambda0 implements Function {
    public static final /* synthetic */ CellInfoUtil$$ExternalSyntheticLambda0 INSTANCE = new CellInfoUtil$$ExternalSyntheticLambda0();

    private /* synthetic */ CellInfoUtil$$ExternalSyntheticLambda0() {
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        String cellInfoToString;
        cellInfoToString = CellInfoUtil.cellInfoToString((CellInfo) obj);
        return cellInfoToString;
    }
}
