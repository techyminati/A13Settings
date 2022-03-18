package com.android.settings.network.helper;

import android.telephony.TelephonyManager;
import android.telephony.UiccCardInfo;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicIntegerArray;
/* loaded from: classes.dex */
public class QueryEsimCardId implements Callable<AtomicIntegerArray> {
    private TelephonyManager mTelephonyManager;

    public QueryEsimCardId(TelephonyManager telephonyManager) {
        this.mTelephonyManager = telephonyManager;
    }

    @Override // java.util.concurrent.Callable
    public AtomicIntegerArray call() {
        List<UiccCardInfo> uiccCardsInfo = this.mTelephonyManager.getUiccCardsInfo();
        if (uiccCardsInfo == null) {
            return new AtomicIntegerArray(0);
        }
        return new AtomicIntegerArray(uiccCardsInfo.stream().filter(QueryEsimCardId$$ExternalSyntheticLambda1.INSTANCE).filter(QueryEsimCardId$$ExternalSyntheticLambda0.INSTANCE).mapToInt(QueryEsimCardId$$ExternalSyntheticLambda2.INSTANCE).toArray());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$call$0(UiccCardInfo uiccCardInfo) {
        return !uiccCardInfo.isRemovable() && uiccCardInfo.getCardId() >= 0;
    }
}
