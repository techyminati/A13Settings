package com.google.android.settings.connecteddevice.dock;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.connecteddevice.DevicePreferenceCallback;
import com.android.settings.connecteddevice.dock.DockUpdater;
import com.android.settings.widget.GearPreference;
import com.google.android.settings.connecteddevice.dock.DockAsyncQueryHandler;
import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
/* loaded from: classes2.dex */
public class SavedDockUpdater implements DockUpdater, DockAsyncQueryHandler.OnQueryListener {
    private final DockAsyncQueryHandler mAsyncQueryHandler;
    private final DockObserver mConnectedDockObserver;
    private final Context mContext;
    private final DevicePreferenceCallback mDevicePreferenceCallback;
    @VisibleForTesting
    boolean mIsObserverRegistered;
    private final DockObserver mSavedDockObserver;
    private Context mPreferenceContext = null;
    private Map<String, String> mSavedDevices = null;
    private String mConnectedDockId = null;
    @VisibleForTesting
    final Map<String, GearPreference> mPreferenceMap = new ArrayMap();

    public SavedDockUpdater(Context context, DevicePreferenceCallback devicePreferenceCallback) {
        this.mContext = context;
        this.mDevicePreferenceCallback = devicePreferenceCallback;
        Handler handler = new Handler(Looper.getMainLooper());
        this.mConnectedDockObserver = new DockObserver(handler, 1, DockContract.DOCK_PROVIDER_CONNECTED_URI);
        this.mSavedDockObserver = new DockObserver(handler, 2, DockContract.DOCK_PROVIDER_SAVED_URI);
        if (isRunningOnMainThread()) {
            DockAsyncQueryHandler dockAsyncQueryHandler = new DockAsyncQueryHandler(context.getContentResolver());
            this.mAsyncQueryHandler = dockAsyncQueryHandler;
            dockAsyncQueryHandler.setOnQueryListener(this);
            return;
        }
        this.mAsyncQueryHandler = null;
    }

    @Override // com.android.settings.connecteddevice.dock.DockUpdater
    public void registerCallback() {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        Uri uri = DockContract.DOCK_PROVIDER_SAVED_URI;
        ContentProviderClient acquireContentProviderClient = contentResolver.acquireContentProviderClient(uri);
        if (acquireContentProviderClient != null) {
            acquireContentProviderClient.release();
            this.mContext.getContentResolver().registerContentObserver(DockContract.DOCK_PROVIDER_CONNECTED_URI, false, this.mConnectedDockObserver);
            this.mContext.getContentResolver().registerContentObserver(uri, false, this.mSavedDockObserver);
            this.mIsObserverRegistered = true;
            forceUpdate();
        }
    }

    @Override // com.android.settings.connecteddevice.dock.DockUpdater
    public void unregisterCallback() {
        if (this.mIsObserverRegistered) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mConnectedDockObserver);
            this.mContext.getContentResolver().unregisterContentObserver(this.mSavedDockObserver);
            this.mIsObserverRegistered = false;
        }
    }

    @Override // com.android.settings.connecteddevice.dock.DockUpdater
    public void forceUpdate() {
        startQuery(1, DockContract.DOCK_PROVIDER_CONNECTED_URI);
        startQuery(2, DockContract.DOCK_PROVIDER_SAVED_URI);
    }

    @Override // com.android.settings.connecteddevice.dock.DockUpdater
    public void setPreferenceContext(Context context) {
        this.mPreferenceContext = context;
    }

    @Override // com.google.android.settings.connecteddevice.dock.DockAsyncQueryHandler.OnQueryListener
    public void onQueryComplete(int i, List<DockDevice> list) {
        if (list == null) {
            return;
        }
        if (i == 2) {
            updateSavedDevicesList(list);
        } else if (i == 1) {
            updateConnectedDevice(list);
        }
    }

    private GearPreference initPreference(String str, String str2) {
        Preconditions.checkNotNull(this.mPreferenceContext, "Preference context cannot be null");
        GearPreference gearPreference = new GearPreference(this.mPreferenceContext, null);
        gearPreference.setIcon(DockUtils.buildRainbowIcon(this.mPreferenceContext, str));
        gearPreference.setTitle(str2);
        if (!TextUtils.isEmpty(str)) {
            gearPreference.setIntent(DockContract.buildDockSettingIntent(str));
            gearPreference.setSelectable(true);
        }
        return gearPreference;
    }

    private boolean isRunningOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startQuery(int i, Uri uri) {
        if (isRunningOnMainThread()) {
            this.mAsyncQueryHandler.startQuery(i, this.mContext, uri, DockContract.DOCK_PROJECTION, null, null, null);
            return;
        }
        try {
            Cursor query = this.mContext.getApplicationContext().getContentResolver().query(uri, DockContract.DOCK_PROJECTION, null, null, null);
            onQueryComplete(i, DockAsyncQueryHandler.parseCursorToDockDevice(query));
            if (query != null) {
                query.close();
            }
        } catch (Exception e) {
            Log.w("SavedDockUpdater", "Query dockProvider fail", e);
        }
    }

    private void updateConnectedDevice(List<DockDevice> list) {
        if (list.isEmpty()) {
            this.mConnectedDockId = null;
            updateDevices();
            return;
        }
        String id = list.get(0).getId();
        this.mConnectedDockId = id;
        if (this.mPreferenceMap.containsKey(id)) {
            this.mDevicePreferenceCallback.onDeviceRemoved(this.mPreferenceMap.get(this.mConnectedDockId));
            this.mPreferenceMap.remove(this.mConnectedDockId);
        }
    }

    private void updateSavedDevicesList(List<DockDevice> list) {
        if (this.mSavedDevices == null) {
            this.mSavedDevices = new ArrayMap();
        }
        this.mSavedDevices.clear();
        for (DockDevice dockDevice : list) {
            String name = dockDevice.getName();
            if (!TextUtils.isEmpty(name)) {
                this.mSavedDevices.put(dockDevice.getId(), name);
            }
        }
        updateDevices();
    }

    private void updateDevices() {
        Map<String, String> map = this.mSavedDevices;
        if (map != null) {
            for (String str : map.keySet()) {
                if (!TextUtils.equals(str, this.mConnectedDockId)) {
                    String str2 = this.mSavedDevices.get(str);
                    if (this.mPreferenceMap.containsKey(str)) {
                        this.mPreferenceMap.get(str).setTitle(str2);
                    } else {
                        this.mPreferenceMap.put(str, initPreference(str, str2));
                        this.mDevicePreferenceCallback.onDeviceAdded(this.mPreferenceMap.get(str));
                    }
                }
            }
            this.mPreferenceMap.keySet().removeIf(new Predicate() { // from class: com.google.android.settings.connecteddevice.dock.SavedDockUpdater$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean hasDeviceBeenRemoved;
                    hasDeviceBeenRemoved = SavedDockUpdater.this.hasDeviceBeenRemoved((String) obj);
                    return hasDeviceBeenRemoved;
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean hasDeviceBeenRemoved(String str) {
        if (this.mSavedDevices.containsKey(str)) {
            return false;
        }
        this.mDevicePreferenceCallback.onDeviceRemoved(this.mPreferenceMap.get(str));
        return true;
    }

    /* loaded from: classes2.dex */
    private class DockObserver extends ContentObserver {
        private final int mToken;
        private final Uri mUri;

        DockObserver(Handler handler, int i, Uri uri) {
            super(handler);
            this.mToken = i;
            this.mUri = uri;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            super.onChange(z);
            SavedDockUpdater.this.startQuery(this.mToken, this.mUri);
        }
    }
}
