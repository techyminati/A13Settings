package com.android.settings.applications.appinfo;

import com.android.settings.applications.appinfo.AppBatteryPreferenceController;
import com.android.settings.fuelgauge.BatteryDiffEntry;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class AppBatteryPreferenceController$1$$ExternalSyntheticLambda2 implements Predicate {
    public static final /* synthetic */ AppBatteryPreferenceController$1$$ExternalSyntheticLambda2 INSTANCE = new AppBatteryPreferenceController$1$$ExternalSyntheticLambda2();

    private /* synthetic */ AppBatteryPreferenceController$1$$ExternalSyntheticLambda2() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean lambda$doInBackground$0;
        lambda$doInBackground$0 = AppBatteryPreferenceController.AnonymousClass1.lambda$doInBackground$0((BatteryDiffEntry) obj);
        return lambda$doInBackground$0;
    }
}
