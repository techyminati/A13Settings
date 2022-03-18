package com.android.wifitrackerlib;

import com.android.wifitrackerlib.StandardWifiEntry;
import java.util.function.Function;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class WifiPickerTracker$$ExternalSyntheticLambda10 implements Function {
    public static final /* synthetic */ WifiPickerTracker$$ExternalSyntheticLambda10 INSTANCE = new WifiPickerTracker$$ExternalSyntheticLambda10();

    private /* synthetic */ WifiPickerTracker$$ExternalSyntheticLambda10() {
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        StandardWifiEntry.ScanResultKey lambda$updateWifiEntries$4;
        lambda$updateWifiEntries$4 = WifiPickerTracker.lambda$updateWifiEntries$4((StandardWifiEntry) obj);
        return lambda$updateWifiEntries$4;
    }
}
