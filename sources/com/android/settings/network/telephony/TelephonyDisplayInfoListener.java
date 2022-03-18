package com.android.settings.network.telephony;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyDisplayInfo;
import android.telephony.TelephonyManager;
import android.util.ArraySet;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
/* loaded from: classes.dex */
public class TelephonyDisplayInfoListener {
    private TelephonyManager mBaseTelephonyManager;
    private Callback mCallback;
    private TelephonyDisplayInfo mTelephonyDisplayInfo = new TelephonyDisplayInfo(0, 0);
    private Map<Integer, PhoneStateListener> mListeners = new HashMap();

    /* loaded from: classes.dex */
    public interface Callback {
        void onTelephonyDisplayInfoChanged(TelephonyDisplayInfo telephonyDisplayInfo);
    }

    public TelephonyDisplayInfoListener(Context context, Callback callback) {
        this.mBaseTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        this.mCallback = callback;
    }

    public void resume() {
        for (Integer num : this.mListeners.keySet()) {
            startListening(num.intValue());
        }
    }

    public void pause() {
        for (Integer num : this.mListeners.keySet()) {
            stopListening(num.intValue());
        }
    }

    public void updateSubscriptionIds(Set<Integer> set) {
        ArraySet arraySet = new ArraySet(this.mListeners.keySet());
        UnmodifiableIterator it = Sets.difference(arraySet, set).iterator();
        while (it.hasNext()) {
            int intValue = ((Integer) it.next()).intValue();
            stopListening(intValue);
            this.mListeners.remove(Integer.valueOf(intValue));
        }
        UnmodifiableIterator it2 = Sets.difference(set, arraySet).iterator();
        while (it2.hasNext()) {
            int intValue2 = ((Integer) it2.next()).intValue();
            this.mListeners.put(Integer.valueOf(intValue2), new PhoneStateListener() { // from class: com.android.settings.network.telephony.TelephonyDisplayInfoListener.1
                @Override // android.telephony.PhoneStateListener
                public void onDisplayInfoChanged(TelephonyDisplayInfo telephonyDisplayInfo) {
                    TelephonyDisplayInfoListener.this.mTelephonyDisplayInfo = telephonyDisplayInfo;
                    TelephonyDisplayInfoListener.this.mCallback.onTelephonyDisplayInfoChanged(telephonyDisplayInfo);
                }
            });
            startListening(intValue2);
        }
    }

    private void startListening(int i) {
        this.mBaseTelephonyManager.createForSubscriptionId(i).listen(this.mListeners.get(Integer.valueOf(i)), 1048576);
    }

    private void stopListening(int i) {
        this.mBaseTelephonyManager.createForSubscriptionId(i).listen(this.mListeners.get(Integer.valueOf(i)), 0);
    }
}
