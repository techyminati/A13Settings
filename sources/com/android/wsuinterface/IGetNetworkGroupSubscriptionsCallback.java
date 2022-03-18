package com.android.wsuinterface;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;
/* loaded from: classes.dex */
public interface IGetNetworkGroupSubscriptionsCallback extends IInterface {
    void onAvailable(List<NetworkGroupSubscription> list) throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IGetNetworkGroupSubscriptionsCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, "com.android.wsuinterface.IGetNetworkGroupSubscriptionsCallback");
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface("com.android.wsuinterface.IGetNetworkGroupSubscriptionsCallback");
            }
            if (i == 1598968902) {
                parcel2.writeString("com.android.wsuinterface.IGetNetworkGroupSubscriptionsCallback");
                return true;
            } else if (i != 1) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                onAvailable(parcel.createTypedArrayList(NetworkGroupSubscription.CREATOR));
                return true;
            }
        }
    }
}
