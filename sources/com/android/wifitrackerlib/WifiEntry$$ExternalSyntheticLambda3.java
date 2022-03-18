package com.android.wifitrackerlib;

import com.android.wifitrackerlib.WifiEntry;
import java.util.function.Consumer;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class WifiEntry$$ExternalSyntheticLambda3 implements Consumer {
    public static final /* synthetic */ WifiEntry$$ExternalSyntheticLambda3 INSTANCE = new WifiEntry$$ExternalSyntheticLambda3();

    private /* synthetic */ WifiEntry$$ExternalSyntheticLambda3() {
    }

    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        ((WifiEntry.ManageSubscriptionAction) obj).onExecute();
    }
}
