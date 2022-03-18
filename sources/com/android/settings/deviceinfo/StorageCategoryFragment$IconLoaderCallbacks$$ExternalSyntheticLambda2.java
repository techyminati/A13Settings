package com.android.settings.deviceinfo;

import com.android.settings.deviceinfo.StorageCategoryFragment;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class StorageCategoryFragment$IconLoaderCallbacks$$ExternalSyntheticLambda2 implements Predicate {
    public static final /* synthetic */ StorageCategoryFragment$IconLoaderCallbacks$$ExternalSyntheticLambda2 INSTANCE = new StorageCategoryFragment$IconLoaderCallbacks$$ExternalSyntheticLambda2();

    private /* synthetic */ StorageCategoryFragment$IconLoaderCallbacks$$ExternalSyntheticLambda2() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean lambda$onLoadFinished$1;
        lambda$onLoadFinished$1 = StorageCategoryFragment.IconLoaderCallbacks.lambda$onLoadFinished$1((AbstractPreferenceController) obj);
        return lambda$onLoadFinished$1;
    }
}
