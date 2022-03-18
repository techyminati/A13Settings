package com.android.wifitrackerlib;

import android.net.wifi.ScanResult;
import java.util.function.ToIntFunction;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class Utils$$ExternalSyntheticLambda1 implements ToIntFunction {
    public static final /* synthetic */ Utils$$ExternalSyntheticLambda1 INSTANCE = new Utils$$ExternalSyntheticLambda1();

    private /* synthetic */ Utils$$ExternalSyntheticLambda1() {
    }

    @Override // java.util.function.ToIntFunction
    public final int applyAsInt(Object obj) {
        int i;
        i = ((ScanResult) obj).level;
        return i;
    }
}
