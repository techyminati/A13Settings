package com.google.android.settings.gestures.columbus;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import androidx.window.R;
import com.android.settings.dashboard.DashboardFragment;
import com.google.android.settings.gestures.columbus.ColumbusGestureHelper;
/* loaded from: classes2.dex */
public class ColumbusGestureLaunchSettingsFragment extends DashboardFragment implements ColumbusGestureHelper.GestureListener {
    private ColumbusGestureHelper mColumbusGestureHelper;
    private Context mContext;
    private Handler mHandler;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "ColumbusLaunchSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1871;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.columbus_launch_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mColumbusGestureHelper = new ColumbusGestureHelper(context);
        this.mContext = context;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mHandler = new Handler(Looper.myLooper());
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mColumbusGestureHelper.bindToColumbusServiceProxy();
        this.mColumbusGestureHelper.setListener(this);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mColumbusGestureHelper.setListener(null);
        this.mColumbusGestureHelper.unbindFromColumbusServiceProxy();
    }

    @Override // com.google.android.settings.gestures.columbus.ColumbusGestureHelper.GestureListener
    public void onTrigger() {
        this.mHandler.post(new Runnable() { // from class: com.google.android.settings.gestures.columbus.ColumbusGestureLaunchSettingsFragment$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ColumbusGestureLaunchSettingsFragment.this.lambda$onTrigger$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onTrigger$0() {
        Toast.makeText(this.mContext, (int) R.string.columbus_gesture_detected, 0).show();
    }
}
