package com.android.settings.network.helper;

import android.telephony.UiccPortInfo;
import java.util.function.ToIntFunction;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class QuerySimSlotIndex$$ExternalSyntheticLambda3 implements ToIntFunction {
    public static final /* synthetic */ QuerySimSlotIndex$$ExternalSyntheticLambda3 INSTANCE = new QuerySimSlotIndex$$ExternalSyntheticLambda3();

    private /* synthetic */ QuerySimSlotIndex$$ExternalSyntheticLambda3() {
    }

    @Override // java.util.function.ToIntFunction
    public final int applyAsInt(Object obj) {
        int logicalSlotIndex;
        logicalSlotIndex = ((UiccPortInfo) obj).getLogicalSlotIndex();
        return logicalSlotIndex;
    }
}
