package com.android.wifitrackerlib;

import android.net.wifi.ScanResult;
import java.util.function.ToIntFunction;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class StandardWifiEntry$$ExternalSyntheticLambda6 implements ToIntFunction {
    public static final /* synthetic */ StandardWifiEntry$$ExternalSyntheticLambda6 INSTANCE = new StandardWifiEntry$$ExternalSyntheticLambda6();

    private /* synthetic */ StandardWifiEntry$$ExternalSyntheticLambda6() {
    }

    @Override // java.util.function.ToIntFunction
    public final int applyAsInt(Object obj) {
        int lambda$getScanResultDescription$4;
        lambda$getScanResultDescription$4 = StandardWifiEntry.lambda$getScanResultDescription$4((ScanResult) obj);
        return lambda$getScanResultDescription$4;
    }
}
