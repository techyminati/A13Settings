package com.android.settings.deviceinfo.storage;

import com.android.settings.deviceinfo.StorageItemPreference;
import java.util.function.ToLongFunction;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class StorageItemPreferenceController$$ExternalSyntheticLambda0 implements ToLongFunction {
    public static final /* synthetic */ StorageItemPreferenceController$$ExternalSyntheticLambda0 INSTANCE = new StorageItemPreferenceController$$ExternalSyntheticLambda0();

    private /* synthetic */ StorageItemPreferenceController$$ExternalSyntheticLambda0() {
    }

    @Override // java.util.function.ToLongFunction
    public final long applyAsLong(Object obj) {
        return ((StorageItemPreference) obj).getStorageSize();
    }
}
