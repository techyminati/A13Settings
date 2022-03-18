package com.google.android.settings.gestures.columbus;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import com.google.android.systemui.columbus.IColumbusService;
import com.google.android.systemui.columbus.IColumbusServiceGestureListener;
/* loaded from: classes2.dex */
public class ColumbusGestureHelper {
    private boolean mBoundToService;
    private Context mContext;
    private GestureListener mGestureListener;
    private IColumbusService mService;
    private final IColumbusServiceGestureListener mColumbusServiceGestureListener = new IColumbusServiceGestureListener.Stub() { // from class: com.google.android.settings.gestures.columbus.ColumbusGestureHelper.1
        @Override // com.google.android.systemui.columbus.IColumbusServiceGestureListener
        public void onTrigger() throws RemoteException {
            if (ColumbusGestureHelper.this.mGestureListener != null) {
                ColumbusGestureHelper.this.mGestureListener.onTrigger();
            }
        }
    };
    private IBinder mToken = new Binder();
    private final ServiceConnection mServiceConnection = new ServiceConnection() { // from class: com.google.android.settings.gestures.columbus.ColumbusGestureHelper.2
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ColumbusGestureHelper.this.mService = IColumbusService.Stub.asInterface(iBinder);
            if (ColumbusGestureHelper.this.mGestureListener != null) {
                try {
                    ColumbusGestureHelper.this.mService.registerGestureListener(ColumbusGestureHelper.this.mToken, (IBinder) ColumbusGestureHelper.this.mColumbusServiceGestureListener);
                } catch (RemoteException e) {
                    Log.e("ColumbusGestureHelper", "registerGestureListener()", e);
                }
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            ColumbusGestureHelper.this.mService = null;
        }
    };

    /* loaded from: classes2.dex */
    public interface GestureListener {
        void onTrigger();
    }

    public ColumbusGestureHelper(Context context) {
        this.mContext = context;
    }

    public void setListener(GestureListener gestureListener) {
        this.mGestureListener = gestureListener;
        IColumbusService iColumbusService = this.mService;
        if (iColumbusService == null) {
            Log.w("ColumbusGestureHelper", "Service is null, should try to reconnect");
            return;
        }
        try {
            if (gestureListener != null) {
                iColumbusService.registerGestureListener(this.mToken, (IBinder) this.mColumbusServiceGestureListener);
            } else {
                iColumbusService.registerGestureListener(this.mToken, null);
            }
        } catch (RemoteException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Failed to ");
            sb.append(gestureListener == null ? "unregister" : "register");
            sb.append(" listener");
            Log.e("ColumbusGestureHelper", sb.toString(), e);
        }
    }

    public void unbindFromColumbusServiceProxy() {
        if (this.mBoundToService) {
            this.mContext.unbindService(this.mServiceConnection);
            this.mBoundToService = false;
        }
    }

    public void bindToColumbusServiceProxy() {
        if (this.mService == null) {
            try {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.android.systemui", "com.google.android.systemui.columbus.ColumbusServiceProxy"));
                this.mContext.bindServiceAsUser(intent, this.mServiceConnection, 1, UserHandle.getUserHandleForUid(0));
                this.mBoundToService = true;
            } catch (SecurityException e) {
                Log.e("ColumbusGestureHelper", "Unable to bind to ColumbusService", e);
            }
        }
    }
}
