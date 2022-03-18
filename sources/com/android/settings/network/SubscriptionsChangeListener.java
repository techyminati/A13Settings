package com.android.settings.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.util.Log;
/* loaded from: classes.dex */
public class SubscriptionsChangeListener extends ContentObserver {
    private SubscriptionsChangeListenerClient mClient;
    private Context mContext;
    private SubscriptionManager mSubscriptionManager;
    private boolean mRunning = false;
    private SubscriptionManager.OnSubscriptionsChangedListener mSubscriptionsChangedListener = new SubscriptionManager.OnSubscriptionsChangedListener(Looper.getMainLooper()) { // from class: com.android.settings.network.SubscriptionsChangeListener.1
        @Override // android.telephony.SubscriptionManager.OnSubscriptionsChangedListener
        public void onSubscriptionsChanged() {
            SubscriptionsChangeListener.this.subscriptionsChangedCallback();
        }
    };
    private Uri mAirplaneModeSettingUri = Settings.Global.getUriFor("airplane_mode_on");
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.settings.network.SubscriptionsChangeListener.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (!isInitialStickyBroadcast()) {
                SubscriptionsChangeListener.this.subscriptionsChangedCallback();
            }
        }
    };

    /* loaded from: classes.dex */
    public interface SubscriptionsChangeListenerClient {
        void onAirplaneModeChanged(boolean z);

        void onSubscriptionsChanged();
    }

    public SubscriptionsChangeListener(Context context, SubscriptionsChangeListenerClient subscriptionsChangeListenerClient) {
        super(new Handler(Looper.getMainLooper()));
        this.mContext = context;
        this.mClient = subscriptionsChangeListenerClient;
        this.mSubscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
    }

    public void start() {
        this.mSubscriptionManager.addOnSubscriptionsChangedListener(this.mContext.getMainExecutor(), this.mSubscriptionsChangedListener);
        this.mContext.getContentResolver().registerContentObserver(this.mAirplaneModeSettingUri, false, this);
        this.mContext.registerReceiver(this.mBroadcastReceiver, new IntentFilter("android.intent.action.RADIO_TECHNOLOGY"));
        this.mRunning = true;
    }

    public void stop() {
        if (this.mRunning) {
            this.mSubscriptionManager.removeOnSubscriptionsChangedListener(this.mSubscriptionsChangedListener);
            this.mContext.getContentResolver().unregisterContentObserver(this);
            this.mContext.unregisterReceiver(this.mBroadcastReceiver);
            this.mRunning = false;
            return;
        }
        Log.d("SubscriptionsChangeListener", "Stop has been called without associated Start.");
    }

    public boolean isAirplaneModeOn() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) != 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void subscriptionsChangedCallback() {
        this.mClient.onSubscriptionsChanged();
    }

    @Override // android.database.ContentObserver
    public void onChange(boolean z, Uri uri) {
        if (uri.equals(this.mAirplaneModeSettingUri)) {
            this.mClient.onAirplaneModeChanged(isAirplaneModeOn());
        }
    }
}
