package com.android.settings.network;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothPan;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.TetheringManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.Looper;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.datausage.DataSaverBackend;
import com.android.settings.widget.SwitchWidgetController;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes.dex */
public class TetherEnabler implements SwitchWidgetController.OnSwitchChangeListener, DataSaverBackend.Listener, LifecycleObserver {
    private static final boolean DEBUG = Log.isLoggable("TetherEnabler", 3);
    private boolean mBluetoothEnableForTether;
    private final AtomicReference<BluetoothPan> mBluetoothPan;
    @VisibleForTesting
    boolean mBluetoothTetheringStoppedByUser;
    private final ConnectivityManager mConnectivityManager;
    private final Context mContext;
    private final DataSaverBackend mDataSaverBackend;
    private boolean mDataSaverEnabled;
    private final String mEthernetRegex;
    @VisibleForTesting
    ConnectivityManager.OnStartTetheringCallback mOnStartTetheringCallback;
    private final SwitchWidgetController mSwitchWidgetController;
    @VisibleForTesting
    TetheringManager.TetheringEventCallback mTetheringEventCallback;
    private final TetheringManager mTetheringManager;
    private final UserManager mUserManager;
    private final WifiManager mWifiManager;
    private final BroadcastReceiver mTetherChangeReceiver = new BroadcastReceiver() { // from class: com.android.settings.network.TetherEnabler.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            boolean z;
            String action = intent.getAction();
            if (TextUtils.equals("android.net.wifi.WIFI_AP_STATE_CHANGED", action)) {
                z = TetherEnabler.this.handleWifiApStateChanged(intent.getIntExtra("wifi_state", 14));
            } else {
                z = TextUtils.equals("android.bluetooth.adapter.action.STATE_CHANGED", action) ? TetherEnabler.this.handleBluetoothStateChanged(intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE)) : false;
            }
            if (z) {
                TetherEnabler.this.updateState(null);
            }
        }
    };
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    @VisibleForTesting
    final List<OnTetherStateUpdateListener> mListeners = new ArrayList();
    private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());

    /* loaded from: classes.dex */
    public interface OnTetherStateUpdateListener {
        void onTetherStateUpdated(int i);
    }

    public static boolean isTethering(int i, int i2) {
        return (i & (1 << i2)) != 0;
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onAllowlistStatusChanged(int i, boolean z) {
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDenylistStatusChanged(int i, boolean z) {
    }

    public TetherEnabler(Context context, SwitchWidgetController switchWidgetController, AtomicReference<BluetoothPan> atomicReference) {
        this.mContext = context;
        this.mSwitchWidgetController = switchWidgetController;
        DataSaverBackend dataSaverBackend = new DataSaverBackend(context);
        this.mDataSaverBackend = dataSaverBackend;
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        this.mTetheringManager = (TetheringManager) context.getSystemService("tethering");
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mBluetoothPan = atomicReference;
        this.mEthernetRegex = context.getString(17039958);
        this.mDataSaverEnabled = dataSaverBackend.isDataSaverEnabled();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        this.mDataSaverBackend.addListener(this);
        this.mSwitchWidgetController.setListener(this);
        this.mSwitchWidgetController.startListening();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.TETHER_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
        intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        this.mContext.registerReceiver(this.mTetherChangeReceiver, intentFilter);
        this.mTetheringEventCallback = new TetheringManager.TetheringEventCallback() { // from class: com.android.settings.network.TetherEnabler.1
            public void onTetheredInterfacesChanged(List<String> list) {
                TetherEnabler.this.updateState((String[]) list.toArray(new String[list.size()]));
            }
        };
        this.mTetheringManager.registerTetheringEventCallback(new HandlerExecutor(this.mMainThreadHandler), this.mTetheringEventCallback);
        this.mOnStartTetheringCallback = new OnStartTetheringCallback(this);
        updateState(null);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        this.mBluetoothTetheringStoppedByUser = false;
        this.mDataSaverBackend.remListener(this);
        this.mSwitchWidgetController.stopListening();
        this.mContext.unregisterReceiver(this.mTetherChangeReceiver);
        this.mTetheringManager.unregisterTetheringEventCallback(this.mTetheringEventCallback);
        this.mTetheringEventCallback = null;
    }

    public void addListener(OnTetherStateUpdateListener onTetherStateUpdateListener) {
        if (onTetherStateUpdateListener != null && !this.mListeners.contains(onTetherStateUpdateListener)) {
            onTetherStateUpdateListener.onTetherStateUpdated(getTetheringState(null));
            this.mListeners.add(onTetherStateUpdateListener);
        }
    }

    public void removeListener(OnTetherStateUpdateListener onTetherStateUpdateListener) {
        if (onTetherStateUpdateListener != null) {
            this.mListeners.remove(onTetherStateUpdateListener);
        }
    }

    private void setSwitchEnabled(boolean z) {
        this.mSwitchWidgetController.setEnabled(z && !this.mDataSaverEnabled && this.mUserManager.isAdminUser());
    }

    @VisibleForTesting
    void updateState(String[] strArr) {
        int tetheringState = getTetheringState(strArr);
        if (DEBUG) {
            Log.d("TetherEnabler", "updateState: " + tetheringState);
        }
        setSwitchCheckedInternal(tetheringState != 0);
        setSwitchEnabled(true);
        int size = this.mListeners.size();
        for (int i = 0; i < size; i++) {
            this.mListeners.get(i).onTetherStateUpdated(tetheringState);
        }
    }

    private void setSwitchCheckedInternal(boolean z) {
        try {
            this.mSwitchWidgetController.stopListening();
            this.mSwitchWidgetController.setChecked(z);
            this.mSwitchWidgetController.startListening();
        } catch (IllegalStateException unused) {
            Log.e("TetherEnabler", "failed to stop switch widget listener when set check internally");
        }
    }

    @VisibleForTesting
    int getTetheringState(String[] strArr) {
        if (strArr == null) {
            strArr = this.mTetheringManager.getTetheredIfaces();
        }
        boolean isWifiApEnabled = this.mWifiManager.isWifiApEnabled();
        if (!this.mBluetoothTetheringStoppedByUser) {
            BluetoothPan bluetoothPan = this.mBluetoothPan.get();
            BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
            if (bluetoothAdapter != null && bluetoothAdapter.getState() == 12 && bluetoothPan != null && bluetoothPan.isTetheringOn()) {
                isWifiApEnabled = (isWifiApEnabled ? 1 : 0) | true;
            }
        }
        String[] tetherableUsbRegexs = this.mTetheringManager.getTetherableUsbRegexs();
        for (String str : strArr) {
            for (String str2 : tetherableUsbRegexs) {
                if (str.matches(str2)) {
                    isWifiApEnabled |= true;
                }
            }
            if (str.matches(this.mEthernetRegex)) {
                boolean z = isWifiApEnabled ? 1 : 0;
                char c = isWifiApEnabled ? 1 : 0;
                char c2 = isWifiApEnabled ? 1 : 0;
                char c3 = isWifiApEnabled ? 1 : 0;
                char c4 = isWifiApEnabled ? 1 : 0;
                char c5 = isWifiApEnabled ? 1 : 0;
                isWifiApEnabled = z | true;
            }
        }
        int i = isWifiApEnabled ? 1 : 0;
        int i2 = isWifiApEnabled ? 1 : 0;
        int i3 = isWifiApEnabled ? 1 : 0;
        int i4 = isWifiApEnabled ? 1 : 0;
        int i5 = isWifiApEnabled ? 1 : 0;
        int i6 = isWifiApEnabled ? 1 : 0;
        int i7 = isWifiApEnabled ? 1 : 0;
        return i;
    }

    @Override // com.android.settings.widget.SwitchWidgetController.OnSwitchChangeListener
    public boolean onSwitchToggled(boolean z) {
        if (z) {
            startTethering(0);
        } else {
            stopTethering(1);
            stopTethering(0);
            stopTethering(2);
            stopTethering(5);
        }
        return true;
    }

    public void stopTethering(int i) {
        if (isTethering(getTetheringState(null), i)) {
            setSwitchEnabled(false);
            this.mConnectivityManager.stopTethering(i);
            if (i == 2) {
                this.mBluetoothTetheringStoppedByUser = true;
                updateState(null);
            }
        }
    }

    public void startTethering(int i) {
        BluetoothAdapter bluetoothAdapter;
        if (i == 2) {
            this.mBluetoothTetheringStoppedByUser = false;
        }
        if (!isTethering(getTetheringState(null), i)) {
            if (i == 2 && (bluetoothAdapter = this.mBluetoothAdapter) != null && bluetoothAdapter.getState() == 10) {
                if (DEBUG) {
                    Log.d("TetherEnabler", "Turn on bluetooth first.");
                }
                this.mBluetoothEnableForTether = true;
                this.mBluetoothAdapter.enable();
                return;
            }
            setSwitchEnabled(false);
            this.mConnectivityManager.startTethering(i, true, this.mOnStartTetheringCallback, this.mMainThreadHandler);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean handleBluetoothStateChanged(int i) {
        if (!(i == Integer.MIN_VALUE || i == 10)) {
            if (i != 12) {
                return false;
            }
            if (this.mBluetoothEnableForTether) {
                startTethering(2);
            }
        }
        this.mBluetoothEnableForTether = false;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean handleWifiApStateChanged(int i) {
        if (i == 11 || i == 13) {
            return true;
        }
        if (i != 14) {
            return false;
        }
        Log.e("TetherEnabler", "Wifi AP is failed!");
        return true;
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDataSaverChanged(boolean z) {
        this.mDataSaverEnabled = z;
        setSwitchEnabled(true);
    }

    /* loaded from: classes.dex */
    private static final class OnStartTetheringCallback extends ConnectivityManager.OnStartTetheringCallback {
        final WeakReference<TetherEnabler> mTetherEnabler;

        OnStartTetheringCallback(TetherEnabler tetherEnabler) {
            this.mTetherEnabler = new WeakReference<>(tetherEnabler);
        }

        public void onTetheringStarted() {
            update();
        }

        public void onTetheringFailed() {
            update();
        }

        private void update() {
            TetherEnabler tetherEnabler = this.mTetherEnabler.get();
            if (tetherEnabler != null) {
                tetherEnabler.updateState(null);
            }
        }
    }
}
