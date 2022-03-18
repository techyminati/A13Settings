package com.google.android.settings.gestures.assist;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.SeekBar;
import androidx.window.R;
import com.android.settings.SetupWizardUtils;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupdesign.GlifLayout;
import com.google.android.setupdesign.util.ThemeHelper;
/* loaded from: classes2.dex */
public class AssistGestureTrainingFinishedActivity extends AssistGestureTrainingSliderBase {
    private boolean mAccessibilityAnnounced;
    private View mAssistGestureCheck;
    private View mAssistGestureIllustration;
    private GlifLayout mLayout;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 993;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.settings.gestures.assist.AssistGestureTrainingSliderBase, com.google.android.settings.gestures.assist.AssistGestureTrainingBase, com.android.settings.core.InstrumentedActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        setTheme(SetupWizardUtils.getTheme(this, getIntent()));
        ThemeHelper.trySetDynamicColor(this);
        setContentView(R.layout.assist_gesture_training_finished_activity);
        this.mLayout = (GlifLayout) findViewById(R.id.layout);
        super.onCreate(bundle);
        setShouldCheckForNoProgress(false);
        FooterBarMixin footerBarMixin = (FooterBarMixin) this.mLayout.getMixin(FooterBarMixin.class);
        footerBarMixin.setSecondaryButton(new FooterButton.Builder(this).setText(R.string.assist_gesture_enrollment_settings).setListener(new View.OnClickListener() { // from class: com.google.android.settings.gestures.assist.AssistGestureTrainingFinishedActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AssistGestureTrainingFinishedActivity.this.onSettingsButtonClicked(view);
            }
        }).setButtonType(0).setTheme(R.style.SudGlifButton_Secondary).build());
        FooterButton secondaryButton = footerBarMixin.getSecondaryButton();
        if (flowTypeDeferredSetup() || flowTypeSetup()) {
            secondaryButton.setVisibility(4);
        }
        footerBarMixin.setPrimaryButton(new FooterButton.Builder(this).setText(R.string.done).setListener(new View.OnClickListener() { // from class: com.google.android.settings.gestures.assist.AssistGestureTrainingFinishedActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AssistGestureTrainingFinishedActivity.this.onNextButtonClicked(view);
            }
        }).setButtonType(5).setTheme(R.style.SudGlifButton_Primary).build());
        FooterButton primaryButton = footerBarMixin.getPrimaryButton();
        if (flowTypeDeferredSetup() || flowTypeSetup()) {
            primaryButton.setText(this, R.string.next_label);
        } else if (flowTypeSettingsSuggestion()) {
            primaryButton.setText(this, R.string.done);
        } else if (flowTypeAccidentalTrigger()) {
            primaryButton.setText(this, R.string.assist_gesture_enrollment_continue_to_assistant);
        }
        this.mAssistGestureCheck = findViewById(R.id.assist_gesture_training_check);
        this.mAssistGestureIllustration = findViewById(R.id.assist_gesture_training_illustration);
        fadeOutCheckAfterDelay();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNextButtonClicked(View view) {
        if (flowTypeDeferredSetup() || flowTypeSettingsSuggestion() || flowTypeSetup()) {
            setResult(-1);
            this.mAssistGestureHelper.setListener(null);
            finishAndRemoveTask();
        } else if (flowTypeAccidentalTrigger()) {
            handleDoneAndLaunch();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSettingsButtonClicked(View view) {
        launchAssistGestureSettings();
    }

    @Override // com.google.android.settings.gestures.assist.AssistGestureTrainingSliderBase, android.widget.SeekBar.OnSeekBarChangeListener
    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        super.onProgressChanged(seekBar, i, z);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.settings.gestures.assist.AssistGestureTrainingSliderBase
    public void showMessage(int i, String str) {
        if (this.mAssistGestureCheck.getVisibility() == 4) {
            super.showMessage(i, str);
        }
    }

    @Override // com.google.android.settings.gestures.assist.AssistGestureTrainingSliderBase
    protected void handleGestureDetected() {
        this.mErrorView.setVisibility(4);
        this.mAssistGestureCheck.animate().cancel();
        this.mAssistGestureCheck.setAlpha(1.0f);
        this.mAssistGestureCheck.setVisibility(0);
        if (!this.mAccessibilityAnnounced) {
            this.mLayout.announceForAccessibility(getApplicationContext().getResources().getString(R.string.accessibility_assist_gesture_complete_or_keep_adjusting));
            this.mAccessibilityAnnounced = true;
        }
        this.mHandler.removeMessages(4);
        this.mHandler.removeMessages(5);
        fadeOutCheckAfterDelay();
        fadeIndicators();
    }

    @Override // com.google.android.settings.gestures.assist.AssistGestureTrainingSliderBase, android.widget.SeekBar.OnSeekBarChangeListener
    public void onStartTrackingTouch(SeekBar seekBar) {
        super.onStartTrackingTouch(seekBar);
        this.mHandler.removeMessages(4);
        this.mHandler.removeMessages(5);
        this.mHandler.obtainMessage(6, this.mAssistGestureCheck).sendToTarget();
        this.mHandler.obtainMessage(7, this.mAssistGestureIllustration).sendToTarget();
    }

    private void fadeOutCheckAfterDelay() {
        Handler handler = this.mHandler;
        handler.sendMessageDelayed(handler.obtainMessage(5, this.mAssistGestureCheck), 1000L);
    }
}
