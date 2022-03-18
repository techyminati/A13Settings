package com.android.settings.deviceinfo.storage;

import android.content.pm.UserInfo;
import java.util.Comparator;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class StorageAsyncLoader$$ExternalSyntheticLambda0 implements Comparator {
    public static final /* synthetic */ StorageAsyncLoader$$ExternalSyntheticLambda0 INSTANCE = new StorageAsyncLoader$$ExternalSyntheticLambda0();

    private /* synthetic */ StorageAsyncLoader$$ExternalSyntheticLambda0() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$getStorageResultsForUsers$0;
        lambda$getStorageResultsForUsers$0 = StorageAsyncLoader.lambda$getStorageResultsForUsers$0((UserInfo) obj, (UserInfo) obj2);
        return lambda$getStorageResultsForUsers$0;
    }
}
