package com.android.settings.deviceinfo.storage;

import android.os.storage.DiskInfo;
import java.util.function.Function;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class StorageUtils$$ExternalSyntheticLambda1 implements Function {
    public static final /* synthetic */ StorageUtils$$ExternalSyntheticLambda1 INSTANCE = new StorageUtils$$ExternalSyntheticLambda1();

    private /* synthetic */ StorageUtils$$ExternalSyntheticLambda1() {
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        StorageEntry lambda$getAllStorageEntries$3;
        lambda$getAllStorageEntries$3 = StorageUtils.lambda$getAllStorageEntries$3((DiskInfo) obj);
        return lambda$getAllStorageEntries$3;
    }
}
