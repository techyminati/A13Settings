package com.android.wsuinterface;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes.dex */
public interface IWsuService extends IInterface {
    void getNetworkGroupSubscriptions(IGetNetworkGroupSubscriptionsCallback iGetNetworkGroupSubscriptionsCallback) throws RemoteException;

    void registerSubscriptionProvisionStatusListener(ISubscriptionProvisionStatusListener iSubscriptionProvisionStatusListener) throws RemoteException;

    void unregisterSubscriptionProvisionStatusListener(ISubscriptionProvisionStatusListener iSubscriptionProvisionStatusListener) throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IWsuService {
        public static IWsuService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.android.wsuinterface.IWsuService");
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IWsuService)) {
                return new Proxy(iBinder);
            }
            return (IWsuService) queryLocalInterface;
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IWsuService {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.android.wsuinterface.IWsuService
            public void getNetworkGroupSubscriptions(IGetNetworkGroupSubscriptionsCallback iGetNetworkGroupSubscriptionsCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.wsuinterface.IWsuService");
                    obtain.writeStrongInterface(iGetNetworkGroupSubscriptionsCallback);
                    this.mRemote.transact(1, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            @Override // com.android.wsuinterface.IWsuService
            public void registerSubscriptionProvisionStatusListener(ISubscriptionProvisionStatusListener iSubscriptionProvisionStatusListener) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.wsuinterface.IWsuService");
                    obtain.writeStrongInterface(iSubscriptionProvisionStatusListener);
                    this.mRemote.transact(2, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            @Override // com.android.wsuinterface.IWsuService
            public void unregisterSubscriptionProvisionStatusListener(ISubscriptionProvisionStatusListener iSubscriptionProvisionStatusListener) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.wsuinterface.IWsuService");
                    obtain.writeStrongInterface(iSubscriptionProvisionStatusListener);
                    this.mRemote.transact(3, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }
        }
    }
}
