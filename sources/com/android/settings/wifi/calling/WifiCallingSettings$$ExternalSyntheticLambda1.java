package com.android.settings.wifi.calling;

import android.telephony.SubscriptionInfo;
import java.util.function.ToIntFunction;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class WifiCallingSettings$$ExternalSyntheticLambda1 implements ToIntFunction {
    public static final /* synthetic */ WifiCallingSettings$$ExternalSyntheticLambda1 INSTANCE = new WifiCallingSettings$$ExternalSyntheticLambda1();

    private /* synthetic */ WifiCallingSettings$$ExternalSyntheticLambda1() {
    }

    @Override // java.util.function.ToIntFunction
    public final int applyAsInt(Object obj) {
        int lambda$subscriptionIdList$0;
        lambda$subscriptionIdList$0 = WifiCallingSettings.lambda$subscriptionIdList$0((SubscriptionInfo) obj);
        return lambda$subscriptionIdList$0;
    }
}
