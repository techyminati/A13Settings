package com.android.settings.homepage.contextualcards;

import java.util.function.Function;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class ContextualCardManager$$ExternalSyntheticLambda1 implements Function {
    public static final /* synthetic */ ContextualCardManager$$ExternalSyntheticLambda1 INSTANCE = new ContextualCardManager$$ExternalSyntheticLambda1();

    private /* synthetic */ ContextualCardManager$$ExternalSyntheticLambda1() {
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        return Integer.valueOf(((ContextualCard) obj).getCardType());
    }
}
