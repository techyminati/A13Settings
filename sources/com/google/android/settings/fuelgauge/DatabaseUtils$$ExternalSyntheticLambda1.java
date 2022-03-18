package com.google.android.settings.fuelgauge;

import com.android.settings.fuelgauge.BatteryEntry;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes2.dex */
public final /* synthetic */ class DatabaseUtils$$ExternalSyntheticLambda1 implements Predicate {
    public static final /* synthetic */ DatabaseUtils$$ExternalSyntheticLambda1 INSTANCE = new DatabaseUtils$$ExternalSyntheticLambda1();

    private /* synthetic */ DatabaseUtils$$ExternalSyntheticLambda1() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean lambda$sendBatteryEntryData$0;
        lambda$sendBatteryEntryData$0 = DatabaseUtils.lambda$sendBatteryEntryData$0((BatteryEntry) obj);
        return lambda$sendBatteryEntryData$0;
    }
}
