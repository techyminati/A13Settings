package com.android.settings.network;

import android.app.FragmentManager;
import android.app.PendingIntent;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.UiccCardInfo;
import android.util.Log;
import com.android.settings.SidecarFragment;
import com.android.settings.network.telephony.EuiccOperationSidecar;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class SwitchToEuiccSubscriptionSidecar extends EuiccOperationSidecar {
    private PendingIntent mCallbackIntent;
    private boolean mIsDuringSimSlotMapping;
    private int mPort;
    private SubscriptionInfo mRemovedSubInfo;
    private int mSubId;

    @Override // com.android.settings.network.telephony.EuiccOperationSidecar
    public String getReceiverAction() {
        return "com.android.settings.network.SWITCH_TO_SUBSCRIPTION";
    }

    public static SwitchToEuiccSubscriptionSidecar get(FragmentManager fragmentManager) {
        return (SwitchToEuiccSubscriptionSidecar) SidecarFragment.get(fragmentManager, "SwitchToEuiccSidecar", SwitchToEuiccSubscriptionSidecar.class, null);
    }

    @Override // com.android.settings.network.telephony.EuiccOperationSidecar, com.android.settings.SidecarFragment.Listener
    public void onStateChange(SidecarFragment sidecarFragment) {
        if (sidecarFragment == this.mSwitchSlotSidecar) {
            onSwitchSlotSidecarStateChange();
        } else {
            Log.wtf("SwitchToEuiccSidecar", "Received state change from a sidecar not expected.");
        }
    }

    public void run(int i, int i2, SubscriptionInfo subscriptionInfo) {
        setState(1, 0);
        this.mCallbackIntent = createCallbackIntent();
        this.mSubId = i;
        int targetSlot = getTargetSlot();
        if (targetSlot < 0) {
            Log.d("SwitchToEuiccSidecar", "There is no esim, the TargetSlot is " + targetSlot);
            setState(3, 0);
            return;
        }
        if (i2 < 0) {
            i2 = getTargetPortId(subscriptionInfo);
        }
        this.mPort = i2;
        this.mRemovedSubInfo = subscriptionInfo;
        Log.d("SwitchToEuiccSidecar", String.format("set esim into the SubId%d Slot%d:Port%d", Integer.valueOf(this.mSubId), Integer.valueOf(targetSlot), Integer.valueOf(this.mPort)));
        if (!this.mTelephonyManager.isMultiSimEnabled() || subscriptionInfo == null || !subscriptionInfo.isEmbedded()) {
            this.mSwitchSlotSidecar.runSwitchToEuiccSlot(targetSlot, this.mPort, subscriptionInfo);
            return;
        }
        this.mIsDuringSimSlotMapping = true;
        this.mEuiccManager.switchToSubscription(-1, this.mPort, this.mCallbackIntent);
    }

    private int getTargetPortId(SubscriptionInfo subscriptionInfo) {
        int i = 0;
        if (this.mTelephonyManager.isMultiSimEnabled() && isMultipleEnabledProfilesSupported()) {
            if (subscriptionInfo != null && subscriptionInfo.isEmbedded()) {
                return subscriptionInfo.getPortIndex();
            }
            for (SubscriptionInfo subscriptionInfo2 : (List) SubscriptionUtil.getActiveSubscriptions((SubscriptionManager) getContext().getSystemService(SubscriptionManager.class)).stream().filter(SwitchToEuiccSubscriptionSidecar$$ExternalSyntheticLambda0.INSTANCE).sorted(Comparator.comparingInt(SwitchToEuiccSubscriptionSidecar$$ExternalSyntheticLambda2.INSTANCE)).collect(Collectors.toList())) {
                if (subscriptionInfo2.getPortIndex() == i) {
                    i++;
                }
            }
        }
        return i;
    }

    private int getTargetSlot() {
        return UiccSlotUtil.getEsimSlotId(getContext());
    }

    private void onSwitchSlotSidecarStateChange() {
        int state = this.mSwitchSlotSidecar.getState();
        if (state == 2) {
            this.mSwitchSlotSidecar.reset();
            Log.i("SwitchToEuiccSidecar", "Successfully SimSlotMapping. Start to enable/disable esim");
            switchToSubscription();
        } else if (state == 3) {
            this.mSwitchSlotSidecar.reset();
            Log.i("SwitchToEuiccSidecar", "Failed to set SimSlotMapping");
            setState(3, 0);
        }
    }

    private boolean isMultipleEnabledProfilesSupported() {
        List<UiccCardInfo> uiccCardsInfo = this.mTelephonyManager.getUiccCardsInfo();
        if (uiccCardsInfo != null) {
            return uiccCardsInfo.stream().anyMatch(SwitchToEuiccSubscriptionSidecar$$ExternalSyntheticLambda1.INSTANCE);
        }
        Log.w("SwitchToEuiccSidecar", "UICC cards info list is empty.");
        return false;
    }

    private void switchToSubscription() {
        this.mEuiccManager.switchToSubscription(this.mSubId, this.mPort, this.mCallbackIntent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.network.telephony.EuiccOperationSidecar
    public void onActionReceived() {
        if (getResultCode() != 0 || !this.mIsDuringSimSlotMapping) {
            super.onActionReceived();
            return;
        }
        this.mIsDuringSimSlotMapping = false;
        this.mSwitchSlotSidecar.runSwitchToEuiccSlot(getTargetSlot(), this.mPort, this.mRemovedSubInfo);
    }
}
