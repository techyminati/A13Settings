package com.android.settings.homepage.contextualcards.slices;

import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class BluetoothDevicesSlice$$ExternalSyntheticLambda1 implements Predicate {
    public static final /* synthetic */ BluetoothDevicesSlice$$ExternalSyntheticLambda1 INSTANCE = new BluetoothDevicesSlice$$ExternalSyntheticLambda1();

    private /* synthetic */ BluetoothDevicesSlice$$ExternalSyntheticLambda1() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean lambda$getPairedBluetoothDevices$1;
        lambda$getPairedBluetoothDevices$1 = BluetoothDevicesSlice.lambda$getPairedBluetoothDevices$1((CachedBluetoothDevice) obj);
        return lambda$getPairedBluetoothDevices$1;
    }
}
