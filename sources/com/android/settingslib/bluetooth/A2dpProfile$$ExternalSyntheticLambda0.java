package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothCodecConfig;
import java.util.Comparator;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class A2dpProfile$$ExternalSyntheticLambda0 implements Comparator {
    public static final /* synthetic */ A2dpProfile$$ExternalSyntheticLambda0 INSTANCE = new A2dpProfile$$ExternalSyntheticLambda0();

    private /* synthetic */ A2dpProfile$$ExternalSyntheticLambda0() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$getHighQualityAudioOptionLabel$0;
        lambda$getHighQualityAudioOptionLabel$0 = A2dpProfile.lambda$getHighQualityAudioOptionLabel$0((BluetoothCodecConfig) obj, (BluetoothCodecConfig) obj2);
        return lambda$getHighQualityAudioOptionLabel$0;
    }
}
