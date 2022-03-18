package com.android.settings.network;

import com.android.settings.network.ProxySubscriptionManager;
import java.util.function.Consumer;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class ProxySubscriptionManager$$ExternalSyntheticLambda2 implements Consumer {
    public static final /* synthetic */ ProxySubscriptionManager$$ExternalSyntheticLambda2 INSTANCE = new ProxySubscriptionManager$$ExternalSyntheticLambda2();

    private /* synthetic */ ProxySubscriptionManager$$ExternalSyntheticLambda2() {
    }

    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        ((ProxySubscriptionManager.OnActiveSubscriptionChangedListener) obj).onChanged();
    }
}
