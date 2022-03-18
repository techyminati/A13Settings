package com.android.settings.network.helper;

import java.util.Objects;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class SubscriptionGrouping$$ExternalSyntheticLambda3 implements Predicate {
    public static final /* synthetic */ SubscriptionGrouping$$ExternalSyntheticLambda3 INSTANCE = new SubscriptionGrouping$$ExternalSyntheticLambda3();

    private /* synthetic */ SubscriptionGrouping$$ExternalSyntheticLambda3() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        return Objects.nonNull((SubscriptionAnnotation) obj);
    }
}
