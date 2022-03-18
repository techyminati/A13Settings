package com.android.settings.network;

import com.android.settings.network.helper.SubscriptionAnnotation;
import java.util.List;
import java.util.function.Function;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class SubscriptionUtil$$ExternalSyntheticLambda7 implements Function {
    public static final /* synthetic */ SubscriptionUtil$$ExternalSyntheticLambda7 INSTANCE = new SubscriptionUtil$$ExternalSyntheticLambda7();

    private /* synthetic */ SubscriptionUtil$$ExternalSyntheticLambda7() {
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        SubscriptionAnnotation defaultSubscriptionSelection;
        defaultSubscriptionSelection = SubscriptionUtil.getDefaultSubscriptionSelection((List) obj);
        return defaultSubscriptionSelection;
    }
}
