package com.android.settings.deviceinfo.storage;

import android.os.storage.VolumeRecord;
import java.util.function.Function;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class StorageUtils$$ExternalSyntheticLambda2 implements Function {
    public static final /* synthetic */ StorageUtils$$ExternalSyntheticLambda2 INSTANCE = new StorageUtils$$ExternalSyntheticLambda2();

    private /* synthetic */ StorageUtils$$ExternalSyntheticLambda2() {
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        StorageEntry lambda$getAllStorageEntries$5;
        lambda$getAllStorageEntries$5 = StorageUtils.lambda$getAllStorageEntries$5((VolumeRecord) obj);
        return lambda$getAllStorageEntries$5;
    }
}
