package com.android.settings.deviceinfo.storage;

import android.os.storage.VolumeInfo;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class StorageUtils$$ExternalSyntheticLambda5 implements Predicate {
    public static final /* synthetic */ StorageUtils$$ExternalSyntheticLambda5 INSTANCE = new StorageUtils$$ExternalSyntheticLambda5();

    private /* synthetic */ StorageUtils$$ExternalSyntheticLambda5() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean isStorageSettingsInterestedVolume;
        isStorageSettingsInterestedVolume = StorageUtils.isStorageSettingsInterestedVolume((VolumeInfo) obj);
        return isStorageSettingsInterestedVolume;
    }
}
