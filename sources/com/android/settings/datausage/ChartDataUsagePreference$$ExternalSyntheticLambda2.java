package com.android.settings.datausage;

import com.android.settings.datausage.ChartDataUsagePreference;
import java.util.function.ToIntFunction;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class ChartDataUsagePreference$$ExternalSyntheticLambda2 implements ToIntFunction {
    public static final /* synthetic */ ChartDataUsagePreference$$ExternalSyntheticLambda2 INSTANCE = new ChartDataUsagePreference$$ExternalSyntheticLambda2();

    private /* synthetic */ ChartDataUsagePreference$$ExternalSyntheticLambda2() {
    }

    @Override // java.util.function.ToIntFunction
    public final int applyAsInt(Object obj) {
        return ((ChartDataUsagePreference.DataUsageSummaryNode) obj).getDataUsagePercentage();
    }
}
