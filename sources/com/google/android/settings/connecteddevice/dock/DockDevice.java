package com.google.android.settings.connecteddevice.dock;
/* loaded from: classes2.dex */
public class DockDevice {
    private String mId;
    private String mName;

    private DockDevice() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DockDevice(String str, String str2) {
        this.mId = str;
        this.mName = str2;
    }

    public String getName() {
        return this.mName;
    }

    public String getId() {
        return this.mId;
    }
}
