package com.android.settingslib.media;

import android.content.Context;
import android.media.MediaRoute2Info;
import android.media.MediaRouter2Manager;
import com.android.settingslib.R$string;
/* loaded from: classes.dex */
public class PhoneMediaDevice extends MediaDevice {
    private String mSummary = "";
    private final DeviceIconUtil mDeviceIconUtil = new DeviceIconUtil();

    @Override // com.android.settingslib.media.MediaDevice
    public boolean isConnected() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PhoneMediaDevice(Context context, MediaRouter2Manager mediaRouter2Manager, MediaRoute2Info mediaRoute2Info, String str) {
        super(context, mediaRouter2Manager, mediaRoute2Info, str);
        initDeviceRecord();
    }

    @Override // com.android.settingslib.media.MediaDevice
    public String getName() {
        CharSequence charSequence;
        int type = this.mRouteInfo.getType();
        if (!(type == 3 || type == 4)) {
            if (type != 9) {
                if (type != 22) {
                    switch (type) {
                        case 11:
                        case 12:
                            break;
                        case 13:
                            break;
                        default:
                            charSequence = this.mContext.getString(R$string.media_transfer_this_device_name);
                            break;
                    }
                    return charSequence.toString();
                }
            }
            charSequence = this.mRouteInfo.getName();
            return charSequence.toString();
        }
        charSequence = this.mContext.getString(R$string.media_transfer_wired_usb_device_name);
        return charSequence.toString();
    }

    int getDrawableResId() {
        return this.mDeviceIconUtil.getIconResIdFromMediaRouteType(this.mRouteInfo.getType());
    }

    @Override // com.android.settingslib.media.MediaDevice
    public String getId() {
        int type = this.mRouteInfo.getType();
        if (type == 3 || type == 4) {
            return "wired_headset_media_device_id";
        }
        if (!(type == 9 || type == 22)) {
            switch (type) {
                case 11:
                case 12:
                case 13:
                    break;
                default:
                    return "phone_media_device_id";
            }
        }
        return "usb_headset_media_device_id";
    }
}
