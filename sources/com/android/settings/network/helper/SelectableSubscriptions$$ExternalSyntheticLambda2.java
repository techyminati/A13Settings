package com.android.settings.network.helper;

import android.telephony.SubscriptionManager;
import java.util.function.Function;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class SelectableSubscriptions$$ExternalSyntheticLambda2 implements Function {
    public static final /* synthetic */ SelectableSubscriptions$$ExternalSyntheticLambda2 INSTANCE = new SelectableSubscriptions$$ExternalSyntheticLambda2();

    private /* synthetic */ SelectableSubscriptions$$ExternalSyntheticLambda2() {
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        return ((SubscriptionManager) obj).getAvailableSubscriptionInfoList();
    }
}
