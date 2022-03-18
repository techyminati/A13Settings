package com.google.android.systemui.columbus;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes2.dex */
public interface IColumbusServiceGestureListener extends IInterface {
    void onTrigger() throws RemoteException;

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IColumbusServiceGestureListener {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, "com.google.android.systemui.columbus.IColumbusServiceGestureListener");
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface("com.google.android.systemui.columbus.IColumbusServiceGestureListener");
            }
            if (i == 1598968902) {
                parcel2.writeString("com.google.android.systemui.columbus.IColumbusServiceGestureListener");
                return true;
            } else if (i != 1) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                onTrigger();
                return true;
            }
        }
    }
}
