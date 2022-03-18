package com.google.android.settings.connecteddevice.dock;

import android.content.ContentProviderClient;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import androidx.window.R;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.connecteddevice.DevicePreferenceCallback;
import com.android.settings.connecteddevice.dock.DockUpdater;
import com.android.settings.widget.GearPreference;
import com.google.android.settings.connecteddevice.dock.DockAsyncQueryHandler;
import com.google.common.base.Preconditions;
import java.util.List;
/* loaded from: classes2.dex */
public class ConnectedDockUpdater implements DockUpdater, DockAsyncQueryHandler.OnQueryListener {
    private final DockAsyncQueryHandler mAsyncQueryHandler;
    private final Context mContext;
    private final DevicePreferenceCallback mDevicePreferenceCallback;
    @VisibleForTesting
    boolean mIsObserverRegistered;
    @VisibleForTesting
    GearPreference mDockPreference = null;
    private Context mPreferenceContext = null;
    private String mDockId = null;
    private String mDockName = null;
    private final Uri mDockProviderUri = DockContract.DOCK_PROVIDER_CONNECTED_URI;
    private final ConnectedDockObserver mConnectedDockObserver = new ConnectedDockObserver(new Handler(Looper.getMainLooper()));

    public ConnectedDockUpdater(Context context, DevicePreferenceCallback devicePreferenceCallback) {
        this.mContext = context;
        this.mDevicePreferenceCallback = devicePreferenceCallback;
        DockAsyncQueryHandler dockAsyncQueryHandler = new DockAsyncQueryHandler(context.getContentResolver());
        this.mAsyncQueryHandler = dockAsyncQueryHandler;
        dockAsyncQueryHandler.setOnQueryListener(this);
    }

    @Override // com.android.settings.connecteddevice.dock.DockUpdater
    public void registerCallback() {
        ContentProviderClient acquireContentProviderClient = this.mContext.getContentResolver().acquireContentProviderClient(this.mDockProviderUri);
        if (acquireContentProviderClient != null) {
            acquireContentProviderClient.release();
            this.mContext.getContentResolver().registerContentObserver(this.mDockProviderUri, false, this.mConnectedDockObserver);
            this.mIsObserverRegistered = true;
            forceUpdate();
        }
    }

    @Override // com.android.settings.connecteddevice.dock.DockUpdater
    public void unregisterCallback() {
        if (this.mIsObserverRegistered) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mConnectedDockObserver);
            this.mIsObserverRegistered = false;
        }
    }

    @Override // com.android.settings.connecteddevice.dock.DockUpdater
    public void forceUpdate() {
        this.mAsyncQueryHandler.startQuery(1, this.mContext, this.mDockProviderUri, DockContract.DOCK_PROJECTION, null, null, null);
    }

    @Override // com.android.settings.connecteddevice.dock.DockUpdater
    public void setPreferenceContext(Context context) {
        this.mPreferenceContext = context;
    }

    @Override // com.google.android.settings.connecteddevice.dock.DockAsyncQueryHandler.OnQueryListener
    public void onQueryComplete(int i, List<DockDevice> list) {
        if (list == null || list.isEmpty()) {
            GearPreference gearPreference = this.mDockPreference;
            if (gearPreference != null && gearPreference.isVisible()) {
                this.mDockPreference.setVisible(false);
                this.mDevicePreferenceCallback.onDeviceRemoved(this.mDockPreference);
                return;
            }
            return;
        }
        DockDevice dockDevice = list.get(0);
        this.mDockId = dockDevice.getId();
        this.mDockName = dockDevice.getName();
        updatePreference();
    }

    private void updatePreference() {
        if (this.mDockPreference == null) {
            initPreference();
        }
        if (!TextUtils.isEmpty(this.mDockName)) {
            this.mDockPreference.setIcon(DockUtils.buildRainbowIcon(this.mPreferenceContext, this.mDockId));
            this.mDockPreference.setTitle(this.mDockName);
            if (!TextUtils.isEmpty(this.mDockId)) {
                this.mDockPreference.setIntent(DockContract.buildDockSettingIntent(this.mDockId));
                this.mDockPreference.setSelectable(true);
            }
            if (!this.mDockPreference.isVisible()) {
                this.mDockPreference.setVisible(true);
                this.mDevicePreferenceCallback.onDeviceAdded(this.mDockPreference);
            }
        } else if (this.mDockPreference.isVisible()) {
            this.mDockPreference.setVisible(false);
            this.mDevicePreferenceCallback.onDeviceRemoved(this.mDockPreference);
        }
    }

    @VisibleForTesting
    void initPreference() {
        if (this.mDockPreference == null) {
            Preconditions.checkNotNull(this.mPreferenceContext, "Preference context cannot be null");
            GearPreference gearPreference = new GearPreference(this.mPreferenceContext, null);
            this.mDockPreference = gearPreference;
            gearPreference.setSummary(this.mContext.getString(R.string.dock_summary_charging_phone));
            this.mDockPreference.setSelectable(false);
            this.mDockPreference.setVisible(false);
        }
    }

    /* loaded from: classes2.dex */
    private class ConnectedDockObserver extends ContentObserver {
        ConnectedDockObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            super.onChange(z);
            ConnectedDockUpdater.this.forceUpdate();
        }
    }
}
