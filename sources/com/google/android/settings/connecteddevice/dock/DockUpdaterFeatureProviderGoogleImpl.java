package com.google.android.settings.connecteddevice.dock;

import android.content.Context;
import com.android.settings.connecteddevice.DevicePreferenceCallback;
import com.android.settings.connecteddevice.dock.DockUpdater;
import com.android.settings.overlay.DockUpdaterFeatureProvider;
/* loaded from: classes2.dex */
public class DockUpdaterFeatureProviderGoogleImpl implements DockUpdaterFeatureProvider {
    @Override // com.android.settings.overlay.DockUpdaterFeatureProvider
    public DockUpdater getConnectedDockUpdater(Context context, DevicePreferenceCallback devicePreferenceCallback) {
        return new ConnectedDockUpdater(context, devicePreferenceCallback);
    }

    @Override // com.android.settings.overlay.DockUpdaterFeatureProvider
    public DockUpdater getSavedDockUpdater(Context context, DevicePreferenceCallback devicePreferenceCallback) {
        return new SavedDockUpdater(context, devicePreferenceCallback);
    }
}
