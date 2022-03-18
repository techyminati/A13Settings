package com.google.android.settings.fuelgauge.reversecharging;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import androidx.window.R;
import com.android.settings.overlay.FeatureFactory;
import com.google.android.systemui.reversecharging.ReverseWirelessCharger;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import vendor.google.wireless_charger.V1_2.RtxStatusInfo;
/* loaded from: classes2.dex */
public class ReverseChargingManager {
    private static final boolean DEBUG = Log.isLoggable("ReverseChargingManager", 3);
    static ReverseChargingManager sInstance;
    Context mContext;
    ReverseWirelessCharger mReverseWirelessCharger;
    Collection<ReverseChargingCallback> mReverseChargingCallbacks = new CopyOnWriteArrayList();
    RtxInformationCallback mRtxInformationCallback = new RtxInformationCallback();
    RtxStatusCallback mRtxStatusCallback = new RtxStatusCallback();
    boolean mIsRtxSupported = isRtxSupported();

    /* loaded from: classes2.dex */
    public interface ReverseChargingCallback {
        void onReverseChargingStateChanged();
    }

    public static ReverseChargingManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ReverseChargingManager(context);
        }
        return sInstance;
    }

    private ReverseChargingManager(Context context) {
        this.mContext = context;
        this.mReverseWirelessCharger = new ReverseWirelessCharger(context);
        this.mReverseWirelessCharger.addRtxInformationCallback(this.mRtxInformationCallback);
        this.mReverseWirelessCharger.addRtxStatusCallback(this.mRtxStatusCallback);
    }

    private boolean isRtxSupported() {
        if (this.mContext.getResources().getBoolean(R.bool.config_show_reverse_charging)) {
            return this.mReverseWirelessCharger.isRtxSupported();
        }
        return false;
    }

    public void registerCallback(ReverseChargingCallback reverseChargingCallback) {
        this.mReverseChargingCallbacks.add(reverseChargingCallback);
    }

    public void unregisterCallback(ReverseChargingCallback reverseChargingCallback) {
        this.mReverseChargingCallbacks.remove(reverseChargingCallback);
    }

    public boolean isReverseChargingOn() {
        if (!isOnWirelessCharge() && this.mIsRtxSupported) {
            return this.mReverseWirelessCharger.isRtxModeOn();
        }
        return false;
    }

    public void setReverseChargingState(boolean z) {
        this.mReverseWirelessCharger.setRtxMode(z);
        FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider().action(this.mContext, 1782, z);
    }

    public boolean isOnWirelessCharge() {
        Intent registerReceiver = this.mContext.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        return registerReceiver != null && registerReceiver.getIntExtra("plugged", -1) == 4;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isSupportedReverseCharging() {
        return this.mIsRtxSupported;
    }

    void dispatchReverseChargingStateChanged() {
        for (ReverseChargingCallback reverseChargingCallback : new CopyOnWriteArrayList(this.mReverseChargingCallbacks)) {
            reverseChargingCallback.onReverseChargingStateChanged();
        }
    }

    /* loaded from: classes2.dex */
    class RtxInformationCallback implements ReverseWirelessCharger.RtxInformationCallback {
        RtxInformationCallback() {
        }
    }

    /* loaded from: classes2.dex */
    class RtxStatusCallback implements ReverseWirelessCharger.RtxStatusCallback {
        RtxStatusCallback() {
        }

        @Override // com.google.android.systemui.reversecharging.ReverseWirelessCharger.RtxStatusCallback
        public void onRtxStatusChanged(RtxStatusInfo rtxStatusInfo) {
            if (ReverseChargingManager.DEBUG) {
                Log.d("ReverseChargingManager", "rtxStatusInfoChanged() rtxStatusInfo : " + rtxStatusInfo.toString());
            }
            ReverseChargingManager.this.dispatchReverseChargingStateChanged();
        }
    }
}
