package com.android.settings.network;

import android.content.Context;
import android.content.IntentFilter;
import android.net.EthernetManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.internal.annotations.VisibleForTesting;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public final class EthernetTetherPreferenceController extends TetherBasePreferenceController {
    @VisibleForTesting
    EthernetManager.Listener mEthernetListener;
    private final EthernetManager mEthernetManager;
    private final String mEthernetRegex;

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController
    public int getTetherType() {
        return 5;
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public EthernetTetherPreferenceController(Context context, String str) {
        super(context, str);
        this.mEthernetRegex = context.getString(17039958);
        this.mEthernetManager = (EthernetManager) context.getSystemService("ethernet");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onStart$0(String str, boolean z) {
        updateState(this.mPreference);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        this.mEthernetListener = new EthernetManager.Listener() { // from class: com.android.settings.network.EthernetTetherPreferenceController$$ExternalSyntheticLambda0
            public final void onAvailabilityChanged(String str, boolean z) {
                EthernetTetherPreferenceController.this.lambda$onStart$0(str, z);
            }
        };
        final Handler handler = new Handler(Looper.getMainLooper());
        this.mEthernetManager.addListener(this.mEthernetListener, new Executor() { // from class: com.android.settings.network.EthernetTetherPreferenceController$$ExternalSyntheticLambda1
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                handler.post(runnable);
            }
        });
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        this.mEthernetManager.removeListener(this.mEthernetListener);
        this.mEthernetListener = null;
    }

    @Override // com.android.settings.network.TetherBasePreferenceController
    public boolean shouldEnable() {
        for (String str : this.mTm.getTetherableIfaces()) {
            if (str.matches(this.mEthernetRegex)) {
                return true;
            }
        }
        return false;
    }

    @Override // com.android.settings.network.TetherBasePreferenceController
    public boolean shouldShow() {
        return !TextUtils.isEmpty(this.mEthernetRegex);
    }
}
