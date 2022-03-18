package com.android.settings.display;

import android.content.Context;
import android.widget.Switch;
import com.android.internal.view.RotationPolicy;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.widget.SettingsMainSwitchBar;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
/* loaded from: classes.dex */
public class AutoRotateSwitchBarController implements OnMainSwitchChangeListener, LifecycleObserver, OnStart, OnStop {
    private final Context mContext;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private final SettingsMainSwitchBar mSwitchBar;
    private boolean mValidListener;

    public AutoRotateSwitchBarController(Context context, SettingsMainSwitchBar settingsMainSwitchBar, Lifecycle lifecycle) {
        this.mSwitchBar = settingsMainSwitchBar;
        this.mContext = context;
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        if (!this.mValidListener) {
            this.mSwitchBar.addOnSwitchChangeListener(this);
            this.mValidListener = true;
        }
        onChange();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        if (this.mValidListener) {
            this.mSwitchBar.removeOnSwitchChangeListener(this);
            this.mValidListener = false;
        }
    }

    @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
    public void onSwitchChanged(Switch r1, boolean z) {
        setRotationLock(z);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onChange() {
        boolean z = !RotationPolicy.isRotationLocked(this.mContext);
        if (z != this.mSwitchBar.isChecked()) {
            if (this.mValidListener) {
                this.mSwitchBar.removeOnSwitchChangeListener(this);
            }
            this.mSwitchBar.setChecked(z);
            if (this.mValidListener) {
                this.mSwitchBar.addOnSwitchChangeListener(this);
            }
        }
    }

    private boolean setRotationLock(boolean z) {
        this.mMetricsFeatureProvider.action(this.mContext, 1753, z);
        RotationPolicy.setRotationLock(this.mContext, !z);
        return true;
    }
}
