package com.google.android.settings.gestures.columbus;

import android.app.ActivityManager;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Pair;
import android.widget.Switch;
import android.widget.Toast;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.window.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
import com.google.android.settings.gestures.columbus.ColumbusGestureHelper;
/* loaded from: classes2.dex */
public class ColumbusEnabledPreferenceController extends BasePreferenceController implements OnMainSwitchChangeListener, ColumbusGestureHelper.GestureListener, LifecycleObserver, OnPause, OnResume {
    static final int COLUMBUS_DISABLED = 0;
    static final int COLUMBUS_ENABLED = 1;
    static final String SECURE_KEY_COLUMBUS_ENABLED = "columbus_enabled";
    private final ColumbusGestureHelper mColumbusGestureHelper;
    private final Handler mHandler = new Handler(Looper.myLooper());
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private MainSwitchPreference mSwitchBar;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
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

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public ColumbusEnabledPreferenceController(Context context, String str) {
        super(context, str);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
        this.mColumbusGestureHelper = new ColumbusGestureHelper(context);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (isAvailable()) {
            MainSwitchPreference mainSwitchPreference = (MainSwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
            this.mSwitchBar = mainSwitchPreference;
            if (mainSwitchPreference != null) {
                mainSwitchPreference.addOnSwitchChangeListener(this);
            }
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (this.mSwitchBar != null) {
            this.mSwitchBar.updateStatus(ColumbusPreferenceController.isColumbusEnabled(this.mContext));
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return ColumbusPreferenceController.isColumbusSupported(this.mContext) ? 0 : 3;
    }

    @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
    public void onSwitchChanged(Switch r3, boolean z) {
        Settings.Secure.putIntForUser(this.mContext.getContentResolver(), SECURE_KEY_COLUMBUS_ENABLED, z ? 1 : 0, ActivityManager.getCurrentUser());
        this.mMetricsFeatureProvider.action(this.mContext, z ? 1740 : 1741, new Pair[0]);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        this.mColumbusGestureHelper.bindToColumbusServiceProxy();
        this.mColumbusGestureHelper.setListener(this);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        this.mColumbusGestureHelper.setListener(null);
        this.mColumbusGestureHelper.unbindFromColumbusServiceProxy();
    }

    @Override // com.google.android.settings.gestures.columbus.ColumbusGestureHelper.GestureListener
    public void onTrigger() {
        this.mHandler.post(new Runnable() { // from class: com.google.android.settings.gestures.columbus.ColumbusEnabledPreferenceController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ColumbusEnabledPreferenceController.this.lambda$onTrigger$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onTrigger$0() {
        Toast.makeText(this.mSwitchBar.getContext(), (int) R.string.columbus_gesture_detected, 0).show();
    }
}
