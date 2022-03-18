package com.android.settings.network;

import android.content.Context;
import android.content.Intent;
import android.os.UserManager;
import android.telephony.SubscriptionManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.window.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.network.SubscriptionsChangeListener;
import com.android.settings.network.helper.SubscriptionAnnotation;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.widget.AddPreference;
import com.android.settingslib.Utils;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class MobileNetworkSummaryController extends AbstractPreferenceController implements SubscriptionsChangeListener.SubscriptionsChangeListenerClient, LifecycleObserver, PreferenceControllerMixin {
    private SubscriptionsChangeListener mChangeListener;
    private AddPreference mPreference;
    private SubscriptionManager mSubscriptionManager;
    private UserManager mUserManager;
    private MobileNetworkSummaryStatus mStatusCache = new MobileNetworkSummaryStatus();
    private final MetricsFeatureProvider mMetricsFeatureProvider = FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider();

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "mobile_network_list";
    }

    public MobileNetworkSummaryController(Context context, Lifecycle lifecycle) {
        super(context);
        this.mSubscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
        if (lifecycle != null) {
            this.mChangeListener = new SubscriptionsChangeListener(context, this);
            lifecycle.addObserver(this);
        }
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

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (AddPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        this.mStatusCache.update(this.mContext, null);
        List<SubscriptionAnnotation> subscriptionList = this.mStatusCache.getSubscriptionList();
        if (subscriptionList.isEmpty()) {
            return this.mStatusCache.isEuiccConfigSupport() ? this.mContext.getResources().getString(R.string.mobile_network_summary_add_a_network) : "";
        }
        if (subscriptionList.size() != 1) {
            return (CharSequence) subscriptionList.stream().mapToInt(MobileNetworkSummaryController$$ExternalSyntheticLambda7.INSTANCE).mapToObj(new IntFunction() { // from class: com.android.settings.network.MobileNetworkSummaryController$$ExternalSyntheticLambda6
                @Override // java.util.function.IntFunction
                public final Object apply(int i) {
                    CharSequence lambda$getSummary$0;
                    lambda$getSummary$0 = MobileNetworkSummaryController.this.lambda$getSummary$0(i);
                    return lambda$getSummary$0;
                }
            }).collect(Collectors.joining(", "));
        }
        SubscriptionAnnotation subscriptionAnnotation = subscriptionList.get(0);
        CharSequence displayName = this.mStatusCache.getDisplayName(subscriptionAnnotation.getSubscriptionId());
        return (subscriptionAnnotation.getSubInfo().isEmbedded() || subscriptionAnnotation.isActive() || this.mStatusCache.isPhysicalSimDisableSupport()) ? displayName : this.mContext.getString(R.string.mobile_network_tap_to_activate, displayName);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CharSequence lambda$getSummary$0(int i) {
        return this.mStatusCache.getDisplayName(i);
    }

    private void logPreferenceClick(Preference preference) {
        this.mMetricsFeatureProvider.logClickedPreference(preference, preference.getExtras().getInt(DashboardFragment.CATEGORY));
    }

    private void startAddSimFlow() {
        Intent intent = new Intent("android.telephony.euicc.action.PROVISION_EMBEDDED_SUBSCRIPTION");
        intent.putExtra("android.telephony.euicc.extra.FORCE_PROVISION", true);
        this.mContext.startActivity(intent);
    }

    private void initPreference() {
        refreshSummary(this.mPreference);
        this.mPreference.setOnPreferenceClickListener(null);
        this.mPreference.setOnAddClickListener(null);
        this.mPreference.setFragment(null);
        this.mPreference.setEnabled(!this.mChangeListener.isAirplaneModeOn());
    }

    private void update() {
        AddPreference addPreference = this.mPreference;
        if (addPreference != null && !addPreference.isDisabledByAdmin()) {
            this.mStatusCache.update(this.mContext, new Consumer() { // from class: com.android.settings.network.MobileNetworkSummaryController$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MobileNetworkSummaryController.this.lambda$update$1((MobileNetworkSummaryStatus) obj);
                }
            });
            final List<SubscriptionAnnotation> subscriptionList = this.mStatusCache.getSubscriptionList();
            if (!subscriptionList.isEmpty()) {
                if (this.mStatusCache.isEuiccConfigSupport()) {
                    this.mPreference.setAddWidgetEnabled(!this.mChangeListener.isAirplaneModeOn());
                    this.mPreference.setOnAddClickListener(new AddPreference.OnAddClickListener() { // from class: com.android.settings.network.MobileNetworkSummaryController$$ExternalSyntheticLambda2
                        @Override // com.android.settings.widget.AddPreference.OnAddClickListener
                        public final void onAddClick(AddPreference addPreference2) {
                            MobileNetworkSummaryController.this.lambda$update$3(addPreference2);
                        }
                    });
                }
                if (subscriptionList.size() == 1) {
                    this.mPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.network.MobileNetworkSummaryController$$ExternalSyntheticLambda1
                        @Override // androidx.preference.Preference.OnPreferenceClickListener
                        public final boolean onPreferenceClick(Preference preference) {
                            boolean lambda$update$4;
                            lambda$update$4 = MobileNetworkSummaryController.this.lambda$update$4(subscriptionList, preference);
                            return lambda$update$4;
                        }
                    });
                } else {
                    this.mPreference.setFragment(MobileNetworkListFragment.class.getCanonicalName());
                }
            } else if (this.mStatusCache.isEuiccConfigSupport()) {
                this.mPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.network.MobileNetworkSummaryController$$ExternalSyntheticLambda0
                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public final boolean onPreferenceClick(Preference preference) {
                        boolean lambda$update$2;
                        lambda$update$2 = MobileNetworkSummaryController.this.lambda$update$2(preference);
                        return lambda$update$2;
                    }
                });
            } else {
                this.mPreference.setEnabled(false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$update$1(MobileNetworkSummaryStatus mobileNetworkSummaryStatus) {
        initPreference();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$update$2(Preference preference) {
        logPreferenceClick(preference);
        startAddSimFlow();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$update$3(AddPreference addPreference) {
        logPreferenceClick(addPreference);
        startAddSimFlow();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$update$4(List list, Preference preference) {
        logPreferenceClick(preference);
        SubscriptionAnnotation subscriptionAnnotation = (SubscriptionAnnotation) list.get(0);
        if (subscriptionAnnotation.getSubInfo().isEmbedded() || subscriptionAnnotation.isActive() || this.mStatusCache.isPhysicalSimDisableSupport()) {
            MobileNetworkUtils.launchMobileNetworkSettings(this.mContext, subscriptionAnnotation.getSubInfo());
            return true;
        }
        SubscriptionUtil.startToggleSubscriptionDialogActivity(this.mContext, subscriptionAnnotation.getSubscriptionId(), true);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return !Utils.isWifiOnly(this.mContext) && this.mUserManager.isAdminUser();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onAirplaneModeChanged$5(MobileNetworkSummaryStatus mobileNetworkSummaryStatus) {
        update();
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onAirplaneModeChanged(boolean z) {
        this.mStatusCache.update(this.mContext, new Consumer() { // from class: com.android.settings.network.MobileNetworkSummaryController$$ExternalSyntheticLambda5
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                MobileNetworkSummaryController.this.lambda$onAirplaneModeChanged$5((MobileNetworkSummaryStatus) obj);
            }
        });
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onSubscriptionsChanged() {
        this.mStatusCache.update(this.mContext, new Consumer() { // from class: com.android.settings.network.MobileNetworkSummaryController$$ExternalSyntheticLambda4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                MobileNetworkSummaryController.this.lambda$onSubscriptionsChanged$6((MobileNetworkSummaryStatus) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onSubscriptionsChanged$6(MobileNetworkSummaryStatus mobileNetworkSummaryStatus) {
        refreshSummary(this.mPreference);
        update();
    }
}
