package com.android.settings.wifi.calling;

import android.content.Context;
import androidx.window.R;
import com.android.internal.annotations.VisibleForTesting;
/* loaded from: classes.dex */
public class EmergencyCallLimitationDisclaimer extends DisclaimerItem {
    @VisibleForTesting
    static final String KEY_HAS_AGREED_EMERGENCY_LIMITATION_DISCLAIMER = "key_has_agreed_emergency_limitation_disclaimer";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.wifi.calling.DisclaimerItem
    public int getMessageId() {
        return R.string.wfc_disclaimer_emergency_limitation_desc_text;
    }

    @Override // com.android.settings.wifi.calling.DisclaimerItem
    protected String getName() {
        return "EmergencyCallLimitationDisclaimer";
    }

    @Override // com.android.settings.wifi.calling.DisclaimerItem
    protected String getPrefKey() {
        return KEY_HAS_AGREED_EMERGENCY_LIMITATION_DISCLAIMER;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.wifi.calling.DisclaimerItem
    public int getTitleId() {
        return R.string.wfc_disclaimer_emergency_limitation_title_text;
    }

    public EmergencyCallLimitationDisclaimer(Context context, int i) {
        super(context, i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.settings.wifi.calling.DisclaimerItem
    public boolean shouldShow() {
        if (getCarrierConfig().getInt("emergency_notification_delay_int") != -1) {
            return super.shouldShow();
        }
        logd("shouldShow: false due to carrier config is default(-1).");
        return false;
    }
}
