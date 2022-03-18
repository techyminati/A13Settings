package com.android.settings.fuelgauge.batterytip;

import com.android.settings.fuelgauge.batterytip.tips.BatteryTip;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class BatteryTipPreferenceController$$ExternalSyntheticLambda0 implements Predicate {
    public static final /* synthetic */ BatteryTipPreferenceController$$ExternalSyntheticLambda0 INSTANCE = new BatteryTipPreferenceController$$ExternalSyntheticLambda0();

    private /* synthetic */ BatteryTipPreferenceController$$ExternalSyntheticLambda0() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        return ((BatteryTip) obj).isVisible();
    }
}
