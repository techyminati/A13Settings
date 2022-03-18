package com.android.settings.accessibility.rtt;

import android.content.Context;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/* loaded from: classes.dex */
public abstract class TelecomUtil {
    public static List<PhoneAccountHandle> getCallCapablePhoneAccounts(Context context) {
        return (List) Optional.ofNullable(getTelecomManager(context).getCallCapablePhoneAccounts()).orElse(new ArrayList());
    }

    public static TelecomManager getTelecomManager(Context context) {
        return (TelecomManager) context.getApplicationContext().getSystemService(TelecomManager.class);
    }

    public static int getSubIdForPhoneAccountHandle(Context context, PhoneAccountHandle phoneAccountHandle) {
        return ((Integer) getSubscriptionInfo(context, phoneAccountHandle).map(TelecomUtil$$ExternalSyntheticLambda0.INSTANCE).orElse(-1)).intValue();
    }

    private static Optional<SubscriptionInfo> getSubscriptionInfo(Context context, PhoneAccountHandle phoneAccountHandle) {
        if (TextUtils.isEmpty(phoneAccountHandle.getId())) {
            return Optional.empty();
        }
        List<SubscriptionInfo> activeSubscriptionInfoList = ((SubscriptionManager) context.getSystemService(SubscriptionManager.class)).getActiveSubscriptionInfoList();
        if (activeSubscriptionInfoList == null) {
            return Optional.empty();
        }
        for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
            if (phoneAccountHandle.getId().startsWith(subscriptionInfo.getIccId())) {
                return Optional.of(subscriptionInfo);
            }
        }
        Log.d("TelecomUtil", "Failed to find SubscriptionInfo for phoneAccountHandle");
        return Optional.empty();
    }
}
