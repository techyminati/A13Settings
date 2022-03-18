package com.android.settings.deviceinfo;

import com.android.settings.deviceinfo.storage.SecondaryUserController;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.function.Function;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class StorageCategoryFragment$$ExternalSyntheticLambda0 implements Function {
    public static final /* synthetic */ StorageCategoryFragment$$ExternalSyntheticLambda0 INSTANCE = new StorageCategoryFragment$$ExternalSyntheticLambda0();

    private /* synthetic */ StorageCategoryFragment$$ExternalSyntheticLambda0() {
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        SecondaryUserController lambda$setSecondaryUsersVisible$1;
        lambda$setSecondaryUsersVisible$1 = StorageCategoryFragment.lambda$setSecondaryUsersVisible$1((AbstractPreferenceController) obj);
        return lambda$setSecondaryUsersVisible$1;
    }
}
