package com.android.settings.network.telephony.gsm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.Looper;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.window.R;
import com.android.settings.network.AllowedNetworkTypesListener;
import com.android.settings.network.CarrierConfigCache;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settings.network.telephony.TelephonyTogglePreferenceController;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class AutoSelectPreferenceController extends TelephonyTogglePreferenceController implements LifecycleObserver {
    private static final long MINIMUM_DIALOG_TIME_MILLIS = TimeUnit.SECONDS.toMillis(1);
    private AllowedNetworkTypesListener mAllowedNetworkTypesListener;
    private List<OnNetworkSelectModeListener> mListeners = new ArrayList();
    private boolean mOnlyAutoSelectInHome;
    private PreferenceScreen mPreferenceScreen;
    ProgressDialog mProgressDialog;
    SwitchPreference mSwitchPreference;
    private TelephonyManager mTelephonyManager;
    private final Handler mUiHandler;

    /* loaded from: classes.dex */
    public interface OnNetworkSelectModeListener {
        void onNetworkSelectModeChanged();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AutoSelectPreferenceController(Context context, String str) {
        super(context, str);
        this.mTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        this.mSubId = -1;
        Handler handler = new Handler(Looper.getMainLooper());
        this.mUiHandler = handler;
        AllowedNetworkTypesListener allowedNetworkTypesListener = new AllowedNetworkTypesListener(new HandlerExecutor(handler));
        this.mAllowedNetworkTypesListener = allowedNetworkTypesListener;
        allowedNetworkTypesListener.setAllowedNetworkTypesListener(new AllowedNetworkTypesListener.OnAllowedNetworkTypesListener() { // from class: com.android.settings.network.telephony.gsm.AutoSelectPreferenceController$$ExternalSyntheticLambda0
            @Override // com.android.settings.network.AllowedNetworkTypesListener.OnAllowedNetworkTypesListener
            public final void onAllowedNetworkTypesChanged() {
                AutoSelectPreferenceController.this.lambda$new$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: updatePreference */
    public void lambda$new$0() {
        PreferenceScreen preferenceScreen = this.mPreferenceScreen;
        if (preferenceScreen != null) {
            displayPreference(preferenceScreen);
        }
        SwitchPreference switchPreference = this.mSwitchPreference;
        if (switchPreference != null) {
            updateState(switchPreference);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        this.mAllowedNetworkTypesListener.register(this.mContext, this.mSubId);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        this.mAllowedNetworkTypesListener.unregister(this.mContext, this.mSubId);
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.network.telephony.TelephonyAvailabilityCallback
    public int getAvailabilityStatus(int i) {
        return MobileNetworkUtils.shouldDisplayNetworkSelectOptions(this.mContext, i) ? 0 : 2;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceScreen = preferenceScreen;
        this.mSwitchPreference = (SwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return this.mTelephonyManager.getNetworkSelectionMode() == 1;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        preference.setSummary((CharSequence) null);
        ServiceState serviceState = this.mTelephonyManager.getServiceState();
        if (serviceState == null) {
            preference.setEnabled(false);
        } else if (serviceState.getRoaming()) {
            preference.setEnabled(true);
        } else {
            preference.setEnabled(!this.mOnlyAutoSelectInHome);
            if (this.mOnlyAutoSelectInHome) {
                preference.setSummary(this.mContext.getString(R.string.manual_mode_disallowed_summary, this.mTelephonyManager.getSimOperatorName()));
            }
        }
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        if (z) {
            setAutomaticSelectionMode();
            return false;
        } else if (this.mSwitchPreference == null) {
            return false;
        } else {
            Intent intent = new Intent();
            intent.setClassName("com.android.settings", "com.android.settings.Settings$NetworkSelectActivity");
            intent.putExtra("android.provider.extra.SUB_ID", this.mSubId);
            this.mSwitchPreference.setIntent(intent);
            return false;
        }
    }

    Future setAutomaticSelectionMode() {
        final long elapsedRealtime = SystemClock.elapsedRealtime();
        showAutoSelectProgressBar();
        SwitchPreference switchPreference = this.mSwitchPreference;
        if (switchPreference != null) {
            switchPreference.setIntent(null);
            this.mSwitchPreference.setEnabled(false);
        }
        return ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.network.telephony.gsm.AutoSelectPreferenceController$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                AutoSelectPreferenceController.this.lambda$setAutomaticSelectionMode$2(elapsedRealtime);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setAutomaticSelectionMode$2(long j) {
        this.mTelephonyManager.setNetworkSelectionModeAutomatic();
        final int networkSelectionMode = this.mTelephonyManager.getNetworkSelectionMode();
        this.mUiHandler.postDelayed(new Runnable() { // from class: com.android.settings.network.telephony.gsm.AutoSelectPreferenceController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                AutoSelectPreferenceController.this.lambda$setAutomaticSelectionMode$1(networkSelectionMode);
            }
        }, Math.max(MINIMUM_DIALOG_TIME_MILLIS - (SystemClock.elapsedRealtime() - j), 0L));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setAutomaticSelectionMode$1(int i) {
        boolean z = true;
        this.mSwitchPreference.setEnabled(true);
        SwitchPreference switchPreference = this.mSwitchPreference;
        if (i != 1) {
            z = false;
        }
        switchPreference.setChecked(z);
        for (OnNetworkSelectModeListener onNetworkSelectModeListener : this.mListeners) {
            onNetworkSelectModeListener.onNetworkSelectModeChanged();
        }
        dismissProgressBar();
    }

    public AutoSelectPreferenceController init(Lifecycle lifecycle, int i) {
        this.mSubId = i;
        this.mTelephonyManager = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mSubId);
        PersistableBundle configForSubId = CarrierConfigCache.getInstance(this.mContext).getConfigForSubId(this.mSubId);
        this.mOnlyAutoSelectInHome = configForSubId != null ? configForSubId.getBoolean("only_auto_select_in_home_network") : false;
        lifecycle.addObserver(this);
        return this;
    }

    public AutoSelectPreferenceController addListener(OnNetworkSelectModeListener onNetworkSelectModeListener) {
        this.mListeners.add(onNetworkSelectModeListener);
        return this;
    }

    private void showAutoSelectProgressBar() {
        if (this.mProgressDialog == null) {
            ProgressDialog progressDialog = new ProgressDialog(this.mContext);
            this.mProgressDialog = progressDialog;
            progressDialog.setMessage(this.mContext.getResources().getString(R.string.register_automatically));
            this.mProgressDialog.setCanceledOnTouchOutside(false);
            this.mProgressDialog.setCancelable(false);
            this.mProgressDialog.setIndeterminate(true);
        }
        this.mProgressDialog.show();
    }

    private void dismissProgressBar() {
        ProgressDialog progressDialog = this.mProgressDialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            try {
                this.mProgressDialog.dismiss();
            } catch (IllegalArgumentException unused) {
            }
        }
    }
}
