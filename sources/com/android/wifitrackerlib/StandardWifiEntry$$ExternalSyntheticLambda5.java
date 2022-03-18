package com.android.wifitrackerlib;

import android.net.wifi.ScanResult;
import java.util.function.ToIntFunction;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class StandardWifiEntry$$ExternalSyntheticLambda5 implements ToIntFunction {
    public static final /* synthetic */ StandardWifiEntry$$ExternalSyntheticLambda5 INSTANCE = new StandardWifiEntry$$ExternalSyntheticLambda5();

    private /* synthetic */ StandardWifiEntry$$ExternalSyntheticLambda5() {
    }

    @Override // java.util.function.ToIntFunction
    public final int applyAsInt(Object obj) {
        int i;
        i = ((ScanResult) obj).level;
        return i;
    }
}
