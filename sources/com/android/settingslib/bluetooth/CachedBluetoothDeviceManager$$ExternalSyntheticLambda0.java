package com.android.settingslib.bluetooth;

import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class CachedBluetoothDeviceManager$$ExternalSyntheticLambda0 implements Predicate {
    public static final /* synthetic */ CachedBluetoothDeviceManager$$ExternalSyntheticLambda0 INSTANCE = new CachedBluetoothDeviceManager$$ExternalSyntheticLambda0();

    private /* synthetic */ CachedBluetoothDeviceManager$$ExternalSyntheticLambda0() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean lambda$clearNonBondedDevices$0;
        lambda$clearNonBondedDevices$0 = CachedBluetoothDeviceManager.lambda$clearNonBondedDevices$0((CachedBluetoothDevice) obj);
        return lambda$clearNonBondedDevices$0;
    }
}
