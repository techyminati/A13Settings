package com.android.settings.wifi.details2;

import java.net.InetAddress;
import java.util.function.Function;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class WifiDetailPreferenceController2$$ExternalSyntheticLambda8 implements Function {
    public static final /* synthetic */ WifiDetailPreferenceController2$$ExternalSyntheticLambda8 INSTANCE = new WifiDetailPreferenceController2$$ExternalSyntheticLambda8();

    private /* synthetic */ WifiDetailPreferenceController2$$ExternalSyntheticLambda8() {
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        return ((InetAddress) obj).getHostAddress();
    }
}
