package com.google.android.settings.gestures.assist;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Log;
import com.google.android.systemui.elmyra.IElmyraService;
import com.google.android.systemui.elmyra.IElmyraServiceGestureListener;
/* loaded from: classes2.dex */
public class AssistGestureHelper {
    private boolean mBoundToService;
    private Context mContext;
    private GestureListener mGestureListener;
    private PowerManager mPowerManager;
    private IElmyraService mService;
    private IBinder mToken = new Binder();
    private final ServiceConnection mServiceConnection = new ServiceConnection() { // from class: com.google.android.settings.gestures.assist.AssistGestureHelper.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            AssistGestureHelper.this.mService = IElmyraService.Stub.asInterface(iBinder);
            if (AssistGestureHelper.this.mGestureListener != null) {
                try {
                    AssistGestureHelper.this.mService.registerGestureListener(AssistGestureHelper.this.mToken, (IBinder) AssistGestureHelper.this.mElmyraServiceGestureListener);
                } catch (RemoteException e) {
                    Log.e("AssistGestureHelper", "registerGestureListener()", e);
                }
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            AssistGestureHelper.this.mService = null;
        }
    };
    private final IElmyraServiceGestureListener mElmyraServiceGestureListener = new IElmyraServiceGestureListener.Stub() { // from class: com.google.android.settings.gestures.assist.AssistGestureHelper.2
        private int mLastStage = 0;

        @Override // com.google.android.systemui.elmyra.IElmyraServiceGestureListener
        public void onGestureProgress(float f, int i) throws RemoteException {
            if (AssistGestureHelper.this.mGestureListener != null) {
                AssistGestureHelper.this.mGestureListener.onGestureProgress(f, i);
            }
            if (this.mLastStage != 2 && i == 2) {
                AssistGestureHelper.this.mPowerManager.userActivity(SystemClock.uptimeMillis(), 0, 0);
            }
            this.mLastStage = i;
        }

        @Override // com.google.android.systemui.elmyra.IElmyraServiceGestureListener
        public void onGestureDetected() throws RemoteException {
            if (AssistGestureHelper.this.mGestureListener != null) {
                AssistGestureHelper.this.mGestureListener.onGestureDetected();
            }
        }
    };

    /* loaded from: classes2.dex */
    public interface GestureListener {
        void onGestureDetected();

        void onGestureProgress(float f, int i);
    }

    public AssistGestureHelper(Context context) {
        this.mContext = context;
        this.mPowerManager = (PowerManager) context.getSystemService("power");
    }

    public void setListener(GestureListener gestureListener) {
        this.mGestureListener = gestureListener;
        IElmyraService iElmyraService = this.mService;
        if (iElmyraService == null) {
            Log.w("AssistGestureHelper", "Service is null, should try to reconnect");
            return;
        }
        try {
            if (gestureListener != null) {
                iElmyraService.registerGestureListener(this.mToken, (IBinder) this.mElmyraServiceGestureListener);
            } else {
                iElmyraService.registerGestureListener(this.mToken, null);
            }
        } catch (RemoteException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Failed to ");
            sb.append(gestureListener == null ? "unregister" : "register");
            sb.append(" listener");
            Log.e("AssistGestureHelper", sb.toString(), e);
        }
    }

    public void launchAssistant() {
        try {
            this.mService.triggerAction();
        } catch (RemoteException e) {
            Log.e("AssistGestureHelper", "Error invoking triggerAction()", e);
        }
    }

    public void unbindFromElmyraServiceProxy() {
        if (this.mBoundToService) {
            this.mContext.unbindService(this.mServiceConnection);
            this.mBoundToService = false;
        }
    }

    public void bindToElmyraServiceProxy() {
        if (this.mService == null) {
            try {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.android.systemui", "com.google.android.systemui.elmyra.ElmyraServiceProxy"));
                this.mContext.bindServiceAsUser(intent, this.mServiceConnection, 1, UserHandle.getUserHandleForUid(0));
                this.mBoundToService = true;
            } catch (SecurityException e) {
                Log.e("AssistGestureHelper", "Unable to bind to ElmyraService", e);
            }
        }
    }
}
