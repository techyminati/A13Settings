package com.google.android.settings.gestures.assist;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.ContextThemeWrapper;
import android.view.WindowManager;
import androidx.window.R;
import com.android.settings.core.InstrumentedActivity;
import com.android.settings.gestures.AssistGestureFeatureProvider;
import com.android.settings.overlay.FeatureFactory;
import com.google.android.settings.gestures.assist.AssistGestureHelper;
/* loaded from: classes2.dex */
public abstract class AssistGestureTrainingBase extends InstrumentedActivity implements AssistGestureHelper.GestureListener {
    protected AssistGestureHelper mAssistGestureHelper;
    private AssistGestureIndicatorView mIndicatorView;
    private String mLaunchedFrom;
    private WindowManager mWindowManager;

    /* loaded from: classes2.dex */
    protected class HandleProgress {
        private boolean mErrorSqueezeBottomShown;
        private final Handler mHandler;
        private int mLastStage;
        private boolean mShouldCheckForNoProgress = true;

        public HandleProgress(Handler handler) {
            this.mHandler = handler;
        }

        public void setShouldCheckForNoProgress(boolean z) {
            this.mShouldCheckForNoProgress = z;
        }

        private boolean checkSqueezeNoProgress(int i) {
            return this.mLastStage == 1 && i == 0;
        }

        private boolean checkSqueezeTooLong(int i) {
            return this.mLastStage == 2 && i == 0;
        }

        public void onGestureProgress(float f, int i) {
            int i2;
            if (this.mLastStage != i) {
                if (!this.mShouldCheckForNoProgress || !checkSqueezeNoProgress(i)) {
                    i2 = checkSqueezeTooLong(i) ? 2 : 0;
                } else {
                    i2 = 1;
                }
                this.mLastStage = i;
                if (i2 != 0) {
                    if (i2 == 1) {
                        if (this.mErrorSqueezeBottomShown) {
                            i2 = 4;
                        }
                        this.mErrorSqueezeBottomShown = true;
                    }
                    this.mHandler.obtainMessage(2, i2, 0).sendToTarget();
                }
            }
        }

        public void onGestureDetected() {
            this.mLastStage = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean flowTypeSetup() {
        return "setup".contentEquals(this.mLaunchedFrom);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean flowTypeDeferredSetup() {
        return "deferred_setup".contentEquals(this.mLaunchedFrom);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean flowTypeSettingsSuggestion() {
        return "settings_suggestion".contentEquals(this.mLaunchedFrom);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean flowTypeAccidentalTrigger() {
        return "accidental_trigger".contentEquals(this.mLaunchedFrom);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mWindowManager = (WindowManager) getSystemService("window");
        AssistGestureHelper assistGestureHelper = new AssistGestureHelper(getApplicationContext());
        this.mAssistGestureHelper = assistGestureHelper;
        assistGestureHelper.setListener(this);
        this.mLaunchedFrom = getIntent().getStringExtra("launched_from");
        this.mIndicatorView = new AssistGestureIndicatorView(new ContextThemeWrapper(getApplicationContext(), getTheme()));
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        boolean z = Settings.Secure.getInt(getContentResolver(), "assist_gesture_enabled", 1) != 0;
        AssistGestureFeatureProvider assistGestureFeatureProvider = FeatureFactory.getFactory(this).getAssistGestureFeatureProvider();
        WindowManager windowManager = this.mWindowManager;
        AssistGestureIndicatorView assistGestureIndicatorView = this.mIndicatorView;
        windowManager.addView(assistGestureIndicatorView, assistGestureIndicatorView.getLayoutParams(getWindow().getAttributes()));
        this.mAssistGestureHelper.bindToElmyraServiceProxy();
        this.mAssistGestureHelper.setListener(this);
        if (!assistGestureFeatureProvider.isSupported(this) || !z) {
            setResult(1);
            finishAndRemoveTask();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        this.mAssistGestureHelper.setListener(null);
        this.mAssistGestureHelper.unbindFromElmyraServiceProxy();
        clearIndicators();
        this.mWindowManager.removeView(this.mIndicatorView);
    }

    @Override // android.app.Activity, android.view.ContextThemeWrapper
    protected void onApplyThemeResource(Resources.Theme theme, int i, boolean z) {
        theme.applyStyle(R.style.SetupWizardPartnerResource, true);
        super.onApplyThemeResource(theme, i, z);
    }

    @Override // com.google.android.settings.gestures.assist.AssistGestureHelper.GestureListener
    public void onGestureProgress(float f, int i) {
        this.mIndicatorView.onGestureProgress(f);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void clearIndicators() {
        this.mIndicatorView.onGestureProgress(0.0f);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void fadeIndicators() {
        this.mIndicatorView.onGestureDetected();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleDoneAndLaunch() {
        setResult(-1);
        this.mAssistGestureHelper.setListener(null);
        this.mAssistGestureHelper.launchAssistant();
        finishAndRemoveTask();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void launchAssistGestureSettings() {
        startActivity(new Intent("android.settings.ASSIST_GESTURE_SETTINGS"));
    }
}
