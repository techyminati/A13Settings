package com.android.settings.fuelgauge;

import java.util.function.Consumer;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class BatteryChartPreferenceController$LoadAllItemsInfoTask$$ExternalSyntheticLambda1 implements Consumer {
    public static final /* synthetic */ BatteryChartPreferenceController$LoadAllItemsInfoTask$$ExternalSyntheticLambda1 INSTANCE = new BatteryChartPreferenceController$LoadAllItemsInfoTask$$ExternalSyntheticLambda1();

    private /* synthetic */ BatteryChartPreferenceController$LoadAllItemsInfoTask$$ExternalSyntheticLambda1() {
    }

    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        ((BatteryDiffEntry) obj).loadLabelAndIcon();
    }
}
