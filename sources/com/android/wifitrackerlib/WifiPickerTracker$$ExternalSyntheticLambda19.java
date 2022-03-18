package com.android.wifitrackerlib;

import android.net.wifi.WifiConfiguration;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class WifiPickerTracker$$ExternalSyntheticLambda19 implements Predicate {
    public static final /* synthetic */ WifiPickerTracker$$ExternalSyntheticLambda19 INSTANCE = new WifiPickerTracker$$ExternalSyntheticLambda19();

    private /* synthetic */ WifiPickerTracker$$ExternalSyntheticLambda19() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean lambda$updateWifiConfigurations$19;
        lambda$updateWifiConfigurations$19 = WifiPickerTracker.lambda$updateWifiConfigurations$19((WifiConfiguration) obj);
        return lambda$updateWifiConfigurations$19;
    }
}
