package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.widget.Switch;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.PreferenceScreen;
import androidx.window.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.network.SubscriptionsChangeListener;
import com.android.settings.widget.SettingsMainSwitchBar;
import com.android.settings.widget.SettingsMainSwitchPreference;
import java.util.Iterator;
/* loaded from: classes.dex */
public class MobileNetworkSwitchController extends BasePreferenceController implements SubscriptionsChangeListener.SubscriptionsChangeListenerClient, LifecycleObserver {
    private static final String TAG = "MobileNetworkSwitchCtrl";
    private SubscriptionsChangeListener mChangeListener;
    private int mSubId = -1;
    private SubscriptionManager mSubscriptionManager = (SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class);
    private SettingsMainSwitchPreference mSwitchBar;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 1;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onAirplaneModeChanged(boolean z) {
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public MobileNetworkSwitchController(Context context, String str) {
        super(context, str);
        this.mChangeListener = new SubscriptionsChangeListener(context, this);
    }

    public void init(Lifecycle lifecycle, int i) {
        lifecycle.addObserver(this);
        this.mSubId = i;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        this.mChangeListener.start();
        update();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        this.mChangeListener.stop();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        SettingsMainSwitchPreference settingsMainSwitchPreference = (SettingsMainSwitchPreference) preferenceScreen.findPreference(this.mPreferenceKey);
        this.mSwitchBar = settingsMainSwitchPreference;
        settingsMainSwitchPreference.setTitle(this.mContext.getString(R.string.mobile_network_use_sim_on));
        this.mSwitchBar.setOnBeforeCheckedChangeListener(new SettingsMainSwitchBar.OnBeforeCheckedChangeListener() { // from class: com.android.settings.network.telephony.MobileNetworkSwitchController$$ExternalSyntheticLambda0
            @Override // com.android.settings.widget.SettingsMainSwitchBar.OnBeforeCheckedChangeListener
            public final boolean onBeforeCheckedChanged(Switch r1, boolean z) {
                boolean lambda$displayPreference$0;
                lambda$displayPreference$0 = MobileNetworkSwitchController.this.lambda$displayPreference$0(r1, z);
                return lambda$displayPreference$0;
            }
        });
        update();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$displayPreference$0(Switch r2, boolean z) {
        if (this.mSubscriptionManager.isActiveSubscriptionId(this.mSubId) == z) {
            return false;
        }
        SubscriptionUtil.startToggleSubscriptionDialogActivity(this.mContext, this.mSubId, z);
        return true;
    }

    private void update() {
        if (this.mSwitchBar != null) {
            SubscriptionInfo subscriptionInfo = null;
            Iterator<SubscriptionInfo> it = SubscriptionUtil.getAvailableSubscriptions(this.mContext).iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                SubscriptionInfo next = it.next();
                if (next.getSubscriptionId() == this.mSubId) {
                    subscriptionInfo = next;
                    break;
                }
            }
            if (subscriptionInfo == null || (!subscriptionInfo.isEmbedded() && !SubscriptionUtil.showToggleForPhysicalSim(this.mSubscriptionManager))) {
                this.mSwitchBar.hide();
                return;
            }
            this.mSwitchBar.show();
            this.mSwitchBar.setCheckedInternal(this.mSubscriptionManager.isActiveSubscriptionId(this.mSubId));
        }
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onSubscriptionsChanged() {
        update();
    }
}
